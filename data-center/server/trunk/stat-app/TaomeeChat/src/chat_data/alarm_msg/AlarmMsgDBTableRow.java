package chat_data.alarm_msg;

import android.os.Bundle;
import chat_data.base.DBTableRow;

public class AlarmMsgDBTableRow extends DBTableRow {
	public static final String C_MSG_FROM_TEXT 		= "msg_from";	///< ��Ϣ��Դ
	public static final String C_MSG_ID_TEXT 		= "msg_id";		///< ��ϢID
	public static final String C_MSG_TITLE_TEXT 	= "msg_title";	///< ��Ϣ����
	public static final String C_MSG_CONTENT_TEXT 	= "msg_content";///< ��Ϣ����
	public static final String C_MSG_STATUS_INT 	= "msg_status";	///< ��Ϣ״̬
	public static final String C_MSG_TIME_TEXT 		= "msg_time";	///< ʱ���ֶ�
	public static final String C_MSG_USER_TEXT 		= "msg_user";	///< ��Ϣ��Ӧ���û�
	public static final String C_MSG_MARK_INT		= "msg_marked";	///< ��Ϣ�ı��
	public static final String C_MSG_CHECKED_INT	= "msg_checked";///< ��Ϣ��ѡ��״̬
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
