using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

namespace MyDEFCON
{
    public static class Constants
    {
        public const int SERVICE_RUNNING_NOTIFICATION_ID = 10000;
        public const string SERVICE_STARTED_KEY = "has_service_been_started";
        public const string BROADCAST_MESSAGE_KEY = "broadcast_message";
        public const string NOTIFICATION_BROADCAST_ACTION = "com.marcusrunge.MyDEFCON.Notification.Action";
        public const string ACTION_START_SERVICE = "com.marcusrunge.MyDEFCON.action.START_SERVICE";
        public const string ACTION_STOP_SERVICE = "com.marcusrunge.MyDEFCON.action.STOP_SERVICE";
        public const string ACTION_MAIN_ACTIVITY = "com.marcusrunge.MyDEFCON.action.MAIN_ACTIVITY";
    }
}