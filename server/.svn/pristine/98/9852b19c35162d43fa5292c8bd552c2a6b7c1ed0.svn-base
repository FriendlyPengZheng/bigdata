package alarm_client;

import java.util.ArrayList;

import main_taomeechat.TaomeeChatMainActivity;
import message_push_client.MsgPushUtils;
import stat_app_proto.AppLoginRequest.StatAppLoginRequest;
import stat_app_proto.AppLoginRequest.StatAppLoginResponse;
import stat_app_proto.AppPullMsg.AppPullMsgRequest;
import stat_app_proto.AppPullMsg.AppPullMsgResponse;
import alarm_client.AlarmTcpClient.LoginResponseCallback;
import alarm_client.AlarmTcpClient.PullMsgResponseCallback;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import chat_data.base.LocalDatabaseHelper;
import chat_setting.SettingDBTable;
import chat_setting.Settings;

import com.taomee.chat.R;

public class AlarmClient {
	private static final String TAG = "AlarmClient";
	
	public static final String ALARM_SERVER_HOST = "61.155.182.20";
	public static final int ALARM_SERVER_PORT = 19400;
	
	public static String m_UserName = "";
//	public static String m_Token = null;
	
	private static AlarmClient s_Instance = null;
	
	/**
	 * 获取AlarmClient的静态实例
	 * @return 
	 */
	public static AlarmClient Instance() {
		return s_Instance;
	}
	
	/**
	 * 初始化静态实例，请在主线程开始的是时候调用
	 * @param context
	 * @return
	 */
	public static AlarmClient InitInstance(Context context, LocalDatabaseHelper db) {
		synchronized (AlarmClient.class) {
			if (null == s_Instance) {
				s_Instance = new AlarmClient(context, db);
				s_Instance.initialize();
			}
		}
		return s_Instance;
	}
	
//	private LoginStatusManager m_accountManager = null;
	private Settings m_setting = null;
	
	private AlarmMsgCenter m_msgCenter = null;
	private Context m_context;
	private Handler m_handler;
	
	private AlarmClient(Context context, LocalDatabaseHelper db) {
		m_context = context;
//		m_accountManager = new LoginStatusManager(context);
		m_msgCenter = new AlarmMsgCenter(db.getAlarmMsgTable());
		m_setting = new Settings(db.getSettingDBTable());
		m_handler = new Handler(context.getMainLooper());
	}
	
	private boolean initialize() {
		return true;
	}
	
	public boolean isLogin() {
//		return (null != m_accountManager.getLoginStatus(LoginStatusManager.ACCOUNT_TYPE_ALARM));
		return (!m_setting.getSettingTable().getSettingValue(SettingDBTable.BD_TOKEN).equals("")
				&& !m_setting.getSettingTable().getSettingValue(SettingDBTable.USER_NAME).equals(""));
	}
	
	public String getLoginName() {
		return m_setting.getSettingTable().getSettingValue(SettingDBTable.USER_NAME);
	}
	
	
	/**
	 * 登录方法的回调接口类
	 * @author lance
	 *
	 */
	public interface Logincallback {
		/**
		 * 登录结果的回调方法
		 * @param result
		 */
		void onResponse(final int result);
	}
	
	public boolean login(final String userName, final String password, final String mobile, final Logincallback callback) {
		if (userName == null || userName.length() == 0 
				|| password == null || password.length() == 0 
				) {
			return false;
		}
		
		StatAppLoginRequest loginRequest = StatAppLoginRequest.newBuilder().setUserName(userName).setPassword(password)
				.setMobile(mobile)
				.setToken(m_setting.getSettingTable().getSettingValue(SettingDBTable.BD_TOKEN))
				.setDeviceType("3")
				.build();
		AlarmTcpClient tcpClient = new AlarmTcpClient(ALARM_SERVER_HOST, ALARM_SERVER_PORT);
		LoginResponseCallback loginResponse = new LoginResponseCallback() {
			@Override
			public void onResponse(StatAppLoginResponse response) {
				int ret = -1;
				if (null != response) {
					switch (response.getRet()) {
					case 0:
						m_setting.getSettingTable().setSetingValue(SettingDBTable.USER_NAME, userName);
//						AccountInfo info = new AccountInfo(LoginStatusManager.ACCOUNT_TYPE_ALARM, userName, "", AccountInfo.STATUS_LOGIN, m_Token);
//						m_accountManager.login(info);
						break;
						
					default:
					}
					ret = response.getRet();
				}
				callback.onResponse(ret);
			}
		};
		
		return tcpClient.sendLoginRequest(loginRequest, loginResponse);
	}
	
	public void onLoginSuccess() {
		
	}
	
//	public boolean logout() {
//		m_UserName = null;
//		m_Token = null;
//		m_accountManager.logout();
//		return true;
//	}
	
	public boolean changeUser(final String userName, final String password, final Logincallback callbcak) {
		return true;
	}
	
	public boolean sendPullMsgRequest(final ArrayList<String> msgList) {
		if (null == msgList)
			return false;
		
		AppPullMsgRequest.Builder builder = AppPullMsgRequest.newBuilder();
		builder.setUserName(getLoginName());
		if (msgList.size() == 0) {
			builder.addMsgId("");
		}
		else {
			for(int i=0; i<msgList.size(); ++i) {
				builder.addMsgId(msgList.get(i));
			}
		}
		builder.setToken(m_setting.getSettingTable().getSettingValue(SettingDBTable.BD_TOKEN));
		
		AppPullMsgRequest request = builder.build();
		Log.d(TAG, request.toString());
		PullMsgResponseCallback callback = new PullMsgResponseCallback() {
			@Override
			public void onResponse(AppPullMsgResponse response) {
				if (null == response) {
					AlarmMsgPuller.pullAlarmMsg();
					showToast(m_context.getString(R.string.pull_msg_timeout));
				}
				
				if (null != response) {
					final int count = m_msgCenter.recvedMsg(response);
					if (count > 0) {
						Intent aIntent = new Intent();
//						aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						aIntent.setAction(MsgPushUtils.ACTION_CHECKED);
						if (!TaomeeChatMainActivity.RUNNING_FLAG ) {
							m_context.startService(new Intent(MsgPushUtils.ACTION_CHECKED));
						}
						else {
							aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							aIntent.setClass(m_context, TaomeeChatMainActivity.class);
							m_context.startActivity(aIntent);
						}
						
						if (m_msgCenter.getPullCount() > 0) 
							AlarmMsgPuller.pullAlarmMsg();
					}
					else {
						showToast(0-count + m_context.getString(R.string.pull_msg_failed));
					}
				}
			}
		};
		AlarmTcpClient tcpClient = new AlarmTcpClient(ALARM_SERVER_HOST, ALARM_SERVER_PORT);
		return tcpClient.sendPullMsgRequest(request, callback);
	}
	
	public void showToast(final String msg) {
		m_handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(m_context, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public AlarmMsgCenter getMsgCenter() {
		return m_msgCenter;
	}
	
	public void onEnd() {
		m_msgCenter.onEnd();
	}
}
