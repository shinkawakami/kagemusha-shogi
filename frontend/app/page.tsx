"use client";

import { useState } from "react";

export default function Home() {
  const [message, setMessage] = useState("");

  const handleClick = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/test");

      const text = await res.text();

      setMessage(text);
    } catch (error) {
      console.error(error);
      setMessage("接続失敗");
    }
  };

  return (
    <main style={{ padding: "40px" }}>
      <h1>影武者将棋</h1>

      <button
        onClick={handleClick}
        style={{
          padding: "10px 20px",
          marginTop: "20px",
          cursor: "pointer",
        }}
      >
        バックエンド接続確認
      </button>

      <p style={{ marginTop: "20px" }}>
        {message}
      </p>
    </main>
  );
}