using Android.App;
using Android.Content;
using CommonServiceLocator;
using MyDEFCON.Models;
using MyDEFCON.Services;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using SQLite;
using System.Net.Sockets;
using System.Threading.Tasks;
using Android.Widget;

namespace MyDEFCON.Receiver
{
    [BroadcastReceiver(Enabled = true)]
    [IntentFilter(new string[] { "com.marcusrunge.MyDEFCON.TCP_ACTION" })]
    public class TcpActionReceiver : BroadcastReceiver
    {
        SQLiteAsyncConnection _sqLiteAsyncConnection;
        IEventService _eventService;
        static bool _isAlreadyConnected = false;
        TcpClient _tcpClient;
        Context _context;

        public TcpActionReceiver()
        {
            _sqLiteAsyncConnection = ServiceLocator.Current.GetInstance<ISQLiteDependencies>().AsyncConnection;
            _eventService = ServiceLocator.Current.GetInstance<IEventService>();
            _tcpClient = new TcpClient();
        }

        public async override void OnReceive(Context context, Intent intent)
        {
            _context = context;
            if (!_isAlreadyConnected) await Task<string>.Run(async () =>
              {
                  _isAlreadyConnected = true;
                  try
                  {
                      var remoteEndPointAddress = intent.GetStringExtra("RemoteEndPointAddress");
                      await _tcpClient?.ConnectAsync(remoteEndPointAddress, 4537);
                      NetworkStream networkStream = _tcpClient?.GetStream();
                      string result;
                      using (StreamReader streamReader = new StreamReader(networkStream))
                      {
                          result = await streamReader?.ReadLineAsync();
                          streamReader?.Close();
                      }
                      networkStream?.Close();
                      _tcpClient?.Close();
                      return result;
                  }
                  catch { return ""; }
              }).ContinueWith(UpdateDatabase);
        }

        private async void UpdateDatabase(Task<string> obj)
        {
            try
            {
                var checkListEntries = JsonConvert.DeserializeObject<List<CheckListEntry>>(obj.Result);
                foreach (var checkListEntry in checkListEntries)
                {
                    CheckListEntry foundCheckListEntry = (await _sqLiteAsyncConnection.FindAsync<CheckListEntry>(c => c.UnixTimeStampCreated == checkListEntry.UnixTimeStampCreated));
                    if (foundCheckListEntry != null)
                    {
                        if (foundCheckListEntry.Deleted != checkListEntry.Deleted)
                        {
                            foundCheckListEntry.Deleted = checkListEntry.Deleted;
                            foundCheckListEntry.Visibility = checkListEntry.Visibility;
                            foundCheckListEntry.Checked = true;
                            await _sqLiteAsyncConnection?.UpdateAsync(foundCheckListEntry);
                        }
                        else if (foundCheckListEntry.UnixTimeStampUpdated < checkListEntry.UnixTimeStampUpdated)
                        {
                            foundCheckListEntry.Item = checkListEntry.Item;
                            foundCheckListEntry.Checked = checkListEntry.Checked;
                            await _sqLiteAsyncConnection?.UpdateAsync(foundCheckListEntry);
                        }
                    }
                    else if (foundCheckListEntry != null) await _sqLiteAsyncConnection?.InsertAsync(checkListEntry);
                }
                _eventService?.OnChecklistUpdatedEvent(new EventArgs());
                //Toast.MakeText(_context, "New Checklist Update received...", ToastLength.Short).Show();
            }
            catch { }
            _isAlreadyConnected = false;
        }
    }
}