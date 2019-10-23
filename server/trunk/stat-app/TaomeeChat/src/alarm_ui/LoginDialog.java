package alarm_ui;

import alarm_client.AlarmClient;
import alarm_client.AlarmClient.Logincallback;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.taomee.chat.R;


public class LoginDialog extends Dialog {
	private Context m_context = null;
	
	private Button m_loginBtn = null;
	private EditText m_nameEdit = null;
	private EditText m_pwdEdit = null;
	private EditText m_mobileEdit = null;
	
	private ProgressDialog m_loginingView= null;
	
	public LoginDialog(Context context, OnDismissListener listener) {
		super(context);
		m_context = context;
		this.setOnDismissListener(listener);
		initLoginDialog();
	}
	
	private void initLoginDialog() {
		if (null != m_context) {
			this.setContentView(R.layout.login);
			m_loginBtn = (Button) this.findViewById(R.id.loginBtn);
			m_nameEdit = (EditText)this.findViewById(R.id.nameEdit);
			m_pwdEdit = (EditText)this.findViewById(R.id.passwordEdit);
			m_mobileEdit = (EditText)this.findViewById(R.id.mobileEdit);
			if (null != m_loginBtn)
				m_loginBtn.setOnClickListener(m_loginBtnListener);
			this.setTitle(R.string.login_title);
//			this.setCancelable(false);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	private View.OnClickListener m_loginBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (m_nameEdit.getText().length() == 0 || m_pwdEdit.getText().length() == 0)
				return;
			onLoginBtnPressed(m_context);
		}
	};
	
	private Logincallback m_LogResultListener = new Logincallback() {
		@Override
		public void onResponse(int result) {
			onLoginResponse(result);
		}
	};
	
	private void onLoginResponse(int result) {
		if (m_loginingView != null) {
			m_loginingView.dismiss();
		}
		
		if (result == 0) {
			this.dismiss();
		}
		else {
			new Handler(m_context.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(m_context, m_context.getString(R.string.login_faild), Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	private void onLoginBtnPressed(Context context) {
		final String username = m_nameEdit.getText().toString().trim().replace(" ", "");
		final String password = m_pwdEdit.getText().toString();
		final String mobile = m_mobileEdit.getText().toString().trim().replace(" ", "");
		
		StringBuilder sb = new StringBuilder();
		sb.append(m_context.getString(R.string.login_username)).append(username).append("\n")
		  .append(m_context.getString(R.string.login_mobile)).append(mobile);
		
		AlertDialog dialog = new AlertDialog.Builder(m_context)
			.setTitle(R.string.login_info_confirm)
			.setMessage(sb.toString())
			.setNegativeButton(R.string.btn_cancel, null)
			.setNeutralButton(R.string.btn_confirm, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AlarmClient.Instance().login(username, password, mobile, m_LogResultListener);
					if (null == m_loginingView)
						m_loginingView = new ProgressDialog(m_context);
					m_loginingView.setMessage("µÇÂ¼ÖÐ¡£¡£¡£");
					m_loginingView.setCancelable(false);
					m_loginingView.show();
				}
			})
			.create();
		dialog.show();
	}
}
