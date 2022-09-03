package com.draw.drawlingandroid.ui.setup.create_room

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.draw.drawlingandroid.R
import com.draw.drawlingandroid.data.remote.ws.Room
import com.draw.drawlingandroid.databinding.FragmentCreateRoomBinding
import com.draw.drawlingandroid.util.Constants
import com.draw.drawlingandroid.util.hideKeyboard
import com.draw.drawlingandroid.util.navigateSafely
import com.draw.drawlingandroid.util.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateRoomFragment : Fragment(R.layout.fragment_create_room) {

    private var _binding: FragmentCreateRoomBinding? = null
    private val binding: FragmentCreateRoomBinding
        get() = _binding!!

    private val viewModel: CreateRoomViewModel by viewModels()

    private val args: CreateRoomFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateRoomBinding.bind(view)
        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setupRoomSizeSpinner()
        listenToEvents()

        binding.btnCreateRoom.setOnClickListener {
            binding.createRoomProgressBar.isVisible = true
            viewModel.createRoom(
                Room(
                    binding.etRoomName.text.toString(),
                    binding.tvMaxPersons.text.toString().toInt()
                )
            )
            requireActivity().hideKeyboard(binding.root)
        }
    }

    private fun listenToEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.setupEvent.collect { event ->
                when (event) {
                    is CreateRoomViewModel.SetupEvent.CreateRoomEvent -> {
                        viewModel.joinRoom(args.username, event.room.name)
                    }
                    is CreateRoomViewModel.SetupEvent.InputEmptyError -> {
                        binding.createRoomProgressBar.isVisible = false
                        snackbar(R.string.error_field_empty)
                    }
                    is CreateRoomViewModel.SetupEvent.InputTooShortError -> {
                        binding.createRoomProgressBar.isVisible = false
                        snackbar(
                            getString(
                                R.string.error_room_name_too_short,
                                Constants.MIN_ROOM_NAME_LENGTH
                            )
                        )
                    }
                    is CreateRoomViewModel.SetupEvent.InputTooLongError -> {
                        binding.createRoomProgressBar.isVisible = false
                        snackbar(
                            getString(
                                R.string.error_room_name_too_long,
                                Constants.MAX_ROOM_NAME_LENGTH
                            )
                        )
                    }
                    is CreateRoomViewModel.SetupEvent.CreateRoomErrorEvent -> {
                        binding.createRoomProgressBar.isVisible = false
                        snackbar(event.error)
                    }
                    is CreateRoomViewModel.SetupEvent.JoinRoomEvent -> {
                        binding.createRoomProgressBar.isVisible = false
                        findNavController().navigateSafely(
                            R.id.action_createRoomFragment_to_drawingActivity,
                            args = Bundle().apply {
                                putString("username", args.username)
                                putString("roomName", event.roomName)
                            }
                        )
                    }
                    is CreateRoomViewModel.SetupEvent.JoinRoomErrorEvent -> {
                        binding.createRoomProgressBar.isVisible = false
                        snackbar(event.error)
                    }
                }
            }
        }
    }

    private fun setupRoomSizeSpinner() {
        val roomSizes = resources.getStringArray(R.array.room_size_array)
        val adapter = ArrayAdapter(requireContext(), R.layout.textview_room_size, roomSizes)
        binding.tvMaxPersons.setAdapter(adapter)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
