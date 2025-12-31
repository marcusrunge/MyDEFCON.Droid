package com.marcusrunge.mydefcon.ui.settings

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.google.firebase.installations.FirebaseInstallations
import com.marcusrunge.mydefcon.R

class InstallationIdPreference(context: Context, attrs: AttributeSet?) : Preference(context, attrs) {

    private var installationId: String? = null
    private var errorOccurred = false

    init {
        layoutResource = R.layout.preference_information
        fetchInstallationId()
    }

    private fun fetchInstallationId() {
        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                installationId = task.result
            } else {
                errorOccurred = true
                Log.e("InstallationIdPreference", "Error getting installation ID", task.exception)
            }
            // We need to redraw the preference to show the new value
            notifyChanged()
        }
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val installationIdTextView = holder.findViewById(R.id.installationIdTextView) as? TextView
        if (installationIdTextView != null) {
            when {
                installationId != null -> installationIdTextView.text = installationId
                errorOccurred -> installationIdTextView.text = context.getString(R.string.could_not_retrieve_installation_id)
                else -> installationIdTextView.text = context.getString(R.string.loading_installation_id)
            }
        }
    }
}
