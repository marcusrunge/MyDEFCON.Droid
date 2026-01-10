package com.marcusrunge.mydefcon.notifications.interfaces

/**
 * An interface that serves as the primary entry point for the notifications module.
 *
 * It provides access to different types of notification channels, such as heads-up and
 * pop-up notifications. This abstraction allows for a clear separation of concerns, making it
 * easier to manage and extend the app's notification capabilities.
 */
interface Notifications {
    /**
     * Provides access to the [HeadsUp] notification handler.
     *
     * The [HeadsUp] handler is responsible for displaying urgent, high-priority notifications
     * that appear as a floating window on the screen.
     *
     * @see HeadsUp
     */
    val headsUp: HeadsUp

    /**
     * Provides access to the [PopUp] notification handler.
     *
     * The [PopUp] handler is responsible for displaying less intrusive notifications,
     * such as toasts or snackbars.
     *
     * @see PopUp
     */
    val popUp: PopUp

    /**
     * Initializes the notification component.
     *
     * This method should be called once, typically during application startup, to perform any
     * necessary setup for the notification channels and handlers. Calling this method ensures
     * that the notification system is ready to be used.
     */
    fun initialize()
}
