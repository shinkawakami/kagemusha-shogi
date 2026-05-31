export type Square = {
  row: number
  col: number
}

export function toShogiCoordinate(
  row: number,
  col: number
) {
  return {
    row: row + 1,
    col: 9 - col,
  }
}