import { BoardResponse } from "@/types/board";

const API_BASE_URL = "http://localhost:8080";

export async function fetchSampleBoard(): Promise<BoardResponse> {
  const response = await fetch(`${API_BASE_URL}/api/sample-board`);

  if (!response.ok) {
    throw new Error("盤面データの取得に失敗しました");
  }

  return response.json();
}