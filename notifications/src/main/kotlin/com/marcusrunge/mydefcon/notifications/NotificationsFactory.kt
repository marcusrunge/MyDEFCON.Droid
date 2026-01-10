package com.marcusrunge.mydefcon.notifications

import android.content.Context
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.notifications.implementations.NotificationsImpl
import com.marcusrunge.mydefcon.notifications.interfaces.Notifications

/**
 * A factory for creating a singleton instance of the [Notifications] interface.
 *
 * This factory is responsible for providing a single point of access to the notifications
 * component of the application, ensuring that only one instance of the component is active
 * at any given time.
 */
interface NotificationsFactory {
    /**
     * Creates or retrieves the singleton instance of [Notifications].
     *
     * On the first invocation, this method initializes the [Notifications] component.
     * Subsequent calls will return the already created instance.
     *
     * @param context The Android [Context]. While nullable, it is required for the initial
     *                creation of the [Notifications] instance.
     * @param core The [Core] instance, which is a dependency for the notifications component.
     *             While nullable, it is required for the initial creation.
     * @return The singleton [Notifications] instance.
     */
    fun create(context: Context?, core: Core?): Notifications
}

/**
 * The default implementation of [NotificationsFactory].
 *
 * This object provides a thread-safe, lazily initialized singleton instance of [Notifications].
 * It is designed to be a simple and efficient way to access the notifications feature.
 */
object NotificationsFactoryImpl : NotificationsFactory {
    @Volatile
    private var notifications: Notifications? = null

    /**
     * Creates or retrieves the singleton instance of [Notifications] in a thread-safe manner.
     *
     * This method uses a double-checked locking pattern to ensure that the [Notifications]
     * instance is created only once. When called for the first time, it will instantiate
     * [NotificationsImpl]. All subsequent calls will return the cached instance.
     *
     * @param context The Android [Context]. Passed to the [NotificationsImpl] constructor.
     * @param core The [Core] instance. Passed to the [NotificationsImpl] constructor.
     * @return The singleton [Notifications] instance.
     */
    override fun create(context: Context?, core: Core?): Notifications {
        return notifications ?: synchronized(this) {
            notifications ?: NotificationsImpl(context, core).also { notifications = it }
        }
    }
}