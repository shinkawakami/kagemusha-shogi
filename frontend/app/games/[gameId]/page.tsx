"use client";

import { useEffect, useState } from 'react'
import { useParams } from 'next/navigation'
import { getBoard } from '@/features/games/api/gamesApi'
import ShogiBoard from '@/features/games/components/ShogiBoard'
import GameSidebar from '@/features/games/components/ShogiSidebar'
import PromoteDialog from '@/features/games/components/PromoteDialog'
import { parseSFEN } from '@/lib/sfen'
import { useGameMove } from '@/features/games/hooks/useGameMove'


export default function GameScreen() {
  const params = useParams()
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