package com.marcusrunge.mydefcon.ui.settings

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.marcusrunge.mydefcon.databinding.GroupPreferenceBinding

class GroupPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {
    private var _binding: GroupPreferenceBinding? = null // **Adjust binding type**
    private val binding get() = _binding!!
    private lateinit var viewModel: GroupPreferenceViewModel
    private var lifecycleOwner: LifecycleOwner? = null // To observe ViewModel changes

    // Method to set the ViewModel and LifecycleOwner from the Fragment
    fun initializeViewModel(viewModel: GroupPreferenceViewModel, lifecycleOwner: LifecycleOwner) {
        this.viewModel = viewModel
        this.lifecycleOwner = lifecycleOwner // Store the lifecycle owner
        // You might want to observe LiveData from the ViewModel here
        // e.g., viewModel.someLiveData.observe(lifecycleOwner, Observer { ... })
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        _binding = GroupPreferenceBinding.bind(holder.itemView)
        if (::viewModel.isInitialized && lifecycleOwner != null) {
            binding.viewmodel = viewModel
            binding.lifecycleOwner = lifecycleOwner // Use the passed lifecycleOwner
            // If your ViewModel is a LifecycleObserver, you might add it like this:
            // lifecycleOwner?.lifecycle?.addObserver(viewModel)
        } else {
            Log.w("GroupPreference", "ViewModel or LifecycleOwner not initialized for binding.")
        }
    }
}