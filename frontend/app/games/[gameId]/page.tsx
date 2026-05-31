"use client";

import { useEffect, useState } from 'react'
import { useParams } from 'next/navigation'
import { getBoard, selectShadow } from '@/features/games/api/gamesApi'
import ShogiBoard from '@/features/games/components/ShogiBoard'
import GameSidebar from '@/features/games/components/ShogiSidebar';
import { parseSFEN } from '@/lib/sfen'


export default function GameScreen() {
  const params = useParams()
  const [board, setBoard] = useState<(string | null)[][]>([])
  const [turn, setTurn] = useState<'b' | 'w'>('b')
  const [capturedPieces, setCapturedPieces] = useState({
    sente: [] as string[],
    gote: [] as string[],
  })
  const [selectedSquare, setSelectedSquare] = useState<{
    row: number
    col: number
  } | null>(null)

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

  return (
    <div className="min-h-screen bg-black text-white flex flex-col lg:flex-row">
      {/* Board */}
      <ShogiBoard
        board={board}
        onCellClick={(row, col) => {
          setSelectedSquare({ row, col })
          console.log({
            row,
            col,
          })
        }}
      />
      {/* Sidebar */}
      <GameSidebar
        capturedPieces={capturedPieces}
        currentTurn={turn}
      />
    </div>
  )
}