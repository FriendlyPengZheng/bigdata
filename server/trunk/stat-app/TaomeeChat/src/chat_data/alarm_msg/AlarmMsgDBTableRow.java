package chat_data.alarm_msg;

import android.os.Bundle;
import chat_data.base.DBTableRow;

public class AlarmMsgDBTableRow extends DBTableRow {
	public static final String C_MSG_FROM_TEXT 		= "msg_from";	///< 消息来源
	public static final String C_MSG_ID_TEXT 		= "msg_id";		///< 消息ID
	public static final String C_MSG_TITLE_TEXT 	= "msg_title";	///< 消息标题
	public static final String C_MSG_CONTENT_TEXT 	= "msg_content";///< 消息内容
	public static final String C_MSG_STATUS_INT 	= "msg_status";	///< 消息状态
	public static final String C_MSG_TIME_TEXT 		= "msg_time";	///< 时间字段
	public static final String C_MSG_USER_TEXT 		= "msg_user";	///< 消息对应的用户
	public static final String C_MSG_MARK_INT		= "msg_marked";	///< 消息的标记
	public static final String C_MSG_CHECKED_INT	= "msg_checked";///< 消息的选中状态
	public static final String C_MSG_TYPE_INT		= "msg_type";
	
	protected AlarmMsgDBTableRow(Bundle bundle) {
		super(bundle);
	}
	
	public static AlarmMsgDBTableRow cast(DBTableRow row) {
		return new AlarmMsgDBTableRow(row.getValues());
	}
	
	public String getSender() {
		return m_conlumnValueBundle.getString(C_MSG_FROM_TEXT);
	}
	
	public String getId() {
		return m_conlumnValueBundle.getString(C_MSG_ID_TEXT);
	}
	public String getTitle() {
		return m_conlumnValueBundle.getString(C_MSG_TITLE_TEXT);
	}
	public String getContent() {
		return m_conlumnValueBundle.getString(C_MSG_CONTENT_TEXT);
	}
	public int getStatus() {
		return m_conlumnValueBundle.getInt(C_MSG_STATUS_INT);
	}
	public void setStatus(int status) {
		m_conlumnValueBundle.putInt(C_MSG_STATUS_INT, status);
	}
	public String getTime() {
		return m_conlumnValueBundle.getString(C_MSG_TIME_TEXT);
	}
	public String getReceiver() {
		return m_conlumnValueBundle.getString(C_MSG_USER_TEXT);
	}
	public int getMark() {
		return m_conlumnValueBundle.getInt(C_MSG_MARK_INT);
	}
	public int getCheckedStatus() {
		return m_conlumnValueBundle.getInt(C_MSG_CHECKED_INT);
	}
	public void setCheckedStatus(int checked) {
		m_conlumnValueBundle.putInt(C_MSG_CHECKED_INT, checked); 
	}
	public int getType() {
		return m_conlumnValueBundle.getInt(C_MSG_TYPE_INT);
	}
}
