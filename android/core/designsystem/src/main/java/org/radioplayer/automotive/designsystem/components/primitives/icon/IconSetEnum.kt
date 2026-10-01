package org.radioplayer.automotive.designsystem.components.primitives.icon

import androidx.annotation.DrawableRes
import org.radioplayer.automotive.designsystem.R

enum class IconSetEnum(@param:DrawableRes val resId: Int) {
    // Android
    Droid(R.drawable.droid),
    AndroidCell(R.drawable.android_cell),
    GlobeLocationPin(R.drawable.globe_location_pin),

    // Action
    PlayArrow(R.drawable.play_arrow),
    Pause(R.drawable.pause),
    Stop(R.drawable.stop),
    FavoriteBorder(R.drawable.favorite_border),
    Favorite(R.drawable.favorite),
    Search(R.drawable.search),

    // Navigation
    ExpandMore(R.drawable.expand_more),
    ExpandLess(R.drawable.expand_less),
    ArrowBack(R.drawable.arrow_back),
    MoreVert(R.drawable.more_vert),
    Close(R.drawable.close),
    ChevronRight(R.drawable.chevron_right),

    // File
    CloudOff(R.drawable.cloud_off),

    // Alert
    CarAlert(R.drawable.car_alert),

    // AV
    Mic(R.drawable.mic),
    Replay10(R.drawable.replay_10),
    Forward10(R.drawable.forward_10),

    ThumbUp(R.drawable.thumb_up),
    Micro(R.drawable.micro),

    //Misc
    Radio(R.drawable.radio),
    SortByAlpha(R.drawable.sort_by_alpha)
}