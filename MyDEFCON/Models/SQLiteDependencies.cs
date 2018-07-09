using SQLite;

namespace MyDEFCON.Models
{
    public interface ISQLiteDependencies
    {
        SQLiteAsyncConnection AsyncConnection { get; set; }
    }
    public class SQLiteDependencies : ISQLiteDependencies
    {       
        public SQLiteAsyncConnection AsyncConnection { get; set; }
        public SQLiteDependencies(string localFilePath)
        {            
            AsyncConnection = new SQLiteAsyncConnection(localFilePath);
        }
        public static SQLiteDependencies GetInstance(string localFilePath)
        {
            var sQLiteDependencies = new SQLiteDependencies(localFilePath);
            return sQLiteDependencies;
        }
    }
}