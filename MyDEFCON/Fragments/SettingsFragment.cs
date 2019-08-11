using Android.Content;
using Android.Media;
using Android.OS;
using Android.Views;
using Android.Widget;
using CommonServiceLocator;
using MyDEFCON.Services;
using MyDEFCON.Utilities;
using System;
using System.Collections.Generic;

namespace MyDEFCON.Fragments
{
    public class SettingsFragment : Android.Support.V4.App.Fragment
    {
        ISettingsService _settingsService;
        IWorkerService _workerService;
        bool _isOnCreateView;
        ViewStates _statusUpdateAlertSelectSpinnerViewState;

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);            
            _settingsService = ServiceLocator.Current.GetInstance<ISettingsService>();
            _workerService = ServiceLocator.Current.GetInstance<IWorkerService>();
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            _isOnCreateView = true;
            var view = inflater.Inflate(Resource.Layout.settings_fragment, null);
            var isBroadcastEnabledSwitch = view.FindViewById<Android.Support.V7.Widget.SwitchCompat>(Resource.Id.isBroadcastEnabledSwitch);
            var isMulticastEnabledSwitch = view.FindViewById<Android.Support.V7.Widget.SwitchCompat>(Resource.Id.isMulticastEnabledSwitch);
            var isForegroundServiceEnabledSwitch = view.FindViewById<Android.Support.V7.Widget.SwitchCompat>(Resource.Id.isForegroundServiceEnabledSwitch);
            var isStatusUpdateAlertEnabledSwitch = view.FindViewById<Android.Support.V7.Widget.SwitchCompat>(Resource.Id.isStatusUpdateAlertEnabledSwitch);
            var statusUpdateAlertSelectSpinner = view.FindViewById<Android.Widget.Spinner>(Resource.Id.statusUpdateAlertSelectSpinner);
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

            isStatusUpdateAlertEnabledSwitch.Checked = _settingsService.GetSetting<bool>("isStatusUpdateAlertEnabled");
            isStatusUpdateAlertEnabledSwitch.CheckedChange += (s, e) =>
            {
                _settingsService.SaveSetting("isStatusUpdateAlertEnabled", e.IsChecked);
                statusUpdateAlertSelectSpinner.Visibility = e.IsChecked ? ViewStates.Visible : ViewStates.Gone;
            };

            var ringtoneManager = new RingtoneManager(Context);
            var ringtoneCursor = ringtoneManager.Cursor;
            List<string> ringtoneTitles = new List<string>();
            ringtoneCursor.MoveToFirst();
            while (ringtoneCursor.MoveToNext())
            {
                ringtoneTitles.Add(ringtoneCursor.GetString(1));
            }
            var arrayAdapter = new ArrayAdapter<string>(Context, Resource.Layout.CustomSpinnerItem, ringtoneTitles.ToArray());
            arrayAdapter.SetDropDownViewResource(Android.Resource.Layout.SimpleSpinnerDropDownItem);
            _statusUpdateAlertSelectSpinnerViewState = isStatusUpdateAlertEnabledSwitch.Checked ? ViewStates.Visible : ViewStates.Gone;
            statusUpdateAlertSelectSpinner.Visibility = _statusUpdateAlertSelectSpinnerViewState;
            statusUpdateAlertSelectSpinner.Adapter = arrayAdapter;
            int statusUpdateAlertSelection = _settingsService.GetSetting<int>("StatusUpdateAlertSelection");
            statusUpdateAlertSelectSpinner.SetSelection(statusUpdateAlertSelection > -1 ? statusUpdateAlertSelection : 0);
            statusUpdateAlertSelectSpinner.ItemSelected += (s, e) =>
            {
                if ((s as Spinner).Visibility == _statusUpdateAlertSelectSpinnerViewState && !_isOnCreateView)
                {
                    _settingsService.SaveSetting("StatusUpdateAlertSelection", e.Position);
                    Notifier.AlertWithAudioNotification(Context, e.Position);
                }
                else
                {
                    _statusUpdateAlertSelectSpinnerViewState = (s as Spinner).Visibility;
                    _isOnCreateView = false;
                }
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