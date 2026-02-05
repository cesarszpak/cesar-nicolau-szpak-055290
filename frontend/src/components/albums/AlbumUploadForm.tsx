// Importa o React
import React from 'react'

// Importa o serviço de capas e o DTO da capa do álbum
import { capaService, type CapaAlbumDTO } from '../../services/capa.service'

// Tamanho máximo permitido por arquivo (em MB)
const MAX_FILE_SIZE_MB = 5
// Tamanho máximo em bytes (5 MB)
const MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024

// Componente responsável pelo upload de imagens (capas) de um álbum
// Props:
// - albumId: identificador do álbum
// - onUploaded (opcional): callback executado após upload bem-sucedido
const AlbumUploadForm: React.FC<{
  albumId: number
  onUploaded?: (added: CapaAlbumDTO[]) => void
}> = ({ albumId, onUploaded }) => {

  // Estado para armazenar os arquivos selecionados
  const [files, setFiles] = React.useState<FileList | null>(null)

  // Estado para controle de carregamento
  const [loading, setLoading] = React.useState(false)

  // Estado para armazenar mensagens de erro
  const [error, setError] = React.useState<string | null>(null)

  // Estado para armazenar mensagem de sucesso
  const [success, setSuccess] = React.useState<string | null>(null)

  /**
   * Valida tamanho dos arquivos selecionados
   * @param fileList lista de arquivos do input
   * @returns mensagem de erro ou null se tudo está OK
   */
  function validateFilesSize(fileList: FileList): string | null {
    for (let i = 0; i < fileList.length; i++) {
      const file = fileList[i]
      if (file.size > MAX_FILE_SIZE_BYTES) {
        const sizeMB = (file.size / (1024 * 1024)).toFixed(2)
        return `Arquivo "${file.name}" excede o tamanho máximo permitido. Tamanho: ${sizeMB} MB, Máximo: ${MAX_FILE_SIZE_MB} MB`
      }
    }
    return null
  }

  // Função executada ao selecionar arquivo
  const onFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const fileList = e.target.files

    // Limpa erros anteriores
    setError(null)

    if (!fileList || fileList.length === 0) {
      setFiles(null)
      return
    }

    // Valida tamanho dos arquivos
    const sizeError = validateFilesSize(fileList)
    if (sizeError) {
      setError(sizeError)
      setFiles(null)
      // Limpa o input
      ;(
        document.getElementById(`input-files-${albumId}`) as HTMLInputElement | null
      )?.value && (
        (document.getElementById(`input-files-${albumId}`) as HTMLInputElement).value = ''
      )
      return
    }

    // Se passou na validação, armazena os arquivos
    setFiles(fileList)
  }

  // Função executada no envio do formulário
  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    // Verifica se o usuário está autenticado
    // (evita enviar arquivos sem token)
    const token = localStorage.getItem('token')
    if (!token) {
      return setError('Usuário não autenticado. Faça login para enviar imagens.')
    }

    // Valida se ao menos um arquivo foi selecionado
    if (!files || files.length === 0) {
      return setError('Selecione pelo menos um arquivo')
    }

    setLoading(true)
    setError(null)
    setSuccess(null)

    try {
      // Converte FileList em array
      const arr = Array.from(files)

      // Envia os arquivos para o backend
      const resp = await capaService.upload(albumId, arr)

      // Executa callback informando as capas criadas
      if (onUploaded) onUploaded(resp as any)

      // Limpa arquivos do estado
      setFiles(null)

      // Limpa o input file manualmente
      ;(
        document.getElementById(`input-files-${albumId}`) as HTMLInputElement | null
      )?.value && (
        (document.getElementById(`input-files-${albumId}`) as HTMLInputElement).value = ''
      )

      // Exibe mensagem de sucesso
      setSuccess(
        `Upload concluído: ${Array.isArray(resp) ? resp.length : 1} arquivo(s)`
      )

      // Remove a mensagem de sucesso após 4 segundos
      setTimeout(() => setSuccess(null), 4000)

    } catch (err: any) {
      // Captura e exibe erro
      setError(err.message || 'Falha no upload')
    } finally {
      // Finaliza o loading
      setLoading(false)
    }
  }

  return (
    // Formulário de upload de imagens
    <form onSubmit={onSubmit} className="flex flex-col gap-2">

      {/* Mensagem de erro */}
      {error && <div className="text-red-500">{error}</div>}

      {/* Mensagem de sucesso */}
      {success && <div className="text-green-600">{success}</div>}

      {/* Input para seleção de múltiplas imagens */}
      <input
        id={`input-files-${albumId}`}
        type="file"
        multiple
        accept="image/*"
        onChange={onFileChange}
      />

      {/* Botão de envio */}
      <div className="flex items-center gap-2">
        <button
          type="submit"
          className="btn-success-md"
          disabled={loading}
        >
          {loading ? 'Enviando...' : 'Cadastrar imagens'}
        </button>
      </div>
    </form>
  )
}

// Exporta o componente
export default AlbumUploadForm
