package com.marcusrunge.mydefcon.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CheckItemsReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action=="com.marcusrunge.mydefcon.CHECKITEMS_RECEIVED"){

        }
    }
}