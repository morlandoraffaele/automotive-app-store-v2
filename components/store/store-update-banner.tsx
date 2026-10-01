'use client'

import { Car, X } from 'lucide-react'
import { useStoreRepository, useStoreSnapshot, useTranslation } from '@/lib/store/store-provider'

export function StoreUpdateBanner() {
  const { storeUpdate } = useStoreSnapshot()
  const repository = useStoreRepository()
  const { t } = useTranslation()

  if (storeUpdate.phase !== 'ready' || storeUpdate.bannerDismissed || !storeUpdate.availableVersion) return null

  return (
    <div role="status" className="flex items-center gap-4 border-b border-border bg-accent ps-6 pe-2 text-accent-foreground">
      <Car aria-hidden="true" className="size-7 shrink-0 text-primary" />
      <p className="flex-1 text-lg">
        <span className="font-semibold">{t('banner.storeReady', { version: storeUpdate.availableVersion })}</span>
        <span className="text-muted-foreground">{' · '}{t('banner.restartsWhenParked')}</span>
      </p>
      <button
        type="button"
        onClick={repository.dismissStoreBanner}
        className="flex size-19 items-center justify-center rounded-2xl text-muted-foreground active:bg-secondary"
      >
        <X aria-hidden="true" className="size-7" />
        <span className="sr-only">{t('banner.dismiss')}</span>
      </button>
    </div>
  )
}
