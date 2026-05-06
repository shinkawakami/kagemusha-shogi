import { Piece } from "@/types/board";

type BoardCellProps = {
  piece: Piece;
};

export default function BoardCell({ piece }: BoardCellProps) {
  return (
    <div
      style={{
        width: "48px",
        height: "48px",
        border: "1px solid #333",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        backgroundColor: "#f5d48a",
        fontWeight: "bold",
        fontSize: "20px",
      }}
    >
      {piece.label}
    </div>
  );
}