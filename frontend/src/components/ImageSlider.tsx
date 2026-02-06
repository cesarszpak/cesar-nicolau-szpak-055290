import React from 'react'
import { ChevronLeftIcon, ChevronRightIcon } from '@heroicons/react/24/solid'

/**
 * Interface que define as propriedades de uma imagem no slider
 */
export interface SliderImage {
  // Identificador único da imagem
  id: number | string
  // URL da imagem
  url: string
  // Título/nome da imagem
  title?: string
}

/**
 * Interface que define as propriedades do componente ImageSlider
 */
interface ImageSliderProps {
  // Lista de imagens a exibir
  images: SliderImage[]
  
  // Tempo de espera (em milissegundos) antes de passar automaticamente para a próxima imagem
  // Padrão: 5000ms (5 segundos)
  autoplayInterval?: number
  
  // Se true, o slider avança automaticamente
  // Padrão: true
  autoplay?: boolean
  
  // Classes CSS customizadas para o container externo
  containerClassName?: string
  
  // Classes CSS customizadas para a imagem
  imageClassName?: string
  
  // Callback disparado ao mudar de imagem
  onImageChange?: (index: number) => void
  
  // Callback disparado ao clicar em uma imagem
  onImageClick?: (image: SliderImage, index: number) => void
}

/**
 * Componente de Slider de Imagens com navegação automática e manual.
 * 
 * Características:
 * - Exibe imagens em um bloco único
 * - Navegação automática a cada N segundos
 * - Ícones de navegação anterior/próxima sobre as imagens
 * - Indicadores de página (dots) abaixo do slider
 * - Reutilizável em múltiplos locais da aplicação
 */
const ImageSlider: React.FC<ImageSliderProps> = ({
  images,
  autoplayInterval = 5000,
  autoplay = true,
  containerClassName = '',
  imageClassName = 'w-full h-auto rounded shadow-lg',
  onImageChange,
  onImageClick
}) => {
  // Estado com o índice da imagem atual
  const [currentIndex, setCurrentIndex] = React.useState(0)
  
  // Timer para controlar o autoplay
  const autoplayTimerId = React.useRef<ReturnType<typeof setTimeout> | null>(null)

  // Se não há imagens, não renderiza nada
  if (!images || images.length === 0) {
    return (
      <div className={`flex items-center justify-center bg-gray-100 rounded ${containerClassName}`}>
        <p className="text-gray-500">Nenhuma imagem disponível</p>
      </div>
    )
  }

  /**
   * Função para ir para a próxima imagem
   */
  const goToNext = React.useCallback(() => {
    setCurrentIndex(prev => (prev + 1) % images.length)
  }, [images.length])

  /**
   * Função para ir para a imagem anterior
   */
  const goToPrevious = React.useCallback(() => {
    setCurrentIndex(prev => (prev - 1 + images.length) % images.length)
  }, [images.length])

  /**
   * Função para ir direto para um índice específico
   */
  const goToIndex = React.useCallback((index: number) => {
    if (index >= 0 && index < images.length) {
      setCurrentIndex(index)
    }
  }, [images.length])

  /**
   * Efeito para disparar o callback ao mudar de imagem
   */
  React.useEffect(() => {
    if (onImageChange) {
      onImageChange(currentIndex)
    }
  }, [currentIndex, onImageChange])

  /**
   * Efeito para configurar o autoplay
   */
  React.useEffect(() => {
    // Limpa o timer anterior, se existir
    if (autoplayTimerId.current) {
      clearInterval(autoplayTimerId.current)
    }

    // Se autoplay está ativo, inicia o timer
    if (autoplay && images.length > 1) {
      autoplayTimerId.current = setInterval(() => {
        goToNext()
      }, autoplayInterval)
    }

    // Cleanup ao desmontar ou quando as dependências mudarem
    return () => {
      if (autoplayTimerId.current) {
        clearInterval(autoplayTimerId.current)
      }
    }
  }, [autoplay, autoplayInterval, goToNext, images.length])

  /**
   * Pausa o autoplay ao fazer hover
   */
  const handleMouseEnter = () => {
    if (autoplayTimerId.current) {
      clearInterval(autoplayTimerId.current)
      autoplayTimerId.current = null
    }
  }

  /**
   * Retoma o autoplay ao sair do hover
   */
  const handleMouseLeave = () => {
    if (autoplay && images.length > 1) {
      autoplayTimerId.current = setInterval(() => {
        goToNext()
      }, autoplayInterval)
    }
  }

  const currentImage = images[currentIndex]

  return (
    <div 
      className={`relative w-full ${containerClassName}`}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
    >
      {/* Container da imagem */}
      <div className="relative w-full overflow-hidden rounded-lg bg-gray-100">
        {/* Imagem atual */}
        <img
          src={currentImage.url}
          alt={currentImage.title || `Imagem ${currentIndex + 1}`}
          className={imageClassName}
          onClick={() => onImageClick?.(currentImage, currentIndex)}
          onError={(e) => {
            const img = e.currentTarget as HTMLImageElement
            // Fallback para placeholder em caso de erro
            img.src = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400"><rect width="100%" height="100%" fill="%23eee"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23999" font-family="Arial" font-size="14">Imagem indisponível</text></svg>'
          }}
        />

        {/* Botão para imagem anterior */}
        {images.length > 1 && (
          <button
            onClick={goToPrevious}
            className="absolute left-4 top-1/2 transform -translate-y-1/2 bg-white/70 hover:bg-white text-gray-800 rounded-full p-2 shadow-lg transition-all duration-200 z-10"
            title="Imagem anterior"
            aria-label="Imagem anterior"
          >
            <ChevronLeftIcon className="h-6 w-6" />
          </button>
        )}

        {/* Botão para próxima imagem */}
        {images.length > 1 && (
          <button
            onClick={goToNext}
            className="absolute right-4 top-1/2 transform -translate-y-1/2 bg-white/70 hover:bg-white text-gray-800 rounded-full p-2 shadow-lg transition-all duration-200 z-10"
            title="Próxima imagem"
            aria-label="Próxima imagem"
          >
            <ChevronRightIcon className="h-6 w-6" />
          </button>
        )}
      </div>

      {/* Indicadores (dots) - mostrar se há mais de uma imagem */}
      {images.length > 1 && (
        <div className="flex items-center justify-center gap-2 mt-4">
          {images.map((_, index) => (
            <button
              key={index}
              onClick={() => goToIndex(index)}
              className={`h-2 rounded-full transition-all duration-300 ${
                index === currentIndex
                  ? 'w-8 bg-indigo-600'
                  : 'w-2 bg-gray-300 hover:bg-gray-400'
              }`}
              title={`Ir para imagem ${index + 1}`}
              aria-label={`Imagem ${index + 1}`}
              aria-current={index === currentIndex}
            />
          ))}
        </div>
      )}

      {/* Contador de imagens - mostrar informação útil */}
      {images.length > 1 && (
        <div className="flex items-center justify-center mt-2 text-sm text-gray-600">
          Imagem {currentIndex + 1} de {images.length}
        </div>
      )}
    </div>
  )
}

export default ImageSlider
