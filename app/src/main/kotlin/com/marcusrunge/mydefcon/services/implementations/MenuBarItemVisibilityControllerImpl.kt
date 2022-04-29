package com.marcusrunge.mydefcon.services.implementations

import com.marcusrunge.mydefcon.services.interfaces.MenuBarItemVisibilityController

internal class MenuBarItemVisibilityControllerImpl() : MenuBarItemVisibilityController {
    override fun registerItemId(id: Int) {
        TODO("Not yet implemented")
    }

    override fun removeItemId(id: Int) {
        TODO("Not yet implemented")
    }

    internal companion object {
        var controller: MenuBarItemVisibilityController? = null
        fun create(): MenuBarItemVisibilityController = when {
            controller != null -> controller!!
            else -> {
                controller = MenuBarItemVisibilityControllerImpl()
                controller!!
            }
        }
    }
}