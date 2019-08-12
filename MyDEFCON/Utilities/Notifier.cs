using Android.Content;
using Android.Media;
using Android.OS;
using System;

namespace MyDEFCON.Utilities
{
    public class Notifier
    {
        public static void AlertWithAudioNotification(Context context, int ringtoneUriIndex)
        {
            var ringtoneManager = new RingtoneManager(context);
            ringtoneManager.SetType(RingtoneType.All);
            var ringtoneCursor = ringtoneManager.Cursor;
            ringtoneCursor.MoveToFirst();
            var ringtoneUri = ringtoneManager.GetRingtoneUri(ringtoneUriIndex);
            var mediaPlayer = new MediaPlayer();
            var audioAttributes = new AudioAttributes.Builder().SetContentType(AudioContentType.Sonification).SetUsage(AudioUsageKind.NotificationEvent).SetLegacyStreamType(Stream.Notification).Build();
            mediaPlayer.SetAudioAttributes(audioAttributes);
            mediaPlayer.SetDataSource(context, ringtoneUri);
            mediaPlayer.Prepare();
            mediaPlayer.Start();
        }

        public static void AlertWithVibration(TimeSpan? vibrateSpan = null)
        {
            if (CanVibrate)
            {
                var milliseconds = vibrateSpan.HasValue ? vibrateSpan.Value.TotalMilliseconds : 500;
                using (var vibratorService = (Vibrator)Android.App.Application.Context.GetSystemService(Context.VibratorService))
                {
                    if ((int)Build.VERSION.SdkInt >= 11)
                    {
#if __ANDROID_11__
                        if (!vibratorService.HasVibrator)
                        {
                            Console.WriteLine("Android device does not have vibrator.");
                            return;
                        }
#endif
                    }
                    if (milliseconds < 0) milliseconds = 0;

                    try
                    {
                        vibratorService.Vibrate(VibrationEffect.CreateOneShot((int)milliseconds, VibrationEffect.DefaultAmplitude));
                    }
                    catch { }
                }
            }
        }

        private static bool CanVibrate
        {
            get
            {
                if ((int)Build.VERSION.SdkInt >= 11)
                {
                    using (var v = (Vibrator)Android.App.Application.Context.GetSystemService(Context.VibratorService))
                        return v.HasVibrator;
                }
                return true;
            }
        }
    }
}