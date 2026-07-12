'use client'

import { useState } from 'react'
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
  gameId?: string
  onResign?: (playerType: 'b' | 'w') => Promise<void>
}

export default function GameSidebar({
  capturedPieces,
  currentTurn,
  selectedCapturedPiece,
  onCapturedPieceClick,
  playerColor = 'b',  // デフォルトは先手（自分）
  gameId,
  onResign,
}: Props) {
  const [showResignDialog, setShowResignDialog] = useState(false)
  const [isResigning, setIsResigning] = useState(false)

  // playerColor = 'b' の場合、自分が先手（下側）、相手が後手（上側）
  const isPlayerSente = playerColor === 'b'
  // sente の駒は大文字、gote の駒は小文字で保存
  const playerCaptured = isPlayerSente ? capturedPieces.sente : capturedPieces.gote
  const opponentCaptured = isPlayerSente ? capturedPieces.gote : capturedPieces.sente
  const playerTurnSymbol = isPlayerSente ? 'b' : 'w'
  const opponentTurnSymbol = isPlayerSente ? 'w' : 'b'

  // 投了ボタンをクリック
  const handleResignClick = () => {
    setShowResignDialog(true)
  }

  // 投了を確認
  const handleConfirmResign = async () => {
    try {
      setIsResigning(true)
      // playerColor に基づいて playerType を決定
      const playerType = playerColor === 'b' ? 'b' : 'w'
      await onResign?.(playerType)
      setShowResignDialog(false)
      // ゲーム結果画面へのナビゲーションはページコンポーネントで処理
    } catch (error) {
      console.error('投了処理エラー:', error)
      setShowResignDialog(false)
    } finally {
      setIsResigning(false)
    }
  }

  // 投了ダイアログをキャンセル
  const handleCancelResign = () => {
    setShowResignDialog(false)
  }

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

        {/* 投了ボタン */}
        <button
          onClick={handleResignClick}
          disabled={isResigning}
          className="w-full bg-red-500 hover:bg-red-400 disabled:bg-red-300 disabled:cursor-not-allowed transition rounded-2xl py-4 font-bold text-lg"
        >
          {isResigning ? '投了処理中...' : '投了する'}
        </button>
      </div>

      {/* 投了確認ダイアログ */}
      {showResignDialog && (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50">
          <div className="bg-zinc-900 rounded-2xl p-8 border border-zinc-700 max-w-sm">
            <h2 className="text-2xl font-bold mb-4">投了しますか？</h2>
            <p className="text-zinc-300 mb-6">
              投了すると、相手が勝利となります。
            </p>

            <div className="flex gap-4">
              <button
                onClick={handleCancelResign}
                disabled={isResigning}
                className="flex-1 bg-zinc-700 hover:bg-zinc-600 disabled:bg-zinc-600 disabled:cursor-not-allowed transition rounded-lg py-3 font-bold text-white"
              >
                キャンセル
              </button>
              <button
                onClick={handleConfirmResign}
                disabled={isResigning}
                className="flex-1 bg-red-500 hover:bg-red-400 disabled:bg-red-300 disabled:cursor-not-allowed transition rounded-lg py-3 font-bold text-white"
              >
                {isResigning ? '処理中...' : '投了'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
