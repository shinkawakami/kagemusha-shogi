"use client";

import { useState } from "react";
import Link from 'next/link'
import Button from '@/components/ui/Button'

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
    <div className="min-h-screen bg-gradient-to-b from-zinc-900 via-zinc-800 to-black text-white flex items-center justify-center p-6">
      <div className="w-full max-w-5xl grid md:grid-cols-2 gap-8 items-center">
        {/* Left Side */}
        <div className="space-y-6">
          <div>
            <p className="text-amber-400 tracking-[0.3em] text-sm mb-3">
              ONLINE SHOGI
            </p>
            <h1 className="text-5xl md:text-7xl font-black leading-tight">
              影武者将棋
            </h1>
          </div>

          <p className="text-zinc-300 text-lg leading-relaxed max-w-xl">
            世界中のプレイヤーとリアルタイム対局。
            レート戦、友達対戦、観戦モードを備えたオンライン将棋プラットフォーム。
          </p>

        <div className="flex flex-wrap gap-4">
          <Link href="/match">
            <Button variant="primary">
              対局開始
            </Button>
          </Link>

          </div>

          <div className="flex gap-6 pt-4 text-sm text-zinc-400">
            <div>
              <p className="text-2xl font-bold text-white">12,481</p>
              <p>オンライン中</p>
            </div>

            <div>
              <p className="text-2xl font-bold text-white">1.2M</p>
              <p>総対局数</p>
            </div>

            <div>
              <p className="text-2xl font-bold text-white">24/7</p>
              <p>マッチング</p>
            </div>
          </div>
        </div>

        {/* Right Side */}
        <div className="relative flex items-center justify-center">
          <div className="absolute inset-0 bg-amber-400/10 blur-3xl rounded-full" />

          <div className="relative bg-zinc-900/80 border border-zinc-700 rounded-[2rem] p-8 shadow-2xl backdrop-blur-xl w-full max-w-md">
            <div className="grid grid-cols-3 gap-2 bg-amber-100 p-4 rounded-2xl shadow-inner">
              {[
                '香', '桂', '銀',
                '金', '王', '金',
                '銀', '桂', '香'
              ].map((piece, index) => (
                <div
                  key={index}
                  className="aspect-square bg-amber-50 rounded-xl flex items-center justify-center text-3xl font-bold text-black shadow"
                >
                  {piece}
                </div>
              ))}
            </div>

            <div className="mt-6 space-y-3">
              <div className="flex items-center justify-between bg-zinc-800 rounded-xl px-4 py-3">
                <span className="text-zinc-400">レート戦</span>
                <span className="font-semibold text-amber-300">人気</span>
              </div>

              <div className="flex items-center justify-between bg-zinc-800 rounded-xl px-4 py-3">
                <span className="text-zinc-400">持ち時間</span>
                <span className="font-semibold">10分</span>
              </div>

              <div className="flex items-center justify-between bg-zinc-800 rounded-xl px-4 py-3">
                <span className="text-zinc-400">ルール</span>
                <span className="font-semibold">通常将棋</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}