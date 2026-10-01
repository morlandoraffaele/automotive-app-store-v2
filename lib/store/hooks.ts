'use client'

import { useMemo } from 'react'
import { getActiveTaskCount, getAppState, getUpdatableAppIds } from './selectors'
import { useStoreSnapshot } from './store-provider'

export function useApp(appId: string) {
  const snapshot = useStoreSnapshot()
  const app = snapshot.catalog.apps.find((a) => a.id === appId)
  const state = useMemo(() => (app ? getAppState(snapshot, app) : null), [snapshot, app])
  return { app, state, catalogStatus: snapshot.catalog.status }
}

export function useUpdateSummary() {
  const snapshot = useStoreSnapshot()
  return useMemo(
    () => ({
      updatableIds: getUpdatableAppIds(snapshot),
      activeTaskCount: getActiveTaskCount(snapshot),
    }),
    [snapshot],
  )
}
