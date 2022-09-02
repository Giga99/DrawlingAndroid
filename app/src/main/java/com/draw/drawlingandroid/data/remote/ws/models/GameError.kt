package com.draw.drawlingandroid.data.remote.ws.models

import com.draw.drawlingandroid.util.Constants.TYPE_GAME_ERROR

data class GameError(
    val errorType: ErrorType
) : BaseModel(type = TYPE_GAME_ERROR) {

    enum class ErrorType {
        ERROR_ROOM_NOT_FOUND
    }
}
