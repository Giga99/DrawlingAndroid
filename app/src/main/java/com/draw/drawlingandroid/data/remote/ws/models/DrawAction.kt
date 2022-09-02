package com.draw.drawlingandroid.data.remote.ws.models

import com.draw.drawlingandroid.util.Constants.TYPE_DRAW_ACTION

data class DrawAction(
    val action: String
) : BaseModel(type = TYPE_DRAW_ACTION) {

    companion object {
        const val ACTION_UNDO = "ACTION_UNDO"
    }
}
