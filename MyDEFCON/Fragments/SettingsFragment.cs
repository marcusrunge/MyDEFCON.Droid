﻿using Android.Content;
using Android.OS;
using Android.Views;
using CommonServiceLocator;
using MyDEFCON.Services;

namespace MyDEFCON.Fragments
{
    public class SettingsFragment : Android.Support.V4.App.Fragment
    {
        ISettingsService _settingsService;
        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            _settingsService = ServiceLocator.Current.GetInstance<ISettingsService>();
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var view = inflater.Inflate(Resource.Layout.settings_fragment, null);
            var isBroadcastEnabledSwitch = view.FindViewById<Android.Support.V7.Widget.SwitchCompat>(Resource.Id.isBroadcastEnabledSwitch);
            var isMulticastEnabledSwitch = view.FindViewById<Android.Support.V7.Widget.SwitchCompat>(Resource.Id.isMulticastEnabledSwitch);
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
            return view;
        }

        public static SettingsFragment NewInstance()
        {
            var settingsFragment = new SettingsFragment { Arguments = new Bundle() };
            return settingsFragment;
        }
    }
}