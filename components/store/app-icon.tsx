import {
  BookAudio,
  CalendarDays,
  CloudSun,
  Gamepad2,
  MessageSquare,
  Music,
  Navigation,
  PlugZap,
  Podcast,
  Radio,
  SquareParking,
  Ticket,
  type LucideIcon,
} from 'lucide-react'
import type { AppIconName } from '@/lib/store/types'
import { cn } from '@/lib/utils'

const ICONS: Record<AppIconName, LucideIcon> = {
  navigation: Navigation,
  music: Music,
  podcast: Podcast,
  charging: PlugZap,
  parking: SquareParking,
  weather: CloudSun,
  audiobook: BookAudio,
  messaging: MessageSquare,
  calendar: CalendarDays,
  radio: Radio,
  games: Gamepad2,
  tolls: Ticket,
}

const SIZES = {
  md: 'size-20 rounded-2xl [&_svg]:size-10',
  lg: 'size-24 rounded-3xl [&_svg]:size-12',
  xl: 'size-32 rounded-[2rem] [&_svg]:size-16',
}

export function AppIcon({
  icon,
  color,
  size = 'md',
  className,
}: {
  icon: AppIconName
  color: string
  size?: keyof typeof SIZES
  className?: string
}) {
  const Icon = ICONS[icon]
  return (
    <div
      aria-hidden="true"
      className={cn('flex shrink-0 items-center justify-center text-white', SIZES[size], className)}
      style={{ backgroundColor: color }}
    >
      <Icon strokeWidth={2.25} />
    </div>
  )
}
