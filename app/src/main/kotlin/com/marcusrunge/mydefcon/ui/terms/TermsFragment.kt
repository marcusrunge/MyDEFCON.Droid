package com.marcusrunge.mydefcon.ui.terms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.marcusrunge.mydefcon.databinding.TermsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * A [Fragment] that displays the terms of service.
 *
 * This fragment is responsible for inflating the layout, setting up data binding with its
 * corresponding [TermsViewModel], and managing the view's lifecycle.
 */
@AndroidEntryPoint
class TermsFragment : Fragment() {

    private var _binding: TermsFragmentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val viewModel by viewModels<TermsViewModel>()

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * This is where the layout is inflated.
     *
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TermsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view.
     *
     * This method binds the ViewModel and the lifecycle owner to the layout.
     *
     * @param view The View returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    /**
     * Called when the view previously created by onCreateView() has been detached from the fragment.
     *
     * This is where the binding is cleaned up to prevent memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
