using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Android.App;
using Android.App.Job;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

namespace MyDEFCON.Services
{
    [Service(Name = "com.marcusrunge.MyDEFCON.KeepForegroundServiceRunningJob", Permission = "android.permission.BIND_JOB_SERVICE")]
    public class KeepForegroundServiceRunningJob : JobService
    {
        public override bool OnStartJob(JobParameters @params)
        {
            Task.Run(() =>
            {
                ActivityManager activityManager = (ActivityManager)GetSystemService(ActivityService);
                bool isForegroundServiceRunning = false;
                foreach (var service in activityManager.GetRunningServices(int.MaxValue))
                {
                    if (service.Process == PackageName && service.Service.ClassName.EndsWith(typeof(ForegroundService).Name)) isForegroundServiceRunning = true;
                }
                if (!isForegroundServiceRunning)
                {
                    Intent startServiceIntent = new Intent(this, typeof(ForegroundService));
                    startServiceIntent.SetAction(Constants.ACTION_START_SERVICE);
                    if (Build.VERSION.SdkInt >= BuildVersionCodes.O) StartForegroundService(startServiceIntent);
                    else StartService(startServiceIntent);
                }
                JobFinished(@params, false);
            });
            return true;
        }

        public override bool OnStopJob(JobParameters @params)
        {
            throw new NotImplementedException();
        }
    }
}