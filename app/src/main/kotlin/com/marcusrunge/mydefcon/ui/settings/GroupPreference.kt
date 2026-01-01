package com.marcusrunge.mydefcon.ui.settings

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.marcusrunge.mydefcon.databinding.PreferenceGroupBinding

/**
 * A custom [Preference] for managing group settings.
 *
 * This preference provides a custom view with data binding to a [GroupPreferenceViewModel].
 * It allows for a more complex UI and logic within the settings screen.
 */
class GroupPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {

    private var _binding: PreferenceGroupBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GroupPreferenceViewModel
    private var lifecycleOwner: LifecycleOwner? = null

    /**
     * Initializes the ViewModel and LifecycleOwner for this preference.
     *
     * This method must be called from the fragment that hosts this preference to
     * enable data binding and communication with the ViewModel.
     *
     * @param viewModel The [GroupPreferenceViewModel] instance.
     * @param lifecycleOwner The [LifecycleOwner] of the hosting fragment.
     */
    fun initializeViewModel(viewModel: GroupPreferenceViewModel, lifecycleOwner: LifecycleOwner) {
        this.viewModel = viewModel
        this.lifecycleOwner = lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(viewModel)
    }

    /**
     * Binds the view for this preference and sets up data binding.
     *
     * @param holder The [PreferenceViewHolder] for this preference.
     */
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        _binding = PreferenceGroupBinding.bind(holder.itemView)
        if (::viewModel.isInitialized && lifecycleOwner != null) {
            binding.viewmodel = viewModel
            binding.lifecycleOwner = lifecycleOwner
        } else {
            Log.w("GroupPreference", "ViewModel or LifecycleOwner not initialized for binding.")
        }
    }
}
