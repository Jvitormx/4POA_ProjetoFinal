"use client";
import { useState } from "react";

export default function Login() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");

  async function Login(e) {
    e.preventDefault();
    setErro("");
    try {
      const res = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, senha }),
      });
      if (!res.ok) {
        throw new Error("Usuário ou senha inválidos");
      }
      const usuario = await res.json();
      // Salve o usuário no localStorage ou contexto, se desejar
      localStorage.setItem("usuario", JSON.stringify(usuario));
      // Redirecione para o feed
      window.location.href = "/feed";
    } catch (err) {
      setErro(err.message);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-100">
      <div className="w-full max-w-md p-8 bg-white rounded-lg shadow-md">
        <h2 className="text-2xl font-bold text-center mb-6">Login</h2>
        <form onSubmit={Login}>
          <div className="mb-4">
            <label
              className="block text-sm font-medium text-gray-700 mb-2"
              htmlFor="email"
            >
              Email
            </label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:ring-teal-500"
              required
            />
          </div>
          <div className="mb-6">
            <label
              className="block text-sm font-medium text-gray-700 mb-2"
              htmlFor="password"
            >
              Senha
            </label>
            <input
              type="password"
              id="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:ring-teal-500"
              required
            />
          </div>
          {erro && <div className="mb-4 text-red-600 text-sm">{erro}</div>}
          <button
            type="submit"
            className="w-full bg-teal-600 text-white px-4 py-2 rounded-md hover:bg-teal-700 transition"
          >
            Login
          </button>
        </form>
      </div>
    </div>
  );
}
