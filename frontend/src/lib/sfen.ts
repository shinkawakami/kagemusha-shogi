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
        gote.push(char.toUpperCase())
      }
    }

    count = ''
  }

  return {
    sente,
    gote,
  }
}

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