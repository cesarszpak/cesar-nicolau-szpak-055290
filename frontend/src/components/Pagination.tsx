import React from 'react' 

// Interface que define as propriedades esperadas pelo componente de paginação
interface Props {
  // Página atual (base 0)
  page: number

  // Total de páginas disponíveis
  totalPages: number

  // Função chamada ao mudar de página
  onChange: (page: number) => void
}

// Componente de Paginação
const Pagination: React.FC<Props> = ({ page, totalPages, onChange }) => {
  // Se existir apenas uma página (ou nenhuma), não renderiza o componente
  if (totalPages <= 1) return null

  // Cria um array com o índice de todas as páginas (base 0)
  const pages = Array.from({ length: totalPages }, (_, i) => i)

  return (
    // Container da paginação
    <nav className="mt-4 flex items-center justify-center space-x-2">

      {/* Botão para ir para a página anterior */}
      <button
        className="px-3 py-1 rounded bg-white border shadow-sm disabled:opacity-50"
        onClick={() => onChange(Math.max(0, page - 1))}
        disabled={page === 0} // Desabilita se estiver na primeira página
      >
        Anterior
      </button>

      {/* Renderiza os botões numéricos das páginas */}
      {pages.map(p => (
        <button
          key={p}
          onClick={() => onChange(p)}
          className={`px-3 py-1 rounded border ${
            // Aplica estilo diferente para a página ativa
            p === page ? 'bg-[#183181] text-white' : 'bg-white'
          }`}
        >
          {/* Exibe o número da página iniciando em 1 */}
          {p + 1}
        </button>
      ))}

      {/* Botão para ir para a próxima página */}
      <button
        className="px-3 py-1 rounded bg-white border shadow-sm disabled:opacity-50"
        onClick={() => onChange(Math.min(totalPages - 1, page + 1))}
        disabled={page === totalPages - 1} // Desabilita se estiver na última página
      >
        Próximo
      </button>
    </nav>
  )
}

export default Pagination
