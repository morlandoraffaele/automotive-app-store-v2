'use client'

import type { CategoryId } from '@/lib/store/types'
import { useTranslation } from '@/lib/store/store-provider'
import { cn } from '@/lib/utils'

export type CategoryFilterValue = CategoryId | 'all'

export function CategoryFilter({
  categories,
  value,
  onChange,
}: {
  categories: CategoryId[]
  value: CategoryFilterValue
  onChange: (value: CategoryFilterValue) => void
}) {
  const { t } = useTranslation()
  const options: CategoryFilterValue[] = ['all', ...categories]

  return (
    <div role="group" aria-label={t('category.filter')} className="scrollbar-none -mx-6 flex gap-3 overflow-x-auto px-6">
      {options.map((option) => {
        const selected = option === value
        return (
          <button
            key={option}
            type="button"
            aria-pressed={selected}
            onClick={() => onChange(option)}
            className={cn(
              'min-h-19 shrink-0 rounded-full px-7 text-lg font-semibold transition-colors',
              selected ? 'bg-foreground text-background' : 'bg-card text-foreground active:bg-secondary',
            )}
          >
            {t(`category.${option}`)}
          </button>
        )
      })}
    </div>
  )
}
