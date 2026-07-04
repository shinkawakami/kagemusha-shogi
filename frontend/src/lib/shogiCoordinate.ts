export type Square = {
  row: string  // 'a' - 'i'
  col: number  // 1 - 9
}

export function toShogiCoordinate(
  rowIndex: number,  // 配列 [0-8]
  colIndex: number   // 配列 [0-8]
): Square {
  const rows = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'];
  
  return {
    row: rows[rowIndex],
    col: 9 - colIndex,
  }
}

/**
 * SFEN座標から移動テキストを生成（駒の種類なし）
 * 例: from { col: 2, row: 'g' } + to { col: 2, row: 'f' } = "2g2f"
 */
export function generateMoveText(
  from: Square,
  to: Square
): string {
  return `${from.col}${from.row}${to.col}${to.row}`
}

/**
 * 駒打ちテキストを生成（駒の種類あり）
 * 例: piece = 'P' (先手の歩), to { col: 2, row: 'd' } = "P2d"
 * 例: piece = 'p' (後手の歩), to { col: 2, row: 'd' } = "p2d"
 */
export function generateDropMoveText(
  piece: string,
  to: Square
): string {
  return `${piece}${to.col}${to.row}`
}
