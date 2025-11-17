package com.marcusrunge.mydefcon.notifications.interfaces

interface Notifications {
    /**
     * Gets the heads-up instance.
     * @see HeadsUp
     */
    val headsUp: HeadsUp

    /**
     * Gets the pop-up instance.
     * @see PopUp
     */
    val popUp: PopUp

    /**
     * Initializes the notifications.
     */
    fun initialize()
}