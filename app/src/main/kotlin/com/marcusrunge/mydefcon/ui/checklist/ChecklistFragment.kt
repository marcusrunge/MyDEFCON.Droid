package com.marcusrunge.mydefcon.ui.checklist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.marcusrunge.mydefcon.databinding.FragmentChecklistBinding
import com.marcusrunge.mydefcon.services.ForegroundSocketService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChecklistFragment : Fragment() {
    private var _binding: FragmentChecklistBinding? = null
    private lateinit var viewModel: ChecklistViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this)[ChecklistViewModel::class.java]

        _binding = FragmentChecklistBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        lifecycle.addObserver(viewModel)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(context, ForegroundSocketService::class.java)
        context?.bindService(serviceIntent, viewModel, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        context?.unbindService(viewModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycle.removeObserver(viewModel)
        _binding = null
    }
}