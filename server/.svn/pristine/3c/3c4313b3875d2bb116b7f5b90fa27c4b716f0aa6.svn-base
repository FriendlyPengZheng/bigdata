package alarm_ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import chat_base.ChatUtils;
import chat_data.alarm_msg.AlarmMsgDBTableRow;

import com.taomee.chat.R;

public class AlarmMsgListAdapter  extends BaseAdapter{
	private LayoutInflater m_inflater = null;
//	private ArrayList<HashMap<String, Object>> m_listViewData = null;
	private ArrayList<AlarmMsgDBTableRow> m_listViewData = null;
	private AlarmMsgListItemClick m_listener = null;
	
	public AlarmMsgListAdapter(Context context, ArrayList<AlarmMsgDBTableRow> data, AlarmMsgListItemClick l) {
		m_inflater = LayoutInflater.from(context);
		m_listViewData = data;
		m_listener = l;
	}

	@Override
	public int getCount() {
		return m_listViewData.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int m_touchX	= 0;
	private int m_l = 0;
	private int m_r = 0;
	private boolean m_flag = false;
	
	@Override
	public View getView(final int position, View converView, ViewGroup parent) {
		// TODO Auto-generated method stub
		AlarmMsgListViewItems showItems;
		if (null == converView) {
			showItems = new AlarmMsgListViewItems();
			converView = m_inflater.inflate(R.layout.msg_list_item, null);
			showItems.timeView = (TextView) converView.findViewById(R.id.text_time);
			showItems.titleView = (TextView) converView.findViewById(R.id.text_title);
			showItems.contentView = (TextView) converView.findViewById(R.id.text_content);
			converView.setTag(showItems);
		}
		else {
			showItems = (AlarmMsgListViewItems) converView.getTag();
		}
		
		showItems.timeView.setText(ChatUtils.timestmp2String(m_listViewData.get(position).getTime()));
		showItems.titleView.setText(m_listViewData.get(position).getTitle());
		showItems.contentView.setText(m_listViewData.get(position).getContent());
		
		if (m_listViewData.get(position).getStatus() == 0) {
			showItems.setUnreadSytle();
		}
		else if (m_listViewData.get(position).getMark() != 0) {
			showItems.setMarkedSytle();
		}
		else if (m_listViewData.get(position).getStatus() < 0) {
			showItems.setDefaultSytle();
			showItems.contentView.setText("");
		}
		else {
			showItems.setDefaultSytle();
		}
		
		converView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (m_listViewData.size() > position) {
					if (m_listViewData.get(position).getCheckedStatus() == 1)
						m_listener.onClick(-1, position);
					else 
						m_listener.onClick(1, position);
				
					changeColorWithCheck(v, m_listViewData.get(position).getCheckedStatus() == 1);
				}
			}
		});
		
		converView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					m_flag = false;
					m_touchX = (int)event.getRawX();
					m_l = v.getLeft();
					m_r = v.getRight();
					break;
					
				case MotionEvent.ACTION_MOVE:
					if (m_flag) return false;
					int moveX = ((int)event.getRawX() - m_touchX);
					
					if (moveX > 200) {
						m_flag = true;
						m_listener.onMoveRight(position);
						break;
					}
					else if (moveX < -200) {
						m_flag = true;
						m_listener.onMoveLeft(position);
						break;
					}
					int l = v.getLeft() + moveX;
					int r = v.getRight() + moveX;
					v.layout(l, v.getTop(), r, v.getBottom());
					break;
					
				case MotionEvent.ACTION_UP:
					v.layout(m_l, v.getTop(), m_r, v.getBottom());
					break;
					
				case MotionEvent.ACTION_CANCEL:
					v.layout(m_l, v.getTop(), m_r, v.getBottom());
					break;

				default:
					break;
				}
				return false;
			}
		});
		
		changeColorWithCheck(converView, m_listViewData.get(position).getCheckedStatus() == 1);
		
		return converView;
	}
	
	private void changeColorWithCheck(View v, boolean checked) {
		if (checked)
			v.setBackgroundColor(Color.YELLOW);
		else 
			v.setBackgroundColor(Color.WHITE);
	}
	
	public interface AlarmMsgListItemClick {
		void onClick(int markedCount, int position);
		void onMoveLeft(int position);
		void onMoveRight(int position);
	}
	
	public class AlarmMsgListViewItems {
		public TextView timeView;
		public TextView titleView;
		public TextView contentView;
		
		public void setDefaultSytle() {
			timeView.setTextSize(15);
			timeView.setTextColor(Color.BLACK);
			titleView.setTextSize(15);
			titleView.setTextColor(Color.BLACK);
			contentView.setTextSize(12);
			contentView.setTextColor(Color.BLACK);
			contentView.getPaint().setFakeBoldText(false);
		}
		
		public void setUnreadSytle() {
			timeView.setTextSize(15);
			timeView.setTextColor(Color.BLUE);
			titleView.setTextSize(15);
			titleView.setTextColor(Color.BLUE);
			contentView.setTextSize(15);
			contentView.setTextColor(Color.BLUE);
			contentView.getPaint().setFakeBoldText(true);
		}
		
		public void setMarkedSytle() {
			timeView.setTextSize(15);
			timeView.setTextColor(Color.BLACK);
			titleView.setTextSize(15);
			titleView.setTextColor(Color.GREEN);
			contentView.setTextSize(12);
			contentView.setTextColor(Color.BLACK);
			contentView.getPaint().setFakeBoldText(false);
		}
	}
}
