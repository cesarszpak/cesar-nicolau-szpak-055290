// Importa o React
import React from 'react'

// Componente Card reutilizável para exibir conteúdo em formato de cartão
// Recebe:
// - children: conteúdo que será renderizado dentro do card
// - className (opcional): classes CSS adicionais para customização
const Card: React.FC<{
  children: React.ReactNode
  className?: string
}> = ({ children, className = '' }) => {

  return (
    // Container do card com estilos padrão e classes adicionais, se informadas
    <div className={`bg-white rounded shadow p-4 ${className}`}>
      {children}
    </div>
  )
}

// Exporta o componente Card
export default Card
