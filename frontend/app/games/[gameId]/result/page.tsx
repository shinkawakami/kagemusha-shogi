"use client";

import { useParams, useRouter, useSearchParams } from 'next/navigation'
import { useState } from 'react'

export default function ResultScreen() {
  const params = useParams()
  const router = useRouter()
  const searchParams = useSearchParams()
  const winner = searchParams.get('winner') as 'SENTE' | 'GOTE' | null
  
  // 開発段階での名前設定
  const opponentName = '相手'
  const playerName = '自分'
  
  // winner に基づいて勝敗を判定
  const getResult = (winner: string | null, playerColor: 'b' | 'w') => {
    if (!winner) return '不明'
    // GOTE（後手/w）が勝ち = 相手が勝ち
    // SENTE（先手/b）が勝ち = 自分が勝ち
    if (playerColor === 'b' && winner === 'SENTE') return '勝利'
    if (playerColor === 'w' && winner === 'GOTE') return '勝利'
    return '敗北'
  }

  const playerColor: 'b' | 'w' = 'b' // 開発段階では自分が先手に固定
  const opponentColor: 'b' | 'w' = 'w' // 相手は後手

  const playerResult = getResult(winner, playerColor)
  const opponentResult = getResult(winner, opponentColor)

  // 結果画面の背景色を決定（勝利か敗北か）
  const isPlayerWon = playerResult === '勝利'

  const handleReturnHome = () => {
    router.push('/')
  }

  const handlePlayAgain = () => {
    router.push('/match')
  }

  return (
    <div className="min-h-screen bg-black text-white flex flex-col items-center justify-center px-4">
      {/* 結果表示コンテナ */}
      <div className="w-full max-w-2xl">
        {/* タイトル */}
        <h1 className="text-5xl font-black text-center mb-16">
          {isPlayerWon ? '対局終了' : '対局終了'}
        </h1>

        {/* 対局結果の表示: 相手（左側）と自分（右側） */}
        <div className="flex gap-8 mb-16 justify-center">
          {/* 相手側（左） */}
          <div className="flex flex-col items-center">
            <div
              className={`
                w-32 h-40 rounded-3xl flex flex-col items-center justify-center border-4 transition
                ${opponentResult === '勝利'
                  ? 'bg-gradient-to-b from-yellow-400/20 to-yellow-600/20 border-yellow-500 shadow-[0_0_30px_rgba(234,179,8,0.5)]'
                  : 'bg-zinc-900 border-zinc-700'
                }
              `}
            >
              <p className="text-zinc-400 text-sm mb-2">相手</p>
              <p className="text-2xl font-black text-white mb-4">{opponentName}</p>
              <p
                className={`
                  text-2xl font-black
                  ${opponentResult === '勝利' ? 'text-yellow-400' : 'text-zinc-400'}
                `}
              >
                {opponentResult}
              </p>
            </div>
          </div>

          {/* vs テキスト */}
          <div className="flex items-center">
            <div className="text-4xl font-black text-zinc-600">VS</div>
          </div>

          {/* 自分側（右） */}
          <div className="flex flex-col items-center">
            <div
              className={`
                w-32 h-40 rounded-3xl flex flex-col items-center justify-center border-4 transition
                ${isPlayerWon
                  ? 'bg-gradient-to-b from-yellow-400/20 to-yellow-600/20 border-yellow-500 shadow-[0_0_30px_rgba(234,179,8,0.5)]'
                  : 'bg-zinc-900 border-zinc-700'
                }
              `}
            >
              <p className="text-zinc-400 text-sm mb-2">自分</p>
              <p className="text-2xl font-black text-white mb-4">{playerName}</p>
              <p
                className={`
                  text-2xl font-black
                  ${isPlayerWon ? 'text-yellow-400' : 'text-zinc-400'}
                `}
              >
                {playerResult}
              </p>
            </div>
          </div>
        </div>

        {/* 詳細情報 */}
        <div className="bg-zinc-900 rounded-2xl p-8 border border-zinc-800 mb-8">
          <div className="space-y-4">
            <div className="flex justify-between">
              <span className="text-zinc-400">対局ID</span>
              <span className="font-mono text-amber-400">{params.gameId}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-zinc-400">勝者</span>
              <span className="font-black text-yellow-400">
                {winner === 'SENTE' ? 'SENTE (先手) - 自分' : winner === 'GOTE' ? 'GOTE (後手) - 相手' : '不明'}
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-zinc-400">終了理由</span>
              <span className="text-white">投了</span>
            </div>
          </div>
        </div>

        {/* アクションボタン */}
        <div className="flex flex-col sm:flex-row gap-4">
          <button
            onClick={handleReturnHome}
            className="flex-1 bg-zinc-700 hover:bg-zinc-600 transition rounded-2xl py-4 font-bold text-lg"
          >
            ホームに戻る
          </button>
          <button
            onClick={handlePlayAgain}
            className="flex-1 bg-amber-500 hover:bg-amber-400 transition rounded-2xl py-4 font-bold text-lg text-black"
          >
            もう一局
          </button>
        </div>

        {/* 開発用情報 */}
        <div className="mt-12 p-4 bg-zinc-950 rounded-lg border border-zinc-800">
          <p className="text-xs text-zinc-500 text-center">
            <span className="block mb-2">開発用情報</span>
            <span className="block">SENTE (先手 / b) = 自分が勝ち</span>
            <span className="block">GOTE (後手 / w) = 相手が勝ち</span>
          </p>
        </div>
      </div>
    </div>
  )
}
