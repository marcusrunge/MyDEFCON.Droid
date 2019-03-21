using Android.Graphics;
using Android.Support.V7.Widget;
using Android.Views;
using Android.Widget;
using MyDEFCON.Models;
using System;
using System.Collections.Generic;

namespace MyDEFCON.Adapter
{
    enum ActionType { LongClick, CheckedChange, AfterTextChanged }
    class ChecklistRecyclerViewAdapter : RecyclerView.Adapter
    {
        public event EventHandler<Tuple<object, int, ActionType>> ViewHolderAction;
        public List<CheckListEntry> _checkListEntryList;
        public int ClickedItem { get; set; }
        View _previousView;

        public ChecklistRecyclerViewAdapter(List<CheckListEntry> checkListEntryList)
        {
            ClickedItem = -1;
            _checkListEntryList = checkListEntryList;
            ViewHolderAction += (s, e) =>
            {
                if (e.Item3 == ActionType.LongClick)
                {
                    if (_previousView != null) _previousView.SetBackgroundColor(Color.Transparent);
                    ClickedItem = e.Item2;
                    _previousView = e.Item1 as LinearLayout;
                };
            };
        }
        public override int ItemCount => _checkListEntryList.Count;

        public override void OnBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            var checklistItemViewHolder = holder as ChecklistItemViewHolder;
            checklistItemViewHolder.CheckListEntryChecked.Checked = _checkListEntryList[position].Checked;
            checklistItemViewHolder.CheckListEntry.Text = _checkListEntryList[position].Item;
            holder.ItemView.SetBackgroundColor(ClickedItem == position ? Color.ParseColor("#80ff0000") : Color.Transparent);
        }

        public override RecyclerView.ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.From(parent.Context).Inflate(Resource.Layout.CheckListItemDataTemplate, parent, false);
            return new ChecklistItemViewHolder(view, OnViewHolderAction);
        }

        public void OnViewHolderAction(Tuple<object, int, ActionType> e) => ViewHolderAction?.Invoke(this, e);

        class ChecklistItemViewHolder : RecyclerView.ViewHolder
        {
            public CheckBox CheckListEntryChecked { get; set; }
            public EditText CheckListEntry { get; set; }
            public ChecklistItemViewHolder(View view, Action<Tuple<object, int, ActionType>> action) : base(view)
            {
                CheckListEntryChecked = view.FindViewById<CheckBox>(Resource.Id.CheckListEntryChecked);
                CheckListEntry = view.FindViewById<EditText>(Resource.Id.CheckListEntry);
                CheckListEntry.LongClick += (s, e) =>
                {
                    view.SetBackgroundColor(Color.ParseColor("#80ff0000"));
                    action(new Tuple<object, int, ActionType>(view, LayoutPosition, ActionType.LongClick));
                };
                CheckListEntryChecked.CheckedChange += (s, e) => action(new Tuple<object, int, ActionType>(s, LayoutPosition, ActionType.CheckedChange));
                CheckListEntry.AfterTextChanged += (s, e) => action(new Tuple<object, int, ActionType>(s, LayoutPosition, ActionType.AfterTextChanged));
            }
        }
    }
}