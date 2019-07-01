using Android.Content;
using Android.OS;
using Android.Views;
using CommonServiceLocator;
using MyDEFCON.Services;
using System;

namespace MyDEFCON.Fragments
{
    public class SettingsFragment : Android.Support.V4.App.Fragment
    {
        ISettingsService _settingsService;
        IWorkerService _workerService;
        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            _settingsService = ServiceLocator.Current.GetInstance<ISettingsService>();
            _workerService = ServiceLocator.Current.GetInstance<IWorkerService>();
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var view = inflater.Inflate(Resource.Layout.settings_fragment, null);
            var isBroadcastEnabledSwitch = view.FindViewById<Android.Support.V7.Widget.SwitchCompat>(Resource.Id.isBroadcastEnabledSwitch);
            var isMulticastEnabledSwitch = view.FindViewById<Android.Support.V7.Widget.SwitchCompat>(Resource.Id.isMulticastEnabledSwitch);
            var isForegroundServiceEnabledSwitch = view.FindViewById<Android.Support.V7.Widget.SwitchCompat>(Resource.Id.isForegroundServiceEnabledSwitch);
            isBroadcastEnabledSwitch.Checked = _settingsService.GetSetting<bool>("IsBroadcastEnabled");
            isBroadcastEnabledSwitch.CheckedChange += (s, e) =>
            {
                _settingsService.SaveSetting("IsBroadcastEnabled", e.IsChecked);
                if (e.IsChecked)
                {
                    Context.StartService(new Intent(Context, typeof(UdpClientService)));
                    if (isMulticastEnabledSwitch.Checked)
                    {
                        var tcpClientServiceIntent = new Intent(Context, typeof(TcpClientService));
                        Context.StopService(tcpClientServiceIntent);
                        Context.StartService(tcpClientServiceIntent);
                    }                    
                }
                else
                {
                    Context.StopService(new Intent(Context, typeof(UdpClientService)));
                    if (isMulticastEnabledSwitch.Checked)
                    {
                        isMulticastEnabledSwitch.Checked = false;
                        isForegroundServiceEnabledSwitch.Checked = false;
                        var tcpClientServiceIntent = new Intent(Context, typeof(TcpClientService));
                        Context.StopService(tcpClientServiceIntent);
                    }
                }
            };

            isMulticastEnabledSwitch.Checked = _settingsService.GetSetting<bool>("IsMulticastEnabled");
            isMulticastEnabledSwitch.CheckedChange += (s, e) =>
            {
                _settingsService.SaveSetting("IsMulticastEnabled", e.IsChecked);
                if (e.IsChecked && !isBroadcastEnabledSwitch.Checked) isBroadcastEnabledSwitch.Checked = true;
                else
                {
                    var tcpClientServiceIntent = new Intent(Context, typeof(TcpClientService));
                    Context.StopService(tcpClientServiceIntent);
                    Context.StartService(tcpClientServiceIntent);
                }
            };
            isForegroundServiceEnabledSwitch.Checked = _settingsService.GetSetting<bool>("IsForegroundServiceEnabled");
            isForegroundServiceEnabledSwitch.CheckedChange += (s, e) =>
            {                
                if (!e.IsChecked)
                {
                    Intent stopServiceIntent = new Intent(Context, typeof(ForegroundService));
                    stopServiceIntent.SetAction(Constants.ACTION_STOP_SERVICE);
                    Context.StopService(stopServiceIntent);
                    if (_settingsService.GetSetting<bool>("IsBroadcastEnabled")) Context.StartService(new Intent(Context, typeof(UdpClientService)));
                    _workerService.CancelWorker<KeepForegroundServiceRunningWorker>();
                }
                else
                {
                    if (!_settingsService.GetSetting<bool>("IsBroadcastEnabled"))
                    {
                        isForegroundServiceEnabledSwitch.Checked = false;
                        return;
                    }
                    Context.StopService(new Intent(Context, typeof(UdpClientService)));
                    Intent startServiceIntent = new Intent(Context, typeof(ForegroundService));
                    startServiceIntent.SetAction(Constants.ACTION_START_SERVICE);
                    if (Build.VERSION.SdkInt >= BuildVersionCodes.O) Context.StartForegroundService(startServiceIntent);
                    else Context.StartService(startServiceIntent);
                    _workerService.CreateUniquePeriodicWorker<KeepForegroundServiceRunningWorker>(TimeSpan.FromMinutes(15));
                }
                _settingsService.SaveSetting("IsForegroundServiceEnabled", e.IsChecked);
            };

            return view;
        }

        public static SettingsFragment NewInstance()
        {
            var settingsFragment = new SettingsFragment { Arguments = new Bundle() };
            return settingsFragment;
        }
    }
}