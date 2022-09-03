package com.draw.drawlingandroid.ui.setup.select_room

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.draw.drawlingandroid.R
import com.draw.drawlingandroid.databinding.FragmentSelectRoomBinding
import com.draw.drawlingandroid.ui.adapters.RoomAdapter
import com.draw.drawlingandroid.util.Constants.SEARCH_DELAY
import com.draw.drawlingandroid.util.navigateSafely
import com.draw.drawlingandroid.util.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SelectRoomFragment : Fragment(R.layout.fragment_select_room) {

    private var _binding: FragmentSelectRoomBinding? = null
    private val binding: FragmentSelectRoomBinding
        get() = _binding!!

    private val viewModel: SelectRoomViewModel by viewModels()

    private val args: SelectRoomFragmentArgs by navArgs()

    @Inject
    lateinit var roomAdapter: RoomAdapter

    private var updateRoomsJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSelectRoomBinding.bind(view)
        setupRecyclerView()
        subscribeToObservers()
        listenToEvents()

        viewModel.getRooms("")

        var searchJob: Job? = null
        binding.etRoomName.addTextChangedListener {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(SEARCH_DELAY)
                viewModel.getRooms(it.toString())
            }
        }

        binding.ibReload.setOnClickListener {
            binding.roomsProgressBar.isVisible = true
            binding.ivNoRoomsFound.isVisible = false
            binding.tvNoRoomsFound.isVisible = false
            viewModel.getRooms(binding.etRoomName.text.toString())
        }

        binding.btnCreateRoom.setOnClickListener {
            findNavController().navigateSafely(
                R.id.action_selectRoomFragment_to_createRoomFragment,
                Bundle().apply { putString("username", args.username) }
            )
        }

        roomAdapter.setOnRoomClickListener {
            viewModel.joinRoom(args.username, it.name)
        }
    }

    private fun listenToEvents() = lifecycleScope.launchWhenStarted {
        viewModel.setupEvent.collect { event ->
            when (event) {
                is SelectRoomViewModel.SetupEvent.JoinRoomEvent -> {
                    findNavController().navigateSafely(
                        R.id.action_selectRoomFragment_to_drawingActivity,
                        args = Bundle().apply {
                            putString("username", args.username)
                            putString("roomName", event.roomName)
                        }
                    )
                }
                is SelectRoomViewModel.SetupEvent.JoinRoomErrorEvent -> {
                    snackbar(event.error)
                }
                is SelectRoomViewModel.SetupEvent.GetRoomErrorEvent -> {
                    binding.apply {
                        roomsProgressBar.isVisible = false
                        tvNoRoomsFound.isVisible = false
                        ivNoRoomsFound.isVisible = false
                    }
                    snackbar(event.error)
                }
                else -> Unit
            }
        }
    }

    private fun subscribeToObservers() = lifecycleScope.launchWhenStarted {
        viewModel.rooms.collect { event ->
            when (event) {
                is SelectRoomViewModel.SetupEvent.GetRoomLoadingEvent -> {
                    binding.roomsProgressBar.isVisible = true
                }
                is SelectRoomViewModel.SetupEvent.GetRoomEvent -> {
                    binding.roomsProgressBar.isVisible = false
                    val isRoomsEmpty = event.rooms.isEmpty()
                    binding.tvNoRoomsFound.isVisible = isRoomsEmpty
                    binding.ivNoRoomsFound.isVisible = isRoomsEmpty

                    updateRoomsJob?.cancel()
                    updateRoomsJob = lifecycleScope.launch {
                        roomAdapter.updateDataset(event.rooms)
                    }
                }
                else -> Unit
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvRooms.apply {
            adapter = roomAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
