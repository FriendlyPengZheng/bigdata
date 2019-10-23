package main_taomeechat;

import message_push_client.MsgPushUtils;
import alarm_client.AlarmClient;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import chat_data.base.LocalDatabaseHelper;
import chat_setting.SettingDBTable;
import chat_setting.Settings;

import com.taomee.chat.R;

public class TaomeeChatBackgroundService extends Service {
	public static final String TAG = "service";
	private static final int ALARM_MSG_NF_REQUEST_CODDE = 0x0001;
	public static String ACTION = "com.taomee.chat.service";
	protected Settings m_setting;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		LocalDatabaseHelper db = new LocalDatabaseHelper(this);
		AlarmClient.InitInstance(this, db);
		m_setting = new Settings(db.getSettingDBTable());
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
		nm.cancelAll();
		super.onDestroy();
	}
	
	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (null == action || action.length() == 0)
			return;
		if (MsgPushUtils.ACTION_MESSAGE.equals(action)) {
			String title = intent.getStringExtra("title");
			String msg = intent.getStringExtra("msg");
			AlarmClient.Instance().getMsgCenter().recvedMsg(title, msg);
		} 
		else if (MsgPushUtils.ACTION_CHECKED.equals(action)) {
			int count = AlarmClient.Instance().getMsgCenter().getUnreadMsgCount();
			if (count > 0) {
				String nfTitle = this.getString(R.string.alarm_notification_title);
				String nfContent = String.valueOf(count) + this.getString(R.string.unread_msg_prompt);
				
				intent.setAction(MsgPushUtils.ACTION_BACKROUND);
				intent.setClass(this, TaomeeChatMainActivity.class);
				PendingIntent pdIntent = PendingIntent.getActivity(this, ALARM_MSG_NF_REQUEST_CODDE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				
				NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
				Notification nf = new Notification();
				nf.icon = getApplicationInfo().icon;
				nf.defaults = Notification.DEFAULT_ALL;
				nf.flags |= (Notification.FLAG_NO_CLEAR | Notification.FLAG_AUTO_CANCEL);
//				Log.d("", "BELL_FLAG : " + m_setting.getSettingTable().getSettingValue(SettingDBTable.BELL_ONCE));
				if (m_setting.getSettingTable().getSettingValue(SettingDBTable.BELL_ONCE).equals("1"))
					nf.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
				
				nf.setLatestEventInfo(this, nfTitle, nfContent, pdIntent);
				nm.notify(ALARM_MSG_NF_REQUEST_CODDE, nf);
			}
		}
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		handleIntent(intent);
		super.onStart(intent, startId);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
}
