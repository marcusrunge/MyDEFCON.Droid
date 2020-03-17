
using Android.App;
using Android.Content;
using Android.OS;
using AndroidX.Work;

namespace MyDEFCON.Services
{
    public class KeepForegroundServiceRunningWorker : Worker
    {
        Context _context;
        public KeepForegroundServiceRunningWorker(Context context, WorkerParameters workerParams) : base(context, workerParams)
        {
            _context = context;
        }
        public override Result DoWork()
        {
            //ActivityManager activityManager = (ActivityManager)_context.GetSystemService(Context.ActivityService);
            //bool isForegroundServiceRunning = false;
            //foreach (var service in activityManager.GetRunningServices(int.MaxValue))
            //{
            //    if (service.Process == _context.PackageName && service.Service.ClassName.EndsWith(typeof(ForegroundService).Name)) isForegroundServiceRunning = true;
            //}
            if (!ForegroundService.IsStarted)
            {
                Intent startServiceIntent = new Intent(_context, typeof(ForegroundService));
                startServiceIntent.SetAction(Constants.ACTION_START_SERVICE);
                if (Build.VERSION.SdkInt >= BuildVersionCodes.O) _context.StartForegroundService(startServiceIntent);
                else _context.StartService(startServiceIntent);
            }
            return Result.InvokeSuccess();
        }
    }
}