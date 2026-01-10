package com.marcusrunge.mydefcon.notifications.bases

import android.content.Context
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.notifications.interfaces.HeadsUp
import com.marcusrunge.mydefcon.notifications.interfaces.Notifications
import com.marcusrunge.mydefcon.notifications.interfaces.PopUp

/**
 * An abstract base class providing a skeletal implementation of the [Notifications] interface.
 *
 * This class serves as a foundation for concrete notification manager implementations. It handles
 * the boilerplate for managing dependencies and structuring different notification channels.
 *
 * Subclasses must initialize the `lateinit` properties to provide the concrete
 * notification handlers and initialization logic. Failure to do so will result in an
 * [UninitializedPropertyAccessException] at runtime.
 *
 * @param context The application's [Context]. Although nullable, a non-null instance is
 *                typically required for interacting with the Android notification system.
 *                Subclasses are responsible for handling the potential nullity.
 * @param core The application's [Core] component. Although nullable, a non-null instance
 *             is often needed to access core business logic and data. Subclasses are
 *             responsible for handling the potential nullity.
 */
internal abstract class NotificationsBase(
    internal val context: Context?,
    internal val core: Core?
) : Notifications {

    /**
     * Backing property for the [headsUp] notification handler.
     * Must be initialized in the subclass constructor or `init` block.
     */
    protected lateinit var _headsUp: HeadsUp

    /**
     * Backing property for the [popUp] notification handler.
     * Must be initialized in the subclass constructor or `init` block.
     */
    protected lateinit var _popUp: PopUp

    /**
     * A lambda containing the deferred initialization logic for the notification component.
     * Must be assigned in the subclass constructor or `init` block.
     */
    protected lateinit var _onInitialize: (() -> Unit)

    /**
     * The handler for heads-up notifications.
     * @see HeadsUp
     */
    override val headsUp: HeadsUp
        get() = _headsUp

    /**
     * The handler for pop-up notifications.
     * @see PopUp
     */
    override val popUp: PopUp
        get() = _popUp

    /**
     * Triggers the initialization logic for the notification component.
     * This method invokes the [_onInitialize] lambda that must be provided by the subclass.
     * It should be called once the component is ready to be set up.
     */
    override fun initialize() {
        _onInitialize.invoke()
    }
}