package com.marcusrunge.mydefcon.ui.settings

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

/**
 * A custom [Preference] that is styled as a button.
 *
 * This preference uses its `title` attribute as the button's text.
 * It can be used to trigger actions from the preference screen.
 *
 * @param context The Context this preference is running in, through which it can
 * access the current theme, resources, etc.
 * @param attrs The attributes of the XML tag that is inflating the preference.
 * @param defStyleAttr An attribute in the current theme that contains a reference
 * to a style resource that supplies default values for the view.
 */
class ButtonPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {

    /**
     * Binds the created View to the data for this preference.
     *
     * This is called when the Preference is being displayed.
     *
     * @param holder The ViewHolder that contains the views to be bound.
     */
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val button = holder.itemView as? Button
        button?.text = title
    }
}
