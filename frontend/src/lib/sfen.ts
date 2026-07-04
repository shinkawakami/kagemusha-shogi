import { Piece } from '@/types/shogi'

export type ParsedSFEN = {
  board: Piece[][]
  turn: 'b' | 'w'
  captured: {
    sente: string[]
    gote: string[]
  }
  moveNumber: number
}

/**
 * SFEN文字列から盤面を作成
 * 
 * @param boardPart 
 * @returns 
 */
function parseBoard(boardPart: string): Piece[][] {
  const rows = boardPart.split('/')

  return rows.map((row) => {
    const cells: Piece[] = []

    for (let i = 0; i < row.length; i++) {
      const char = row[i]

      if (!isNaN(Number(char))) {
        const emptyCount = Number(char)

        for (let j = 0; j < emptyCount; j++) {
          cells.push(null)
        }
        continue
      }

      // 成り駒
      if (char === '+') {
        cells.push(`+${row[i + 1]}`)
        i++
        continue
      }

      cells.push(char)
    }

    return cells
  })
}

/**
 * SFEN文字列から持ち駒を作成
 * 
 * @param capturedPart 
 * @returns 
 */
function parseCaptured(capturedPart: string) {
  const sente: string[] = []
  const gote: string[] = []

  if (capturedPart === '-') {
    return {
      sente,
      gote,
    }
  }

  let count = ''

  for (const char of capturedPart) {
    if (!isNaN(Number(char))) {
      count += char
      continue
    }

    const amount = count === '' ? 1 : Number(count)

    for (let i = 0; i < amount; i++) {
      if (char === char.toUpperCase()) {
        sente.push(char)
      } else {
        gote.push(char)  // 小文字のまま保持
      }
    }

    count = ''
  }

  return {
    sente,
    gote,
  }
}

/**
 * SFEN文字列から持ちゴマと盤面を作成
 * 
 * @param sfen 
 * @returns 
 */
export function parseSFEN(sfen: string): ParsedSFEN {
  const [
    boardPart,
    turnPart,
    capturedPart,
    moveNumberPart,
  ] = sfen.split(' ')

  return {
    board: parseBoard(boardPart),
    turn: turnPart as 'b' | 'w',
    captured: parseCaptured(capturedPart),
    moveNumber: Number(moveNumberPart),
  }
}