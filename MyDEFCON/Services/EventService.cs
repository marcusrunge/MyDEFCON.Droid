using System;

namespace MyDEFCON.Services
{
    public interface IEventService
    {
        event EventHandler MenuItemPressedEvent;
        void OnMenuItemPressedEvent(MenuItemPressedEventArgs eventArgs);
        event EventHandler DefconStatusChangedEvent;
        void OnDefconStatusChangedEvent(DefconStatusChangedEventArgs eventArgs);
    }
    public class EventService : IEventService
    {
        public static EventService Instance() => new EventService();
        public event EventHandler MenuItemPressedEvent;
        public event EventHandler DefconStatusChangedEvent;
        public void OnMenuItemPressedEvent(MenuItemPressedEventArgs eventArgs) => MenuItemPressedEvent?.Invoke(this, eventArgs);
        public void OnDefconStatusChangedEvent(DefconStatusChangedEventArgs eventArgs) => DefconStatusChangedEvent?.Invoke(this, eventArgs);
    }

    public class MenuItemPressedEventArgs : EventArgs
    {
        public MenuItemPressedEventArgs(string menuItemTitle) => MenuItemTitle = menuItemTitle;
        public string MenuItemTitle { get; }
    }

    public class DefconStatusChangedEventArgs : EventArgs
    {
        public DefconStatusChangedEventArgs(int newDefconStatus) => NewDefconStatus = newDefconStatus;
        public int NewDefconStatus { get; }
    }
}