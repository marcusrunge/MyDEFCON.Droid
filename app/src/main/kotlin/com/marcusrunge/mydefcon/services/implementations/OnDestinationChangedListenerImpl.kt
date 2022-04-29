package com.marcusrunge.mydefcon.services.implementations

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination
import com.marcusrunge.mydefcon.services.interfaces.OnDestinationChangedService

internal class OnDestinationChangedListenerImpl(private val service: OnDestinationChangedService) :
    OnDestinationChangedListener {
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        service.onDestinationChanged(destination.id)
    }
}