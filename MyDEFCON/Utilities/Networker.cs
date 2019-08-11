using System.Net;
using System.Net.Sockets;

namespace MyDEFCON.Utilities
{
    public class Networker
    {
        public static string GetLocalIp()
        {
            string localIp = null;
            IPAddress[] iPAddresses = Dns.GetHostAddresses(Dns.GetHostName());
            foreach (IPAddress address in iPAddresses)
            {
                if (address.AddressFamily == AddressFamily.InterNetwork)
                {
                    localIp = address.ToString();
                    break;
                }
            }
            return localIp;
        }
    }
}