"use client";
import { useEffect, useState } from "react";

export default function Feed() {
  const [usuario, setUsuario] = useState(null);

  useEffect(() => {
    const userStr = localStorage.getItem("usuario");
    if (userStr) {
      setUsuario(JSON.parse(userStr));
    }
  }, []);}

export default function Feed() {
    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-100">
        <div className="w-full max-w-md p-8 bg-white rounded-lg shadow-md">
            <h2 className="text-2xl font-bold text-center mb-6">Feed</h2>
            <p className="text-gray-600 text-center">
            Bem-vindo ao seu feed! Aqui você verá as atualizações mais recentes.
            </p>
        </div>
        </div>
    );
} 