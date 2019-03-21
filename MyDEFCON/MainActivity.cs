using Android.App;
using Android.Content.PM;
using Android.OS;
using Android.Views;

using MyDEFCON.Fragments;
using Android.Support.V7.App;
using Android.Support.Design.Widget;
using MyDEFCON.Services;
using SQLite;
using MyDEFCON.Models;
using static Android.App.ActivityManager;
using Android.Graphics;
using Unity;
using Unity.ServiceLocation;
using CommonServiceLocator;
using Android.Support.V7.Widget;
using Android.Content;
using Android.Support.Transitions;
using MyDEFCON.Receiver;

namespace MyDEFCON
{
    [Activity(Label = "@string/app_name", MainLauncher = true, Theme = "@style/splashscreen", LaunchMode = LaunchMode.SingleTop, Icon = "@drawable/Icon", RoundIcon = "@mipmap/ic_launcher")]
    public class MainActivity : AppCompatActivity, BottomNavigationView.IOnNavigationItemSelectedListener
    {      
        BottomNavigationView _navigation;
        int _lastFragmentId;
        IEventService _eventService;
        IMenu _menu;
        IUnityContainer unityContainer;
        ISettingsService _settingsService;
        string _fragmentTag;
        AppRestrictionsReceiver _appRestrictiosReceiver;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);            
            unityContainer = new UnityContainer();
            unityContainer.RegisterSingleton<IEventService, EventService>();
            unityContainer.RegisterInstance<ISettingsService>(SettingsService.Instance());
            _eventService = unityContainer.Resolve<IEventService>();
            _settingsService = unityContainer.Resolve<ISettingsService>();
            unityContainer.RegisterInstance<ISQLiteDependencies>(SQLiteDependencies.GetInstance(_settingsService.GetLocalFilePath("checklist.db")));
            unityContainer.RegisterType<ICounterService, CounterService>();
            UnityServiceLocator unityServiceLocator = new UnityServiceLocator(unityContainer);
            ServiceLocator.SetLocatorProvider(() => unityServiceLocator);

            SetContentView(Resource.Layout.activity_main);
            _navigation = FindViewById<BottomNavigationView>(Resource.Id.bottomNavigationView);
            _navigation.SetOnNavigationItemSelectedListener(this);

            var toolbar = FindViewById<Toolbar>(Resource.Id.toolbar);
            if (toolbar != null)
            {
                SetSupportActionBar(toolbar);
            }

            int iconRes;
            if (Build.VERSION.SdkInt >= BuildVersionCodes.NMr1) iconRes = Resource.Mipmap.ic_launcher;
            else iconRes = Resource.Drawable.Icon;
            TaskDescription taskDescription = new TaskDescription("MyDEFCON", iconRes, Color.ParseColor("#e8ff00"));
            SetTaskDescription(taskDescription);

            //if first time you will want to go ahead and click first item.
            if (savedInstanceState == null)
            {
                LoadFragment(Resource.Id.menu_status);
                _navigation.SelectedItemId = Resource.Id.menu_status;
            }

            try
            {
                var connection = new SQLiteAsyncConnection(_settingsService.GetLocalFilePath("checklist.db"));
                connection.CreateTableAsync<CheckListEntry>();
            }
            catch (System.Exception) { }

            if (_settingsService.GetSetting<bool>("IsBroadcastEnabled"))
            {
                var udpClientServiceIntent = new Intent(this, typeof(UdpClientService));
                StopService(udpClientServiceIntent);
                StartService(udpClientServiceIntent);
                if (_settingsService.GetSetting<bool>("IsMulticastEnabled"))
                {
                    var tcpClientServiceIntent = new Intent(this, typeof(TcpClientService));
                    StopService(tcpClientServiceIntent);
                    StartService(tcpClientServiceIntent);
                }
            }
            _appRestrictiosReceiver = new AppRestrictionsReceiver();
        }

        void LoadFragment(int id)
        {
            Android.Support.V4.App.Fragment existingFragment;
            existingFragment = SupportFragmentManager.FindFragmentByTag("CHK");
            if (existingFragment != null) SupportFragmentManager.PopBackStackImmediate(existingFragment.Id, 0);
            existingFragment = SupportFragmentManager.FindFragmentByTag("STS");
            if (existingFragment != null) SupportFragmentManager.PopBackStackImmediate(existingFragment.Id, 0);
            existingFragment = SupportFragmentManager.FindFragmentByTag("ABT");
            if (existingFragment != null) SupportFragmentManager.PopBackStackImmediate(existingFragment.Id, 0);
            
            //string fragmentTag;
            Android.Support.V4.App.Fragment fragment = null;
            Fade fade = new Fade();
            fade.SetDuration(200);
            if (id == Resource.Id.menu_status)
            {
                _fragmentTag = "STS";
                fragment = StatusFragment.GetInstance(Resources);
                fragment.EnterTransition = fade;
                fragment.ExitTransition = fade;
                SupportActionBar.SetTitle(Resource.String.statusTitle);
                _lastFragmentId = id;
                if (_menu != null)
                {
                    _menu.FindItem(Resource.Id.menu_share).SetVisible(true);
                }

            }
            else if (id == Resource.Id.menu_checklist)
            {
                _fragmentTag = "CHK";
                fragment = ChecklistFragment.GetInstance();
                fragment.EnterTransition = fade;
                fragment.ExitTransition = fade;
                SupportActionBar.SetTitle(Resource.String.checklistTitle);
                _lastFragmentId = id;
                if (_settingsService.GetSetting<bool>("IsMulticastEnabled")) _menu.FindItem(Resource.Id.menu_share).SetVisible(true);
                else _menu.FindItem(Resource.Id.menu_share).SetVisible(false);
            }
            else return;

            SupportFragmentManager.BeginTransaction().Replace(Resource.Id.content_frame, fragment, _fragmentTag).Commit();
        }

        public override bool OnOptionsItemSelected(IMenuItem item)
        {
            Android.Support.V4.App.Fragment existingFragment;
            existingFragment = SupportFragmentManager.FindFragmentByTag("CHK");
            if (existingFragment != null) SupportFragmentManager.PopBackStackImmediate(existingFragment.Id, 0);
            existingFragment = SupportFragmentManager.FindFragmentByTag("STS");
            if (existingFragment != null) SupportFragmentManager.PopBackStackImmediate(existingFragment.Id, 0);
            existingFragment = SupportFragmentManager.FindFragmentByTag("ABT");
            if (existingFragment != null) SupportFragmentManager.PopBackStackImmediate(existingFragment.Id, 0);

            Android.Support.V4.App.Fragment fragment = null;
            if (item.ItemId == Resource.Id.menu_about)
            {
                _fragmentTag = "ABT";
                fragment = AboutFragment.NewInstance();
                SupportActionBar.SetTitle(Resource.String.aboutTitle);
                _navigation.Visibility = ViewStates.Gone;
                _menu.FindItem(Resource.Id.menu_share).SetVisible(false);
                SupportFragmentManager.BeginTransaction().Replace(Resource.Id.content_frame, fragment, _fragmentTag).Commit();
            }

            if (item.ItemId == Resource.Id.menu_settings)
            {
                _fragmentTag = "SET";
                fragment = SettingsFragment.NewInstance();
                SupportActionBar.SetTitle(Resource.String.settingsTitle);
                _navigation.Visibility = ViewStates.Gone;
                _menu.FindItem(Resource.Id.menu_share).SetVisible(false);
                SupportFragmentManager.BeginTransaction().Replace(Resource.Id.content_frame, fragment, _fragmentTag).Commit();
            }

            _eventService.OnMenuItemPressedEvent(new MenuItemPressedEventArgs(item.TitleFormatted.ToString(), _fragmentTag));

            return base.OnOptionsItemSelected(item);
        }

        public bool OnNavigationItemSelected(IMenuItem item)
        {
            LoadFragment(item.ItemId);
            return true;
        }

        public override bool OnCreateOptionsMenu(IMenu menu)
        {
            _menu = menu;
            MenuInflater.Inflate(Resource.Menu.toolbar_menu, _menu);
            return base.OnCreateOptionsMenu(menu);
        }

        public override void OnBackPressed()
        {
            if (_navigation.Visibility == ViewStates.Gone)
            {
                LoadFragment(_lastFragmentId);
                _navigation.Visibility = ViewStates.Visible;
            }
            else base.OnBackPressed();
        }

        protected override void OnResume()
        {
            base.OnResume();
            if (_settingsService.GetSetting<bool>("IsBroadcastEnabled"))
            {
                var udpClientServiceIntent = new Intent(this, typeof(UdpClientService));
                StopService(udpClientServiceIntent);
                StartService(udpClientServiceIntent);
            }
            RegisterReceiver(_appRestrictiosReceiver, new IntentFilter(Intent.ActionApplicationRestrictionsChanged));
        }

        protected override void OnPause()
        {
            UnregisterReceiver(_appRestrictiosReceiver);
            base.OnPause();
        }

        protected override void OnDestroy()
        {
            base.OnDestroy();
            StopService(new Intent(this, typeof(UdpClientService)));
            StopService(new Intent(this, typeof(TcpClientService)));
        }
    }
}