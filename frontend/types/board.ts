export type Piece = {
  type: string;
  owner: string;
  label: string;
  row: number;
  col: number;
};

export type BoardResponse = {
  board: Piece[][];
};