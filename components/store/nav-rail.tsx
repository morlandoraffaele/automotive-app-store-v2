'use client'

import { LayoutGrid, Moon, Settings, Sun, SquareStack } from 'lucide-react'
import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { useUpdateSummary } from '@/lib/store/hooks'
import { useStoreRepository, useStoreSnapshot, useTranslation } from '@/lib/store/store-provider'
import { cn } from '@/lib/utils'

export function NavRail() {
  const pathname = usePathname()
  const { t } = useTranslation()
  const repository = useStoreRepository()
  const { theme } = useStoreSnapshot().settings
  const { updatableIds } = useUpdateSummary()

  const items = [
    { href: '/', label: t('nav.store'), icon: LayoutGrid, active: pathname === '/' || pathname.startsWith('/apps') },
    { href: '/installed', label: t('nav.installed'), icon: SquareStack, active: pathname === '/installed', badge: updatableIds.length },
    { href: '/settings', label: t('nav.settings'), icon: Settings, active: pathname === '/settings' },
  ]

  const nextTheme = theme === 'night' ? 'day' : 'night'

  return (
    <nav
      aria-label={t('nav.main')}
      className="flex w-28 shrink-0 flex-col items-center gap-3 border-e border-border bg-card py-4 lg:w-32"
    >
      <ul className="flex flex-1 flex-col gap-3">
        {items.map((item) => (
          <li key={item.href}>
            <Link
              href={item.href}
              aria-current={item.active ? 'page' : undefined}
              className={cn(
                'relative flex size-24 flex-col items-center justify-center gap-1.5 rounded-2xl text-sm font-semibold transition-colors',
                item.active ? 'bg-primary text-primary-foreground' : 'text-muted-foreground active:bg-secondary',
              )}
            >
              <item.icon aria-hidden="true" className="size-8" />
              <span>{item.label}</span>
              {item.badge ? (
                <span
                  className={cn(
                    'absolute end-2 top-2 flex h-7 min-w-7 items-center justify-center rounded-full px-1.5 text-sm font-bold tabular-nums',
                    item.active ? 'bg-primary-foreground text-primary' : 'bg-primary text-primary-foreground',
                  )}
                >
                  <span className="sr-only">{t('topbar.updatesAvailable', { n: item.badge })}</span>
                  <span aria-hidden="true">{item.badge}</span>
                </span>
              ) : null}
            </Link>
          </li>
        ))}
      </ul>
      <button
        type="button"
        onClick={() => repository.updateSettings({ theme: nextTheme })}
        aria-label={nextTheme === 'day' ? t('theme.toggleToDay') : t('theme.toggleToNight')}
        className="flex size-24 items-center justify-center rounded-2xl text-muted-foreground transition-colors active:bg-secondary"
      >
        {theme === 'night' ? <Sun aria-hidden="true" className="size-8" /> : <Moon aria-hidden="true" className="size-8" />}
      </button>
    </nav>
  )
}
