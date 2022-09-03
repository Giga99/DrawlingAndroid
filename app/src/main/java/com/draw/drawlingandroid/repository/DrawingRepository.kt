package com.draw.drawlingandroid.repository

import com.draw.drawlingandroid.data.remote.ws.models.BaseModel
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.flow.Flow

interface DrawingRepository {

    suspend fun observeEvents(): Flow<WebSocket.Event>

    suspend fun sendBaseModel(baseModel: BaseModel): Boolean

    suspend fun observeBaseModels(): Flow<BaseModel>
}
