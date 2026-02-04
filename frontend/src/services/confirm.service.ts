import Swal from 'sweetalert2'
import 'sweetalert2/dist/sweetalert2.min.css'

// Serviço reutilizável para confirmações (uso em várias partes do sistema)
// Sempre exibe mensagens
export async function confirm(options: {
  title?: string
  text?: string
  confirmButtonText?: string
  cancelButtonText?: string
}) {
  const result = await Swal.fire({
    title: options.title ?? 'Confirmação',
    text: options.text ?? 'Deseja continuar?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: options.confirmButtonText ?? 'Sim',
    cancelButtonText: options.cancelButtonText ?? 'Cancelar',
    customClass: {
      confirmButton: 'swal2-confirm',
      cancelButton: 'swal2-cancel'
    }
  })

  return result.isConfirmed
}

// Exporta também atalho específico para exclusão
export const confirmDelete = (name = 'o registro') =>
  confirm({ title: 'Confirma exclusão', text: `Tem certeza que deseja excluir ${name}?`, confirmButtonText: 'Excluir', cancelButtonText: 'Cancelar' })
