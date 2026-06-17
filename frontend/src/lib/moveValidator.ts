import { type Square } from './shogiCoordinate'
import { parseSFEN, type ParsedSFEN } from './sfen'

export type Piece = string | null

/**
 * 盤面から指定位置の駒を取得
 */
export function getPieceAt(board: (string | null)[][], pos: Square): string | null {
  // Square: { row: 'a'-'i', col: 1-9 }
  // board: ParsedSFEN.board は配列インデックス [0-8][0-8]
  
  const rowIndex = pos.row.charCodeAt(0) - 'a'.charCodeAt(0)
  const colIndex = 9 - pos.col
  
  if (rowIndex < 0 || rowIndex > 8 || colIndex < 0 || colIndex > 8) {
    return null
  }
  
  return board[rowIndex]?.[colIndex] ?? null
}

/**
 * 指定位置に駒を配置
 */
export function setPieceAt(board: (string | null)[][], pos: Square, piece: string | null): void {
  const rowIndex = pos.row.charCodeAt(0) - 'a'.charCodeAt(0)
  const colIndex = 9 - pos.col
  
  if (rowIndex >= 0 && rowIndex <= 8 && colIndex >= 0 && colIndex <= 8) {
    board[rowIndex][colIndex] = piece
  }
}

/**
 * 先手か後手かを判定
 * 大文字 = 先手 (SENTE)
 * 小文字 = 後手 (GOTE)
 */
function isPlayerSente(piece: string): boolean {
  return piece === piece.toUpperCase()
}

/**
 * 前進方向を取得
 * 先手: -1 (行が減る方向)
 * 後手: 1 (行が増える方向)
 */
function getForwardDirection(piece: string): number {
  return isPlayerSente(piece) ? -1 : 1
}

/**
 * 移動経路が空いているか確認（飛車・角・香用）
 */
function isPathClear(board: (string | null)[][], from: Square, to: Square): boolean {
  const fromRowIdx = from.row.charCodeAt(0) - 'a'.charCodeAt(0)
  const fromColIdx = 9 - from.col
  const toRowIdx = to.row.charCodeAt(0) - 'a'.charCodeAt(0)
  const toColIdx = 9 - to.col
  
  const rowDiff = toRowIdx - fromRowIdx
  const colDiff = toColIdx - fromColIdx
  
  const rowStep = rowDiff === 0 ? 0 : rowDiff > 0 ? 1 : -1
  const colStep = colDiff === 0 ? 0 : colDiff > 0 ? 1 : -1
  
  let currentRowIdx = fromRowIdx + rowStep
  let currentColIdx = fromColIdx + colStep
  
  while (currentRowIdx !== toRowIdx || currentColIdx !== toColIdx) {
    if (board[currentRowIdx]?.[currentColIdx] !== null && board[currentRowIdx]?.[currentColIdx] !== undefined) {
      return false
    }
    
    currentRowIdx += rowStep
    currentColIdx += colStep
  }
  
  return true
}

/**
 * 指定位置が盤面内か確認
 */
function isWithinBoard(pos: Square): boolean {
  const rowIdx = pos.row.charCodeAt(0) - 'a'.charCodeAt(0)
  return rowIdx >= 0 && rowIdx <= 8 && pos.col >= 1 && pos.col <= 9
}

/**
 * 歩の合法手を取得
 */
function getValidFuMoves(board: (string | null)[][], from: Square, piece: string): Square[] {
  const moves: Square[] = []
  const forward = getForwardDirection(piece)
  const targetRowIdx = from.row.charCodeAt(0) - 'a'.charCodeAt(0) + forward
  
  if (targetRowIdx < 0 || targetRowIdx > 8) return moves
  
  const targetRow = String.fromCharCode('a'.charCodeAt(0) + targetRowIdx)
  const targetPos: Square = { row: targetRow, col: from.col }
  
  const targetPiece = getPieceAt(board, targetPos)
  if (!targetPiece || isPlayerSente(targetPiece) !== isPlayerSente(piece)) {
    moves.push(targetPos)
  }
  
  return moves
}

/**
 * 香の合法手を取得
 */
function getValidKyoMoves(board: (string | null)[][], from: Square, piece: string): Square[] {
  const moves: Square[] = []
  const forward = getForwardDirection(piece)
  const fromRowIdx = from.row.charCodeAt(0) - 'a'.charCodeAt(0)
  
  for (let i = 1; i <= 9; i++) {
    const targetRowIdx = fromRowIdx + i * forward
    if (targetRowIdx < 0 || targetRowIdx > 8) break
    
    const targetRow = String.fromCharCode('a'.charCodeAt(0) + targetRowIdx)
    const targetPos: Square = { row: targetRow, col: from.col }
    const targetPiece = getPieceAt(board, targetPos)
    
    if (!targetPiece) {
      moves.push(targetPos)
    } else {
      if (isPlayerSente(targetPiece) !== isPlayerSente(piece)) {
        moves.push(targetPos)
      }
      break
    }
  }
  
  return moves
}

/**
 * 桂の合法手を取得
 */
function getValidKeimaMoves(board: (string | null)[][], from: Square, piece: string): Square[] {
  const moves: Square[] = []
  const forward = getForwardDirection(piece)
  const fromRowIdx = from.row.charCodeAt(0) - 'a'.charCodeAt(0)
  
  // 前に2、左右に1
  const offsets = [[forward * 2, -1], [forward * 2, 1]]
  
  for (const [rowOffset, colOffset] of offsets) {
    const targetRowIdx = fromRowIdx + rowOffset
    const targetCol = from.col + colOffset
    
    if (targetRowIdx < 0 || targetRowIdx > 8 || targetCol < 1 || targetCol > 9) continue
    
    const targetRow = String.fromCharCode('a'.charCodeAt(0) + targetRowIdx)
    const targetPos: Square = { row: targetRow, col: targetCol }
    const targetPiece = getPieceAt(board, targetPos)
    
    if (!targetPiece || isPlayerSente(targetPiece) !== isPlayerSente(piece)) {
      moves.push(targetPos)
    }
  }
  
  return moves
}

/**
 * 銀の合法手を取得
 */
function getValidGinMoves(board: (string | null)[][], from: Square, piece: string): Square[] {
  const moves: Square[] = []
  const forward = getForwardDirection(piece)
  const fromRowIdx = from.row.charCodeAt(0) - 'a'.charCodeAt(0)
  
  // 前、斜め前、斜め後ろ
  const offsets = [
    [forward, 0],      // 前
    [forward, -1],     // 斜め前左
    [forward, 1],      // 斜め前右
    [-forward, -1],    // 斜め後ろ左
    [-forward, 1],     // 斜め後ろ右
  ]
  
  for (const [rowOffset, colOffset] of offsets) {
    const targetRowIdx = fromRowIdx + rowOffset
    const targetCol = from.col + colOffset
    
    if (targetRowIdx < 0 || targetRowIdx > 8 || targetCol < 1 || targetCol > 9) continue
    
    const targetRow = String.fromCharCode('a'.charCodeAt(0) + targetRowIdx)
    const targetPos: Square = { row: targetRow, col: targetCol }
    const targetPiece = getPieceAt(board, targetPos)
    
    if (!targetPiece || isPlayerSente(targetPiece) !== isPlayerSente(piece)) {
      moves.push(targetPos)
    }
  }
  
  return moves
}

/**
 * 金の合法手を取得
 */
function getValidKinMoves(board: (string | null)[][], from: Square, piece: string): Square[] {
  const moves: Square[] = []
  const forward = getForwardDirection(piece)
  const fromRowIdx = from.row.charCodeAt(0) - 'a'.charCodeAt(0)
  
  // 前、斜め前、横、後ろ
  const offsets = [
    [forward, 0],      // 前
    [forward, -1],     // 斜め前左
    [forward, 1],      // 斜め前右
    [0, -1],           // 左
    [0, 1],            // 右
    [-forward, 0],     // 後ろ
  ]
  
  for (const [rowOffset, colOffset] of offsets) {
    const targetRowIdx = fromRowIdx + rowOffset
    const targetCol = from.col + colOffset
    
    if (targetRowIdx < 0 || targetRowIdx > 8 || targetCol < 1 || targetCol > 9) continue
    
    const targetRow = String.fromCharCode('a'.charCodeAt(0) + targetRowIdx)
    const targetPos: Square = { row: targetRow, col: targetCol }
    const targetPiece = getPieceAt(board, targetPos)
    
    if (!targetPiece || isPlayerSente(targetPiece) !== isPlayerSente(piece)) {
      moves.push(targetPos)
    }
  }
  
  return moves
}

/**
 * 王の合法手を取得
 */
function getValidGyokuMoves(board: (string | null)[][], from: Square, piece: string): Square[] {
  const moves: Square[] = []
  const fromRowIdx = from.row.charCodeAt(0) - 'a'.charCodeAt(0)
  
  // 周囲8方向
  const offsets = [
    [-1, -1], [-1, 0], [-1, 1],
    [0, -1],           [0, 1],
    [1, -1], [1, 0], [1, 1],
  ]
  
  for (const [rowOffset, colOffset] of offsets) {
    const targetRowIdx = fromRowIdx + rowOffset
    const targetCol = from.col + colOffset
    
    if (targetRowIdx < 0 || targetRowIdx > 8 || targetCol < 1 || targetCol > 9) continue
    
    const targetRow = String.fromCharCode('a'.charCodeAt(0) + targetRowIdx)
    const targetPos: Square = { row: targetRow, col: targetCol }
    const targetPiece = getPieceAt(board, targetPos)
    
    if (!targetPiece || isPlayerSente(targetPiece) !== isPlayerSente(piece)) {
      moves.push(targetPos)
    }
  }
  
  return moves
}

/**
 * 飛車の合法手を取得
 */
function getValidHishaMoves(board: (string | null)[][], from: Square, piece: string): Square[] {
  const moves: Square[] = []
  const fromRowIdx = from.row.charCodeAt(0) - 'a'.charCodeAt(0)
  
  // 上下左右の4方向
  const directions = [
    [-1, 0], [1, 0], [0, -1], [0, 1],
  ]
  
  for (const [rowDir, colDir] of directions) {
    for (let i = 1; i <= 9; i++) {
      const targetRowIdx = fromRowIdx + i * rowDir
      const targetCol = from.col + i * colDir
      
      if (targetRowIdx < 0 || targetRowIdx > 8 || targetCol < 1 || targetCol > 9) break
      
      const targetRow = String.fromCharCode('a'.charCodeAt(0) + targetRowIdx)
      const targetPos: Square = { row: targetRow, col: targetCol }
      const targetPiece = getPieceAt(board, targetPos)
      
      if (!targetPiece) {
        moves.push(targetPos)
      } else {
        if (isPlayerSente(targetPiece) !== isPlayerSente(piece)) {
          moves.push(targetPos)
        }
        break
      }
    }
  }
  
  return moves
}

/**
 * 角の合法手を取得
 */
function getValidKakuMoves(board: (string | null)[][], from: Square, piece: string): Square[] {
  const moves: Square[] = []
  const fromRowIdx = from.row.charCodeAt(0) - 'a'.charCodeAt(0)
  
  // 斜めの4方向
  const directions = [
    [-1, -1], [-1, 1], [1, -1], [1, 1],
  ]
  
  for (const [rowDir, colDir] of directions) {
    for (let i = 1; i <= 9; i++) {
      const targetRowIdx = fromRowIdx + i * rowDir
      const targetCol = from.col + i * colDir
      
      if (targetRowIdx < 0 || targetRowIdx > 8 || targetCol < 1 || targetCol > 9) break
      
      const targetRow = String.fromCharCode('a'.charCodeAt(0) + targetRowIdx)
      const targetPos: Square = { row: targetRow, col: targetCol }
      const targetPiece = getPieceAt(board, targetPos)
      
      if (!targetPiece) {
        moves.push(targetPos)
      } else {
        if (isPlayerSente(targetPiece) !== isPlayerSente(piece)) {
          moves.push(targetPos)
        }
        break
      }
    }
  }
  
  return moves
}

/**
 * 駒打ちの合法手を取得
 * @param board 盤面
 * @param piece 打つ駒（大文字=先手, 小文字=後手）
 * @returns 駒打ちが可能なマスのリスト
 */
export function getValidDropMoves(
  board: (string | null)[][],
  piece: string
): Square[] {
  const moves: Square[] = []
  const pieceLower = piece.toLowerCase()
  
  for (let rowIdx = 0; rowIdx < 9; rowIdx++) {
    for (let colIdx = 0; colIdx < 9; colIdx++) {
      const row = String.fromCharCode('a'.charCodeAt(0) + rowIdx)
      const col = 9 - colIdx
      const pos: Square = { row, col }
      
      // 1. 駒が重ならないこと
      if (board[rowIdx]?.[colIdx] !== null && board[rowIdx]?.[colIdx] !== undefined) {
        continue
      }
      
      // 駒種ごとのルールをチェック
      if (isValidDropPosition(pos, pieceLower, piece)) {
        moves.push(pos)
      }
    }
  }
  
  return moves
}

/**
 * 駒打ち位置が合法かチェック
 */
function isValidDropPosition(pos: Square, pieceLower: string, piece: string): boolean {
  const rowIdx = pos.row.charCodeAt(0) - 'a'.charCodeAt(0)
  const playerIsSente = isPlayerSente(piece)
  
  switch (pieceLower) {
    case 'p': // 歩
      // 先手は1段目（a行）に置かない
      // 後手は9段目（i行）に置かない
      if (playerIsSente && rowIdx === 0) return false
      if (!playerIsSente && rowIdx === 8) return false
      return true
      
    case 'l': // 香車
      // 先手は1段目（a行）に置かない
      // 後手は9段目（i行）に置かない
      if (playerIsSente && rowIdx === 0) return false
      if (!playerIsSente && rowIdx === 8) return false
      return true
      
    case 'n': // 桂馬
      // 先手は1段目（a行）と2段目（b行）に置かない
      // 後手は9段目（i行）と8段目（h行）に置かない
      if (playerIsSente && (rowIdx === 0 || rowIdx === 1)) return false
      if (!playerIsSente && (rowIdx === 8 || rowIdx === 7)) return false
      return true
      
    default: // その他の駒は制限なし
      return true
  }
}


/**
 * 駒が成可能かどうかを判定
 * 将棋のルール：移動元が敵陣 OR 移動先が敵陣
 * @param piece 駒の文字列
 * @param from 移動元座標
 * @param to 移動先座標
 * @returns 成可能な場合 true
 */
export function canPromote(piece: string, from: Square, to: Square): boolean {
  if (!piece) return false
  
  const pieceLower = piece.toLowerCase()
  
  // 成不可能な駒
  if (pieceLower === 'k' || pieceLower === 'g') {
    return false
  }
  
  // 既に成駒の場合
  if (piece.startsWith('+')) {
    return false
  }
  
  const isPlayerSente = piece === piece.toUpperCase()
  const fromRowIdx = from.row.charCodeAt(0) - 'a'.charCodeAt(0)
  const toRowIdx = to.row.charCodeAt(0) - 'a'.charCodeAt(0)
  
  // 先手：移動元がa～c行 OR 移動先がa～c行
  if (isPlayerSente && (
    (fromRowIdx >= 0 && fromRowIdx <= 2) || 
    (toRowIdx >= 0 && toRowIdx <= 2)
  )) {
    return true
  }
  
  // 後手：移動元がg～i行 OR 移動先がg～i行
  if (!isPlayerSente && (
    (fromRowIdx >= 6 && fromRowIdx <= 8) || 
    (toRowIdx >= 6 && toRowIdx <= 8)
  )) {
    return true
  }
  
  return false
}

/**
 * 成駒かどうかを判定
 */
function isPromotedPiece(piece: string): boolean {
  return piece.startsWith('+')
}

/**
 * 成駒から元の駒の種類を取得
 * '+R' -> 'R', 'R' -> 'R'
 */
function getBasePieceType(piece: string): string {
  return piece.startsWith('+') ? piece.slice(1) : piece
}

/**
 * 成駒（竜）の合法手を取得
 * 飛車の動きに加えて、王のように周囲8方向に1マス
 */
function getValidRyuMoves(board: (string | null)[][], from: Square, piece: string): Square[] {
  const moves: Square[] = []
  const fromRowIdx = from.row.charCodeAt(0) - 'a'.charCodeAt(0)
  
  // 飛車の動き（縦横に何マスでも）
  const directions = [
    [-1, 0], [1, 0], [0, -1], [0, 1],
  ]
  
  for (const [rowDir, colDir] of directions) {
    for (let i = 1; i <= 9; i++) {
      const targetRowIdx = fromRowIdx + i * rowDir
      const targetCol = from.col + i * colDir
      
      if (targetRowIdx < 0 || targetRowIdx > 8 || targetCol < 1 || targetCol > 9) break
      
      const targetRow = String.fromCharCode('a'.charCodeAt(0) + targetRowIdx)
      const targetPos: Square = { row: targetRow, col: targetCol }
      const targetPiece = getPieceAt(board, targetPos)
      
      if (!targetPiece) {
        moves.push(targetPos)
      } else {
        if (isPlayerSente(targetPiece) !== isPlayerSente(piece)) {
          moves.push(targetPos)
        }
        break
      }
    }
  }
  
  // 王のように周囲8方向に1マス
  const kingOffsets = [
    [-1, -1], [-1, 0], [-1, 1],
    [0, -1],           [0, 1],
    [1, -1], [1, 0], [1, 1],
  ]
  
  for (const [rowOffset, colOffset] of kingOffsets) {
    const targetRowIdx = fromRowIdx + rowOffset
    const targetCol = from.col + colOffset
    
    if (targetRowIdx < 0 || targetRowIdx > 8 || targetCol < 1 || targetCol > 9) continue
    
    const targetRow = String.fromCharCode('a'.charCodeAt(0) + targetRowIdx)
    const targetPos: Square = { row: targetRow, col: targetCol }
    const targetPiece = getPieceAt(board, targetPos)
    
    if (!targetPiece || isPlayerSente(targetPiece) !== isPlayerSente(piece)) {
      moves.push(targetPos)
    }
  }
  
  return moves
}

/**
 * 成駒（馬）の合法手を取得
 * 角の動きに加えて、王のように周囲8方向に1マス
 */
function getValidUmaMoves(board: (string | null)[][], from: Square, piece: string): Square[] {
  const moves: Square[] = []
  const fromRowIdx = from.row.charCodeAt(0) - 'a'.charCodeAt(0)
  
  // 角の動き（斜めに何マスでも）
  const directions = [
    [-1, -1], [-1, 1], [1, -1], [1, 1],
  ]
  
  for (const [rowDir, colDir] of directions) {
    for (let i = 1; i <= 9; i++) {
      const targetRowIdx = fromRowIdx + i * rowDir
      const targetCol = from.col + i * colDir
      
      if (targetRowIdx < 0 || targetRowIdx > 8 || targetCol < 1 || targetCol > 9) break
      
      const targetRow = String.fromCharCode('a'.charCodeAt(0) + targetRowIdx)
      const targetPos: Square = { row: targetRow, col: targetCol }
      const targetPiece = getPieceAt(board, targetPos)
      
      if (!targetPiece) {
        moves.push(targetPos)
      } else {
        if (isPlayerSente(targetPiece) !== isPlayerSente(piece)) {
          moves.push(targetPos)
        }
        break
      }
    }
  }
  
  // 王のように周囲8方向に1マス
  const kingOffsets = [
    [-1, -1], [-1, 0], [-1, 1],
    [0, -1],           [0, 1],
    [1, -1], [1, 0], [1, 1],
  ]
  
  for (const [rowOffset, colOffset] of kingOffsets) {
    const targetRowIdx = fromRowIdx + rowOffset
    const targetCol = from.col + colOffset
    
    if (targetRowIdx < 0 || targetRowIdx > 8 || targetCol < 1 || targetCol > 9) continue
    
    const targetRow = String.fromCharCode('a'.charCodeAt(0) + targetRowIdx)
    const targetPos: Square = { row: targetRow, col: targetCol }
    const targetPiece = getPieceAt(board, targetPos)
    
    if (!targetPiece || isPlayerSente(targetPiece) !== isPlayerSente(piece)) {
      moves.push(targetPos)
    }
  }
  
  return moves
}

/**
 * 成駒の合法手を取得
 * 成駒は全て金と同じ移動ルール（周囲1マス）を持つ
 */
function getValidPromotedPieceMoves(board: (string | null)[][], from: Square, piece: string): Square[] {
  const basePiece = getBasePieceType(piece)
  
  // 成駒の種類により異なる移動ルール
  switch (basePiece.toLowerCase()) {
    case 'r': // 竜（成飛車）
      return getValidRyuMoves(board, from, piece)
    case 'b': // 馬（成角）
      return getValidUmaMoves(board, from, piece)
    default: // 成歩・成香・成桂・成銀は金と同じ
      return getValidKinMoves(board, from, piece)
  }
}

/**
 * 指定位置の駒の合法手を取得
 * @param board ParsedSFEN.board
 * @param from 移動元座標
 * @param piece 駒の文字列
 * @returns 合法手のリスト
 */
export function getValidMoves(
  board: (string | null)[][],
  from: Square,
  piece: string
): Square[] {
  if (!piece) return []
  
  // 成駒かチェック
  if (isPromotedPiece(piece)) {
    return getValidPromotedPieceMoves(board, from, piece)
  }
  
  const pieceLower = piece.toLowerCase()
  
  switch (pieceLower) {
    case 'p':  // 歩
      return getValidFuMoves(board, from, piece)
    case 'l':  // 香
      return getValidKyoMoves(board, from, piece)
    case 'n':  // 桂
      return getValidKeimaMoves(board, from, piece)
    case 's':  // 銀
      return getValidGinMoves(board, from, piece)
    case 'g':  // 金
      return getValidKinMoves(board, from, piece)
    case 'k':  // 王
      return getValidGyokuMoves(board, from, piece)
    case 'r':  // 飛車
      return getValidHishaMoves(board, from, piece)
    case 'b':  // 角
      return getValidKakuMoves(board, from, piece)
    default:
      return []
  }
}
