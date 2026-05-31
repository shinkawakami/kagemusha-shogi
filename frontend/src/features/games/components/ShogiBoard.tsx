import { pieceMap } from '@/features/games/lib/pieceMap'

type Props = {
  board: (string | null)[][]
  onCellClick?: (row: number, col: number) => void
}

export default function ShogiBoard({
  board,
  onCellClick,
}: Props) {
  return (
    <div className="flex-1 flex items-center justify-center p-6 overflow-auto">
      <div className="bg-amber-100 p-4 rounded-[2rem] shadow-2xl border-4 border-amber-300">
        <div className="grid grid-cols-9 gap-[2px] bg-amber-900">
          {board.map((row, rowIndex) =>
            row.map((piece, colIndex) => {
              const basePiece =
                piece?.startsWith('+')
                  ? piece.slice(1)
                  : piece

              const isGote =
                !!basePiece &&
                basePiece === basePiece.toLowerCase()

              return (
                <button
                  key={`${rowIndex}-${colIndex}`}
                  onClick={() => onCellClick?.(rowIndex, colIndex)}
                  className="
                    w-14 h-14
                    md:w-16 md:h-16
                    bg-amber-50
                    hover:bg-amber-200
                    transition
                    flex items-center justify-center
                    text-2xl md:text-3xl
                    font-bold text-black
                  "
                >
                  <span
                    className={`inline-block ${
                      isGote ? 'rotate-180' : ''
                    }`}
                  >
                    {piece ? pieceMap[piece] : ''}
                  </span>
                </button>
              )
            })
          )}
        </div>
      </div>
    </div>
  )
}