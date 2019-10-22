using Android.Graphics;
using Android.OS;
using Android.Support.Design.Widget;
using Android.Support.V4.App;
using Android.Support.V7.Widget;
using Android.Views;
using Android.Views.InputMethods;
using Android.Widget;
using MyDEFCON.Adapter;
using MyDEFCON.Models;
using MyDEFCON.Services;
using SQLite;
using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace MyDEFCON.Fragments
{

    public class ChecklistFragment : Fragment, ActionMode.ICallback
    {
        Button checklist1Button, checklist2Button, checklist3Button, checklist4Button, checklist5Button;
        FloatingActionButton addFloatingActionButton;
        TextView itemsCounter1, itemsCounter2, itemsCounter3, itemsCounter4, itemsCounter5;
        int fragmentDefconStatus, applicationDefconStatus, _actionModeItemId;
        List<CheckListEntry> _checkList;
        RecyclerView _recyclerView;
        ChecklistRecyclerViewAdapter _checklistRecyclerViewAdapter;
        ISettingsService _settingsService;
        IEventService _eventService;
        ICounterService _counterService;
        SQLiteAsyncConnection _sqLiteAsyncConnection;
        View _onCreateView, _actionModeItemView;
        ActionMode _actionMode;

        public override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            _checkList = new List<CheckListEntry>();
            try
            {
                applicationDefconStatus = GetApplicationDefconStatus();
                
            }
            catch (Exception) { applicationDefconStatus = 5; }
            fragmentDefconStatus = applicationDefconStatus;
        }

        public static ChecklistFragment GetInstance(IEventService eventService, ISettingsService settingsService, ICounterService counterService, ISQLiteDependencies sQLiteDependencies)
        {
            var checklistFragment = new ChecklistFragment(eventService, settingsService, counterService, sQLiteDependencies) { Arguments = new Bundle() };
            return checklistFragment;
        }

        public ChecklistFragment(IEventService eventService, ISettingsService settingsService, ICounterService counterService, ISQLiteDependencies sQLiteDependencies)
        {
            _eventService = eventService;
            _settingsService = settingsService;
            _counterService = counterService;
            _sqLiteAsyncConnection = sQLiteDependencies.AsyncConnection;
        }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var view = inflater.Inflate(Resource.Layout.checklist_fragment, null);

            checklist1Button = view.FindViewById<Button>(Resource.Id.checklist1Button);
            checklist2Button = view.FindViewById<Button>(Resource.Id.checklist2Button);
            checklist3Button = view.FindViewById<Button>(Resource.Id.checklist3Button);
            checklist4Button = view.FindViewById<Button>(Resource.Id.checklist4Button);
            checklist5Button = view.FindViewById<Button>(Resource.Id.checklist5Button);

            addFloatingActionButton = view.FindViewById<FloatingActionButton>(Resource.Id.addFloatingActionButton);

            checklist1Button.Click += async (s, e) => { fragmentDefconStatus = 1; SetButtonColors(1); await ReloadCheckList(1); };
            checklist2Button.Click += async (s, e) => { fragmentDefconStatus = 2; SetButtonColors(2); await ReloadCheckList(2); };
            checklist3Button.Click += async (s, e) => { fragmentDefconStatus = 3; SetButtonColors(3); await ReloadCheckList(3); };
            checklist4Button.Click += async (s, e) => { fragmentDefconStatus = 4; SetButtonColors(4); await ReloadCheckList(4); };
            checklist5Button.Click += async (s, e) => { fragmentDefconStatus = 5; SetButtonColors(5); await ReloadCheckList(5); };

            addFloatingActionButton.Click += async (s, e) =>
            {
                _onCreateView.PerformHapticFeedback(FeedbackConstants.VirtualKey, FeedbackFlags.IgnoreGlobalSetting);
                var checkListEntry = new CheckListEntry() { DefconStatus = fragmentDefconStatus, UnixTimeStampCreated = DateTimeOffset.Now.ToUnixTimeMilliseconds(), FontSize = 26 };
                _checkList.Add(checkListEntry);
                await _sqLiteAsyncConnection.InsertAsync(checkListEntry);
                _checklistRecyclerViewAdapter.NotifyDataSetChanged();
                await SetCounter();
            };

            itemsCounter1 = view.FindViewById<TextView>(Resource.Id.counterOne);
            itemsCounter2 = view.FindViewById<TextView>(Resource.Id.counterTwo);
            itemsCounter3 = view.FindViewById<TextView>(Resource.Id.counterThree);
            itemsCounter4 = view.FindViewById<TextView>(Resource.Id.counterFour);
            itemsCounter5 = view.FindViewById<TextView>(Resource.Id.counterFife);

            _onCreateView = view;
            return view;
        }

        public override async void OnViewCreated(View view, Bundle savedInstanceState)
        {
            try
            {
                base.OnViewCreated(view, savedInstanceState);
                var loadedChecklist = await LoadChecklist(fragmentDefconStatus);
                foreach (var checkListEntry in loadedChecklist) _checkList.Add(checkListEntry);
                _recyclerView = view.FindViewById<RecyclerView>(Resource.Id.checkListRecyclerView);
                var layoutManager = new LinearLayoutManager(Context);
                _checklistRecyclerViewAdapter = new ChecklistRecyclerViewAdapter(_checkList);
                _recyclerView.SetLayoutManager(layoutManager);
                _recyclerView.SetAdapter(_checklistRecyclerViewAdapter);
                _checklistRecyclerViewAdapter.ViewHolderAction += async (s, e) =>
                {
                    switch (e.Item3)
                    {
                        case ActionType.LongClick:
                            _actionModeItemId = e.Item2;
                            _actionModeItemView = (e.Item1 as LinearLayout);
                            _actionModeItemView.SetBackgroundColor(Color.ParseColor("#80ff0000"));
                            if (_actionMode != null) break;
                            _actionMode = _onCreateView.StartActionMode(this);
                            break;
                        case ActionType.CheckedChange:
                            _checkList[e.Item2].Checked = (e.Item1 as CheckBox).Checked;
                            _checkList[e.Item2].UnixTimeStampUpdated = DateTimeOffset.Now.ToUnixTimeMilliseconds();
                            await _sqLiteAsyncConnection.UpdateAsync(_checkList[e.Item2]);
                            await SetCounter();
                            break;
                        case ActionType.AfterTextChanged:
                            _checkList[e.Item2].Item = (e.Item1 as AppCompatEditText).EditableText.ToString();
                            _checkList[e.Item2].UnixTimeStampUpdated = DateTimeOffset.Now.ToUnixTimeMilliseconds();
                            await _sqLiteAsyncConnection.UpdateAsync(_checkList[e.Item2]);
                            break;
                        default:
                            break;
                    }
                };
                _eventService.MenuItemPressedEvent += _eventService_MenuItemPressedEvent;
                _eventService.ChecklistUpdatedEvent += (s, e) => Activity.RunOnUiThread(async () => await ReloadCheckList(fragmentDefconStatus));
                await InitButtonAndCounterColors(fragmentDefconStatus);
            }
            catch (Exception) { }
        }

        private async void _eventService_MenuItemPressedEvent(object sender, EventArgs e)
        {
            {
                if ((e as MenuItemPressedEventArgs).MenuItemTitle.Equals("Share") && (e as MenuItemPressedEventArgs).FragmentTag.Equals("CHK"))
                {
                    _onCreateView.PerformHapticFeedback(FeedbackConstants.VirtualKey, FeedbackFlags.IgnoreGlobalSetting);
                    _eventService.OnBlockConnectionEvent(new BlockConnectionEventArgs(true));
                    using (var udpClient = new UdpClient())
                    {
                        udpClient.EnableBroadcast = true;
                        var ipEndpoint = new IPEndPoint(IPAddress.Broadcast, 4536);
                        var datagram = Encoding.ASCII.GetBytes("0");
                        try
                        {
                            await udpClient.SendAsync(datagram, datagram.Length, ipEndpoint);
                        }
                        catch { }

                        udpClient.Close();
                    }
                }
            };
        }

        private async Task ReloadCheckList(int defconStatus)
        {
            try
            {
                _checkList.Clear();
                var loadedCheckList = await LoadChecklist(defconStatus);
                foreach (var checkListEntry in loadedCheckList)
                {
                    _checkList.Add(checkListEntry);
                }
                _checklistRecyclerViewAdapter.NotifyDataSetChanged();
                await SetCounter();
            }
            catch (Exception) { }
        }

        private async Task InitButtonAndCounterColors(int defconStatus)
        {
            SetButtonColors(defconStatus);
            await SetCounter();
        }

        private async Task SetCounter()
        {
            await _counterService.CalculateCounter(applicationDefconStatus);

            itemsCounter1.SetBackgroundColor(_counterService.Counter1Color);
            itemsCounter2.SetBackgroundColor(_counterService.Counter2Color);
            itemsCounter3.SetBackgroundColor(_counterService.Counter3Color);
            itemsCounter4.SetBackgroundColor(_counterService.Counter4Color);
            itemsCounter5.SetBackgroundColor(_counterService.Counter5Color);

            itemsCounter1.Text = _counterService.Counter1Value.ToString();
            itemsCounter2.Text = _counterService.Counter2Value.ToString();
            itemsCounter3.Text = _counterService.Counter3Value.ToString();
            itemsCounter4.Text = _counterService.Counter4Value.ToString();
            itemsCounter5.Text = _counterService.Counter5Value.ToString();
        }

        private void SetButtonColors(int defconStatus)
        {
            InputMethodManager inputMethodManager;
            if (Context != null)
            {
                try
                {
                    inputMethodManager = (InputMethodManager)Context.GetSystemService(Android.Content.Context.InputMethodService);
                    if (_onCreateView != null) inputMethodManager.HideSoftInputFromWindow(_onCreateView.WindowToken, HideSoftInputFlags.None);
                }
                catch (Exception) { }
            }

            switch (defconStatus)
            {
                case 1:
                    if (Build.VERSION.SdkInt >= BuildVersionCodes.Q)
                    {
                        checklist1Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FFFFFFFF"), BlendMode.SrcAtop));
                        checklist2Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF400C00"), BlendMode.SrcAtop));
                        checklist3Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF404000"), BlendMode.SrcAtop));
                        checklist4Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF003500"), BlendMode.SrcAtop));
                        checklist5Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF002340"), BlendMode.SrcAtop));
                    }
                    else
                    {

                    }                    
                    checklist1Button.SetTextColor(Color.ParseColor("#FF404040"));                    
                    checklist2Button.SetTextColor(Color.ParseColor("#FFFF7100"));                    
                    checklist3Button.SetTextColor(Color.ParseColor("#FFFFFF00"));                    
                    checklist4Button.SetTextColor(Color.ParseColor("#FF00F200"));                    
                    checklist5Button.SetTextColor(Color.ParseColor("#FF0066FF"));
                    break;
                case 2:
                    if (Build.VERSION.SdkInt >= BuildVersionCodes.Q)
                    {
                        checklist1Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF404040"), BlendMode.SrcAtop));
                        checklist2Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FFFF7100"), BlendMode.SrcAtop));
                        checklist3Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF404000"), BlendMode.SrcAtop));
                        checklist4Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF003500"), BlendMode.SrcAtop));
                        checklist5Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF002340"), BlendMode.SrcAtop));
                    }
                    else
                    {
                        checklist1Button.Background.SetColorFilter(Color.ParseColor("#FF404040"), PorterDuff.Mode.Multiply);
                        checklist2Button.Background.SetColorFilter(Color.ParseColor("#FFFF7100"), PorterDuff.Mode.Multiply);
                        checklist3Button.Background.SetColorFilter(Color.ParseColor("#FF404000"), PorterDuff.Mode.Multiply);
                        checklist4Button.Background.SetColorFilter(Color.ParseColor("#FF003500"), PorterDuff.Mode.Multiply);
                        checklist5Button.Background.SetColorFilter(Color.ParseColor("#FF002340"), PorterDuff.Mode.Multiply);
                    }                    
                    checklist1Button.SetTextColor(Color.ParseColor("#FFFFFFFF"));                    
                    checklist2Button.SetTextColor(Color.ParseColor("#FF400C00"));                    
                    checklist3Button.SetTextColor(Color.ParseColor("#FFFFFF00"));                    
                    checklist4Button.SetTextColor(Color.ParseColor("#FF00F200"));                    
                    checklist5Button.SetTextColor(Color.ParseColor("#FF0066FF"));
                    break;
                case 3:
                    if (Build.VERSION.SdkInt >= BuildVersionCodes.Q)
                    {
                        checklist1Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF404040"), BlendMode.SrcAtop));
                        checklist2Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF400C00"), BlendMode.SrcAtop));
                        checklist3Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FFFFFF00"), BlendMode.SrcAtop));
                        checklist4Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF003500"), BlendMode.SrcAtop));
                        checklist5Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF002340"), BlendMode.SrcAtop));
                    }
                    else
                    {
                        checklist1Button.Background.SetColorFilter(Color.ParseColor("#FF404040"), PorterDuff.Mode.Multiply);
                        checklist2Button.Background.SetColorFilter(Color.ParseColor("#FF400C00"), PorterDuff.Mode.Multiply);
                        checklist3Button.Background.SetColorFilter(Color.ParseColor("#FFFFFF00"), PorterDuff.Mode.Multiply);
                        checklist4Button.Background.SetColorFilter(Color.ParseColor("#FF003500"), PorterDuff.Mode.Multiply);
                        checklist5Button.Background.SetColorFilter(Color.ParseColor("#FF002340"), PorterDuff.Mode.Multiply);
                    }                    
                    checklist1Button.SetTextColor(Color.ParseColor("#FFFFFFFF"));                    
                    checklist2Button.SetTextColor(Color.ParseColor("#FFFF7100"));                    
                    checklist3Button.SetTextColor(Color.ParseColor("#FF404000"));                    
                    checklist4Button.SetTextColor(Color.ParseColor("#FF00F200"));                    
                    checklist5Button.SetTextColor(Color.ParseColor("#FF0066FF"));
                    break;
                case 4:
                    if (Build.VERSION.SdkInt >= BuildVersionCodes.Q)
                    {
                        checklist1Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF404040"), BlendMode.SrcAtop));
                        checklist2Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF400C00"), BlendMode.SrcAtop));
                        checklist3Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF404000"), BlendMode.SrcAtop));
                        checklist4Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF00F200"), BlendMode.SrcAtop));
                        checklist5Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF002340"), BlendMode.SrcAtop));
                    }
                    else
                    {
                        checklist1Button.Background.SetColorFilter(Color.ParseColor("#FF404040"), PorterDuff.Mode.Multiply);
                        checklist2Button.Background.SetColorFilter(Color.ParseColor("#FF400C00"), PorterDuff.Mode.Multiply);
                        checklist3Button.Background.SetColorFilter(Color.ParseColor("#FF404000"), PorterDuff.Mode.Multiply);
                        checklist4Button.Background.SetColorFilter(Color.ParseColor("#FF00F200"), PorterDuff.Mode.Multiply);
                        checklist5Button.Background.SetColorFilter(Color.ParseColor("#FF002340"), PorterDuff.Mode.Multiply);
                    }                    
                    checklist1Button.SetTextColor(Color.ParseColor("#FFFFFFFF"));                    
                    checklist2Button.SetTextColor(Color.ParseColor("#FFFF7100"));                    
                    checklist3Button.SetTextColor(Color.ParseColor("#FFFFFF00"));                    
                    checklist4Button.SetTextColor(Color.ParseColor("#FF003500"));                    
                    checklist5Button.SetTextColor(Color.ParseColor("#FF0066FF"));
                    break;
                case 5:
                    if (Build.VERSION.SdkInt >= BuildVersionCodes.Q)
                    {
                        checklist1Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF404040"), BlendMode.SrcAtop));
                        checklist2Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF400C00"), BlendMode.SrcAtop));
                        checklist3Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF404000"), BlendMode.SrcAtop));
                        checklist4Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF003500"), BlendMode.SrcAtop));
                        checklist5Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF0066FF"), BlendMode.SrcAtop));
                    }
                    else
                    {
                        checklist1Button.Background.SetColorFilter(Color.ParseColor("#FF404040"), PorterDuff.Mode.Multiply);
                        checklist2Button.Background.SetColorFilter(Color.ParseColor("#FF400C00"), PorterDuff.Mode.Multiply);
                        checklist3Button.Background.SetColorFilter(Color.ParseColor("#FF404000"), PorterDuff.Mode.Multiply);
                        checklist4Button.Background.SetColorFilter(Color.ParseColor("#FF003500"), PorterDuff.Mode.Multiply);
                        checklist5Button.Background.SetColorFilter(Color.ParseColor("#FF0066FF"), PorterDuff.Mode.Multiply);
                    }                    
                    checklist1Button.SetTextColor(Color.ParseColor("#FFFFFFFF"));                    
                    checklist2Button.SetTextColor(Color.ParseColor("#FFFF7100"));                    
                    checklist3Button.SetTextColor(Color.ParseColor("#FFFFFF00"));                    
                    checklist4Button.SetTextColor(Color.ParseColor("#FF00F200"));                    
                    checklist5Button.SetTextColor(Color.ParseColor("#FF002340"));
                    break;
                default:
                    if (Build.VERSION.SdkInt >= BuildVersionCodes.Q)
                    {
                        checklist1Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF404040"), BlendMode.SrcAtop));
                        checklist2Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF400C00"), BlendMode.SrcAtop));
                        checklist3Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF404000"), BlendMode.SrcAtop));
                        checklist4Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF003500"), BlendMode.SrcAtop));
                        checklist5Button.Background.SetColorFilter(new BlendModeColorFilter(Color.ParseColor("#FF002340"), BlendMode.SrcAtop));
                    }
                    else
                    {
                        checklist1Button.Background.SetColorFilter(Color.ParseColor("#FF404040"), PorterDuff.Mode.Multiply);
                        checklist2Button.Background.SetColorFilter(Color.ParseColor("#FF400C00"), PorterDuff.Mode.Multiply);
                        checklist3Button.Background.SetColorFilter(Color.ParseColor("#FF404000"), PorterDuff.Mode.Multiply);
                        checklist4Button.Background.SetColorFilter(Color.ParseColor("#FF003500"), PorterDuff.Mode.Multiply);
                        checklist5Button.Background.SetColorFilter(Color.ParseColor("#FF002340"), PorterDuff.Mode.Multiply);
                    }                    
                    checklist1Button.SetTextColor(Color.ParseColor("#FFFFFFFF"));                    
                    checklist2Button.SetTextColor(Color.ParseColor("#FFFF7100"));                    
                    checklist3Button.SetTextColor(Color.ParseColor("#FFFFFF00"));                    
                    checklist4Button.SetTextColor(Color.ParseColor("#FF00F200"));                    
                    checklist5Button.SetTextColor(Color.ParseColor("#FF0066FF"));
                    break;
            }
        }

        private async Task<List<CheckListEntry>> LoadChecklist(int defconStatus, bool includeDeleted = false)
        {
            if (_sqLiteAsyncConnection == null) return new List<CheckListEntry>();
            var queryList = await Task.Factory.StartNew(async () =>
            {
                if (!includeDeleted) return await _sqLiteAsyncConnection.QueryAsync<CheckListEntry>("SELECT * FROM CheckListEntry WHERE DefconStatus = ? AND Deleted = 0", defconStatus);
                else return await _sqLiteAsyncConnection.QueryAsync<CheckListEntry>("SELECT * FROM CheckListEntry WHERE DefconStatus = ?", defconStatus);
            }).Result;
            if (queryList.Count > 0) return queryList;
            else return new List<CheckListEntry>();
        }

        public bool OnActionItemClicked(ActionMode mode, IMenuItem item)
        {
            _onCreateView.PerformHapticFeedback(FeedbackConstants.VirtualKey, FeedbackFlags.IgnoreGlobalSetting);
            if (item.TitleFormatted.ToString().Equals("Delete"))
            {
                var checkListEntry = _checkList[_actionModeItemId];
                checkListEntry.Checked = true;
                checkListEntry.Visibility = 1;
                checkListEntry.Deleted = true;
                //_sqLiteAsyncConnection.DeleteAsync(checkListEntry);
                _checkList.RemoveAt(_actionModeItemId);
                _checklistRecyclerViewAdapter.NotifyDataSetChanged();
                //Task.Factory.StartNew(async () =>
                //{
                //    await _sqLiteAsyncConnection.UpdateAsync(checkListEntry);
                //    await SetCounter();
                //});
                _sqLiteAsyncConnection.UpdateAsync(checkListEntry);
                Task.Run(() => Activity.RunOnUiThread(async () => await SetCounter()));
                _checklistRecyclerViewAdapter.ClickedItem = -1;
                mode.Finish();
            }
            return true;
        }

        public bool OnCreateActionMode(ActionMode mode, IMenu menu)
        {
            mode.MenuInflater.Inflate(Resource.Menu.action_menu, menu);
            return true;
        }

        public void OnDestroyActionMode(ActionMode mode)
        {
            _actionModeItemView.SetBackgroundColor(Color.Transparent);
            _actionMode = null;
            _checklistRecyclerViewAdapter.ClickedItem = -1;
        }

        public bool OnPrepareActionMode(ActionMode mode, IMenu menu)
        {
            return false;
        }

        public override void OnDetach()
        {
            base.OnDetach();
            _eventService.MenuItemPressedEvent -= _eventService_MenuItemPressedEvent;
        }

        public override void OnResume()
        {
            base.OnResume();
            if (_checklistRecyclerViewAdapter != null && _recyclerView != null)
            {
                _recyclerView.GetLayoutManager().ScrollToPosition(_checklistRecyclerViewAdapter.ItemCount);
            }
            else
            {
                try
                {
                    _checklistRecyclerViewAdapter = new ChecklistRecyclerViewAdapter(_checkList);
                    _recyclerView.SetAdapter(_checklistRecyclerViewAdapter);
                    _checklistRecyclerViewAdapter.NotifyDataSetChanged();
                }
                catch (Exception) { }
            }
        }

        private int GetApplicationDefconStatus()
        {
            int defconStatus = 5;
            var returnedDefconStatus = _settingsService.GetSetting<string>("DefconStatus");
            if (!String.IsNullOrEmpty(returnedDefconStatus)) return int.Parse(returnedDefconStatus);
            return defconStatus;
        }
    }
}