package com.marcusrunge.mydefcon.services.interfaces

interface MenuBarItemVisibilityController {
    /**
     * Registers an item id with the controller.
     */
    fun registerItemId(id: Int)

    /**
     * Removes an item id from the controller.
     */
    fun removeItemId(id: Int)
}