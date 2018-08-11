using Android.Content;
using Android.Content.Res;
using Android.Graphics;
using Android.OS;
using Android.Support.V4.Content;
using Android.Views;
using Android.Widget;
using CommonServiceLocator;
using MyDEFCON.Receiver;
using MyDEFCON.Services;
using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace MyDEFCON.Fragments
{
    public class StatusFragment : Android.Support.V4.App.Fragment
    {
        Button defcon1Button, defcon2Button, defcon3Button, defcon4Button, defcon5Button;
        ICounterService _counterService;
        IEventService _eventService;
        ISettingsService _settingsService;
        int _applicationDefconStatus;
        static Resources _resources;
        private DefconStatusReceiver _defconStatusReceiver;
        private bool _isButtonPressed = false;
        private bool _isInitializing = true;
        private bool _isReceiving = false;

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            _counterService = ServiceLocator.Current.GetInstance<ICounterService>();
            _eventService = ServiceLocator.Current.GetInstance<IEventService>();
            _settingsService = ServiceLocator.Current.GetInstance<ISettingsService>();
            _applicationDefconStatus = GetApplicationDefconStatus();
            if (_settingsService.GetSetting<bool>("IsBroadcastEnabled"))
            {
                _defconStatusReceiver = new DefconStatusReceiver();
                LocalBroadcastManager.GetInstance(Context).RegisterReceiver(_defconStatusReceiver, new IntentFilter("com.marcusrunge.MyDEFCON.DEFCON_UPDATE"));
            }
        }

        private int GetApplicationDefconStatus()
        {
            int defconStatus = 5;
            var returnedDefconStatus = _settingsService.GetSetting<string>("DefconStatus");
            if (!String.IsNullOrEmpty(returnedDefconStatus)) return int.Parse(returnedDefconStatus);
            return defconStatus;
        }

        public static StatusFragment GetInstance(Resources resources)
        {
            _resources = resources;
            var statusFragment = new StatusFragment() { Arguments = new Bundle() };
            return statusFragment;
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var view = inflater.Inflate(Resource.Layout.status_fragment, null);

            defcon1Button = view.FindViewById<Button>(Resource.Id.defcon1Button);
            defcon2Button = view.FindViewById<Button>(Resource.Id.defcon2Button);
            defcon3Button = view.FindViewById<Button>(Resource.Id.defcon3Button);
            defcon4Button = view.FindViewById<Button>(Resource.Id.defcon4Button);
            defcon5Button = view.FindViewById<Button>(Resource.Id.defcon5Button);

            defcon1Button.Click += async (s, e) => { _isButtonPressed = true; view.PerformHapticFeedback(FeedbackConstants.VirtualKey, FeedbackFlags.IgnoreGlobalSetting); await SetButtonColors(1); await BroadcastDefconStatus(1); SendDefconIntent(1); };
            defcon2Button.Click += async (s, e) => { _isButtonPressed = true; view.PerformHapticFeedback(FeedbackConstants.VirtualKey, FeedbackFlags.IgnoreGlobalSetting); await SetButtonColors(2); await BroadcastDefconStatus(2); SendDefconIntent(2); };
            defcon3Button.Click += async (s, e) => { _isButtonPressed = true; view.PerformHapticFeedback(FeedbackConstants.VirtualKey, FeedbackFlags.IgnoreGlobalSetting); await SetButtonColors(3); await BroadcastDefconStatus(3); SendDefconIntent(3); };
            defcon4Button.Click += async (s, e) => { _isButtonPressed = true; view.PerformHapticFeedback(FeedbackConstants.VirtualKey, FeedbackFlags.IgnoreGlobalSetting); await SetButtonColors(4); await BroadcastDefconStatus(4); SendDefconIntent(4); };
            defcon5Button.Click += async (s, e) => { _isButtonPressed = true; view.PerformHapticFeedback(FeedbackConstants.VirtualKey, FeedbackFlags.IgnoreGlobalSetting); await SetButtonColors(5); await BroadcastDefconStatus(5); SendDefconIntent(5); };

            _eventService.MenuItemPressedEvent += (s, e) =>
            {
                view.PerformHapticFeedback(FeedbackConstants.VirtualKey, FeedbackFlags.IgnoreGlobalSetting);
                if ((e as MenuItemPressedEventArgs).MenuItemTitle.Equals("Share") && (e as MenuItemPressedEventArgs).FragmentTag.Equals("STS"))
                {
                    Bitmap bitmap = BitmapFactory.DecodeResource(_resources, GetDefconFileResource());
                    string path = _settingsService.GetLocalFilePath("shareimage.png");
                    using (var fileStream = new FileStream(path, FileMode.OpenOrCreate))
                    {
                        bitmap.Compress(Bitmap.CompressFormat.Png, 100, fileStream);
                    }
                    bitmap.Dispose();
                    var uri = FileProvider.GetUriForFile(Context, "com.marcusrunge.MyDEFCON.provider", (new Java.IO.File(path)));
                    var sharingIntent = new Intent();
                    sharingIntent.SetAction(Intent.ActionSend);
                    sharingIntent.PutExtra(Intent.ExtraStream, uri);
                    sharingIntent.SetType("image/*");
                    sharingIntent.AddFlags(ActivityFlags.GrantReadUriPermission);
                    if (Context != null) Context.StartActivity(Intent.CreateChooser(sharingIntent, "Status Update"));
                }
            };

            _eventService.DefconStatusChangedEvent += async (s, e) =>
            {
                if (!_isInitializing && !_isButtonPressed)
                {
                    _isReceiving = true;
                    await SetButtonColors((e as DefconStatusChangedEventArgs).NewDefconStatus);
                    _isReceiving = false;
                }
            };

            return view;
        }
        public override async void OnViewCreated(View view, Bundle savedInstanceState)
        {
            base.OnViewCreated(view, savedInstanceState);
            await InitButtonColors(_applicationDefconStatus);
        }
        private int GetDefconFileResource()
        {
            switch (_applicationDefconStatus)
            {
                case 1: return Resource.Drawable.Defcon1;
                case 2: return Resource.Drawable.Defcon2;
                case 3: return Resource.Drawable.Defcon3;
                case 4: return Resource.Drawable.Defcon4;
                default: return Resource.Drawable.Defcon5;
            }
        }

        private async Task InitButtonColors(int defconStatus)
        {
            _isInitializing = true;
            await SetButtonColors(defconStatus);
            _isInitializing = false;
        }

        private void SendDefconIntent(int defconStatus)
        {
            Intent intent = new Intent(Activity, typeof(MyDefconWidget));
            intent.SetAction("com.marcusrunge.MyDEFCON.DEFCON_UPDATE");
            intent.PutExtra("DefconStatus", defconStatus.ToString());
            if (!_isReceiving) Context.SendBroadcast(intent);
        }

        private async Task SetButtonColors(int defconStatus)
        {
            _applicationDefconStatus = defconStatus;

            switch (defconStatus)
            {
                case 1:
                    defcon1Button.Background.SetColorFilter(Color.ParseColor("#FFFFFFFF"), PorterDuff.Mode.Multiply);
                    defcon1Button.SetTextColor(Color.ParseColor("#FF404040"));
                    defcon2Button.Background.SetColorFilter(Color.ParseColor("#FF400C00"), PorterDuff.Mode.Multiply);
                    defcon2Button.SetTextColor(Color.ParseColor("#FFFF7100"));
                    defcon3Button.Background.SetColorFilter(Color.ParseColor("#FF404000"), PorterDuff.Mode.Multiply);
                    defcon3Button.SetTextColor(Color.ParseColor("#FFFFFF00"));
                    defcon4Button.Background.SetColorFilter(Color.ParseColor("#FF003500"), PorterDuff.Mode.Multiply);
                    defcon4Button.SetTextColor(Color.ParseColor("#FF00F200"));
                    defcon5Button.Background.SetColorFilter(Color.ParseColor("#FF002340"), PorterDuff.Mode.Multiply);
                    defcon5Button.SetTextColor(Color.ParseColor("#FF0066FF"));
                    break;
                case 2:
                    defcon1Button.Background.SetColorFilter(Color.ParseColor("#FF404040"), PorterDuff.Mode.Multiply);
                    defcon1Button.SetTextColor(Color.ParseColor("#FFFFFFFF"));
                    defcon2Button.Background.SetColorFilter(Color.ParseColor("#FFFF7100"), PorterDuff.Mode.Multiply);
                    defcon2Button.SetTextColor(Color.ParseColor("#FF400C00"));
                    defcon3Button.Background.SetColorFilter(Color.ParseColor("#FF404000"), PorterDuff.Mode.Multiply);
                    defcon3Button.SetTextColor(Color.ParseColor("#FFFFFF00"));
                    defcon4Button.Background.SetColorFilter(Color.ParseColor("#FF003500"), PorterDuff.Mode.Multiply);
                    defcon4Button.SetTextColor(Color.ParseColor("#FF00F200"));
                    defcon5Button.Background.SetColorFilter(Color.ParseColor("#FF002340"), PorterDuff.Mode.Multiply);
                    defcon5Button.SetTextColor(Color.ParseColor("#FF0066FF"));
                    break;
                case 3:
                    defcon1Button.Background.SetColorFilter(Color.ParseColor("#FF404040"), PorterDuff.Mode.Multiply);
                    defcon1Button.SetTextColor(Color.ParseColor("#FFFFFFFF"));
                    defcon2Button.Background.SetColorFilter(Color.ParseColor("#FF400C00"), PorterDuff.Mode.Multiply);
                    defcon2Button.SetTextColor(Color.ParseColor("#FFFF7100"));
                    defcon3Button.Background.SetColorFilter(Color.ParseColor("#FFFFFF00"), PorterDuff.Mode.Multiply);
                    defcon3Button.SetTextColor(Color.ParseColor("#FF404000"));
                    defcon4Button.Background.SetColorFilter(Color.ParseColor("#FF003500"), PorterDuff.Mode.Multiply);
                    defcon4Button.SetTextColor(Color.ParseColor("#FF00F200"));
                    defcon5Button.Background.SetColorFilter(Color.ParseColor("#FF002340"), PorterDuff.Mode.Multiply);
                    defcon5Button.SetTextColor(Color.ParseColor("#FF0066FF"));
                    break;
                case 4:
                    defcon1Button.Background.SetColorFilter(Color.ParseColor("#FF404040"), PorterDuff.Mode.Multiply);
                    defcon1Button.SetTextColor(Color.ParseColor("#FFFFFFFF"));
                    defcon2Button.Background.SetColorFilter(Color.ParseColor("#FF400C00"), PorterDuff.Mode.Multiply);
                    defcon2Button.SetTextColor(Color.ParseColor("#FFFF7100"));
                    defcon3Button.Background.SetColorFilter(Color.ParseColor("#FF404000"), PorterDuff.Mode.Multiply);
                    defcon3Button.SetTextColor(Color.ParseColor("#FFFFFF00"));
                    defcon4Button.Background.SetColorFilter(Color.ParseColor("#FF00F200"), PorterDuff.Mode.Multiply);
                    defcon4Button.SetTextColor(Color.ParseColor("#FF003500"));
                    defcon5Button.Background.SetColorFilter(Color.ParseColor("#FF002340"), PorterDuff.Mode.Multiply);
                    defcon5Button.SetTextColor(Color.ParseColor("#FF0066FF"));
                    break;
                case 5:
                    defcon1Button.Background.SetColorFilter(Color.ParseColor("#FF404040"), PorterDuff.Mode.Multiply);
                    defcon1Button.SetTextColor(Color.ParseColor("#FFFFFFFF"));
                    defcon2Button.Background.SetColorFilter(Color.ParseColor("#FF400C00"), PorterDuff.Mode.Multiply);
                    defcon2Button.SetTextColor(Color.ParseColor("#FFFF7100"));
                    defcon3Button.Background.SetColorFilter(Color.ParseColor("#FF404000"), PorterDuff.Mode.Multiply);
                    defcon3Button.SetTextColor(Color.ParseColor("#FFFFFF00"));
                    defcon4Button.Background.SetColorFilter(Color.ParseColor("#FF003500"), PorterDuff.Mode.Multiply);
                    defcon4Button.SetTextColor(Color.ParseColor("#FF00F200"));
                    defcon5Button.Background.SetColorFilter(Color.ParseColor("#FF0066FF"), PorterDuff.Mode.Multiply);
                    defcon5Button.SetTextColor(Color.ParseColor("#FF002340"));
                    break;
                default:
                    defcon1Button.Background.SetColorFilter(Color.ParseColor("#FF404040"), PorterDuff.Mode.Multiply);
                    defcon1Button.SetTextColor(Color.ParseColor("#FFFFFFFF"));
                    defcon2Button.Background.SetColorFilter(Color.ParseColor("#FF400C00"), PorterDuff.Mode.Multiply);
                    defcon2Button.SetTextColor(Color.ParseColor("#FFFF7100"));
                    defcon3Button.Background.SetColorFilter(Color.ParseColor("#FF404000"), PorterDuff.Mode.Multiply);
                    defcon3Button.SetTextColor(Color.ParseColor("#FFFFFF00"));
                    defcon4Button.Background.SetColorFilter(Color.ParseColor("#FF003500"), PorterDuff.Mode.Multiply);
                    defcon4Button.SetTextColor(Color.ParseColor("#FF00F200"));
                    defcon5Button.Background.SetColorFilter(Color.ParseColor("#FF002340"), PorterDuff.Mode.Multiply);
                    defcon5Button.SetTextColor(Color.ParseColor("#FF0066FF"));
                    break;
            }
            _settingsService.SaveSetting("DefconStatus", defconStatus.ToString());

            await _counterService.CalculateCounter(defconStatus);
            _isButtonPressed = false;
        }

        public async override void OnResume()
        {
            base.OnResume();
            _applicationDefconStatus = GetApplicationDefconStatus();
            await InitButtonColors(_applicationDefconStatus);
            _defconStatusReceiver = new DefconStatusReceiver();
            if (_defconStatusReceiver != null && _settingsService.GetSetting<bool>("IsBroadcastEnabled")) LocalBroadcastManager.GetInstance(Context).RegisterReceiver(_defconStatusReceiver, new IntentFilter("com.marcusrunge.MyDEFCON.DEFCON_UPDATE"));
        }

        private async Task BroadcastDefconStatus(int defconStatus)
        {
            if (_settingsService.GetSetting<bool>("IsBroadcastEnabled"))
            {
                _eventService.OnBlockConnectionEvent(new BlockConnectionEventArgs(true));
                using (var udpClient = new UdpClient())
                {
                    udpClient.EnableBroadcast = true;
                    var ipEndpoint = new IPEndPoint(IPAddress.Broadcast, 4536);
                    var datagram = Encoding.ASCII.GetBytes(defconStatus.ToString());
                    await udpClient.SendAsync(datagram, datagram.Length, ipEndpoint);
                    udpClient.Close();
                }
            }
        }

        public override void OnPause()
        {
            if (_defconStatusReceiver != null && _settingsService.GetSetting<bool>("IsBroadcastEnabled")) LocalBroadcastManager.GetInstance(Context).UnregisterReceiver(_defconStatusReceiver);
            base.OnPause();
        }
    }
}