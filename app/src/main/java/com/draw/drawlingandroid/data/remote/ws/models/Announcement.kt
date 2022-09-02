package com.draw.drawlingandroid.data.remote.ws.models

import com.draw.drawlingandroid.util.Constants.TYPE_ANNOUNCEMENT

data class Announcement(
    val message: String,
    val timestamp: Long,
    val announcementType: AnnouncementType
) : BaseModel(type = TYPE_ANNOUNCEMENT) {

    enum class AnnouncementType {
        TYPE_PLAYER_GUESSED_WORD,
        TYPE_PLAYER_JOINED,
        TYPE_PLAYER_LEFT,
        TYPE_EVERYBODY_GUESSED_IT
    }
}
