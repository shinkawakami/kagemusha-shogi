import { type Square } from '@/lib/shogiCoordinate'

type Props = {
  isOpen: boolean
  from: Square | null
  to: Square | null
  gameId: string
  onPromote: (gameId: string, from: Square, to: Square, promote: boolean) => void
  onCancel: () => void
}

export default function PromoteDialog({
  isOpen,
  from,
  to,
  gameId,
  onPromote,
  onCancel,
}: Props) {
  if (!isOpen || !from || !to) {
    return null
  }

  const handleBackgroundClick = (e: React.MouseEvent<HTMLDivElement>) => {
    // 背景をクリックしたときのみ（ダイアログ自体をクリックしたときは実行しない）
    if (e.target === e.currentTarget) {
      onCancel()
    }
  }

  return (
    <div 
      className="fixed inset-0 bg-black/50 flex items-center justify-center z-[9999]"
      onClick={handleBackgroundClick}
    >
      <div className="bg-zinc-900 rounded-lg p-8 border-2 border-amber-400 shadow-2xl">
        <p className="text-white mb-8 text-center text-xl font-semibold">
          駒を成りますか？
        </p>
        <div className="flex gap-4 justify-center">
          <button
            onClick={() => onPromote(gameId, from, to, true)}
            className="px-8 py-3 bg-amber-500 hover:bg-amber-600 text-white font-bold rounded-lg transition-colors"
          >
            成る
          </button>
          <button
            onClick={() => onPromote(gameId, from, to, false)}
            className="px-8 py-3 bg-gray-600 hover:bg-gray-700 text-white font-bold rounded-lg transition-colors"
          >
            成らない
          </button>
        </div>
      </div>
    </div>
  )
}
