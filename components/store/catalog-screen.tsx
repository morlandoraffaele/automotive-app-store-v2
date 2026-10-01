'use client'

import { CloudOff, PackageOpen, SearchX } from 'lucide-react'
import { useCallback, useMemo, useState } from 'react'
import { getAppState } from '@/lib/store/selectors'
import { useStoreRepository, useStoreSnapshot, useTranslation } from '@/lib/store/store-provider'
import type { CategoryId } from '@/lib/store/types'
import { AppTile } from './app-tile'
import { CategoryFilter, type CategoryFilterValue } from './category-filter'
import { SearchBar } from './search-bar'
import { StateMessage, TileSkeletonGrid } from './state-views'
import { TouchButton } from './touch-button'

const CATEGORY_ORDER: CategoryId[] = ['navigation', 'media', 'charging', 'communication', 'utilities', 'parked']

export function CatalogScreen() {
  const snapshot = useStoreSnapshot()
  const repository = useStoreRepository()
  const { t } = useTranslation()
  const [category, setCategory] = useState<CategoryFilterValue>('all')
  const [query, setQuery] = useState('')
  const handleQuery = useCallback((q: string) => setQuery(q), [])

  const { status, apps } = snapshot.catalog

  const visibleApps = useMemo(() => {
    const needle = query.trim().toLowerCase()
    return apps.filter((app) => {
      if (category !== 'all' && app.category !== category) return false
      if (!needle) return true
      return [app.name, app.tagline, app.developer, app.category, app.description].some((field) =>
        field.toLowerCase().includes(needle),
      )
    })
  }, [apps, category, query])

  const resetFilters = () => {
    setCategory('all')
    setQuery('')
  }

  return (
    <div className="flex flex-col gap-6 p-6">
      <div className="flex flex-col gap-4">
        <SearchBar query={query} onQueryChange={handleQuery} />
        <CategoryFilter categories={CATEGORY_ORDER} value={category} onChange={setCategory} />
      </div>

      {status === 'loading' ? (
        <TileSkeletonGrid label={t('catalog.loading')} />
      ) : status === 'error' ? (
        <StateMessage
          icon={CloudOff}
          tone="destructive"
          title={t('catalog.errorTitle')}
          body={t('catalog.errorBody')}
          action={<TouchButton onClick={repository.reloadCatalog}>{t('catalog.retry')}</TouchButton>}
        />
      ) : apps.length === 0 ? (
        <StateMessage icon={PackageOpen} title={t('catalog.emptyTitle')} body={t('catalog.emptyBody')} />
      ) : visibleApps.length === 0 ? (
        <StateMessage
          icon={SearchX}
          title={t('catalog.noMatchTitle')}
          body={t('catalog.noMatchBody')}
          action={
            <TouchButton variant="secondary" onClick={resetFilters}>
              {t('catalog.showAll')}
            </TouchButton>
          }
        />
      ) : (
        <section aria-labelledby="catalog-heading" className="flex flex-col gap-4">
          <div className="flex items-baseline justify-between gap-4">
            <h2 id="catalog-heading" className="text-2xl font-bold">
              {query ? t('search.resultsFor', { q: query }) : t(`category.${category === 'all' ? 'all' : category}`)}
            </h2>
            <p className="text-lg text-muted-foreground tabular-nums">{t('catalog.count', { n: visibleApps.length })}</p>
          </div>
          <ul className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {visibleApps.map((app) => (
              <li key={app.id}>
                <AppTile app={app} state={getAppState(snapshot, app)} />
              </li>
            ))}
          </ul>
        </section>
      )}
    </div>
  )
}
