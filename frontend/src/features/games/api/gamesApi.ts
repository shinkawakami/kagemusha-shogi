const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL

export async function selectShadow(gameId: number) {
  const res = await fetch(`${BASE_URL}/api/games/${gameId}/shadow`, {
    method: 'POST',
  })

  if (!res.ok) {
    throw new Error('影武者選択失敗')
  }

  return res.json()
}

export async function getBoard(gameId: string) {
  const res = await fetch(`${BASE_URL}/api/games/${gameId}`, {
    method: 'GET',
  })

  if (!res.ok) {
    throw new Error('盤面取得失敗')
  }

  return res.json()
}