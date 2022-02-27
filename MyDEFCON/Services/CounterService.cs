using Android.Graphics;
using MyDEFCON.Models;
using Plugin.Badge;
using SQLite;
using System.Linq;
using System.Threading.Tasks;

namespace MyDEFCON.Services
{
    public interface ICounterService
    {
        Task CalculateCounter(int actualDefconStatus);

        Color Counter1Color { get; }
        int Counter1Value { get; }
        Color Counter2Color { get; }
        int Counter2Value { get; }
        Color Counter3Color { get; }
        int Counter3Value { get; }
        Color Counter4Color { get; }
        int Counter4Value { get; }
        Color Counter5Color { get; }
        int Counter5Value { get; }
        int BadgeCounterValue { get; }
    }

    public class CounterService : ICounterService
    {
        private int _counter1Value;
        public int Counter1Value => _counter1Value;

        private int _counter2Value;
        public int Counter2Value => _counter2Value;

        private int _counter3Value;
        public int Counter3Value => _counter3Value;

        private int _counter4Value;
        public int Counter4Value => _counter4Value;

        private int _counter5Value;
        public int Counter5Value => _counter5Value;

        private int _badgeCounterValue;
        public int BadgeCounterValue => _badgeCounterValue;

        private Color _counter1Color;
        public Color Counter1Color => _counter1Color;

        private Color _counter2Color;
        public Color Counter2Color => _counter2Color;

        private Color _counter3Color;
        public Color Counter3Color => _counter3Color;

        private Color _counter4Color;
        public Color Counter4Color => _counter4Color;

        private Color _counter5Color;
        public Color Counter5Color => _counter5Color;

        private SQLiteAsyncConnection sqLiteAsyncConnection;

        public CounterService(ISQLiteDependencies sQLiteDependencies)
        {
            sqLiteAsyncConnection = sQLiteDependencies.AsyncConnection;
        }

        public async Task CalculateCounter(int actualDefconStatus)
        {
            if (sqLiteAsyncConnection == null) return;
            var checkList = await Task.Factory.StartNew(async () =>
                            {
                                return await sqLiteAsyncConnection.Table<CheckListEntry>().Where(x => !x.Deleted).ToListAsync();
                            }).Result;

            if (checkList != null && checkList.Count > 0)
            {
                switch (actualDefconStatus)
                {
                    case 1:
                        _counter1Value = checkList.Where((x) => x.DefconStatus == 1).Where((x) => x.Checked == false).Count();
                        _counter2Value = checkList.Where((x) => x.DefconStatus == 2).Where((x) => x.Checked == false).Count();
                        _counter3Value = checkList.Where((x) => x.DefconStatus == 3).Where((x) => x.Checked == false).Count();
                        _counter4Value = checkList.Where((x) => x.DefconStatus == 4).Where((x) => x.Checked == false).Count();
                        _counter5Value = checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == false).Count();

                        if (checkList.Where((x) => x.DefconStatus == 1).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 1).Count() > 0) _counter1Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 1).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 1).Count()) _counter1Color = Color.ParseColor("#00FF00");
                        else _counter1Color = Color.ParseColor("#FFB300");

                        if (checkList.Where((x) => x.DefconStatus == 2).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 2).Count() > 0) _counter2Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 2).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 2).Count()) _counter2Color = Color.ParseColor("#00FF00");
                        else _counter2Color = Color.ParseColor("#FFB300");

                        if (checkList.Where((x) => x.DefconStatus == 3).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 3).Count() > 0) _counter3Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 3).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 3).Count()) _counter3Color = Color.ParseColor("#00FF00");
                        else _counter3Color = Color.ParseColor("#FFB300");

                        if (checkList.Where((x) => x.DefconStatus == 4).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 4).Count() > 0) _counter4Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 4).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 4).Count()) _counter4Color = Color.ParseColor("#00FF00");
                        else _counter4Color = Color.ParseColor("#FFB300");

                        if (checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 5).Count() > 0) _counter5Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 5).Count()) _counter5Color = Color.ParseColor("#00FF00");
                        else _counter5Color = Color.ParseColor("#FFB300");
                        break;

                    case 2:
                        _counter1Value = 0;
                        _counter1Color = Color.ParseColor("#00FF00");
                        _counter2Value = checkList.Where((x) => x.DefconStatus == 2).Where((x) => x.Checked == false).Count();
                        _counter3Value = checkList.Where((x) => x.DefconStatus == 3).Where((x) => x.Checked == false).Count();
                        _counter4Value = checkList.Where((x) => x.DefconStatus == 4).Where((x) => x.Checked == false).Count();
                        _counter5Value = checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == false).Count();

                        if (checkList.Where((x) => x.DefconStatus == 2).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 2).Count() > 0) _counter2Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 2).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 2).Count()) _counter2Color = Color.ParseColor("#00FF00");
                        else _counter2Color = Color.ParseColor("#FFB300");

                        if (checkList.Where((x) => x.DefconStatus == 3).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 3).Count() > 0) _counter3Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 3).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 3).Count()) _counter3Color = Color.ParseColor("#00FF00");
                        else _counter3Color = Color.ParseColor("#FFB300");

                        if (checkList.Where((x) => x.DefconStatus == 4).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 4).Count() > 0) _counter4Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 4).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 4).Count()) _counter4Color = Color.ParseColor("#00FF00");
                        else _counter4Color = Color.ParseColor("#FFB300");

                        if (checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 5).Count() > 0) _counter5Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 5).Count()) _counter5Color = Color.ParseColor("#00FF00");
                        else _counter5Color = Color.ParseColor("#FFB300");
                        break;

                    case 3:
                        _counter1Value = 0;
                        _counter2Value = 0;
                        _counter1Color = Color.ParseColor("#00FF00");
                        _counter2Color = Color.ParseColor("#00FF00");
                        _counter3Value = checkList.Where((x) => x.DefconStatus == 3).Where((x) => x.Checked == false).Count();
                        _counter4Value = checkList.Where((x) => x.DefconStatus == 4).Where((x) => x.Checked == false).Count();
                        _counter5Value = checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == false).Count();

                        if (checkList.Where((x) => x.DefconStatus == 3).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 3).Count() > 0) _counter3Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 3).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 3).Count()) _counter3Color = Color.ParseColor("#00FF00");
                        else _counter3Color = Color.ParseColor("#FFB300");

                        if (checkList.Where((x) => x.DefconStatus == 4).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 4).Count() > 0) _counter4Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 4).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 4).Count()) _counter4Color = Color.ParseColor("#00FF00");
                        else _counter4Color = Color.ParseColor("#FFB300");

                        if (checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 5).Count() > 0) _counter5Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 5).Count()) _counter5Color = Color.ParseColor("#00FF00");
                        else _counter5Color = Color.ParseColor("#FFB300");
                        break;

                    case 4:
                        _counter1Value = 0;
                        _counter2Value = 0;
                        _counter3Value = 0;
                        _counter1Color = Color.ParseColor("#00FF00");
                        _counter2Color = Color.ParseColor("#00FF00");
                        _counter3Color = Color.ParseColor("#00FF00");
                        _counter4Value = checkList.Where((x) => x.DefconStatus == 4).Where((x) => x.Checked == false).Count();
                        _counter5Value = checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == false).Count();

                        if (checkList.Where((x) => x.DefconStatus == 4).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 4).Count() > 0) _counter4Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 4).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 4).Count()) _counter4Color = Color.ParseColor("#00FF00");
                        else _counter4Color = Color.ParseColor("#FFB300");

                        if (checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 5).Count() > 0) _counter5Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 5).Count()) _counter5Color = Color.ParseColor("#00FF00");
                        else _counter5Color = Color.ParseColor("#FFB300");
                        break;

                    case 5:
                        _counter1Value = 0;
                        _counter2Value = 0;
                        _counter3Value = 0;
                        _counter4Value = 0;
                        _counter1Color = Color.ParseColor("#00FF00");
                        _counter2Color = Color.ParseColor("#00FF00");
                        _counter3Color = Color.ParseColor("#00FF00");
                        _counter4Color = Color.ParseColor("#00FF00");
                        _counter5Value = checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == false).Count();

                        if (checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == true).Count() == 0 && checkList.Where((x) => x.DefconStatus == 5).Count() > 0) _counter5Color = Color.ParseColor("#FC0800");
                        else if (checkList.Where((x) => x.DefconStatus == 5).Where((x) => x.Checked == true).Count() == checkList.Where((x) => x.DefconStatus == 5).Count()) _counter5Color = Color.ParseColor("#00FF00");
                        else _counter5Color = Color.ParseColor("#FFB300");
                        break;

                    default:
                        break;
                }
            }
            else
            {
                _counter1Color = Color.Green;
                _counter2Color = Color.Green;
                _counter3Color = Color.Green;
                _counter4Color = Color.Green;
                _counter5Color = Color.Green;

                _counter1Value = 0;
                _counter2Value = 0;
                _counter3Value = 0;
                _counter4Value = 0;
                _counter5Value = 0;
            }
            try
            {
                _badgeCounterValue = _counter1Value + _counter2Value + _counter3Value + _counter4Value + _counter5Value;
                if (_badgeCounterValue == 0) CrossBadge.Current.ClearBadge();
                else CrossBadge.Current.SetBadge(_badgeCounterValue);
            }
            catch { }
        }
    }
}