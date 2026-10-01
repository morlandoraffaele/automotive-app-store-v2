'use client'

import { Download, ExternalLink, LoaderCircle, RotateCw, X } from 'lucide-react'
import { useEffect, useState } from 'react'
import type { AppState } from '@/lib/store/selectors'
import { useStoreRepository, useTranslation } from '@/lib/store/store-provider'
import { cn } from '@/lib/utils'
import { TouchButton } from './touch-button'

export function AppActionButton({
  appId,
  appName,
  state,
  className,
}: {
  appId: string
  appName: string
  state: AppState
  className?: string
}) {
  const repository = useStoreRepository()
  const { t } = useTranslation()
  const [opening, setOpening] = useState(false)

  useEffect(() => {
    if (!opening) return
    const timer = setTimeout(() => setOpening(false), 1500)
    return () => clearTimeout(timer)
  }, [opening])

  const base = cn('min-w-44', className)

  switch (state.status) {
    case 'not-installed':
      return (
        <TouchButton className={base} onClick={() => repository.install(appId)} aria-label={`${t('action.install')} ${appName}`}>
          <Download aria-hidden="true" />
          {t('action.install')}
        </TouchButton>
      )
    case 'update-available':
      return (
        <TouchButton className={base} onClick={() => repository.update(appId)} aria-label={`${t('action.update')} ${appName}`}>
          <Download aria-hidden="true" />
          {t('action.update')}
        </TouchButton>
      )
    case 'downloading':
      return (
        <TouchButton
          variant="outline"
          className={base}
          onClick={() => repository.cancel(appId)}
          aria-label={`${t('action.cancel')} ${appName}`}
        >
          <X aria-hidden="true" />
          {t('action.cancel')}
        </TouchButton>
      )
    case 'installing':
      return (
        <TouchButton variant="secondary" className={base} aria-disabled="true" tabIndex={-1}>
          <LoaderCircle aria-hidden="true" className="animate-spin" />
          {t('action.installing')}
        </TouchButton>
      )
    case 'failed':
      return (
        <TouchButton
          variant="destructive"
          className={base}
          onClick={() => repository.retry(appId)}
          aria-label={`${t('action.retry')} ${appName}`}
        >
          <RotateCw aria-hidden="true" />
          {t('action.retry')}
        </TouchButton>
      )
    default:
      return (
        <TouchButton
          variant="secondary"
          className={base}
          onClick={() => setOpening(true)}
          aria-label={`${t('action.open')} ${appName}`}
        >
          <ExternalLink aria-hidden="true" className="rtl:-scale-x-100" />
          {opening ? t('action.opening') : t('action.open')}
        </TouchButton>
      )
  }
}
