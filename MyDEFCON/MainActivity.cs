using Android.App;
using Android.Content;
using Android.Content.PM;
using Android.Graphics;
using Android.OS;
using Android.Support.Design.Widget;
using Android.Support.Transitions;
using Android.Support.V7.App;
using Android.Support.V7.Widget;
using Android.Views;
using CommonServiceLocator;
using MyDEFCON.Fragments;
using MyDEFCON.Models;
using MyDEFCON.Receiver;
using MyDEFCON.Services;
using MyDEFCON.Utilities;
using SQLite;
using System;
using Unity;
using Unity.ServiceLocation;
using static Android.App.ActivityManager;
using ForegroundService = MyDEFCON.Services.ForegroundService;

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
        IWorkerService _workerService;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            unityContainer = new UnityContainer();
            //unityContainer.RegisterInstance<Context>(ApplicationContext);
            unityContainer.RegisterSingleton<IEventService, EventService>();
            unityContainer.RegisterInstance<ISettingsService>(SettingsService.Instance());
            _eventService = unityContainer.Resolve<IEventService>();
            _settingsService = unityContainer.Resolve<ISettingsService>();
            unityContainer.RegisterInstance<ISQLiteDependencies>(SQLiteDependencies.GetInstance(_settingsService.GetLocalFilePath("checklist.db")));
            unityContainer.RegisterType<ICounterService, CounterService>();
            unityContainer.RegisterSingleton<IWorkerService, WorkerService>();
            _workerService = unityContainer.Resolve<IWorkerService>();
            if (!ServiceLocator.IsLocationProviderSet)
            {
                UnityServiceLocator unityServiceLocator = new UnityServiceLocator(unityContainer);
                ServiceLocator.SetLocatorProvider(() => unityServiceLocator);
            }
            SetContentView(Resource.Layout.activity_main);
            _navigation = FindViewById<BottomNavigationView>(Resource.Id.bottomNavigationView);
            _navigation.SetOnNavigationItemSelectedListener(this);

            var toolbar = FindViewById<Toolbar>(Resource.Id.toolbar);
            if (toolbar != null)
            {
                SetSupportActionBar(toolbar);
            }

            Bitmap bitmap;
            if (Build.VERSION.SdkInt >= BuildVersionCodes.NMr1) bitmap = BitmapFactory.DecodeResource(Resources, Resource.Mipmap.ic_launcher);
            else bitmap = BitmapFactory.DecodeResource(Resources, Resource.Drawable.Icon);
            TaskDescription taskDescription = new TaskDescription("MyDEFCON", bitmap, Color.ParseColor("#e8ff00"));
            SetTaskDescription(taskDescription);
            //if first time you will want to go ahead and click first item.
            if (savedInstanceState == null)
            {
                try
                {
                    LoadFragment(Resource.Id.menu_status);
                    _navigation.SelectedItemId = Resource.Id.menu_status;
                }
                catch (Exception)
                {
                }
            }

            try
            {
                var connection = new SQLiteAsyncConnection(_settingsService.GetLocalFilePath("checklist.db"));
                connection.CreateTableAsync<CheckListEntry>();
            }
            catch (Exception) { }

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

            if (_settingsService.GetSetting<bool>("IsForegroundServiceEnabled"))
            {
                Intent startServiceIntent = new Intent(this, typeof(ForegroundService));
                startServiceIntent.SetAction(Constants.ACTION_START_SERVICE);
                if (Build.VERSION.SdkInt >= BuildVersionCodes.O) StartForegroundService(startServiceIntent);
                else StartService(startServiceIntent);
                _workerService.CreateUniquePeriodicWorker<KeepForegroundServiceRunningWorker>(TimeSpan.FromMinutes(15));
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
                fragment = StatusFragment.GetInstance(Resources, _eventService, _settingsService, unityContainer.Resolve<ICounterService>());
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
                fragment = ChecklistFragment.GetInstance(_eventService, _settingsService, unityContainer.Resolve<ICounterService>(), unityContainer.Resolve<ISQLiteDependencies>());
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
                fragment = SettingsFragment.NewInstance(_settingsService, unityContainer.Resolve<IWorkerService>());
                SupportActionBar.SetTitle(Resource.String.settingsTitle);
                _navigation.Visibility = ViewStates.Gone;
                _menu.FindItem(Resource.Id.menu_share).SetVisible(false);
                SupportFragmentManager.BeginTransaction().Replace(Resource.Id.content_frame, fragment, _fragmentTag).Commit();
            }

            _eventService.OnMenuItemPressedEvent(new MenuItemPressedEventArgs(item.TitleFormatted.ToString(), _fragmentTag));
            //_eventService.OnSpareEvent(new EventArgs());
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

        protected override void OnStart()
        {
            base.OnStart();
        }

        protected override void OnResume()
        {
            base.OnResume();
            try
            {
                RegisterReceiver(_appRestrictiosReceiver, new IntentFilter(Intent.ActionApplicationRestrictionsChanged));
            }
            catch (Exception)
            {
            }
            Restrictor.ResolveRestrictions(ApplicationContext, _eventService);
            if (_settingsService.GetSetting<bool>("IsBroadcastEnabled") && !_settingsService.GetSetting<bool>("IsForegroundServiceEnabled"))
            {
                var udpClientServiceIntent = new Intent(this, typeof(UdpClientService));
                StopService(udpClientServiceIntent);
                StartService(udpClientServiceIntent);
            }
        }

        protected override void OnPause()
        {
            try
            {
                UnregisterReceiver(_appRestrictiosReceiver);
            }
            catch (Exception)
            {
            }
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