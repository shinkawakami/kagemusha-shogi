import { useState } from 'react'
import { movePiece } from '@/features/games/api/gamesApi'
import { toShogiCoordinate, generateMoveText, generateDropMoveText, type Square } from '@/lib/shogiCoordinate'
import { parseSFEN, type ParsedSFEN } from '@/lib/sfen'
import { getValidMoves, getPieceAt, getValidDropMoves, canPromote } from '@/lib/moveValidator'

export type PromoteDialog = {
  isOpen: boolean
  from: Square | null
  to: Square | null
}

export function useGameMove() {
  const [selectedPos, setSelectedPos] = useState<Square | null>(null)
  const [selectedCapturedPiece, setSelectedCapturedPiece] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const [promoteDialog, setPromoteDialog] = useState<PromoteDialog>({
    isOpen: false,
    from: null,
    to: null,
  })

  const handleCellClick = async (
    rowIndex: number,
    colIndex: number,
    gameId: string,
    board: (string | null)[][],
    turn: 'b' | 'w',
    onBoardUpdate: (parsed: ParsedSFEN) => void
  ) => {
    const clickedPos = toShogiCoordinate(rowIndex, colIndex)

    // 駒打ちモード：持ち駒をクリック後、盤面にクリック
    if (selectedCapturedPiece) {
      // 合法手かチェック
      const dropValidMoves = getValidDropMoves(board, selectedCapturedPiece)
      const isValidDrop = dropValidMoves.some(m => 
        m.row === clickedPos.row && m.col === clickedPos.col
      )

      if (!isValidDrop) {
        // 非合法な駒打ち
        console.log('非合法な駒打ち:', selectedCapturedPiece, clickedPos)
        setSelectedCapturedPiece(null)
        return
      }

      try {
        setLoading(true)
        const moveText = generateDropMoveText(selectedCapturedPiece, clickedPos)
        console.log('駒打ち実行:', moveText)

        const response = await movePiece(gameId, moveText)

        if (response.success) {
          const parsed = parseSFEN(response.data.board)
          onBoardUpdate(parsed)
          setSelectedCapturedPiece(null)
          console.log('駒打ち成功')
        }
      } catch (error) {
        console.error('駒打ちエラー:', error)
      } finally {
        setLoading(false)
      }
      return
    }

    // 通常の移動モード
    // 移動元が未選択の場合
    if (!selectedPos) {
      const clickedPiece = getPieceAt(board, clickedPos)
      
      // 駒がない場合は選択しない
      if (!clickedPiece) {
        console.log('駒がないマスをクリック')
        return
      }

      // 現在の手番の駒か確認（成駒の場合は基本部分で判定）
      const basePiece = clickedPiece.startsWith('+') ? clickedPiece.slice(1) : clickedPiece
      const isPlayerPiece = (turn === 'b' && basePiece === basePiece.toUpperCase()) ||
                            (turn === 'w' && basePiece === basePiece.toLowerCase())
      
      if (!isPlayerPiece) {
        console.log('相手の駒は選択できません')
        return
      }
      
      setSelectedPos(clickedPos)
      console.log('移動元選択:', clickedPos)
      return
    }

    // 同じマスをクリックした場合（選択解除）
    if (selectedPos.row === clickedPos.row && selectedPos.col === clickedPos.col) {
      setSelectedPos(null)
      console.log('選択解除')
      return
    }

    // 非合法手をクリックした場合（別の駒を選択するか、選択解除）
    const movingPiece = getPieceAt(board, selectedPos)
    if (movingPiece) {
      const validMoves = getValidMoves(board, selectedPos, movingPiece)
      const isValidMove = validMoves.some(m => 
        m.row === clickedPos.row && m.col === clickedPos.col
      )

      if (!isValidMove) {
        // 非合法手なので選択解除
        setSelectedPos(null)
        console.log('非合法手で選択解除')
        return
      }
    }

    // 合法手を実行
    try {
      setLoading(true)
      const movingPiece = getPieceAt(board, selectedPos)
      
      if (!movingPiece) {
        return
      }

      // 成可能かチェック
      if (canPromote(movingPiece, selectedPos, clickedPos)) {
        // 成可能 → ダイアログ表示
        setPromoteDialog({
          isOpen: true,
          from: selectedPos,
          to: clickedPos,
        })
        setLoading(false)
        return
      }

      // 成不可 → そのまま実行
      const moveText = generateMoveText(selectedPos, clickedPos)
      console.log('移動実行:', moveText)

      const response = await movePiece(gameId, moveText)

      if (response.success) {
        const parsed = parseSFEN(response.data.board)
        onBoardUpdate(parsed)
        setSelectedPos(null)
        console.log('移動成功')
      }
    } catch (error) {
      console.error('移動エラー:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleCapturedPieceClick = (piece: string, turn: 'b' | 'w') => {
    // 成駒は持ち駒にならないため、このチェックは基本的に不要
    // ただし、念のため正規化して判定
    const basePiece = piece.startsWith('+') ? piece.slice(1) : piece
    
    // 現在の手番の駒か確認
    const isPlayerPiece = (turn === 'b' && basePiece === basePiece.toUpperCase()) ||
                          (turn === 'w' && basePiece === basePiece.toLowerCase())
    
    if (!isPlayerPiece) {
      console.log('相手の持ち駒は選択できません')
      return
    }

    if (selectedCapturedPiece === piece) {
      // 同じ駒をクリック → 解除
      setSelectedCapturedPiece(null)
      console.log('駒打ち選択解除')
    } else {
      // 駒打ちモードに切り替え
      setSelectedPos(null)
      setSelectedCapturedPiece(piece)
      console.log('駒打ち選択:', piece)
    }
  }

  const cancelDropMode = () => {
    setSelectedCapturedPiece(null)
  }

  const executeMove = async (
    gameId: string,
    from: Square,
    to: Square,
    promote: boolean,
    onBoardUpdate: (parsed: ParsedSFEN) => void
  ) => {
    try {
      setLoading(true)
      let moveText = generateMoveText(from, to)
      
      if (promote) {
        moveText += '+'
      }
      
      console.log('移動実行:', moveText)

      const response = await movePiece(gameId, moveText)

      if (response.success) {
        const parsed = parseSFEN(response.data.board)
        onBoardUpdate(parsed)
        setSelectedPos(null)
        setPromoteDialog({ isOpen: false, from: null, to: null })
        console.log('移動成功')
      }
    } catch (error) {
      console.error('移動エラー:', error)
    } finally {
      setLoading(false)
    }
  }

  const cancelPromoteDialog = () => {
    setPromoteDialog({ isOpen: false, from: null, to: null })
    setSelectedPos(null)
  }

  return {
    selectedPos,
    selectedCapturedPiece,
    promoteDialog,
    handleCellClick,
    handleCapturedPieceClick,
    cancelDropMode,
    executeMove,
    cancelPromoteDialog,
    loading,
  }
}

