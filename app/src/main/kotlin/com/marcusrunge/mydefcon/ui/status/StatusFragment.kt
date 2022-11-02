package com.marcusrunge.mydefcon.ui.status

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.communication.interfaces.Communication
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.databinding.FragmentStatusBinding
import com.marcusrunge.mydefcon.receiver.DefconStatusReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StatusViewModel

    /*@Inject
    lateinit var core: Core

    @Inject
    lateinit var communication: Communication*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[StatusViewModel::class.java]
        /*val statusObserver = Observer<Int> { button ->
            val status = when (button) {
                R.id.radio_defcon1 -> 1
                R.id.radio_defcon2 -> 2
                R.id.radio_defcon3 -> 3
                R.id.radio_defcon4 -> 4
                else -> 5
            }
                Intent(context, DefconStatusReceiver::class.java).also { intent ->
                    intent.action = "com.marcusrunge.mydefcon.DEFCONSTATUS_SELECTED"
                    intent.putExtra("data", status)
                    intent.putExtra("source", StatusFragment::class.java.canonicalName)
                    context?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intent) }
                }
                lifecycleScope.launch { communication.network.client.sendDefconStatus(status) }
        }
        viewModel.checkedRadioButtonId.observe(viewLifecycleOwner, statusObserver)*/
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        lifecycle.addObserver(viewModel)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycle.removeObserver(viewModel)
        _binding = null
    }
}