import { pieceMap } from '@/features/games/lib/pieceMap'

type Props = {
  capturedPieces: {
    sente: string[]
    gote: string[]
  }
  currentTurn: 'b' | 'w'
  selectedCapturedPiece?: string | null
  onCapturedPieceClick?: (piece: string) => void
  playerColor?: 'b' | 'w'  // 'b' = 先手（自分）, 'w' = 後手（相手）
}

export default function GameSidebar({
  capturedPieces,
  currentTurn,
  selectedCapturedPiece,
  onCapturedPieceClick,
  playerColor = 'b',  // デフォルトは先手（自分）
}: Props) {
  // playerColor = 'b' の場合、自分が先手（下側）、相手が後手（上側）
  const isPlayerSente = playerColor === 'b'
  // sente の駒は大文字、gote の駒は小文字で保存
  const playerCaptured = isPlayerSente ? capturedPieces.sente : capturedPieces.gote
  const opponentCaptured = isPlayerSente ? capturedPieces.gote : capturedPieces.sente
  const playerTurnSymbol = isPlayerSente ? 'b' : 'w'
  const opponentTurnSymbol = isPlayerSente ? 'w' : 'b'

  return (
    <div className="w-full lg:w-72 border-l border-zinc-800 bg-zinc-950 p-6 flex flex-col justify-between">

      {/* 上側：相手 */}
      <div>
        <p className="text-amber-400 tracking-[0.3em] text-xs mb-2">
          ONLINE MATCH
        </p>

        <h1 className="text-3xl font-black mb-8">
          対局中
        </h1>

        {/* 相手持ち駒 */}
        <div className="bg-zinc-900 rounded-2xl p-5 border border-zinc-800 mb-4">
          <p className="text-zinc-400 mb-3">
            相手持ち駒
          </p>

          <div className="flex flex-wrap gap-2 text-2xl">
            {opponentCaptured.map((piece, index) => (
              <button
                key={index}
                onClick={() => onCapturedPieceClick?.(piece)}
                className={`
                  w-12 h-12 rounded-xl text-black flex items-center justify-center font-bold
                  transition cursor-pointer
                  ${selectedCapturedPiece === piece
                    ? 'bg-blue-400 ring-2 ring-blue-600'
                    : 'bg-amber-100 hover:bg-amber-200'
                  }
                `}
              >
                {pieceMap[piece]}
              </button>
            ))}
          </div>
        </div>

        {/* 相手時間 */}
        <div
          className={`
            bg-zinc-900 rounded-2xl p-5 border
            ${
              currentTurn === opponentTurnSymbol
                ? 'border-yellow-400 shadow-[0_0_20px_rgba(250,204,21,0.7)] animate-pulse'
                : 'border-zinc-800'
            }
          `}
        >
          <p className="text-zinc-400 mb-2">相手</p>

          <div
            className={`text-5xl font-black ${
              currentTurn === opponentTurnSymbol
                ? 'text-yellow-300'
                : 'text-white'
            }`}
          >
            08:41
          </div>
        </div>
      </div>

      {/* 下側：自分 */}
      <div>
        {/* 自分時間 */}
        <div
          className={`
            bg-zinc-900 rounded-2xl p-5 border mb-4
            ${
              currentTurn === playerTurnSymbol
                ? 'border-yellow-400 shadow-[0_0_20px_rgba(250,204,21,0.7)] animate-pulse'
                : 'border-zinc-800'
            }
          `}
        >
          <p className="text-zinc-400 mb-2">自分</p>

          <div
            className={`text-5xl font-black ${
              currentTurn === playerTurnSymbol
                ? 'text-yellow-300'
                : 'text-white'
            }`}
          >
            09:52
          </div>
        </div>

        {/* 自分持ち駒 */}
        <div className="bg-zinc-900 rounded-2xl p-5 border border-zinc-800 mb-6">
          <p className="text-zinc-400 mb-3">
            自分持ち駒
          </p>

          <div className="flex flex-wrap gap-2 text-2xl">
            {playerCaptured.map((piece, index) => {
              const normalizedPiece = piece.toUpperCase()
              return (
                <button
                  key={index}
                  onClick={() => onCapturedPieceClick?.(piece)}
                  className={`
                    w-12 h-12 rounded-xl text-black flex items-center justify-center font-bold
                    transition cursor-pointer
                    ${selectedCapturedPiece === piece
                      ? 'bg-blue-400 ring-2 ring-blue-600'
                      : 'bg-amber-100 hover:bg-amber-200'
                    }
                  `}
                >
                  {pieceMap[normalizedPiece]}
                </button>
              )
            })}
          </div>
        </div>

        <button className="w-full bg-red-500 hover:bg-red-400 transition rounded-2xl py-4 font-bold text-lg">
          投了する
        </button>
      </div>
    </div>
  )
}