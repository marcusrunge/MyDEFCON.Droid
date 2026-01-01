package com.marcusrunge.mydefcon.ui.settings

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.google.firebase.installations.FirebaseInstallations
import com.marcusrunge.mydefcon.R

/**
 * A custom [Preference] that displays the Firebase Installation ID.
 *
 * This preference fetches the unique identifier for the app installation from Firebase
 * and displays it. It handles loading and error states.
 */
class InstallationIdPreference @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : Preference(context, attrs) {

    private var installationId: String? = null
    private var errorOccurred = false

    init {
        layoutResource = R.layout.preference_information
        fetchInstallationId()
    }

    /**
     * Fetches the Firebase Installation ID asynchronously.
     *
     * On successful completion, it stores the ID and notifies the preference to update its view.
     * In case of failure, it logs the error and sets a flag to display an error message.
     */
    private fun fetchInstallationId() {
        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                installationId = task.result
            } else {
                errorOccurred = true
                Log.e("InstallationIdPreference", "Error getting installation ID", task.exception)
            }
            // Notify the preference to redraw itself with the new data.
            notifyChanged()
        }
    }

    /**
     * Binds the view for this preference and displays the installation ID or status.
     *
     * @param holder The [PreferenceViewHolder] for this preference.
     */
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val installationIdTextView = holder.findViewById(R.id.installationIdTextView) as? TextView
        installationIdTextView?.text = when {
            installationId != null -> installationId
            errorOccurred -> context.getString(R.string.could_not_retrieve_installation_id)
            else -> context.getString(R.string.loading_installation_id)
        }
    }
}
