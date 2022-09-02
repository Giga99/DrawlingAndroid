package com.draw.drawlingandroid.data.remote.ws.models

import com.draw.drawlingandroid.data.remote.ws.Room
import com.draw.drawlingandroid.util.Constants.TYPE_PHASE_CHANGE

data class PhaseChange(
    var phase: Room.Phase?,
    var time: Long,
    val drawingPlayer: String? = null
) : BaseModel(type = TYPE_PHASE_CHANGE)
