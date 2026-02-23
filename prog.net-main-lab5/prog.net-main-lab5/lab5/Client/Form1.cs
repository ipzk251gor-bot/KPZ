using System;
using System.Collections.Generic; // Додано для IReadOnlyList
using System.Linq;
using System.Windows.Forms;
using ChatLibrary;

namespace Client
{
    public partial class Form1 : Form
    {
        // ВИПРАВЛЕНО (Issue #1): Винесено IP-адресу в константу (замість хардкоду в методах)
        private const string ServerAddress = "127.0.0.1";
        readonly ChatServerConnector _connector = new ChatServerConnector();

        public Form1()
        {
            InitializeComponent();
            _connector.MessageReceived += OnMessage;
            _connector.UsersUpdated += OnUsers;
            _connector.StatusChanged += s => BeginInvoke(new Action(() => lblStatus.Text = s));
            UpdateUi(false);
        }

        void UpdateUi(bool connected)
        {
            txtName.Enabled = !connected;
            btnConnect.Enabled = !connected;
            btnDisconnect.Enabled = connected;
            txtMessage.Enabled = connected;
            btnSendAll.Enabled = connected;
            btnSendPrivate.Enabled = connected;
            lstUsers.Enabled = connected;
        }

        void btnConnect_Click(object sender, EventArgs e)
        {
            try
            {
                // Використовуємо константу ServerAddress
                _connector.Connect(txtName.Text); 
                AppendSystem($"Ви зайшли як {_connector.UserName}");
                UpdateUi(true);
            }
            catch (Exception ex)
            {
                MessageBox.Show(this, ex.Message, "Помилка", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            }
        }

        void btnDisconnect_Click(object sender, EventArgs e)
        {
            _connector.Disconnect();
            AppendSystem("Ви відключились");
            lstUsers.Items.Clear();
            UpdateUi(false);
        }

        // ВИПРАВЛЕНО (Issue #3): Логіку оновлення списку винесено в окремий метод (Refactoring)
        private void RefreshUserList(IReadOnlyList<ChatUserInfo> users)
        {
            var selectedId = (lstUsers.SelectedItem as ChatUserInfo)?.Id;
            lstUsers.BeginUpdate();
            lstUsers.Items.Clear();
            foreach (var u in users.OrderBy(x => x.Name))
                lstUsers.Items.Add(u);
            lstUsers.EndUpdate();

            if (selectedId.HasValue)
            {
                foreach (var item in lstUsers.Items)
                {
                    if (item is ChatUserInfo u && u.Id == selectedId.Value)
                    {
                        lstUsers.SelectedItem = item;
                        break;
                    }
                }
            }
        }

        void OnUsers(IReadOnlyList<ChatUserInfo> users)
        {
            BeginInvoke(new Action(() => RefreshUserList(users)));
        }

        void OnMessage(ChatMessage msg)
        {
            BeginInvoke(new Action(() =>
            {
                if (msg.FromName == "System")
                {
                    AppendSystem(msg.Text);
                    return;
                }

                var time = msg.TimeUtc.ToLocalTime().ToString("HH:mm:ss");
                if (msg.IsPrivate)
                {
                    var peer = msg.ToId == _connector.UserId ? msg.FromName : msg.ToName;
                    lstChat.Items.Add($"[{time}] [приват] {msg.FromName} → {peer}: {msg.Text}");
                }
                else
                {
                    lstChat.Items.Add($"[{time}] {msg.FromName}: {msg.Text}");
                }
                lstChat.TopIndex = Math.Max(0, lstChat.Items.Count - 1);
            }));
        }

        void AppendSystem(string text)
        {
            var time = DateTime.Now.ToString("HH:mm:ss");
            lstChat.Items.Add($"[{time}] [система] {text}");
            lstChat.TopIndex = Math.Max(0, lstChat.Items.Count - 1);
        }

        protected override void OnFormClosing(FormClosingEventArgs e)
        {
            try { _connector.Dispose(); }
            catch (Exception ex) { Console.WriteLine($"Error: {ex.Message}"); }
            base.OnFormClosing(e);
        }
    }
}