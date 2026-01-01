package com.marcusrunge.mydefcon.ui.checklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.marcusrunge.mydefcon.databinding.FragmentChecklistBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * A [Fragment] that displays the checklist screen.
 *
 * This fragment is responsible for inflating the layout, setting up the [ChecklistViewModel],
 * and managing the view's lifecycle. It uses data binding to connect the layout with the
 * ViewModel.
 */
@AndroidEntryPoint
class ChecklistFragment : Fragment() {

    private lateinit var viewModel: ChecklistViewModel
    private var _binding: FragmentChecklistBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /**
     * Inflates the fragment's view, initializes the [ChecklistViewModel], and sets up data binding.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this)[ChecklistViewModel::class.java]

        _binding = FragmentChecklistBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        lifecycle.addObserver(viewModel)
        return binding.root
    }

    /**
     * Cleans up resources when the view is destroyed.
     *
     * This method removes the lifecycle observer and nullifies the view binding to prevent
     * memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        lifecycle.removeObserver(viewModel)
        _binding = null
    }
}
