package com.draw.drawlingandroid.data.remote.ws.models

import com.draw.drawlingandroid.util.Constants.TYPE_CHAT_MESSAGE

data class ChatMessage(
    val from: String,
    val roomName: String,
    val message: String,
    val timestamp: Long
) : BaseModel(type = TYPE_CHAT_MESSAGE)
