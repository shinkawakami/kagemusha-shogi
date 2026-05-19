"use client";

import { useState } from "react";
import { useRouter } from 'next/navigation'
import { createMatch } from '@/features/match/api/matchApi'
import Button from '@/components/ui/Button'


export default function MatchmakingScreen() {
  const router = useRouter()

  const [mode, setMode] = useState('rank')
  const [selectedTime, setSelectedTime] = useState('10分')

  const handleStartMatching = async () => {
    const data = await createMatch()

    router.push(`/games/${data.data.gameId}`)
  }

  return (
    <div className="min-h-screen bg-gradient-to-b from-zinc-950 via-zinc-900 to-black text-white flex items-center justify-center p-6">
      <div className="w-full max-w-3xl">
        <div className="bg-zinc-900/80 border border-zinc-800 rounded-[2rem] p-8 shadow-2xl backdrop-blur-xl">
          <div className="text-center mb-10">
            <p className="text-amber-400 tracking-[0.3em] text-sm mb-3">
              MATCHMAKING
            </p>

            <h1 className="text-4xl md:text-5xl font-black mb-4">
              対局を開始
            </h1>

            <p className="text-zinc-400 text-lg">
              ルールと持ち時間を選択してください。
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            <div className="space-y-4">
              <h2 className="text-xl font-bold mb-3">対局モード</h2>

              <Button
                variant="selectable"
                selected={mode === 'rank'}
                className="w-full p-5 text-left"
                onClick={() => setMode('rank')}
              >
                <p className="text-xl font-bold">レート戦</p>

                <p className="text-sm opacity-70 mt-1">
                  ランキングに反映されます
                </p>
              </Button>

              <Button
                variant="selectable"
                selected={mode === 'casual'}
                className="w-full p-5 text-left"
                onClick={() => setMode('casual')}
              >
                <p className="text-xl font-bold">カジュアル戦</p>

                <p className="text-sm opacity-70 mt-1">
                  気軽に遊べる通常対局
                </p>
              </Button>

              <Button
                variant="selectable"
                selected={mode === 'friend'}
                className="w-full p-5 text-left"
                onClick={() => setMode('friend')}
              >
                <p className="text-xl font-bold">友達対戦</p>

                <p className="text-sm opacity-70 mt-1">
                  ルームIDで招待
                </p>
              </Button>
            </div>

            <div className="space-y-6">
              <div>
                <h2 className="text-xl font-bold mb-3">持ち時間</h2>

                <div className="grid grid-cols-2 gap-3">
                  {['3分', '5分', '10分', '30分'].map((timeOption) => (
                    <Button
                      key={timeOption}
                      variant="selectable"
                      selected={selectedTime === timeOption}
                      onClick={() => setSelectedTime(timeOption)}
                      className="w-full p-4"
                    >
                      {timeOption}
                    </Button>
                  ))}
                </div>
              </div>

              <div>
                <h2 className="text-xl font-bold mb-3">現在の状況</h2>

                <div className="space-y-3">
                  <div className="bg-zinc-800 rounded-2xl px-5 py-4 flex justify-between items-center">
                    <span className="text-zinc-400">オンライン人数</span>
                    <span className="font-bold text-2xl">12,481</span>
                  </div>

                  <div className="bg-zinc-800 rounded-2xl px-5 py-4 flex justify-between items-center">
                    <span className="text-zinc-400">待機中プレイヤー</span>
                    <span className="font-bold text-2xl text-amber-300">384</span>
                  </div>
                </div>
              </div>
              <Button
                variant="primary"
                onClick={handleStartMatching}
                className="w-full mt-4 py-5 text-xl"
              >
                マッチング開始
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}