export default function ItemConexao({ match, usuario, onClick }) {

  const outroUsuario =
    match.usuario.id === usuario.id
      ? match.publicacao.usuario
      : match.usuario;

  return (
    <div
      className="flex items-center gap-4 p-3 border rounded hover:bg-gray-50 cursor-pointer"
      onClick={onClick}
    >
      <img
        src={outroUsuario.fotoPerfilUrl}
        alt="Foto perfil"
        className="w-10 h-10 rounded-full"
      />
      <div>
        <div className="font-semibold">{outroUsuario.nome}</div>
        <div className="text-sm text-gray-600">
          sobre <span className="font-medium">{match.publicacao.titulo}</span>
        </div>
      </div>
      <div className="ml-auto text-xs text-gray-500">
        {match.status.nome}
      </div>
    </div>
  );
}