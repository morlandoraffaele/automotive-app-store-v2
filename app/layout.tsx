import { Analytics } from '@vercel/analytics/next'
import type { Metadata, Viewport } from 'next'
import { Geist, Geist_Mono, Noto_Sans_Arabic } from 'next/font/google'
import { StoreShell } from '@/components/store/store-shell'
import { StoreProvider } from '@/lib/store/store-provider'
import './globals.css'

const geistSans = Geist({ subsets: ['latin'], variable: '--font-geist-sans' })
const geistMono = Geist_Mono({ subsets: ['latin'], variable: '--font-geist-mono' })
const notoArabic = Noto_Sans_Arabic({ subsets: ['arabic'], variable: '--font-arabic' })

export const metadata: Metadata = {
  title: 'Vehicle App Store',
  description: 'Browse, install, and update apps for your vehicle’s head unit.',
  generator: 'v0.app',
  icons: {
    icon: [
      { url: '/icon-light-32x32.png', media: '(prefers-color-scheme: light)' },
      { url: '/icon-dark-32x32.png', media: '(prefers-color-scheme: dark)' },
      { url: '/icon.svg', type: 'image/svg+xml' },
    ],
    apple: '/apple-icon.png',
  },
}

export const viewport: Viewport = {
  width: 'device-width',
  initialScale: 1,
  userScalable: false,
  themeColor: '#171b22',
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="en" dir="ltr" className={`dark ${geistSans.variable} ${geistMono.variable} ${notoArabic.variable}`} suppressHydrationWarning>
      <body className="font-sans antialiased">
        <StoreProvider>
          <StoreShell>{children}</StoreShell>
        </StoreProvider>
        {process.env.NODE_ENV === 'production' && <Analytics />}
      </body>
    </html>
  )
}
