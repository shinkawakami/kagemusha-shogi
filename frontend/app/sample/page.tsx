"use client";

import { useEffect, useState } from "react";
import Board from "@/components/Board";
import { fetchSampleBoard } from "@/lib/api";
import { Piece } from "@/types/board";

export default function SamplePage() {
  const [board, setBoard] = useState<Piece[][]>([]);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    fetchSampleBoard()
      .then((data) => {
        setBoard(data.board);
      })
      .catch((error) => {
        console.error(error);
        setErrorMessage("盤面データの取得に失敗しました");
      });
  }, []);

  return (
    <main style={{ padding: "24px" }}>
      <h1>盤面表示サンプル</h1>

      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}

      {board.length > 0 ? (
        <Board board={board} />
      ) : (
        <p>読み込み中...</p>
      )}
    </main>
  );
}