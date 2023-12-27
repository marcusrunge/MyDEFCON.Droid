package com.marcusrunge.mydefcon.notifications.interfaces

interface HeadsUp {
    fun showBasicUrgent(smallIcon: Int?, textTitle: String?, textContent: String?, ongoing: Boolean)
    fun showBasicHigh(smallIcon: Int?, textTitle: String?, textContent: String?, ongoing: Boolean)
    fun showBasicMedium(smallIcon: Int?, textTitle: String?, textContent: String?, ongoing: Boolean)
    fun showBasicLow(smallIcon: Int?, textTitle: String?, textContent: String?, ongoing: Boolean)
    fun showExpandedUrgent(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    )

    fun showExpandedHigh(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    )

    fun showExpandedMedium(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    )

    fun showExpandedLow(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    )
}