package com.draw.drawlingandroid.domain.repositories

import com.draw.drawlingandroid.data.remote.ws.Room
import com.draw.drawlingandroid.util.Resource

interface SetupRepository {

    suspend fun createRoom(room: Room): Resource<Unit>

    suspend fun getRooms(searchQuery: String): Resource<List<Room>>

    suspend fun joinRoom(username: String, roomName: String): Resource<Unit>
}
