export default function UsuarioPublicacao({ usuario }) {
  return (
    <div className="flex gap-4">
      <img src={usuario.fotoPerfilUrl} alt="Foto perfil" className="w-24 h-24 rounded-lg" />
      <div>
        <h2>{usuario.nome}</h2>
        <p>{usuario.descricao}</p>
      </div>
    </div>
  );
}