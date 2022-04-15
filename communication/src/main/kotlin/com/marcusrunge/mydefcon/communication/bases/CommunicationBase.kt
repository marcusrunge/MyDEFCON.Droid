package com.marcusrunge.mydefcon.communication.bases

import android.content.Context
import com.marcusrunge.mydefcon.communication.interfaces.Communication

internal abstract class CommunicationBase(internal val context: Context?) : Communication {
}