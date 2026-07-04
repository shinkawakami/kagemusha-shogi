const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL

/**
 * 影武者選択
 * 
 * @param  gameId ゲームID
 * @returns
 * @throws Error
 */
export async function selectShadow(gameId: number) {
  const res = await fetch(`${BASE_URL}/api/games/${gameId}/shadow`, {
    method: 'POST',
  })

  if (!res.ok) {
    throw new Error('影武者選択失敗')
  }

  return res.json()
}

/**
 * 盤面取得API
 * 
 * @param gameId 
 * @returns 
 */
export async function getBoard(gameId: string) {
  const res = await fetch(`${BASE_URL}/api/games/${gameId}`, {
    method: 'GET',
  })

  if (!res.ok) {
    throw new Error('盤面取得失敗')
  }

  return res.json()
}

/**
 * コマ移動API
 * 
 * @param gameId
 * @param move
 * @returns
 * @throws Error
 */
export async function movePiece(gameId: string, move: string) {
  const res = await fetch(`${BASE_URL}/api/games/${gameId}/moves`, {
    method: 'POST',
    body: JSON.stringify({ move }),
    headers: { 'Content-Type': 'application/json' },
  })

  if (!res.ok) {
    throw new Error('コマ移動失敗')
  }
  
  return res.json()
}