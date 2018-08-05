using System;

namespace MyDEFCON.Services
{
    public interface IEventService
    {
        event EventHandler MenuItemPressedEvent;
        void OnMenuItemPressedEvent(MenuItemPressedEventArgs eventArgs);
        event EventHandler DefconStatusChangedEvent;
        void OnDefconStatusChangedEvent(DefconStatusChangedEventArgs eventArgs);
        event EventHandler ChecklistUpdatedEvent;
        void OnChecklistUpdatedEvent();
    }
    public class EventService : IEventService
    {
        public static EventService Instance() => new EventService();
        public event EventHandler MenuItemPressedEvent;
        public event EventHandler DefconStatusChangedEvent;
        public event EventHandler ChecklistUpdatedEvent;
        public void OnMenuItemPressedEvent(MenuItemPressedEventArgs eventArgs) => MenuItemPressedEvent?.Invoke(this, eventArgs);
        public void OnDefconStatusChangedEvent(DefconStatusChangedEventArgs eventArgs) => DefconStatusChangedEvent?.Invoke(this, eventArgs);
        public void OnChecklistUpdatedEvent() => DefconStatusChangedEvent?.Invoke(this, null);
    }

    public class MenuItemPressedEventArgs : EventArgs
    {
        public MenuItemPressedEventArgs(string menuItemTitle, string fragmentTag)
        {
            MenuItemTitle = menuItemTitle;
            FragmentTag = fragmentTag;
        }
        public string MenuItemTitle { get; }
        public string FragmentTag { get; }
    }

    public class DefconStatusChangedEventArgs : EventArgs
    {
        public DefconStatusChangedEventArgs(int newDefconStatus) => NewDefconStatus = newDefconStatus;
        public int NewDefconStatus { get; }
    }
}