namespace Client
{
    partial class Form1
    {
        System.ComponentModel.IContainer components = null;

        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        void InitializeComponent()
        {
            this.txtName = new System.Windows.Forms.TextBox();
            this.btnConnect = new System.Windows.Forms.Button();
            this.btnDisconnect = new System.Windows.Forms.Button();
            this.lstUsers = new System.Windows.Forms.ListBox();
            this.lstChat = new System.Windows.Forms.ListBox();
            this.txtMessage = new System.Windows.Forms.TextBox();
            this.btnSendAll = new System.Windows.Forms.Button();
            this.btnSendPrivate = new System.Windows.Forms.Button();
            this.lblStatus = new System.Windows.Forms.Label();
            this.SuspendLayout();
            //
            // txtName
            //
            this.txtName.Location = new System.Drawing.Point(12, 12);
            this.txtName.Name = "txtName";
            this.txtName.Size = new System.Drawing.Size(180, 22);
            this.txtName.TabIndex = 0;
            //
            // btnConnect
            //
            this.btnConnect.Location = new System.Drawing.Point(198, 11);
            this.btnConnect.Name = "btnConnect";
            this.btnConnect.Size = new System.Drawing.Size(94, 25);
            this.btnConnect.TabIndex = 1;
            this.btnConnect.Text = "Увійти";
            this.btnConnect.UseVisualStyleBackColor = true;
            this.btnConnect.Click += new System.EventHandler(this.btnConnect_Click);
            //
            // btnDisconnect
            //
            this.btnDisconnect.Location = new System.Drawing.Point(298, 11);
            this.btnDisconnect.Name = "btnDisconnect";
            this.btnDisconnect.Size = new System.Drawing.Size(94, 25);
            this.btnDisconnect.TabIndex = 2;
            this.btnDisconnect.Text = "Вийти";
            this.btnDisconnect.UseVisualStyleBackColor = true;
            this.btnDisconnect.Click += new System.EventHandler(this.btnDisconnect_Click);
            //
            // lblStatus
            //
            this.lblStatus.AutoSize = true;
            this.lblStatus.Location = new System.Drawing.Point(408, 15);
            this.lblStatus.Name = "lblStatus";
            this.lblStatus.Size = new System.Drawing.Size(78, 16);
            this.lblStatus.TabIndex = 3;
            this.lblStatus.Text = "Відключено";
            //
            // lstUsers
            //
            this.lstUsers.FormattingEnabled = true;
            this.lstUsers.ItemHeight = 16;
            this.lstUsers.Location = new System.Drawing.Point(12, 48);
            this.lstUsers.Name = "lstUsers";
            this.lstUsers.Size = new System.Drawing.Size(180, 340);
            this.lstUsers.TabIndex = 4;
            //
            // lstChat
            //
            this.lstChat.FormattingEnabled = true;
            this.lstChat.ItemHeight = 16;
            this.lstChat.Location = new System.Drawing.Point(198, 48);
            this.lstChat.Name = "lstChat";
            this.lstChat.Size = new System.Drawing.Size(574, 340);
            this.lstChat.TabIndex = 5;
            //
            // txtMessage
            //
            this.txtMessage.Location = new System.Drawing.Point(198, 400);
            this.txtMessage.Name = "txtMessage";
            this.txtMessage.Size = new System.Drawing.Size(380, 22);
            this.txtMessage.TabIndex = 6;
            //
            // btnSendAll
            //
            this.btnSendAll.Location = new System.Drawing.Point(584, 398);
            this.btnSendAll.Name = "btnSendAll";
            this.btnSendAll.Size = new System.Drawing.Size(90, 25);
            this.btnSendAll.TabIndex = 7;
            this.btnSendAll.Text = "В чат";
            this.btnSendAll.UseVisualStyleBackColor = true;
            this.btnSendAll.Click += new System.EventHandler(this.btnSendAll_Click);
            //
            // btnSendPrivate
            //
            this.btnSendPrivate.Location = new System.Drawing.Point(680, 398);
            this.btnSendPrivate.Name = "btnSendPrivate";
            this.btnSendPrivate.Size = new System.Drawing.Size(92, 25);
            this.btnSendPrivate.TabIndex = 8;
            this.btnSendPrivate.Text = "Особисто";
            this.btnSendPrivate.UseVisualStyleBackColor = true;
            this.btnSendPrivate.Click += new System.EventHandler(this.btnSendPrivate_Click);
            //
            // Form1
            //
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(784, 441);
            this.Controls.Add(this.btnSendPrivate);
            this.Controls.Add(this.btnSendAll);
            this.Controls.Add(this.txtMessage);
            this.Controls.Add(this.lstChat);
            this.Controls.Add(this.lstUsers);
            this.Controls.Add(this.lblStatus);
            this.Controls.Add(this.btnDisconnect);
            this.Controls.Add(this.btnConnect);
            this.Controls.Add(this.txtName);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.Name = "Form1";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "WCF Chat";
            this.ResumeLayout(false);
            this.PerformLayout();
        }

        private System.Windows.Forms.TextBox txtName;
        private System.Windows.Forms.Button btnConnect;
        private System.Windows.Forms.Button btnDisconnect;
        private System.Windows.Forms.ListBox lstUsers;
        private System.Windows.Forms.ListBox lstChat;
        private System.Windows.Forms.TextBox txtMessage;
        private System.Windows.Forms.Button btnSendAll;
        private System.Windows.Forms.Button btnSendPrivate;
        private System.Windows.Forms.Label lblStatus;
    }
}
