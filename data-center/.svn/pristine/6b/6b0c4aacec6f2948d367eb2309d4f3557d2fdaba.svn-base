package main_taomeechat;

import message_push_client.BDCloudPushClient;
import message_push_client.MsgPushUtils;

import org.json.JSONException;
import org.json.JSONObject;

import alarm_client.AlarmClient;
import alarm_client.AlarmMsgPuller;
import alarm_ui.AlarmMsgOperatorBar;
import alarm_ui.AlarmMsgUIController;
import alarm_ui.LoginDialog;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import chat_data.base.LocalDatabaseHelper;
import chat_setting.SettingDBTable;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.taomee.chat.R;

public class TaomeeChatMainActivity extends ActionBarActivity implements OnDismissListener{
	private static final String TAG = "MAIN";
	
	public static boolean RUNNING_FLAG = false;
	private ProgressDialog m_loadingView = null;
	
	private LocalDatabaseHelper m_dbHelper = null;
	private AlarmClient m_alarmClient = null;
	private BDCloudPushClient m_pushClient = null;
	private AlarmMsgUIController m_alarmMsgCtl = null;
	
	private LinearLayout m_msgLayout = null;
	private LinearLayout m_actionBarLayout = null;
	
	private boolean m_alarmMode = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RUNNING_FLAG = true;
		stopService(new Intent("TaomeeChatService"));
		setContentView(R.layout.activity_taomee_chat_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		initialize();
		if (m_alarmClient.getMsgCenter().getPullCount() > 0) {
			AlarmMsgPuller.pullAlarmMsg();
		}
		handleIntent(getIntent());
	}
	
	@Override
	public void onStart() {
		super.onStart();

		PushManager.activityStarted(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);

		handleIntent(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode) {
		case KeyEvent.KEYCODE_BACK:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.msg_quit);
			builder.setPositiveButton(R.string.btn_quit, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					quit();
				}
			});
			builder.setNegativeButton(R.string.btn_cancel, null);
			builder.create().show();
			return true;
			
		case KeyEvent.KEYCODE_ALT_LEFT:
//			Log.d("", "press left key------------");
			break;
		case KeyEvent.KEYCODE_ALT_RIGHT:
//			Log.d("", "press rignt key------------");
			break;
			
		default:
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private void quit() {
		RUNNING_FLAG = false;
		m_alarmClient.getMsgCenter().doActionOnRows(AlarmMsgOperatorBar.ACTION_MARKED_ALL, 0, 0);
//		m_alarmClient.onEnd();
		this.finish();
	}
	
	private void initialize() {
		m_dbHelper = new LocalDatabaseHelper(this);
		m_alarmClient = AlarmClient.InitInstance(this, m_dbHelper);
		m_pushClient = BDCloudPushClient.InitInstance(this);
		m_pushClient.setNotification(getResources(), getPackageName(), getApplicationInfo().icon);
		m_alarmMsgCtl = new AlarmMsgUIController(this, m_alarmClient.getMsgCenter());
		m_msgLayout = (LinearLayout)findViewById(R.id.msg_layout);
		m_actionBarLayout = (LinearLayout)findViewById(R.id.operator_bar_layout);
		m_alarmMsgCtl.initialize(m_msgLayout, m_actionBarLayout);
		
		m_pushClient.startWork();
		m_loadingView = new ProgressDialog(this);
		m_loadingView.setMessage(this.getString(R.string.msg_app_init));
		m_loadingView.show();
		return;
	}
	
	/**
	 * ´¦ÀíIntent
	 * 
	 * @param intent
	 *            intent
	 */
	private boolean handleIntent(final Intent intent) {
		String action = intent.getAction();
//		Log.d("Main", "handleIntent--->" + action);
		if (MsgPushUtils.ACTION_RESPONSE.equals(action)) {
//			Log.d("Main", "Recv a Action Response!");

			String method = intent.getStringExtra(MsgPushUtils.RESPONSE_METHOD);

			if (PushConstants.METHOD_BIND.equals(method)) {
				int errorCode = intent.getIntExtra(MsgPushUtils.RESPONSE_ERRCODE, 0);
				if (errorCode == 0) {
					String content = intent
							.getStringExtra(MsgPushUtils.RESPONSE_CONTENT);
					String userid = "";

					try {
						JSONObject jsonContent = new JSONObject(content);
						JSONObject params = jsonContent
								.getJSONObject("response_params");
						userid = params.getString("user_id");
						m_dbHelper.getSettingDBTable().setSetingValue(SettingDBTable.BD_TOKEN, userid);
						
						if (null != m_loadingView) {
							m_loadingView.dismiss();
							m_loadingView = null;
						}
						
						if (!m_alarmClient.isLogin()) {
							LoginDialog lgDialog = new LoginDialog(this, this);
							lgDialog.show();
						}
						else {
							m_alarmMsgCtl.showAlarmMsg();
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else {
					if (errorCode == 30607) {
					}
				}
			}
			return true;
		} 
		else if (MsgPushUtils.ACTION_MESSAGE.equals(action)
					|| MsgPushUtils.ACTION_CLICK.equals(action)) {
			String title = intent.getStringExtra("title");
			String msg = intent.getStringExtra("msg");
			m_alarmClient.getMsgCenter().recvedMsg(title, msg);
			return true;
		} if (MsgPushUtils.ACTION_BACKROUND.equals(action)) {
			m_alarmMsgCtl.showAlarmMsg();
			return true;
		} if (MsgPushUtils.ACTION_CHECKED.equals(action)) {
			m_alarmMsgCtl.receivedAlarmMsg();
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		PushManager.activityStoped(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.taomee_chat_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_relogin:
			LoginDialog lgDialog = new LoginDialog(this, this);
			lgDialog.show();
			return true;
			
		case R.id.action_bell:
			new AlertDialog.Builder(this).setTitle(R.string.action_set_bell)
				.setNegativeButton(R.string.action_open, new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						m_dbHelper.getSettingDBTable().setSetingValue(SettingDBTable.BELL_ONCE, "1");
					}
				})
				.setNeutralButton(R.string.action_close, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						m_dbHelper.getSettingDBTable().setSetingValue(SettingDBTable.BELL_ONCE, "0");
					}
				}).create().show();
			return true;
			
//		case R.id.action_chat:
//			m_msgLayout.removeAllViews();
//			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_taomee_chat_main, container, false);
			return rootView;
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (null != m_loadingView) 
			m_loadingView.dismiss();
		m_loadingView = null;
		if (!m_alarmClient.isLogin()) {
			quit();
			return;
		}
		else {
			m_alarmMsgCtl.showAlarmMsg();
		}
	}
}
