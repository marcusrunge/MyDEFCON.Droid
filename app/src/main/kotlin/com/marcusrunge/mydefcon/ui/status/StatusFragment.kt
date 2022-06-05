package com.marcusrunge.mydefcon.ui.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.communication.interfaces.Communication
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.databinding.FragmentStatusBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var core: Core
    @Inject
    lateinit var communication: Communication

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel =
            ViewModelProvider(this)[StatusViewModel::class.java]
        val statusObserver = Observer<Int> { status ->
            when (status) {
                R.id.radio_defcon1 -> core.preferences.status = 1
                R.id.radio_defcon2 -> core.preferences.status = 2
                R.id.radio_defcon3 -> core.preferences.status = 3
                R.id.radio_defcon4 -> core.preferences.status = 4
                R.id.radio_defcon5 -> core.preferences.status = 5
            }
            lifecycleScope.launch { communication.network.sender.sendDefconStatus(core.preferences.status) }
        }
        viewModel.checkedRadioButtonId.observe(viewLifecycleOwner, statusObserver)
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}