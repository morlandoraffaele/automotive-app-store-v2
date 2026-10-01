'use client'

import { createContext, useContext, useEffect, useMemo, useState, useSyncExternalStore } from 'react'
import { createTranslator, LOCALE_META } from '@/lib/i18n'
import { MockStoreRepository } from './mock-repository'
import type { StoreRepository } from './types'

const StoreContext = createContext<StoreRepository | null>(null)

export function StoreProvider({
  children,
  createRepository = () => new MockStoreRepository(),
}: {
  children: React.ReactNode
  createRepository?: () => StoreRepository
}) {
  const [repository] = useState(createRepository)
  return (
    <StoreContext.Provider value={repository}>
      <DocumentPreferences repository={repository} />
      {children}
    </StoreContext.Provider>
  )
}

function DocumentPreferences({ repository }: { repository: StoreRepository }) {
  const { theme, locale } = useSyncExternalStore(
    repository.subscribe,
    () => repository.getSnapshot().settings,
    () => repository.getSnapshot().settings,
  )
  useEffect(() => {
    const root = document.documentElement
    root.classList.toggle('dark', theme === 'night')
    root.classList.toggle('light', theme === 'day')
    root.lang = locale
    root.dir = LOCALE_META[locale].dir
  }, [theme, locale])
  return null
}

export function useStoreRepository() {
  const repository = useContext(StoreContext)
  if (!repository) throw new Error('useStoreRepository must be used inside StoreProvider')
  return repository
}

export function useStoreSnapshot() {
  const repository = useStoreRepository()
  return useSyncExternalStore(repository.subscribe, repository.getSnapshot, repository.getSnapshot)
}

export function useTranslation() {
  const locale = useStoreSnapshot().settings.locale
  return useMemo(() => ({ t: createTranslator(locale), locale }), [locale])
}
