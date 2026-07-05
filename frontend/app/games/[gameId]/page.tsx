"use client";

import { useEffect, useState } from 'react'
import { useParams, useRouter } from 'next/navigation'
import { getBoard, resignGame } from '@/features/games/api/gamesApi'
import ShogiBoard from '@/features/games/components/ShogiBoard'
import GameSidebar from '@/features/games/components/ShogiSidebar'
import PromoteDialog from '@/features/games/components/PromoteDialog'
import { parseSFEN } from '@/lib/sfen'
import { useGameMove } from '@/features/games/hooks/useGameMove'

export default function GameScreen() {
  const params = useParams()
  const router = useRouter()
  const { selectedPos, selectedCapturedPiece, promoteDialog, handleCellClick, handleCapturedPieceClick, executeMove, cancelPromoteDialog } = useGameMove()
  const [board, setBoard] = useState<(string | null)[][]>([])
  const [turn, setTurn] = useState<'b' | 'w'>('b')
  const [capturedPieces, setCapturedPieces] = useState({
    sente: [] as string[],
    gote: [] as string[],
  })

  useEffect(() => {
    const fetchGame = async () => {
      const gameId = params.gameId as string

      const data = await getBoard(gameId)
      const parsed = parseSFEN(data.data.board)

      setBoard(parsed.board)
      setCapturedPieces(parsed.captured)
      setTurn(parsed.turn)
    }

    fetchGame()
  }, [params.gameId])

  const handleBoardUpdate = (parsed: ReturnType<typeof parseSFEN>) => {
    setBoard(parsed.board)
    setTurn(parsed.turn)
    setCapturedPieces(parsed.captured)
  }

  const handlePromoteDialog = (gameId: string, from: any, to: any, promote: boolean) => {
    executeMove(gameId, from, to, promote, handleBoardUpdate)
  }

  // 投了ハンドラー
  const handleResign = async (playerType: 'b' | 'w') => {
    try {
      const gameId = params.gameId as string
      // 'b'/'w' を 'SENTE'/'GOTE' に変換してAPI呼び出し
      const player = playerType === 'b' ? 'SENTE' : 'GOTE' as const
      const response = await resignGame(gameId, player)
      
      // 成功時は結果画面にリダイレクト
      if (response.status === 'FINISHED' || response.data?.status === 'FINISHED') {
        // 結果画面が存在する場合はそこへナビゲート
        // 現在は結果画面が未実装なので、ゲーム画面に留まるか、ホームに戻る
        router.push(`/games/${gameId}/result?winner=${response.data?.winner || ''}`)
      }
    } catch (error) {
      console.error('投了エラー:', error)
      // エラー表示（トースト等）は別途実装
    }
  }

  return (
    <div className="min-h-screen bg-black text-white flex flex-col lg:flex-row">
      {/* Board */}
      <ShogiBoard
        board={board}
        onCellClick={(row, col) =>
          handleCellClick(row, col, params.gameId as string, board, turn, handleBoardUpdate)
        }
        selectedPos={selectedPos}
        selectedCapturedPiece={selectedCapturedPiece}
      />
      {/* Sidebar */}
      <GameSidebar
        capturedPieces={capturedPieces}
        currentTurn={turn}
        selectedCapturedPiece={selectedCapturedPiece}
        onCapturedPieceClick={(piece) => handleCapturedPieceClick(piece, turn)}
        playerColor="b"  // オンラインの場合、自分が先手（下側）
        gameId={params.gameId as string}
        onResign={handleResign}
      />
      {/* Promote Dialog */}
      <PromoteDialog
        isOpen={promoteDialog.isOpen}
        from={promoteDialog.from}
        to={promoteDialog.to}
        gameId={params.gameId as string}
        onPromote={handlePromoteDialog}
        onCancel={cancelPromoteDialog}
      />
    </div>
  )
}