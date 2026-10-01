import { ChannelsScreen } from '@/components/store/channels-screen'

export default async function ChannelsPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params
  return <ChannelsScreen appId={id} />
}
