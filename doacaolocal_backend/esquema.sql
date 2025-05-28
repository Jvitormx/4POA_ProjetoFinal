-- Tipos customizados para melhor semântica
CREATE TYPE tipo_publicacao AS ENUM ('OFERTA', 'PEDIDO');
CREATE TYPE status_publicacao AS ENUM ('ABERTA', 'EM_NEGOCIACAO', 'CONCLUIDA', 'CANCELADA');
CREATE TYPE status_match AS ENUM ('PENDENTE', 'CONFIRMADO', 'RECUSADO', 'CANCELADO');

-- Tabela de Usuários
CREATE TABLE usuarios (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome            VARCHAR(100) NOT NULL,
  email           VARCHAR(150) NOT NULL UNIQUE,
  hash_senha      VARCHAR(255) NOT NULL,
  telefone        VARCHAR(20),
  eh_instituicao  BOOLEAN NOT NULL DEFAULT FALSE,
  latitude_padrao DOUBLE PRECISION,
  longitude_padrao DOUBLE PRECISION,
  raio_busca_km   INTEGER NOT NULL DEFAULT 5,
  criado_em       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  atualizado_em   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- Tabela unificada de Publicações (ofertas + pedidos)
CREATE TABLE publicacoes (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tipo            tipo_publicacao NOT NULL,
  usuario_id      UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
  titulo          VARCHAR(150) NOT NULL,
  descricao       TEXT,
  categoria       VARCHAR(50) NOT NULL,
  quantidade      INTEGER NOT NULL DEFAULT 1 CHECK (quantidade >= 0),
  quantidade_original INTEGER NOT NULL, -- Para cálculo de porcentagem atendida
  inicio_coleta   TIMESTAMP WITH TIME ZONE,
  fim_coleta      TIMESTAMP WITH TIME ZONE,
  latitude        DOUBLE PRECISION NOT NULL,
  longitude       DOUBLE PRECISION NOT NULL,
  status          status_publicacao NOT NULL,
  permite_entrega BOOLEAN DEFAULT FALSE,
  urgente         BOOLEAN DEFAULT FALSE, -- Para pedidos prioritários
  criado_em       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  atualizado_em   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  CONSTRAINT check_dates CHECK (
    (tipo = 'OFERTA' AND inicio_coleta IS NOT NULL AND fim_coleta IS NOT NULL) OR
    (tipo = 'PEDIDO' AND inicio_coleta IS NULL AND fim_coleta IS NULL)
  )
);

-- Tabela de Matches (CORRIGIDA: sem CHECK constraints com subconsultas)
CREATE TABLE matches (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  oferta_id       UUID REFERENCES publicacoes(id), -- Validação de tipo será feita por trigger
  pedido_id       UUID REFERENCES publicacoes(id), -- Validação de tipo será feita por trigger
  quantidade      INTEGER NOT NULL CHECK (quantidade > 0),
  status          status_match NOT NULL,
  criado_em       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  atualizado_em   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  CONSTRAINT valid_match CHECK (
    (oferta_id IS NOT NULL AND pedido_id IS NULL) OR -- Oferta direta
    (oferta_id IS NULL AND pedido_id IS NOT NULL) OR -- Pedido direto
    (oferta_id IS NOT NULL AND pedido_id IS NOT NULL) -- Matching automático
  )
);

-- Tabela de Mensagens (agora vinculada a matches)
CREATE TABLE mensagens (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  match_id        UUID NOT NULL REFERENCES matches(id) ON DELETE CASCADE,
  remetente_id    UUID NOT NULL REFERENCES usuarios(id) ON DELETE SET NULL,
  conteudo        TEXT NOT NULL,
  enviado_em      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
  -- Se quiser um campo atualizado_em aqui, adicione:
  -- atualizado_em   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- Índices geoespaciais
CREATE INDEX idx_publicacoes_geo ON publicacoes USING GIST (
  ll_to_earth(latitude, longitude)
);

-- Índices para buscas comuns
CREATE INDEX idx_publicacoes_tipo_status ON publicacoes (tipo, status);
CREATE INDEX idx_matches_status ON matches (status);
CREATE INDEX idx_mensagens_match ON mensagens (match_id);

-- Função para Atualização de timestamps
CREATE OR REPLACE FUNCTION atualizar_data()
RETURNS TRIGGER AS $$
BEGIN
   NEW.atualizado_em = now();
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Função para Controle de quantidade para matches
CREATE OR REPLACE FUNCTION atualizar_quantidades()
RETURNS TRIGGER AS $$
BEGIN
  -- Atualização na confirmação de match
  IF (TG_OP = 'INSERT' AND NEW.status = 'CONFIRMADO') OR
     (TG_OP = 'UPDATE' AND OLD.status != 'CONFIRMADO' AND NEW.status = 'CONFIRMADO') THEN

    IF NEW.oferta_id IS NOT NULL THEN
      UPDATE publicacoes
      SET quantidade = quantidade - NEW.quantidade
      WHERE id = NEW.oferta_id;
    END IF;

    IF NEW.pedido_id IS NOT NULL THEN
      UPDATE publicacoes
      SET quantidade = quantidade - NEW.quantidade
      WHERE id = NEW.pedido_id;
    END IF;
  END IF;

  -- Devolução de quantidade em cancelamentos ou recusas de matches previamente confirmados
  IF (TG_OP = 'UPDATE' AND OLD.status = 'CONFIRMADO' AND (NEW.status = 'CANCELADO' OR NEW.status = 'RECUSADO')) OR
     (TG_OP = 'DELETE' AND OLD.status = 'CONFIRMADO') THEN

    IF OLD.oferta_id IS NOT NULL THEN
      UPDATE publicacoes
      SET quantidade = quantidade + OLD.quantidade
      WHERE id = OLD.oferta_id;
    END IF;

    IF OLD.pedido_id IS NOT NULL THEN
      UPDATE publicacoes
      SET quantidade = quantidade + OLD.quantidade
      WHERE id = OLD.pedido_id;
    END IF;
  END IF;

  IF TG_OP = 'DELETE' THEN
    RETURN OLD;
  ELSE
    RETURN NEW;
  END IF;
END;
$$ LANGUAGE plpgsql;

-- Função para Verificação de status das publicações
CREATE OR REPLACE FUNCTION verificar_status_publicacao()
RETURNS TRIGGER AS $$
BEGIN
  -- Para ofertas
  IF NEW.tipo = 'OFERTA' THEN
    IF NEW.quantidade <= 0 THEN
      NEW.status := 'CONCLUIDA';
    ELSIF NEW.fim_coleta < NOW() AND NEW.status != 'CONCLUIDA' THEN
      NEW.status := 'CANCELADA';
    ELSIF NEW.status NOT IN ('EM_NEGOCIACAO', 'CONCLUIDA', 'CANCELADA') THEN
      NEW.status := 'ABERTA';
    END IF;

  -- Para pedidos
  ELSE -- tipo = 'PEDIDO'
    IF NEW.quantidade <= 0 THEN
      NEW.status := 'CONCLUIDA';
    ELSIF NEW.status NOT IN ('EM_NEGOCIACAO', 'CONCLUIDA', 'CANCELADA') THEN
      NEW.status := 'ABERTA';
    END IF;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Função para Validação de quantidade em matches
CREATE OR REPLACE FUNCTION validar_quantidade_match()
RETURNS TRIGGER AS $$
DECLARE
  disponivel_oferta INTEGER;
  disponivel_pedido INTEGER;
  status_oferta_atual status_publicacao;
  status_pedido_atual status_publicacao;
BEGIN
  IF NEW.oferta_id IS NOT NULL THEN
    SELECT quantidade, status INTO disponivel_oferta, status_oferta_atual
    FROM publicacoes WHERE id = NEW.oferta_id;

    IF status_oferta_atual NOT IN ('ABERTA', 'EM_NEGOCIACAO') THEN
        RAISE EXCEPTION 'A oferta não está disponível para novos matches (status: %)', status_oferta_atual;
    END IF;

    IF NEW.quantidade > disponivel_oferta THEN
      RAISE EXCEPTION 'Quantidade solicitada (% unidades) excede o disponível na oferta (% unidades)', NEW.quantidade, disponivel_oferta;
    END IF;
  END IF;

  IF NEW.pedido_id IS NOT NULL THEN
    SELECT quantidade, status INTO disponivel_pedido, status_pedido_atual
    FROM publicacoes WHERE id = NEW.pedido_id;

    IF status_pedido_atual NOT IN ('ABERTA', 'EM_NEGOCIACAO') THEN
        RAISE EXCEPTION 'O pedido não está disponível para novos matches (status: %)', status_pedido_atual;
    END IF;

    IF NEW.quantidade > disponivel_pedido THEN
      RAISE EXCEPTION 'Quantidade ofertada (% unidades) excede o necessário no pedido (% unidades)', NEW.quantidade, disponivel_pedido;
    END IF;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Função para validar o TIPO da publicação em um match
CREATE OR REPLACE FUNCTION verificar_tipos_publicacao_match()
RETURNS TRIGGER AS $$
DECLARE
  tipo_publicacao_ref tipo_publicacao;
BEGIN
  IF NEW.oferta_id IS NOT NULL THEN
    SELECT tipo INTO tipo_publicacao_ref
    FROM publicacoes
    WHERE id = NEW.oferta_id;

    IF NOT FOUND THEN
      RAISE EXCEPTION 'Publicação de oferta com ID % não encontrada.', NEW.oferta_id;
    END IF;

    IF tipo_publicacao_ref != 'OFERTA' THEN
      RAISE EXCEPTION 'A publicação referenciada por oferta_id (%) deve ser do tipo ''OFERTA'', mas é do tipo ''%''.', NEW.oferta_id, tipo_publicacao_ref;
    END IF;
  END IF;

  IF NEW.pedido_id IS NOT NULL THEN
    SELECT tipo INTO tipo_publicacao_ref
    FROM publicacoes
    WHERE id = NEW.pedido_id;

    IF NOT FOUND THEN
      RAISE EXCEPTION 'Publicação de pedido com ID % não encontrada.', NEW.pedido_id;
    END IF;

    IF tipo_publicacao_ref != 'PEDIDO' THEN
      RAISE EXCEPTION 'A publicação referenciada por pedido_id (%) deve ser do tipo ''PEDIDO'', mas é do tipo ''%''.', NEW.pedido_id, tipo_publicacao_ref;
    END IF;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Gatilhos
CREATE TRIGGER trg_usuarios_atualizado
BEFORE UPDATE ON usuarios
FOR EACH ROW EXECUTE PROCEDURE atualizar_data();

CREATE TRIGGER trg_publicacoes_atualizado
BEFORE UPDATE ON publicacoes
FOR EACH ROW EXECUTE PROCEDURE atualizar_data();

CREATE TRIGGER trg_matches_atualizado
BEFORE UPDATE ON matches
FOR EACH ROW EXECUTE PROCEDURE atualizar_data();

-- Se a tabela mensagens tiver um campo atualizado_em, descomente o trigger abaixo:
-- CREATE TRIGGER trg_mensagens_atualizado
-- BEFORE UPDATE ON mensagens
-- FOR EACH ROW EXECUTE PROCEDURE atualizar_data();

CREATE TRIGGER trg_publicacoes_status
BEFORE INSERT OR UPDATE OF quantidade, fim_coleta ON publicacoes
FOR EACH ROW EXECUTE PROCEDURE verificar_status_publicacao();

CREATE TRIGGER trg_matches_quantidade
AFTER INSERT OR UPDATE OF status, quantidade ON matches
FOR EACH ROW EXECUTE PROCEDURE atualizar_quantidades();

CREATE TRIGGER trg_validar_match_quantidade
BEFORE INSERT OR UPDATE OF quantidade, oferta_id, pedido_id ON matches
FOR EACH ROW EXECUTE PROCEDURE validar_quantidade_match();

CREATE TRIGGER trg_verificar_tipos_publicacao_no_match
BEFORE INSERT OR UPDATE OF oferta_id, pedido_id ON matches
FOR EACH ROW EXECUTE FUNCTION verificar_tipos_publicacao_match();
