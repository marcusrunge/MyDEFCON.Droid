using Android.App;
using Android.Content;
using System;
using System.Collections.Generic;
using System.IO;

namespace MyDEFCON.Services
{
    public interface ISettingsService
    {
        T GetSetting<T>(string key);
        string GetLocalFilePath(string filename);
        void SaveSetting(string key, object value);
    }
    public class SettingsService : ISettingsService
    {
        ISharedPreferences _sharedPreferences;
        public SettingsService()
        {
            _sharedPreferences = Application.Context.GetSharedPreferences("MyDefconAppSettings", FileCreationMode.Private);
        }

        public static SettingsService Instance() => new SettingsService();

        /// <summary>    
        /// Retrieves local file path
        /// </summary>   
        /// <param name="filename">File Name</param>  
        /// <returns>File path with file name</returns>
        public string GetLocalFilePath(string filename)
        {
            string personalFolder = Environment.GetFolderPath(Environment.SpecialFolder.Personal);
            return Path.Combine(personalFolder, filename);
        }

        /// <summary>    
        /// Retrieves application settings of types bool, float, int, long, ICollection<string> and string
        /// </summary>   
        /// <param name="key">Settings Key</param>  
        /// <returns>Application Settings of types bool, float, int, long, ICollection<string> or string</returns> 
        public T GetSetting<T>(string key)
        {
            if (typeof(T) == typeof(bool)) return (T)(object)_sharedPreferences.GetBoolean(key, false);
            else if (typeof(T) == typeof(float)) return (T)(object)_sharedPreferences.GetFloat(key, float.MinValue);
            else if (typeof(T) == typeof(int)) return (T)(object)_sharedPreferences.GetInt(key, int.MinValue);
            else if (typeof(T) == typeof(long)) return (T)(object)_sharedPreferences.GetFloat(key, long.MinValue);
            else if (typeof(T) == typeof(ICollection<string>)) return (T)(object)_sharedPreferences.GetStringSet(key, null);
            else return (T)(object)_sharedPreferences.GetString(key, String.Empty);
        }

        /// <summary>    
        /// Saves application settings of types bool, float, int, long, ICollection<string> and string 
        /// </summary>   
        /// <param name="key">Settings Key</param>  
        /// <param name="value">Settings Value</param> 
        /// <returns></returns>  
        public void SaveSetting(string key, object value)
        {
            var editor = _sharedPreferences.Edit();
            var type = value.GetType();
            if (type == typeof(bool)) editor.PutBoolean(key, (bool)value);
            else if (type == typeof(float)) editor.PutFloat(key, (float)value);
            else if (type == typeof(int)) editor.PutInt(key, (int)value);
            else if (type == typeof(long)) editor.PutLong(key, (long)value);
            else if (type == typeof(ICollection<string>)) editor.PutStringSet(key, (ICollection<string>)value);
            else editor.PutString(key, (string)value);
            editor.Commit();
        }
    }
}