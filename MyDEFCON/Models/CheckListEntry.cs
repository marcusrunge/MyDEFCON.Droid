using SQLite;

namespace MyDEFCON.Models
{
    public class CheckListEntry
    {
        [PrimaryKey, AutoIncrement]
        public int Id { get; set; }
        public long UnixTimeStampCreated { get; set; }
        public long UnixTimeStampUpdated { get; set; }
        public int DefconStatus { get; set; }
        public string Item { get; set; }
        public bool Checked { get; set; }
        public bool Deleted { get; set; }
        public int Visibility { get; set; }
        public double FontSize { get; set; }
        public double Width { get; set; }
    }
}