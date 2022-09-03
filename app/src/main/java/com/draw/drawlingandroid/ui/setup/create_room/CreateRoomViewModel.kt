package com.draw.drawlingandroid.ui.setup.create_room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.draw.drawlingandroid.data.remote.ws.Room
import com.draw.drawlingandroid.data.remote.ws.WordList
import com.draw.drawlingandroid.domain.repositories.SetupRepository
import com.draw.drawlingandroid.util.Constants.MAX_ROOM_NAME_LENGTH
import com.draw.drawlingandroid.util.Constants.MIN_ROOM_NAME_LENGTH
import com.draw.drawlingandroid.util.DispatcherProvider
import com.draw.drawlingandroid.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRoomViewModel @Inject constructor(
    private val repository: SetupRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    sealed class SetupEvent {
        object InputEmptyError : SetupEvent()
        object InputTooShortError : SetupEvent()
        object InputTooLongError : SetupEvent()

        data class CreateRoomEvent(val room: Room) : SetupEvent()
        data class CreateRoomErrorEvent(val error: String) : SetupEvent()

        data class JoinRoomEvent(val roomName: String) : SetupEvent()
        data class JoinRoomErrorEvent(val error: String) : SetupEvent()
    }

    private val _setupEvent = MutableSharedFlow<SetupEvent>()
    val setupEvent: SharedFlow<SetupEvent> = _setupEvent

    private val _wordLists = MutableStateFlow(listOf(WordList.DEFAULT_WORDLIST))
    val wordLists: StateFlow<List<WordList>> = _wordLists

    init {
        viewModelScope.launch {
            val response = repository.getAvailableWordList()
            if (response is Resource.Success && response.data != null) _wordLists.emit(response.data)
        }
    }

    fun createRoom(roomName: String, maxPlayers: Int, wordListUiValue: String) {
        viewModelScope.launch(dispatchers.main) {
            val trimmedRoomName = roomName.trim()
            when {
                trimmedRoomName.isEmpty() -> {
                    _setupEvent.emit(SetupEvent.InputEmptyError)
                }
                trimmedRoomName.length < MIN_ROOM_NAME_LENGTH -> {
                    _setupEvent.emit(SetupEvent.InputTooShortError)
                }
                trimmedRoomName.length > MAX_ROOM_NAME_LENGTH -> {
                    _setupEvent.emit(SetupEvent.InputTooLongError)
                }
                else -> {
                    val room = Room(
                        name = trimmedRoomName,
                        maxPlayers = maxPlayers,
                        wordList = WordList.fromUiValue(wordListUiValue)
                    )
                    val result = repository.createRoom(room)
                    if (result is Resource.Success) {
                        _setupEvent.emit(SetupEvent.CreateRoomEvent(room))
                    } else {
                        _setupEvent.emit(
                            SetupEvent.CreateRoomErrorEvent(
                                result.message ?: return@launch
                            )
                        )
                    }
                }
            }
        }
    }

    fun joinRoom(username: String, roomName: String) {
        viewModelScope.launch(dispatchers.main) {
            val result = repository.joinRoom(username, roomName)
            if (result is Resource.Success) {
                _setupEvent.emit(SetupEvent.JoinRoomEvent(roomName))
            } else {
                _setupEvent.emit(SetupEvent.JoinRoomErrorEvent(result.message ?: return@launch))
            }
        }
    }
}