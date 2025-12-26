package com.marcusrunge.mydefcon.ui.settings

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.marcusrunge.mydefcon.databinding.PreferenceFollowersBinding

class FollowersPreference@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr){
    private var _binding: PreferenceFollowersBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FollowersPreferenceViewModel
    private var lifecycleOwner: LifecycleOwner? = null

    fun initializeViewModel(viewModel: FollowersPreferenceViewModel, lifecycleOwner: LifecycleOwner) {
        this.viewModel = viewModel
        this.lifecycleOwner = lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(viewModel)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        _binding = PreferenceFollowersBinding.bind(holder.itemView)
        if (::viewModel.isInitialized && lifecycleOwner != null) {
            binding.viewmodel = viewModel
            binding.lifecycleOwner = lifecycleOwner
        } else {
            Log.w("GroupPreference", "ViewModel or LifecycleOwner not initialized for binding.")
        }
    }
}