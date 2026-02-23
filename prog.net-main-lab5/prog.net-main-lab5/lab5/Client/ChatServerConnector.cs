using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using ChatLibrary;

namespace Client
{
    public sealed class ChatServerConnector : IChatServiceCallback, IDisposable
    {
        readonly object _sync = new object();

        DuplexChannelFactory<IChatService> _factory = null;
        IChatService _proxy = null;

        public int UserId { get; private set; }
        public string UserName { get; private set; } = "";
        public IReadOnlyList<ChatUserInfo> Users { get; private set; } = Array.Empty<ChatUserInfo>();

        public event Action<ChatMessage> MessageReceived;
        public event Action<IReadOnlyList<ChatUserInfo>> UsersUpdated;
        public event Action<string> StatusChanged;

        public void Connect(string username)
        {
            username = (username ?? "").Trim();
            if (string.IsNullOrWhiteSpace(username))
                throw new ArgumentException("Введіть ім'я (нікнейм).");

            lock (_sync)
            {
                if (_proxy != null)
                    return;

                var ctx = new InstanceContext(this);
                _factory = new DuplexChannelFactory<IChatService>(ctx, "ChatTcp");
                _proxy = _factory.CreateChannel();
            }

            var id = _proxy.Connect(username);
            UserId = id;
            UserName = username;
            StatusChanged?.Invoke("Підключено");
        }

        public void Disconnect()
        {
            lock (_sync)
            {
                try
                {
                    if (_proxy != null && UserId != 0)
                        _proxy.Disconnect(UserId);
                }
                catch
                {
                }

                try
                {
                    if (_factory != null)
                        _factory.Close();
                }
                catch
                {
                    try { if (_factory != null) _factory.Abort(); } catch { }
                }

                _proxy = null;
                _factory = null;
                Users = Array.Empty<ChatUserInfo>();
                UserId = 0;
            }

            StatusChanged?.Invoke("Відключено");
        }

        public void SendToAll(string text)
        {
            var msg = (text ?? "").Trim();
            if (string.IsNullOrWhiteSpace(msg))
                return;

            if (_proxy != null)
                _proxy.SendToAll(UserId, msg);
        }

        public void SendPrivate(int toId, string text)
        {
            var msg = (text ?? "").Trim();
            if (string.IsNullOrWhiteSpace(msg))
                return;

            if (_proxy != null)
                _proxy.SendPrivate(UserId, toId, msg);
        }

        public void ReceiveMessage(ChatMessage message)
        {
            var h = MessageReceived;
            if (h != null)
                h(message);
        }

        public void UpdateUsers(List<ChatUserInfo> users)
        {
            Users = (users ?? new List<ChatUserInfo>()).ToList();
            var h = UsersUpdated;
            if (h != null)
                h(Users);
        }

        public void Dispose()
        {
            Disconnect();
        }
    }
}
