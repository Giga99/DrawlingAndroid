package com.draw.drawlingandroid.ui.setup.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.draw.drawlingandroid.databinding.FragmentSelectRoomBinding

class SelectRoomFragment : Fragment() {

    private var _binding: FragmentSelectRoomBinding? = null
    private val binding: FragmentSelectRoomBinding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSelectRoomBinding.bind(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
