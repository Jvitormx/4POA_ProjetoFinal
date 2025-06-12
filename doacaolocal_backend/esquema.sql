-- Tabelas de domínio
CREATE TABLE tipo_publicacao (
  id SERIAL PRIMARY KEY,
  nome VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE status_publicacao (
  id SERIAL PRIMARY KEY,
  nome VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE status_match (
  id SERIAL PRIMARY KEY,
  nome VARCHAR(30) NOT NULL UNIQUE
);

-- Popule as tabelas de domínio
INSERT INTO tipo_publicacao (nome) VALUES ('OFERTA'), ('PEDIDO');
INSERT INTO status_publicacao (nome) VALUES ('ABERTA'), ('EM_NEGOCIACAO'), ('CONCLUIDA'), ('CANCELADA');
INSERT INTO status_match (nome) VALUES ('PENDENTE'), ('CONFIRMADO'), ('RECUSADO'), ('CANCELADO');

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
  tipo_id         INTEGER NOT NULL REFERENCES tipo_publicacao(id),
  usuario_id      UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
  titulo          VARCHAR(150) NOT NULL,
  descricao       TEXT,
  categoria       VARCHAR(50) NOT NULL,
  quantidade      INTEGER NOT NULL DEFAULT 1 CHECK (quantidade >= 0),
  quantidade_original INTEGER NOT NULL,
  inicio_coleta   TIMESTAMP WITH TIME ZONE,
  fim_coleta      TIMESTAMP WITH TIME ZONE,
  latitude        DOUBLE PRECISION NOT NULL,
  longitude       DOUBLE PRECISION NOT NULL,
  status_id       INTEGER NOT NULL REFERENCES status_publicacao(id),
  permite_entrega BOOLEAN DEFAULT FALSE,
  urgente         BOOLEAN DEFAULT FALSE,
  criado_em       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  atualizado_em   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  CONSTRAINT check_dates CHECK (
    ((tipo_id = 1) AND inicio_coleta IS NOT NULL AND fim_coleta IS NOT NULL) OR
    ((tipo_id = 2) AND inicio_coleta IS NULL AND fim_coleta IS NULL)
  )
);

-- Tabela de Matches
CREATE TABLE matches (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  publicacao_id   UUID NOT NULL REFERENCES publicacoes(id),
  usuario_id      UUID NOT NULL REFERENCES usuarios(id),
  quantidade      INTEGER NOT NULL CHECK (quantidade > 0),
  status_id       INTEGER NOT NULL REFERENCES status_match(id),
  criado_em       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  atualizado_em   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- Tabela de Mensagens
CREATE TABLE mensagens (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  match_id        UUID NOT NULL REFERENCES matches(id) ON DELETE CASCADE,
  remetente_id    UUID NOT NULL REFERENCES usuarios(id) ON DELETE SET NULL,
  conteudo        TEXT NOT NULL,
  enviado_em      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- Índices geoespaciais
CREATE INDEX idx_publicacoes_geo ON publicacoes USING GIST (
  ll_to_earth(latitude, longitude)
);

-- Índices para buscas comuns
CREATE INDEX idx_publicacoes_tipo_status ON publicacoes (tipo_id, status_id);
CREATE INDEX idx_matches_status ON matches (status_id);
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
  IF (TG_OP = 'INSERT' AND NEW.status_id = (SELECT id FROM status_match WHERE nome = 'CONFIRMADO')) OR
     (TG_OP = 'UPDATE' AND OLD.status_id != (SELECT id FROM status_match WHERE nome = 'CONFIRMADO') AND NEW.status_id = (SELECT id FROM status_match WHERE nome = 'CONFIRMADO')) THEN

    UPDATE publicacoes
    SET quantidade = quantidade - NEW.quantidade
    WHERE id = NEW.publicacao_id;
  END IF;

  -- Devolução de quantidade em cancelamentos ou recusas de matches previamente confirmados
  IF (TG_OP = 'UPDATE' AND OLD.status_id = (SELECT id FROM status_match WHERE nome = 'CONFIRMADO') AND (NEW.status_id = (SELECT id FROM status_match WHERE nome = 'CANCELADO') OR NEW.status_id = (SELECT id FROM status_match WHERE nome = 'RECUSADO'))) OR
     (TG_OP = 'DELETE' AND OLD.status_id = (SELECT id FROM status_match WHERE nome = 'CONFIRMADO')) THEN

    UPDATE publicacoes
    SET quantidade = quantidade + OLD.quantidade
    WHERE id = OLD.publicacao_id;
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
DECLARE
  tipo_nome VARCHAR(20);
BEGIN
  SELECT nome INTO tipo_nome FROM tipo_publicacao WHERE id = NEW.tipo_id;

  -- Para ofertas
  IF tipo_nome = 'OFERTA' THEN
    IF NEW.quantidade <= 0 THEN
      NEW.status_id := (SELECT id FROM status_publicacao WHERE nome = 'CONCLUIDA');
    ELSIF NEW.fim_coleta < NOW() AND NEW.status_id != (SELECT id FROM status_publicacao WHERE nome = 'CONCLUIDA') THEN
      NEW.status_id := (SELECT id FROM status_publicacao WHERE nome = 'CANCELADA');
    ELSIF NEW.status_id NOT IN ((SELECT id FROM status_publicacao WHERE nome = 'EM_NEGOCIACAO'), (SELECT id FROM status_publicacao WHERE nome = 'CONCLUIDA'), (SELECT id FROM status_publicacao WHERE nome = 'CANCELADA')) THEN
      NEW.status_id := (SELECT id FROM status_publicacao WHERE nome = 'ABERTA');
    END IF;

  -- Para pedidos
  ELSE -- tipo_nome = 'PEDIDO'
    IF NEW.quantidade <= 0 THEN
      NEW.status_id := (SELECT id FROM status_publicacao WHERE nome = 'CONCLUIDA');
    ELSIF NEW.status_id NOT IN ((SELECT id FROM status_publicacao WHERE nome = 'EM_NEGOCIACAO'), (SELECT id FROM status_publicacao WHERE nome = 'CONCLUIDA'), (SELECT id FROM status_publicacao WHERE nome = 'CANCELADA')) THEN
      NEW.status_id := (SELECT id FROM status_publicacao WHERE nome = 'ABERTA');
    END IF;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Função para Validação de quantidade em matches
CREATE OR REPLACE FUNCTION validar_quantidade_match()
RETURNS TRIGGER AS $$
DECLARE
  disponivel INTEGER;
  status_atual_id INTEGER;
BEGIN
  SELECT quantidade, status_id INTO disponivel, status_atual_id
  FROM publicacoes WHERE id = NEW.publicacao_id;

  IF status_atual_id NOT IN ((SELECT id FROM status_publicacao WHERE nome = 'ABERTA'), (SELECT id FROM status_publicacao WHERE nome = 'EM_NEGOCIACAO')) THEN
      RAISE EXCEPTION 'A publicação não está disponível para novos matches (status_id: %)', status_atual_id;
  END IF;

  IF NEW.quantidade > disponivel THEN
    RAISE EXCEPTION 'Quantidade solicitada (% unidades) excede o disponível na publicação (% unidades)', NEW.quantidade, disponivel;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Função para validar o TIPO da publicação em um match
CREATE OR REPLACE FUNCTION verificar_tipos_publicacao_match()
RETURNS TRIGGER AS $$
DECLARE
  tipo_publicacao_ref_id INTEGER;
  tipo_publicacao_nome VARCHAR(20);
BEGIN
  IF NEW.oferta_id IS NOT NULL THEN
    SELECT tipo_id INTO tipo_publicacao_ref_id
    FROM publicacoes
    WHERE id = NEW.oferta_id;

    IF NOT FOUND THEN
      RAISE EXCEPTION 'Publicação de oferta com ID % não encontrada.', NEW.oferta_id;
    END IF;

    SELECT nome INTO tipo_publicacao_nome FROM tipo_publicacao WHERE id = tipo_publicacao_ref_id;
    IF tipo_publicacao_nome != 'OFERTA' THEN
      RAISE EXCEPTION 'A publicação referenciada por oferta_id (%) deve ser do tipo ''OFERTA'', mas é do tipo ''%''.', NEW.oferta_id, tipo_publicacao_nome;
    END IF;
  END IF;

  IF NEW.pedido_id IS NOT NULL THEN
    SELECT tipo_id INTO tipo_publicacao_ref_id
    FROM publicacoes
    WHERE id = NEW.pedido_id;

    IF NOT FOUND THEN
      RAISE EXCEPTION 'Publicação de pedido com ID % não encontrada.', NEW.pedido_id;
    END IF;

    SELECT nome INTO tipo_publicacao_nome FROM tipo_publicacao WHERE id = tipo_publicacao_ref_id;
    IF tipo_publicacao_nome != 'PEDIDO' THEN
      RAISE EXCEPTION 'A publicação referenciada por pedido_id (%) deve ser do tipo ''PEDIDO'', mas é do tipo ''%''.', NEW.pedido_id, tipo_publicacao_nome;
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

-- Atualização de timestamps
CREATE TRIGGER trg_matches_atualizado
BEFORE UPDATE ON matches
FOR EACH ROW EXECUTE PROCEDURE atualizar_data();

-- Controle de quantidade
CREATE TRIGGER trg_matches_quantidade
AFTER INSERT OR UPDATE OF status_id, quantidade ON matches
FOR EACH ROW EXECUTE PROCEDURE atualizar_quantidades();

-- Validação de quantidade
CREATE TRIGGER trg_validar_match_quantidade
BEFORE INSERT OR UPDATE OF quantidade, publicacao_id ON matches
FOR EACH ROW EXECUTE PROCEDURE validar_quantidade_match();

-- Validação do tipo da publicação
CREATE TRIGGER trg_verificar_tipo_publicacao_no_match
BEFORE INSERT OR UPDATE OF publicacao_id ON matches
FOR EACH ROW EXECUTE FUNCTION verificar_tipo_publicacao_match();

CREATE OR REPLACE FUNCTION verificar_tipo_publicacao_match()
RETURNS TRIGGER AS $$
DECLARE
  tipo_publicacao_nome VARCHAR(20);
BEGIN
  SELECT tp.nome INTO tipo_publicacao_nome
  FROM publicacoes p
  JOIN tipo_publicacao tp ON tp.id = p.tipo_id
  WHERE p.id = NEW.publicacao_id;

  IF tipo_publicacao_nome NOT IN ('OFERTA', 'PEDIDO') THEN
    RAISE EXCEPTION 'A publicação referenciada por publicacao_id (%) deve ser do tipo OFERTA ou PEDIDO, mas é do tipo %.', NEW.publicacao_id, tipo_publicacao_nome;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;