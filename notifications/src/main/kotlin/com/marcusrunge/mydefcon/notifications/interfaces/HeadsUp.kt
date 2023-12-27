package com.marcusrunge.mydefcon.notifications.interfaces

interface HeadsUp {
    fun showBasicUrgent(textTitle:String, textContent:String, ongoing:Boolean)
    fun showBasicHigh(textTitle:String, textContent:String, ongoing:Boolean)
    fun showBasicMedium(textTitle:String, textContent:String, ongoing:Boolean)
    fun showBasicLow(textTitle:String, textContent:String, ongoing:Boolean)
    fun showExpandedUrgent(textTitle:String, textContent:String, ongoing:Boolean)
    fun showExpandedHigh(textTitle:String, textContent:String, ongoing:Boolean)
    fun showExpandedMedium(textTitle:String, textContent:String, ongoing:Boolean)
    fun showExpandedLow(textTitle:String, textContent:String, ongoing:Boolean)
}