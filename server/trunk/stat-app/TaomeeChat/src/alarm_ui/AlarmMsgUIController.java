package alarm_ui;

import java.util.ArrayList;

import alarm_client.AlarmMsgCenter;
import alarm_ui.AlarmMsgListAdapter.AlarmMsgListItemClick;
import alarm_ui.AlarmMsgOperatorBar.OperatorBarListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import chat_data.alarm_msg.AlarmMsgDBTableRow;
import chat_data.base.DBTableRow;

import com.taomee.chat.R;

public class AlarmMsgUIController implements OnClickListener, OperatorBarListener, AlarmMsgListItemClick{
	private static final String TAG = "AlarmUiCtl";
	
	private static final int ACTION_LIST_UPDATE					= 0x0001;
	private static final int ACTION_BEGIN_SHOW_MSG				= 0x0002;
	private static final int ACTION_UPDATE_DATA					= 0x0003;
	private static final int ACTION_SCROLL_LIST					= 0x0004;
	private static final int ACTION_RECVED_MSG					= 0xA001;
	
	private static final int SCROLL_TO_FIRST	= 0;
	private static final int SCROLL_TO_BOTTOM	= -1;
	private static final int SCROLL_NO_SCROLL	= -2;
	
	public static final int ALARM_MSG_ALL 		= 0x0001;
	public static final int ALARM_MSG_READ 		= 0x0002;
	public static final int ALARM_MSG_UNREAD 	= 0x0003;
	public static final int ALARM_MSG_MARKED	= 0x0004;
	public static final int ALARM_MSG_PULL		= 0x0005;
	
	private static final int MAX_MSG_ONCE_SHOW = 30;
	private static final int MORE_COUT_TRY_GET = 10;
	
	private Context m_context;
	private Handler m_handler;
	
	private Button m_msgAllBtn = null;
	private Button m_msgReadBtn = null;
	private Button m_msgUnreadBtn = null;
	private Button m_msgMarkedBtn = null;
	private Button m_msgPullBtn = null;
	
	private AlarmMsgCenter m_msgCenter = null;
	private AlarmMsgOperatorBar m_operatorBar = null;
	
	private ListView m_msgListView = null;
	private ArrayList<AlarmMsgDBTableRow> m_listViewData = null;
	private AlarmMsgListAdapter m_alarmAdapter;
	private int m_msgDisplayType = 0;
	
	private int m_totalCount = 0;
	private int m_checkedCount = 0;
	
	public AlarmMsgUIController(Context context, AlarmMsgCenter msgCenter) {
		m_context = context;
		m_msgCenter = msgCenter;
	}
	
	private void handleAlarmMsgAction(Message msg) {
		switch (msg.what) {
		case ACTION_LIST_UPDATE:
//			Log.d(TAG, "ACTION_LIST_UPDATE");
			m_alarmAdapter.notifyDataSetChanged();
			break;
		
		case ACTION_BEGIN_SHOW_MSG:
			changeDisplayType(msg.arg1);
			break;
			
		case ACTION_UPDATE_DATA:
			updateMsgData(msg.arg1);
			break;
			
		case ACTION_SCROLL_LIST:
//			Log.d(TAG, "ACTION_SCROLL_LIST");
			setSelection(msg.arg1);
			break;
			
		case ACTION_RECVED_MSG:
//			Log.d(TAG, "ACTION_RECVED_MSG");
			onRecvedMsg();
			break;
		default:
		}
	}
	
	public void initialize(LinearLayout msgLayout, LinearLayout barLayout) {
		m_handler = new Handler(m_context.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				handleAlarmMsgAction(msg);
			}
		};
		
		findViews(msgLayout);
		m_operatorBar = new AlarmMsgOperatorBar(m_context, this, barLayout);
		m_listViewData = new ArrayList<AlarmMsgDBTableRow>();
		m_alarmAdapter = new AlarmMsgListAdapter(m_context, m_listViewData, this);
		m_msgListView.setAdapter(m_alarmAdapter);
	}
	
	public void showAlarmMsg() {
		Message msg = new Message();
		msg.what = ACTION_BEGIN_SHOW_MSG;
		msg.arg1 = (0 == m_msgCenter.getUnreadMsgCount()) ? ALARM_MSG_ALL : ALARM_MSG_UNREAD;
		m_handler.sendMessage(msg);
	}
	
	public void receivedAlarmMsg() {
		m_handler.sendEmptyMessage(ACTION_RECVED_MSG);
		if (m_msgDisplayType == ALARM_MSG_UNREAD && m_listViewData.size() != MAX_MSG_ONCE_SHOW) {
			int first = m_msgListView.getFirstVisiblePosition();
			Message msg = new Message();
			msg.what = ACTION_UPDATE_DATA;
			msg.arg1 = first;
			m_handler.sendMessage(msg);
		}
	}
	
	public void onRecvedMsg() {
		int ucount = m_msgCenter.getUnreadMsgCount();
		if (ucount == 0)
			return;
		
		String toastMsg = String.format(m_context.getString(R.string.recv_msg_toast), ucount);
		Toast.makeText(m_context, toastMsg, Toast.LENGTH_SHORT).show();
		if (m_msgDisplayType != ALARM_MSG_UNREAD && 0 != ucount)
			m_msgUnreadBtn.setText(m_context.getString(R.string.unread_msg) + "*");
	}
	
	private void findViews(LinearLayout layout) {
		View v = LayoutInflater.from(m_context).inflate(R.layout.alarm_msg_display, layout);
		
		m_msgListView = (ListView)v.findViewById(R.id.list_msg);
		m_msgAllBtn = (Button)v.findViewById(R.id.btn_all_msg);
		m_msgReadBtn = (Button)v.findViewById(R.id.btn_read_msg);
		m_msgUnreadBtn = (Button)v.findViewById(R.id.btn_unread_msg);
		m_msgMarkedBtn = (Button)v.findViewById(R.id.btn_marked_msg);
		m_msgPullBtn = (Button)v.findViewById(R.id.btn_pull_msg);
		
		m_msgListView.setOnScrollListener(new AlarmOnScrollListener());
		m_msgAllBtn.setOnClickListener(this);
		m_msgReadBtn.setOnClickListener(this);
		m_msgUnreadBtn.setOnClickListener(this);
		m_msgMarkedBtn.setOnClickListener(this);
		m_msgPullBtn.setOnClickListener(this);
		m_msgPullBtn.setVisibility(View.GONE);
		if (m_msgAllBtn == null || m_msgReadBtn == null) {
			Log.e(TAG, "Find View Error!");
		}
	}

	@Override
	public void onClick(View v) {
//		Log.d(TAG, "Click : " + v.getId());
		switch(v.getId()) {
		case R.id.btn_all_msg:
			changeDisplayType(ALARM_MSG_ALL);
			break;
		case R.id.btn_read_msg:
			changeDisplayType(ALARM_MSG_READ);
			break;
		case R.id.btn_unread_msg:
			m_msgUnreadBtn.setText(m_context.getString(R.string.unread_msg));
			changeDisplayType(ALARM_MSG_UNREAD);
			break;
		case R.id.btn_marked_msg:
			changeDisplayType(ALARM_MSG_MARKED);
			break;
		case R.id.btn_pull_msg:
			changeDisplayType(ALARM_MSG_PULL);
//			Toast.makeText(m_context, "^_^:Please expect next version.", Toast.LENGTH_SHORT).show();
			break;
		default:
			return;
		}
	}
	
	private void changeDisplayType(int type) {
		if (m_msgDisplayType != type) { 
			m_msgDisplayType = type;
			m_msgAllBtn.setEnabled(!(ALARM_MSG_ALL == type));
			m_msgReadBtn.setEnabled(!(ALARM_MSG_READ == type));
			m_msgUnreadBtn.setEnabled(!(ALARM_MSG_UNREAD == type));
			if (ALARM_MSG_UNREAD != type && 0 != m_msgCenter.getUnreadMsgCount()) 
				m_msgUnreadBtn.setText(m_context.getString(R.string.unread_msg) + "*");
			m_msgMarkedBtn.setEnabled(!(ALARM_MSG_MARKED == type));
			m_msgPullBtn.setEnabled(!(ALARM_MSG_PULL == type));
		}
		
		if (m_checkedCount != 0) {
			m_msgCenter.doActionOnRows(AlarmMsgOperatorBar.ACTION_MARKED_ALL, 0, 0);
			m_checkedCount = 0;
		}
		m_operatorBar.hideOperatorBar();
		Message msg = new Message();
		msg.what = ACTION_UPDATE_DATA;
		msg.arg1 = (type == ALARM_MSG_UNREAD) ? SCROLL_TO_FIRST : SCROLL_TO_BOTTOM;
		m_handler.sendMessage(msg);
	}
	
	private void updateMsgData(int item) {
		ArrayList<DBTableRow> list = new ArrayList<DBTableRow>();
		
		switch (m_msgDisplayType) {
		case ALARM_MSG_ALL:
			list = m_msgCenter.getAllMsg(MAX_MSG_ONCE_SHOW);
			m_totalCount = MAX_MSG_ONCE_SHOW >= list.size() ? m_msgCenter.getAllMsgCount() : list.size();
			break;
			
		case ALARM_MSG_READ: 
			list = m_msgCenter.getReadMsg(MAX_MSG_ONCE_SHOW);
			m_totalCount = MAX_MSG_ONCE_SHOW >= list.size() ? m_msgCenter.getReadMsgCount() : list.size();
			break;
		
		case ALARM_MSG_UNREAD: 
			list = m_msgCenter.getUnreadMsg(MAX_MSG_ONCE_SHOW);
			m_totalCount = MAX_MSG_ONCE_SHOW >= list.size() ? m_msgCenter.getUnreadMsgCount() : list.size();
			break;
			
		case ALARM_MSG_MARKED:
			list = m_msgCenter.getmarkedMsg(MAX_MSG_ONCE_SHOW);
			m_totalCount = MAX_MSG_ONCE_SHOW >= list.size() ? m_msgCenter.getMarkedCount() : list.size();
			break;
		case ALARM_MSG_PULL:
			list = m_msgCenter.getPullMsg(MAX_MSG_ONCE_SHOW);
			m_totalCount = MAX_MSG_ONCE_SHOW >= list.size() ? m_msgCenter.getPullCount() : list.size();
			break;
		default:
			return;
		}
		
		if (null != m_msgListView) {
			Log.i(TAG, "Update log - > count : " + list.size());
			if (m_msgDisplayType == ALARM_MSG_UNREAD) 
				tryAfterFlag =  (list.size() == MAX_MSG_ONCE_SHOW);
			else 
				tryBeforeFlag =  (list.size() == MAX_MSG_ONCE_SHOW);
			m_listViewData.clear();
			for (int i = 0; i < list.size(); ++i) {
				m_listViewData.add(AlarmMsgDBTableRow.cast(list.get(i)));
			}
			m_handler.sendEmptyMessage(ACTION_LIST_UPDATE);
			Message msg = new Message();
			msg.what = ACTION_SCROLL_LIST;
			msg.arg1 = item;
			m_handler.sendMessage(msg);
		}
	}
	
	private void setSelection(int item) {
		if (item == SCROLL_TO_BOTTOM)
			item = m_listViewData.size()>0 ? m_listViewData.size()-1 : 0;
		else if (item == SCROLL_NO_SCROLL)
			return;
		m_msgListView.setSelection(item);
	}

	@Override
	public void onOperatorAction(final int actionType, final boolean checked) {
		int ret = checkAction(actionType);
		if (-1 == ret) 
			return;
		else if (0 == ret) {
			afterConfirm(actionType, checked, true);
			return;
		}
		else {
			DialogInterface.OnClickListener onCancel = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					afterConfirm(actionType, checked, false);
				}
			};
			
			DialogInterface.OnClickListener onConfirm = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					afterConfirm(actionType, checked, true);
				}
			};
			new AlertDialog.Builder(m_context).setTitle("^_^ " + m_checkedCount + m_context.getString(R.string.marked_count))
					.setMessage(getConfirmString(actionType))
					.setNegativeButton(R.string.btn_cancel, onCancel)
					.setNeutralButton(R.string.btn_confirm, onConfirm)
					.setCancelable(true)
					.create().show();
		}
	}
	
	private int checkAction(int actionType) {
		if (AlarmMsgOperatorBar.ACTION_MARKED_ALL == actionType 
				|| AlarmMsgOperatorBar.ACTION_MARKED_CARE == actionType
				|| AlarmMsgOperatorBar.ACTION_DISMISS_CARE == actionType )
			return 0;
		
		if (AlarmMsgOperatorBar.ACTION_DISCUSS_WITH == actionType) {
			Toast.makeText(m_context, "^_^:Please expect next version.", Toast.LENGTH_SHORT).show();
			return 0;
		}
		
		switch (m_msgDisplayType) {
		case ALARM_MSG_ALL:
			break;
		case ALARM_MSG_PULL:
		case ALARM_MSG_MARKED:
			return -1;
		case ALARM_MSG_READ:
			if (AlarmMsgOperatorBar.ACTION_MARKED_READ == actionType)
				return -1;
			
			break;
		case ALARM_MSG_UNREAD:
			if (AlarmMsgOperatorBar.ACTION_MARKED_UNREAD == actionType) 
				return -1;
			
			break;
		default:
			return 0;
		}
		return 1;
	}
	
	private String getConfirmString(int actionType) {
		switch (actionType) {
		case AlarmMsgOperatorBar.ACTION_DELETE_MARKED:
			return m_context.getString(R.string.marked_to_delete);
		case AlarmMsgOperatorBar.ACTION_MARKED_READ:
			return m_context.getString(R.string.marked_to_read);
		case AlarmMsgOperatorBar.ACTION_MARKED_UNREAD:
			return m_context.getString(R.string.marked_to_unread);
		case AlarmMsgOperatorBar.ACTION_MARKED_CARE:
		case AlarmMsgOperatorBar.ACTION_DISCUSS_WITH:
		case AlarmMsgOperatorBar.ACTION_MARKED_ALL:
		}
		return"";
	}
	
	private void afterConfirm(int actionType, boolean checked, boolean isConfirm) {
		if (!isConfirm)
			return;
		
		int state = checked ? 1:0;
		m_msgCenter.doActionOnRows(actionType, state, m_msgDisplayType);
		
		if (AlarmMsgOperatorBar.ACTION_MARKED_ALL == actionType && checked) {
			m_checkedCount = m_totalCount;
			m_operatorBar.showOperatorBar();
		}
		else {
			m_checkedCount = 0;
			m_operatorBar.hideOperatorBar();
		}
		if (actionType == AlarmMsgOperatorBar.ACTION_MARKED_UNREAD)
			m_msgUnreadBtn.setText(m_context.getString(R.string.unread_msg) + "*");
		Message msg = new Message();
		msg.what = ACTION_UPDATE_DATA;
		msg.arg1 = SCROLL_NO_SCROLL;
		m_handler.sendMessage(msg);
	}
	
	private boolean tryAfterFlag = false;
	private boolean tryBeforeFlag = false;
	
	private class AlarmOnScrollListener implements OnScrollListener {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState != OnScrollListener.SCROLL_STATE_IDLE || m_listViewData.size() == m_totalCount)
				return;
			
			int firstVisibleItem = view.getFirstVisiblePosition();
			int lastVisibleItem = view.getLastVisiblePosition();
			int flag = 0;
			int position = 0;
			if (firstVisibleItem == 0 && tryBeforeFlag) {
				position = loadMoreMsg(false);
				flag = 1;
			}
			else if (lastVisibleItem == MAX_MSG_ONCE_SHOW-1 && tryAfterFlag) {
				position = loadMoreMsg(true);
				flag = 2;
			}
			else 
				return;
			if (position <= 0) 
				return;
			
			m_handler.sendEmptyMessage(ACTION_LIST_UPDATE);
			Message msg = new Message();
			msg.what = ACTION_SCROLL_LIST;
			if (flag == 1) 
				msg.arg1 = position-1;
			else
				msg.arg1 = firstVisibleItem - position >= 0 ? firstVisibleItem - position : 0;
			m_handler.sendMessage(msg);
		}
	}
	
	private ArrayList<DBTableRow> getMoreMsg(boolean isAfter) {
		ArrayList<DBTableRow> list = null;
		int index = isAfter ? m_listViewData.size()-1 : 0;
		long rowId = m_listViewData.get(index).getRowId();
//		Log.d("", "index : " + index + "|rowid : " + rowId + "|0 : " + m_listViewData.get(0).getRowId() + "|last : " + m_listViewData.get(m_listViewData.size()-1).getRowId());
		switch (m_msgDisplayType) {
		case ALARM_MSG_MARKED:
			list = m_msgCenter.getMoreMarkedMsg(rowId, isAfter, MORE_COUT_TRY_GET);
			break;
		case ALARM_MSG_READ:
			list = m_msgCenter.getMoreReadMsg(rowId, isAfter, MORE_COUT_TRY_GET);
			break;
		case ALARM_MSG_UNREAD:
			list = m_msgCenter.getMoreUnreadMsg(rowId, isAfter, MORE_COUT_TRY_GET);
			break;
		case ALARM_MSG_ALL:
			list = m_msgCenter.getAllMsgMore(rowId, isAfter, MORE_COUT_TRY_GET);
			break;
		case ALARM_MSG_PULL:
			list = m_msgCenter.getMorePullMsg(rowId, isAfter, MORE_COUT_TRY_GET);
		}
		if (null == list) 
			return null;
		
		if (list.size() < MORE_COUT_TRY_GET)
			if (isAfter) 
				tryAfterFlag = false;
			else
				tryBeforeFlag = false;
		else 
			if (isAfter) 
				tryAfterFlag = true;
			else
				tryBeforeFlag = true;
		
		return list;
	}
	
	private int loadMoreMsg(boolean isAfter) {
		ArrayList<DBTableRow> list = getMoreMsg(isAfter);
		if (list == null || list.size() == 0)
			return 0;
//		Log.d(TAG, "LoadMore--isAfter : " + isAfter + " | get cout more : " + list.size());
		
		if (isAfter) {
			for (int i=0; i< list.size(); ++i) {
				m_listViewData.remove(0);
				m_listViewData.add(AlarmMsgDBTableRow.cast(list.get(i)));
			}
			tryBeforeFlag = true;
		}
		else {
			for (int i=0; i< list.size(); ++i) {
				m_listViewData.remove(m_listViewData.size()-1);
				m_listViewData.add(0, AlarmMsgDBTableRow.cast(list.get(list.size()-i-1)));
			}
			tryAfterFlag = true;
		}
		return list.size();
	}
	
	@Override
	public void onClick(int markedCount, int position) {
		if (ALARM_MSG_PULL == m_msgDisplayType) 
			return;
		
		m_checkedCount += markedCount;
		(m_listViewData.get(position)).setCheckedStatus((1==markedCount ? 1 : 0));
		m_msgCenter.setMsgCheckedStatus((m_listViewData.get(position)).getId(), (1==markedCount));
		if (m_checkedCount == markedCount)
			m_operatorBar.showOperatorBar();
		
		if (m_checkedCount == 0)
			m_operatorBar.hideOperatorBar();
		
		if (m_checkedCount == m_totalCount) 
			m_operatorBar.setMarkedAll(true);
		else
			m_operatorBar.setMarkedAll(false);
//		Log.d(TAG, "List item click : " + m_checkedCount + " : " + m_listViewData.size());
	}

	@Override
	public void onMoveLeft(final int position) {
		if (ALARM_MSG_PULL == m_msgDisplayType) 
			return;
		
		if (m_listViewData.get(position).getStatus() != 0)
			return;
		
		String msgId = m_listViewData.get(position).getId();
		if (m_msgDisplayType == ALARM_MSG_UNREAD) {
			m_listViewData.remove(position).setStatus(1);
			m_totalCount -= 1;
		}
		else 
			m_listViewData.get(position).setStatus(1);
		
		m_msgCenter.markMsgAsRead(msgId);
		Toast.makeText(m_context, "Marked An Alarm Message As Read", Toast.LENGTH_SHORT).show();
		
		m_handler.sendEmptyMessage(ACTION_LIST_UPDATE);
	}

	@Override
	public void onMoveRight(final int position) {
		String msgId = ((AlarmMsgDBTableRow)m_listViewData.remove(position)).getId();
		m_totalCount -= 1;
		m_msgCenter.removeMsg(msgId);
		Toast.makeText(m_context, "Delete An Alarm Message", Toast.LENGTH_SHORT).show();
		m_handler.sendEmptyMessage(ACTION_LIST_UPDATE);
	}
}
