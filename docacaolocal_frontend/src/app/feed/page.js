"use client";
import { useEffect, useState } from "react";

export default function Feed() {
  const [usuario, setUsuario] = useState(null);

  useEffect(() => {
    const userStr = localStorage.getItem("usuario");
    if (userStr) {
      setUsuario(JSON.parse(userStr));
    }
  }, []);

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-100">
      <div className="w-full max-w-md p-8 bg-white rounded-lg shadow-md">
        <h2 className="text-2xl font-bold text-center mb-6">Feed</h2>
        {usuario ? (
          <div className="mb-4 text-center">
            <p className="text-lg font-semibold">Bem-vindo, {usuario.nome}!</p>
            <p className="text-gray-600">Email: {usuario.email}</p>
            {/* Adicione outros campos conforme necessário */}
          </div>
        ) : (
          <p className="text-gray-600 text-center">
            Carregando informações do usuário...
          </p>
        )}
        <p className="text-gray-600 text-center">
          Aqui você verá as atualizações mais recentes.
        </p>
      </div>
    </div>
  );
}