import { pieceMap } from '@/features/games/lib/pieceMap'
import { type Square } from '@/lib/shogiCoordinate'
import { toShogiCoordinate } from '@/lib/shogiCoordinate'
import { getValidMoves, getPieceAt, getValidDropMoves } from '@/lib/moveValidator'
import styles from '@/features/games/styles/ShogiBoard.module.css'
import { useState } from 'react'

type Props = {
  board: (string | null)[][]
  onCellClick?: (row: number, col: number) => void
  selectedPos?: Square | null
  selectedCapturedPiece?: string | null
}

export default function ShogiBoard({
  board,
  onCellClick,
  selectedPos,
  selectedCapturedPiece,
}: Props) {
  const [validMoves, setValidMoves] = useState<Square[]>([])

  const handleCellClickWithValidation = (rowIndex: number, colIndex: number) => {
    const clickedPos = toShogiCoordinate(rowIndex, colIndex)
    
    // 駒打ちモード：持ち駒を選択している場合
    if (selectedCapturedPiece) {
      // 駒打ちモード中は合法手表示をしない（useGameMove で検証）
      setValidMoves([])
      onCellClick?.(rowIndex, colIndex)
      return
    }
    
    // 通常の移動モード
    // 移動元が未選択の場合
    if (!selectedPos) {
      const piece = getPieceAt(board, clickedPos)
      if (piece) {
        const moves = getValidMoves(board, clickedPos, piece)
        setValidMoves(moves)
      } else {
        setValidMoves([])
      }
      onCellClick?.(rowIndex, colIndex)
      return
    }

    // 移動元が既に選択されている場合
    // 合法手表示をクリアしてから次の処理へ
    setValidMoves([])
    onCellClick?.(rowIndex, colIndex)
  }

  // 駒打ち時の合法手を計算
  const dropValidMoves = selectedCapturedPiece ? getValidDropMoves(board, selectedCapturedPiece) : []

  return (
    <div className="flex-1 flex items-center justify-center p-6 overflow-auto">
      <div className="bg-amber-100 p-4 rounded-[2rem] shadow-2xl border-4 border-amber-300">
        <div className="grid grid-cols-9 gap-[2px] bg-amber-900">
          {board.map((row, rowIndex) =>
            row.map((piece, colIndex) => {
              const currentPos = toShogiCoordinate(rowIndex, colIndex)
              const isSelected = selectedPos && 
                selectedPos.row === currentPos.row && 
                selectedPos.col === currentPos.col
              
              const isValidMove = validMoves.some(m => 
                m.row === currentPos.row && m.col === currentPos.col
              )

              const isValidDropMove = dropValidMoves.some(m =>
                m.row === currentPos.row && m.col === currentPos.col
              )
              
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
                  onClick={() => handleCellClickWithValidation(rowIndex, colIndex)}
                  className={`
                    w-14 h-14
                    md:w-16 md:h-16
                    transition
                    flex items-center justify-center
                    text-2xl md:text-3xl
                    font-bold text-black
                    ${isSelected 
                      ? styles.blinking
                      : isValidMove || isValidDropMove
                      ? 'bg-green-300 hover:bg-green-400'
                      : 'bg-amber-50 hover:bg-amber-200'
                    }
                  `}
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