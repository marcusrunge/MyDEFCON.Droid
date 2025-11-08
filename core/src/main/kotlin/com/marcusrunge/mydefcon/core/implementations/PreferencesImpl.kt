package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.Preferences

internal class PreferencesImpl(private val coreBase: CoreBase) : Preferences {

    override var status: Int
        get() = coreBase.preferencesOperations.getInt("status")
        set(value) {
            coreBase.preferencesOperations.setInt("status", value)
        }
    override var fcmRegistrationToken: String
        get() = coreBase.preferencesOperations.getString("fcmRegistrationToken")
        set(value) {
            coreBase.preferencesOperations.setString("fcmRegistrationToken", value)
        }

    override var createdDefconGroupId: String
        get() = coreBase.preferencesOperations.getString("createdDefconGroupId")
        set(value) {
            coreBase.preferencesOperations.setString("createdDefconGroupId", value)
        }

    override var joinedDefconGroupId: String
        get() = coreBase.preferencesOperations.getString("joinedDefconGroupId")
        set(value) {
            coreBase.preferencesOperations.setString("joinedDefconGroupId", value)
        }

    internal companion object {
        private var preferences: Preferences? = null
        fun create(coreBase: CoreBase): Preferences = when {
            preferences != null -> preferences!!
            else -> {
                preferences = PreferencesImpl(coreBase)
                preferences!!
            }
        }
    }
}