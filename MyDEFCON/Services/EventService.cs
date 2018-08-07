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
        void OnChecklistUpdatedEvent(EventArgs eventArgs);
        event EventHandler BlockConnectionEvent;
        void OnBlockConnectionEvent(BlockConnectionEventArgs eventArgs);
    }
    public class EventService : IEventService
    {
        public static EventService Instance() => new EventService();
        public event EventHandler MenuItemPressedEvent;
        public event EventHandler DefconStatusChangedEvent;
        public event EventHandler ChecklistUpdatedEvent;
        public event EventHandler BlockConnectionEvent;
        public void OnMenuItemPressedEvent(MenuItemPressedEventArgs eventArgs) => MenuItemPressedEvent?.Invoke(this, eventArgs);
        public void OnDefconStatusChangedEvent(DefconStatusChangedEventArgs eventArgs) => DefconStatusChangedEvent?.Invoke(this, eventArgs);
        public void OnChecklistUpdatedEvent(EventArgs eventArgs) => ChecklistUpdatedEvent?.Invoke(this, eventArgs);
        public void OnBlockConnectionEvent(BlockConnectionEventArgs eventArgs) => BlockConnectionEvent?.Invoke(this, eventArgs);
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

    public class BlockConnectionEventArgs : EventArgs
    {
        public BlockConnectionEventArgs(bool blocked) => Blocked = blocked;
        public bool Blocked { get; }
    }
}