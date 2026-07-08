import { Piece } from "@/types/board";
import BoardCell from "./BoardCell";

type BoardProps = {
  board: Piece[][];
};

export default function Board({ board }: BoardProps) {
  return (
    <div
      style={{
        display: "grid",
        gridTemplateColumns: "repeat(9, 48px)",
        width: "432px",
      }}
    >
      {board.flatMap((row) =>
        row.map((piece) => (
          <BoardCell key={`${piece.row}-${piece.col}`} piece={piece} />
        ))
      )}
    </div>
  );
}