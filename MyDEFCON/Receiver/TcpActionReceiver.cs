using Android.App;
using Android.Content;
using CommonServiceLocator;
using MyDEFCON.Models;
using MyDEFCON.Services;
using Newtonsoft.Json;
using SQLite;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Sockets;
using System.Threading.Tasks;

namespace MyDEFCON.Receiver
{
    [BroadcastReceiver(Enabled = true)]
    [IntentFilter(new string[] { "com.marcusrunge.MyDEFCON.TCP_ACTION" })]
    public class TcpActionReceiver : BroadcastReceiver
    {
        private SQLiteAsyncConnection _sqLiteAsyncConnection;
        private IEventService _eventService;
        private static bool _isAlreadyConnected = false;
        //TcpClient _tcpClient;
        //Context _context;

        public TcpActionReceiver()
        {
            _sqLiteAsyncConnection = ServiceLocator.Current.GetInstance<ISQLiteDependencies>().AsyncConnection;
            _eventService = ServiceLocator.Current.GetInstance<EventService>();
            //_tcpClient = new TcpClient();
        }

        public override async void OnReceive(Context context, Intent intent)
        {
            //_context = context;
            if (!_isAlreadyConnected) await Task.Run(async () =>
              {
                  _isAlreadyConnected = true;
                  var remoteEndPointAddress = intent.GetStringExtra("RemoteEndPointAddress");
                  //TcpClient tcpClient = new TcpClient(remoteEndPointAddress, 4537);
                  TcpClient tcpClient = new TcpClient();
                  try
                  {
                      await tcpClient.ConnectAsync(remoteEndPointAddress, 4537);
                      NetworkStream networkStream = tcpClient.GetStream();
                      string result;
                      using (StreamReader streamReader = new StreamReader(networkStream))
                      {
                          result = await streamReader.ReadLineAsync();
                          streamReader.Close();
                      }
                      networkStream.Close();
                      tcpClient.Close();
                      return result;
                  }
                  catch (Exception)
                  {
                      tcpClient.Close();
                      tcpClient.Dispose();
                      return "";
                  }
              }).ContinueWith(UpdateDatabase);
            /*if (!_isAlreadyConnected) await Task.Run(async () =>
            {
                _isAlreadyConnected = true;
                var remoteEndPointAddress = intent.GetStringExtra("RemoteEndPointAddress");
                TcpClient tcpClient = new TcpClient(remoteEndPointAddress, 4537);
                try
                {
                    StringBuilder stringBuilder = new StringBuilder();
                    using (NetworkStream networkStream = tcpClient.GetStream())
                    {
                        byte[] receiveBuffer = new byte[tcpClient.ReceiveBufferSize];
                        int bytes = -1;
                        do
                        {
                            bytes = await networkStream.ReadAsync(receiveBuffer, 0, receiveBuffer.Length);
                            stringBuilder.Append(Encoding.ASCII.GetString(receiveBuffer));
                            if (stringBuilder.ToString().IndexOf("\0") != -1) break;
                        } while (bytes != 0);
                        networkStream.Close();
                        networkStream.Dispose();
                    }
                    tcpClient.Close();
                    tcpClient.Dispose();
                    return stringBuilder.ToString();
                }
                catch (Exception)
                {
                    tcpClient.Close();
                    tcpClient.Dispose();
                    _isAlreadyConnected = false;
                    return "";
                }
            }).ContinueWith(UpdateDatabase);*/
        }

        private async Task UpdateDatabase(Task<string> task)
        {
            try
            {
                var checkListEntries = JsonConvert.DeserializeObject<List<CheckListEntry>>(task.Result);
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
                        else if (foundCheckListEntry.UnixTimeStampUpdated != checkListEntry.UnixTimeStampUpdated)
                        {
                            foundCheckListEntry.Item = checkListEntry.Item;
                            foundCheckListEntry.Checked = checkListEntry.Checked;
                            foundCheckListEntry.FontSize = checkListEntry.FontSize;
                            foundCheckListEntry.Width = checkListEntry.Width;
                            foundCheckListEntry.Visibility = checkListEntry.Visibility;
                            await _sqLiteAsyncConnection?.UpdateAsync(foundCheckListEntry);
                        }
                    }
                    else if (foundCheckListEntry == null) await _sqLiteAsyncConnection.InsertAsync(checkListEntry);
                }
                _eventService.OnChecklistUpdatedEvent(new EventArgs());
                //Toast.MakeText(_context, "New Checklist Update received...", ToastLength.Short).Show();
            }
            catch { }
            _isAlreadyConnected = false;
        }
    }
}