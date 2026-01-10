package com.marcusrunge.mydefcon.notifications.implementations

import com.marcusrunge.mydefcon.notifications.bases.NotificationsBase
import com.marcusrunge.mydefcon.notifications.interfaces.PopUp

/**
 * An implementation of the [PopUp] interface.
 *
 * This class is responsible for handling pop-up notifications within the application.
 * It follows a singleton pattern to ensure that only one instance of the pop-up handler exists at any given time.
 * The singleton instance is created and retrieved through the companion object's `create` method.
 *
 * @param notificationsBase The base class for notifications, which provides the necessary context and functionality.
 */
internal class PopUpImpl(private val notificationsBase: NotificationsBase) : PopUp {
    // TODO: Implement the methods of the PopUp interface to display pop-up notifications.

    companion object {
        /**
         * The singleton instance of the [PopUp] interface.
         * The `@Volatile` annotation ensures that writes to this property are immediately visible to other threads.
         */
        @Volatile
        private var popUp: PopUp? = null

        /**
         * Creates or retrieves the singleton instance of the [PopUp] interface.
         *
         * This function uses a thread-safe, double-checked locking mechanism to ensure that only one instance
         * of [PopUpImpl] is created. If an instance already exists, it is returned; otherwise, a new instance
         * is created and stored.
         *
         * @param notificationsBase The base class for notifications, required for creating the instance.
         * @return The singleton [PopUp] instance.
         */
        fun create(notificationsBase: NotificationsBase): PopUp =
            popUp ?: synchronized(this) {
                popUp ?: PopUpImpl(notificationsBase).also { popUp = it }
            }
    }
}
