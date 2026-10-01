'use client'

import { Keyboard, Mic, Search, X } from 'lucide-react'
import { useEffect, useRef, useState } from 'react'
import { useTranslation } from '@/lib/store/store-provider'
import { cn } from '@/lib/utils'
import { TouchButton } from './touch-button'

const VOICE_SAMPLES = ['charging', 'music', 'weather', 'parking']

export function SearchBar({ query, onQueryChange }: { query: string; onQueryChange: (q: string) => void }) {
  const { t } = useTranslation()
  const [listening, setListening] = useState(false)
  const [typing, setTyping] = useState(false)
  const inputRef = useRef<HTMLInputElement>(null)
  const sampleIndex = useRef(0)

  useEffect(() => {
    if (!listening) return
    const timer = setTimeout(() => {
      onQueryChange(VOICE_SAMPLES[sampleIndex.current % VOICE_SAMPLES.length])
      sampleIndex.current += 1
      setListening(false)
    }, 1600)
    return () => clearTimeout(timer)
  }, [listening, onQueryChange])

  useEffect(() => {
    if (typing) inputRef.current?.focus()
  }, [typing])

  return (
    <div className="flex items-center gap-3">
      <TouchButton
        onClick={() => setListening((v) => !v)}
        aria-pressed={listening}
        className={cn('min-w-19 px-6', listening && 'animate-pulse')}
      >
        <Mic aria-hidden="true" />
        <span className={cn(!listening && 'sr-only md:not-sr-only')}>
          {listening ? t('search.listening') : t('search.voice')}
        </span>
      </TouchButton>

      {typing || query ? (
        <div className="flex min-h-19 min-w-0 flex-1 items-center gap-3 rounded-2xl bg-card ps-5">
          <Search aria-hidden="true" className="size-7 shrink-0 text-muted-foreground" />
          <label htmlFor="store-search" className="sr-only">
            {t('search.placeholder')}
          </label>
          <input
            id="store-search"
            ref={inputRef}
            type="search"
            value={query}
            onChange={(e) => onQueryChange(e.target.value)}
            onBlur={() => !query && setTyping(false)}
            placeholder={t('search.placeholder')}
            autoComplete="off"
            className="min-w-0 flex-1 bg-transparent text-xl outline-none placeholder:text-muted-foreground [&::-webkit-search-cancel-button]:hidden"
          />
          <button
            type="button"
            onClick={() => {
              onQueryChange('')
              setTyping(false)
            }}
            className="flex size-19 shrink-0 items-center justify-center rounded-2xl text-muted-foreground active:bg-secondary"
          >
            <X aria-hidden="true" className="size-7" />
            <span className="sr-only">{t('search.clear')}</span>
          </button>
        </div>
      ) : (
        <>
          <TouchButton variant="secondary" size="icon" onClick={() => setTyping(true)}>
            <Keyboard aria-hidden="true" />
            <span className="sr-only">{t('search.type')}</span>
          </TouchButton>
          <p className="hidden truncate text-lg text-muted-foreground lg:block">{t('search.hint')}</p>
        </>
      )}
    </div>
  )
}
