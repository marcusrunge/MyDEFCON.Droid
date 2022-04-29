package com.marcusrunge.mydefcon.services.implementations

import com.marcusrunge.mydefcon.services.interfaces.MenuBarItemVisibilityController
import com.marcusrunge.mydefcon.services.interfaces.OnDestinationChangedService

internal class OnDestinationChangedServiceImpl(controller: MenuBarItemVisibilityController) :
    OnDestinationChangedService {

    override fun onDestinationChanged(destinationId: Int) {
        TODO("Not yet implemented")
    }

    internal companion object {
        var service: OnDestinationChangedService? = null
        fun create(controller: MenuBarItemVisibilityController): OnDestinationChangedService =
            when {
                service != null -> service!!
                else -> {
                    service = OnDestinationChangedServiceImpl(controller)
                    service!!
                }
            }
    }
}