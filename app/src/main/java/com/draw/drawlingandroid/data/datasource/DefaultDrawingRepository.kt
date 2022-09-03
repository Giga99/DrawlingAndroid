package com.draw.drawlingandroid.data.datasource

import com.draw.drawlingandroid.data.remote.ws.DrawingApi
import com.draw.drawlingandroid.data.remote.ws.models.BaseModel
import com.draw.drawlingandroid.domain.repositories.DrawingRepository
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultDrawingRepository @Inject constructor(
    private val drawingApi: DrawingApi,
) : DrawingRepository {

    override suspend fun observeEvents(): Flow<WebSocket.Event> = drawingApi.observeEvents()

    override suspend fun sendBaseModel(baseModel: BaseModel): Boolean =
        drawingApi.sendBaseModel(baseModel)

    override suspend fun observeBaseModels(): Flow<BaseModel> = drawingApi.observeBaseModels()
}
