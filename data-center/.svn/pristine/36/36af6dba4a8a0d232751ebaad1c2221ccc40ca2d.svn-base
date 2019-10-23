package alarm_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.taomee.chat.R;

public class AlarmMsgOperatorBar implements OnClickListener {
	public static final int ACTION_DELETE_MARKED 	= 0x0001;
	public static final int ACTION_MARKED_READ		= 0x0002;
	public static final int ACTION_MARKED_UNREAD	= 0x0003;
	public static final int ACTION_MARKED_CARE		= 0x0004;
	public static final int ACTION_DISCUSS_WITH		= 0x0005;
	public static final int ACTION_MARKED_ALL		= 0x0006;
	public static final int ACTION_DISMISS_CARE		= 0x0007;
	
	Button m_deleteBtn = null;
	Button m_markReadBtn = null;
	Button m_markUnreadBtn = null;
	Button m_markCareBtn = null;
	Button m_dismissCareBtn = null;
//	Button m_discussWithBtn = null;
	CheckBox m_markedAll = null;
	
	private LinearLayout m_barLayout;
	
	boolean m_operatorState = false;
	OperatorBarListener m_listener = null;
	
	boolean m_isUserClick = true;
	
	public AlarmMsgOperatorBar(Context context, OperatorBarListener listener, LinearLayout barLayout) {
		m_listener = listener;
		m_barLayout = barLayout;
		View v = LayoutInflater.from(context).inflate(R.layout.alarm_msg_opreator_bar, barLayout);
		m_deleteBtn = (Button)v.findViewById(R.id.btn_delete);
		m_markReadBtn = (Button)v.findViewById(R.id.btn_marked_read);
		m_markUnreadBtn = (Button)v.findViewById(R.id.btn_marked_unread);
		m_markCareBtn = (Button)v.findViewById(R.id.btn_marked_care);
		m_dismissCareBtn = (Button)v.findViewById(R.id.btn_dismiss_care);
//		m_discussWithBtn = (Button)v.findViewById(R.id.btn_discuss_with);
		m_markedAll = (CheckBox)v.findViewById(R.id.check_all);
		setOperatorClickListener();
		hideOperatorBar();
	}
	
	public void showOperatorBar() {
		if (m_operatorState)
			return;
		
		m_operatorState = true;
		m_barLayout.setVisibility(View.VISIBLE);
	}
	
	public void hideOperatorBar() {
		m_operatorState = false;
		setMarkedAll(false);
		m_barLayout.setVisibility(View.GONE);
	}
	
	public void setMarkedAll(boolean isChecked) {
		if (m_markedAll.isChecked() == isChecked)
			return;
		
		m_isUserClick = false;
		m_markedAll.setChecked(isChecked);
	}
	
	private void setOperatorClickListener() {
		m_deleteBtn.setOnClickListener(this);
		m_markReadBtn.setOnClickListener(this);
		m_markUnreadBtn.setOnClickListener(this);
		m_markCareBtn.setOnClickListener(this);
		m_dismissCareBtn.setOnClickListener(this);
//		m_discussWithBtn.setOnClickListener(this);
		m_markedAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (null != m_listener && m_isUserClick)
					m_listener.onOperatorAction(ACTION_MARKED_ALL, isChecked);
				m_isUserClick = true;
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		if (null == m_listener) {
			return;
		}
		switch (v.getId()) {
		case R.id.btn_delete:
			m_listener.onOperatorAction(ACTION_DELETE_MARKED, m_markedAll.isChecked());
			break;
		case R.id.btn_marked_read:
			m_listener.onOperatorAction(ACTION_MARKED_READ, m_markedAll.isChecked());
			break;
		case R.id.btn_marked_unread:
			m_listener.onOperatorAction(ACTION_MARKED_UNREAD, m_markedAll.isChecked());
			break;
		case R.id.btn_marked_care:
			m_listener.onOperatorAction(ACTION_MARKED_CARE, m_markedAll.isChecked());
			break;
		case R.id.btn_dismiss_care:
			m_listener.onOperatorAction(ACTION_DISMISS_CARE, m_markedAll.isChecked());
			break;
//		case R.id.btn_discuss_with:
//			m_listener.onOperatorAction(ACTION_DISCUSS_WITH, m_markedAll.isChecked());
//			break;
		default:
			return;
		}
	}
	
	public interface OperatorBarListener {
		void onOperatorAction(int actionType, boolean checked);
	}
}
