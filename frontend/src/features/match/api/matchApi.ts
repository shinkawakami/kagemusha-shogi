const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL

export async function createMatch() {
  const res = await fetch(`${BASE_URL}/api/games`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
  })

  if (!res.ok) {
    throw new Error('マッチ作成失敗')
  }

  return res.json()
}