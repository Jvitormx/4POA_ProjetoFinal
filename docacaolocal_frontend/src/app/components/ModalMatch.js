import { useState } from "react";

export default function ModalMatch({ aberto, onClose, onConfirm, maxQuantidade }) {
  const [quantidade, setQuantidade] = useState(1);
  const [mensagem, setMensagem] = useState("");
  const [erro, setErro] = useState("");

  const handleConfirm = () => {
    if (quantidade < 1 || quantidade > maxQuantidade) {
      setErro(`Quantidade deve ser entre 1 e ${maxQuantidade}`);
      return;
    }
    setErro("");
    onConfirm({ quantidade, mensagem });
  };

  if (!aberto) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50 modal-overlay">
      <div className="bg-white rounded-lg p-6 w-full max-w-md shadow-lg relative z-50">
        <h2 className="text-xl font-bold mb-4">Solicitar Match</h2>
        <div className="mb-4">
          <label className="block mb-1 font-medium">Quantidade</label>
          <input
            type="number"
            min={1}
            max={maxQuantidade}
            value={quantidade}
            onChange={e => setQuantidade(Number(e.target.value))}
            className="w-full border rounded p-2"
          />
        </div>
        <div className="mb-4">
          <label className="block mb-1 font-medium">Mensagem (opcional)</label>
          <textarea
            value={mensagem}
            onChange={e => setMensagem(e.target.value)}
            className="w-full border rounded p-2"
            rows={3}
          />
        </div>
        {erro && <div className="text-red-600 mb-2">{erro}</div>}
        <div className="flex justify-end gap-2">
          <button onClick={onClose} className="px-4 py-2 rounded bg-gray-200">Cancelar</button>
          <button onClick={handleConfirm} className="px-4 py-2 rounded bg-purple-600 text-white">Confirmar</button>
        </div>
      </div>
    </div>
  );
}