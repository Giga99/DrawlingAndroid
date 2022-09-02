package com.draw.drawlingandroid.data.remote.ws.models

import com.draw.drawlingandroid.util.Constants.TYPE_GAME_STATE

data class GameState(
    val drawingPlayer: String,
    val word: String
) : BaseModel(type = TYPE_GAME_STATE)
