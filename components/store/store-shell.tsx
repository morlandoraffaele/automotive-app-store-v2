'use client'

import { NavRail } from './nav-rail'
import { StoreUpdateBanner } from './store-update-banner'
import { TopBar } from './top-bar'

export function StoreShell({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex h-dvh overflow-hidden bg-background text-foreground">
      <NavRail />
      <div className="flex min-w-0 flex-1 flex-col">
        <TopBar />
        <StoreUpdateBanner />
        <main className="min-h-0 flex-1 overflow-y-auto overscroll-contain">{children}</main>
      </div>
    </div>
  )
}
