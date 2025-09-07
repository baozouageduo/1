// src/utils/auth.ts
export type Role = 'ADMIN' | 'ITEM_ADMIN' | 'STUDENT' | 'USER' | string

export function parseJwt(token: string | null): any {
  if (!token) return null
  const parts = token.split('.')
  if (parts.length !== 3) return null
  try {
    const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/')
    const json = decodeURIComponent(atob(base64).split('').map(c => {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))
    return JSON.parse(json)
  } catch {
    try { return JSON.parse(atob(parts[1])) } catch { return null }
  }
}

export function deriveRole(payload: any, fallbackRole?: Role | null): Role | null {
  if (!payload) return fallbackRole ?? null
  // Common places to find roles/scopes
  const direct = payload.role || payload.roles || payload.authorities || payload.scope || payload.scp
  if (typeof direct === 'string') return direct
  if (Array.isArray(direct) && direct.length) return direct[0]
  // Spring Security often uses "authorities" array
  if (Array.isArray(payload.authorities) && payload.authorities.length) return payload.authorities[0]
  return fallbackRole ?? null
}

export function routeForRole(role: Role | null): string {
  switch (String(role || '').toUpperCase()) {
    case 'ADMIN':
      return '/dashboard'
    case 'ITEM_ADMIN':
      return '/items'
    case 'STUDENT':
      return '/lost'
    default:
      return '/dashboard'
  }
}
