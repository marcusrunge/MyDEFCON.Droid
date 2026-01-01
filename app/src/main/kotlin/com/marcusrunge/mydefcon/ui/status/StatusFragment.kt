package com.marcusrunge.mydefcon.ui.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.marcusrunge.mydefcon.databinding.FragmentStatusBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * A [Fragment] that displays the current DEFCON status.
 *
 * This fragment is responsible for inflating the layout, setting up the [StatusViewModel],
 * and managing the view's lifecycle. It uses data binding to connect the layout with the
 * ViewModel.
 */
@AndroidEntryPoint
class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: StatusViewModel by viewModels()

    /**
     * Inflates the fragment's view, initializes the [StatusViewModel], and sets up data binding.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    /**
     * Cleans up resources when the view is destroyed.
     *
     * This method nullifies the view binding to prevent memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
