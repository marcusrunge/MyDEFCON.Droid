using SQLite;

namespace MyDEFCON.Models
{
    public class CheckListEntry
    {
        [PrimaryKey, AutoIncrement]
        public int ID { get; set; }
        public int Status { get; set; }
        public bool Checked { get; set; }
        public string CheckItem { get; set; }
    }
}