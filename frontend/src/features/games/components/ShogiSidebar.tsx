import { pieceMap } from '@/features/games/lib/pieceMap'

type Props = {
  capturedPieces: {
    sente: string[]
    gote: string[]
  }
  currentTurn: 'b' | 'w'
}

export default function GameSidebar({
  capturedPieces,
  currentTurn,
}: Props) {
  return (
    <div className="w-full lg:w-72 border-l border-zinc-800 bg-zinc-950 p-6 flex flex-col justify-between">

      {/* 上側 */}
      <div>
        <p className="text-amber-400 tracking-[0.3em] text-xs mb-2">
          ONLINE MATCH
        </p>

        <h1 className="text-3xl font-black mb-8">
          対局中
        </h1>

        {/* 後手持ち駒 */}
        <div className="bg-zinc-900 rounded-2xl p-5 border border-zinc-800 mb-4">
          <p className="text-zinc-400 mb-3">
            後手持ち駒
          </p>

          <div className="flex flex-wrap gap-2 text-2xl">
            {capturedPieces.gote.map((piece, index) => (
              <div
                key={index}
                className="w-12 h-12 rounded-xl bg-amber-100 text-black flex items-center justify-center font-bold"
              >
                {pieceMap[piece]}
              </div>
            ))}
          </div>
        </div>

        {/* 後手時間 */}
        <div
          className={`
            bg-zinc-900 rounded-2xl p-5 border
            ${
              currentTurn === 'w'
                ? 'border-yellow-400 shadow-[0_0_20px_rgba(250,204,21,0.7)] animate-pulse'
                : 'border-zinc-800'
            }
          `}
        >
          <p className="text-zinc-400 mb-2">後手</p>

          <div
            className={`text-5xl font-black ${
              currentTurn === 'w'
                ? 'text-yellow-300'
                : 'text-white'
            }`}
          >
            08:41
          </div>
        </div>
      </div>

      {/* 下側 */}
      <div>
        {/* 先手時間 */}
        <div
          className={`
            bg-zinc-900 rounded-2xl p-5 border mb-4
            ${
              currentTurn === 'b'
                ? 'border-yellow-400 shadow-[0_0_20px_rgba(250,204,21,0.7)] animate-pulse'
                : 'border-zinc-800'
            }
          `}
        >
          <p className="text-zinc-400 mb-2">先手</p>

          <div
            className={`text-5xl font-black ${
              currentTurn === 'b'
                ? 'text-yellow-300'
                : 'text-white'
            }`}
          >
            09:52
          </div>
        </div>

        {/* 先手持ち駒 */}
        <div className="bg-zinc-900 rounded-2xl p-5 border border-zinc-800 mb-6">
          <p className="text-zinc-400 mb-3">
            先手持ち駒
          </p>

          <div className="flex flex-wrap gap-2 text-2xl">
            {capturedPieces.sente.map((piece, index) => (
              <div
                key={index}
                className="w-12 h-12 rounded-xl bg-amber-100 text-black flex items-center justify-center font-bold"
              >
                {pieceMap[piece]}
              </div>
            ))}
          </div>
        </div>

        <button className="w-full bg-red-500 hover:bg-red-400 transition rounded-2xl py-4 font-bold text-lg">
          投了する
        </button>
      </div>
    </div>
  )
}