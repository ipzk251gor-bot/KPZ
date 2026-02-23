using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;

namespace ChatLibrary
{
    [DataContract]
    public sealed class ChatUserInfo
    {
        [DataMember] public int Id { get; set; }
        [DataMember] public string Name { get; set; } = "";
    }

    [DataContract]
    public sealed class ChatMessage
    {
        [DataMember] public DateTime TimeUtc { get; set; }
        [DataMember] public int FromId { get; set; }
        [DataMember] public string FromName { get; set; } = "";
        [DataMember] public int ToId { get; set; }
        [DataMember] public string ToName { get; set; } = "";
        [DataMember] public string Text { get; set; } = "";
        [DataMember] public bool IsPrivate { get; set; }
    }

    public interface IChatServiceCallback
    {
        [OperationContract(IsOneWay = true)]
        void ReceiveMessage(ChatMessage message);

        [OperationContract(IsOneWay = true)]
        void UpdateUsers(List<ChatUserInfo> users);
    }

    [ServiceContract(CallbackContract = typeof(IChatServiceCallback))]
    public interface IChatService
    {
        [OperationContract]
        int Connect(string username);

        [OperationContract(IsOneWay = true)]
        void Disconnect(int id);

        [OperationContract(IsOneWay = true)]
        void SendToAll(int fromId, string message);

        [OperationContract(IsOneWay = true)]
        void SendPrivate(int fromId, int toId, string message);
    }

    internal sealed class ChatUser
    {
        public int Id { get; set; }
        public string Name { get; set; } = "";
        public OperationContext Context { get; set; }
    }

    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single, ConcurrencyMode = ConcurrencyMode.Multiple)]
    public sealed class ChatService : IChatService
    {
        readonly object _sync = new object();
        readonly List<ChatUser> _users = new List<ChatUser>();
        int _nextId = 1;

        public int Connect(string username)
        {
            username = (username ?? "").Trim();
            if (string.IsNullOrWhiteSpace(username))
                username = "User";

            username = NormalizeName(username);

            ChatUser user;
            lock (_sync)
            {
                if (_users.Any(u => string.Equals(u.Name, username, StringComparison.OrdinalIgnoreCase)))
                    username = MakeUnique(username);

                user = new ChatUser
                {
                    Id = _nextId++,
                    Name = username,
                    Context = OperationContext.Current
                };

                _users.Add(user);
            }

            BroadcastSystem($"{user.Name} приєднався до чату");
            BroadcastUsers();

            return user.Id;
        }

        public void Disconnect(int id)
        {
            ChatUser removed = null;

            lock (_sync)
            {
                removed = _users.FirstOrDefault(u => u.Id == id);
                if (removed != null)
                    _users.Remove(removed);
            }

            if (removed != null)
            {
                BroadcastSystem($"{removed.Name} покинув чат");
                BroadcastUsers();
            }
        }

        public void SendToAll(int fromId, string message)
        {
            var from = GetUser(fromId);
            if (from == null)
                return;

            var text = (message ?? "").Trim();
            if (string.IsNullOrWhiteSpace(text))
                return;

            var msg = new ChatMessage
            {
                TimeUtc = DateTime.UtcNow,
                FromId = from.Id,
                FromName = from.Name,
                Text = text,
                IsPrivate = false
            };

            foreach (var u in SnapshotUsers())
                SafeSendMessage(u, msg);
        }

        public void SendPrivate(int fromId, int toId, string message)
        {
            var from = GetUser(fromId);
            var to = GetUser(toId);
            if (from == null || to == null)
                return;

            var text = (message ?? "").Trim();
            if (string.IsNullOrWhiteSpace(text))
                return;

            var msg = new ChatMessage
            {
                TimeUtc = DateTime.UtcNow,
                FromId = from.Id,
                FromName = from.Name,
                ToId = to.Id,
                ToName = to.Name,
                Text = text,
                IsPrivate = true
            };

            SafeSendMessage(to, msg);
            if (from.Id != to.Id)
                SafeSendMessage(from, msg);
        }

        void BroadcastUsers()
        {
            var users = SnapshotUsers()
                .Select(u => new ChatUserInfo { Id = u.Id, Name = u.Name })
                .OrderBy(u => u.Name)
                .ToList();

            foreach (var u in SnapshotUsers())
            {
                try
                {
                    u.Context.GetCallbackChannel<IChatServiceCallback>().UpdateUsers(users);
                }
                catch
                {
                }
            }
        }

        void BroadcastSystem(string text)
        {
            var msg = new ChatMessage
            {
                TimeUtc = DateTime.UtcNow,
                FromId = 0,
                FromName = "System",
                Text = text,
                IsPrivate = false
            };

            foreach (var u in SnapshotUsers())
                SafeSendMessage(u, msg);
        }

        void SafeSendMessage(ChatUser user, ChatMessage msg)
        {
            try
            {
                user.Context.GetCallbackChannel<IChatServiceCallback>().ReceiveMessage(msg);
            }
            catch
            {
            }
        }

        ChatUser GetUser(int id)
        {
            lock (_sync)
                return _users.FirstOrDefault(u => u.Id == id);
        }

        List<ChatUser> SnapshotUsers()
        {
            lock (_sync)
                return _users.ToList();
        }

        static string NormalizeName(string name)
        {
            name = new string(name.Where(c => !char.IsControl(c)).ToArray());
            if (name.Length > 24)
                name = name.Substring(0, 24);
            return name.Trim();
        }

        string MakeUnique(string baseName)
        {
            var i = 2;
            var name = baseName;
            lock (_sync)
            {
                while (_users.Any(u => string.Equals(u.Name, name, StringComparison.OrdinalIgnoreCase)))
                {
                    name = baseName + "_" + i;
                    i++;
                }
            }
            return name;
        }
    }
}
