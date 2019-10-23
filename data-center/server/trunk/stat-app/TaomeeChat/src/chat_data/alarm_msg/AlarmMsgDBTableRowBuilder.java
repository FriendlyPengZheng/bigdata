package chat_data.alarm_msg;

import android.os.Bundle;
import chat_data.base.DBTableRow;
import chat_data.base.TableRowBuilder;

public class AlarmMsgDBTableRowBuilder extends TableRowBuilder {
	@Override
	public boolean check(Bundle bundle) {
		return (m_bundle.containsKey(AlarmMsgDBTableRow.C_MSG_ID_TEXT) 
				&& m_bundle.containsKey(AlarmMsgDBTableRow.C_MSG_TIME_TEXT) 
				&& m_bundle.containsKey(AlarmMsgDBTableRow.C_MSG_STATUS_INT));
	}
	
	@Override
	protected void fillDefaultValues() {
		if (!m_bundle.containsKey(AlarmMsgDBTableRow.C_MSG_CONTENT_TEXT)) 
			m_bundle.putString(AlarmMsgDBTableRow.C_MSG_CONTENT_TEXT, "");
		if (!m_bundle.containsKey(AlarmMsgDBTableRow.C_MSG_FROM_TEXT)) 
			m_bundle.putString(AlarmMsgDBTableRow.C_MSG_FROM_TEXT, "ALARMCENTER");
		if (!m_bundle.containsKey(AlarmMsgDBTableRow.C_MSG_MARK_INT)) 
			m_bundle.putInt(AlarmMsgDBTableRow.C_MSG_MARK_INT, 1);
		if (!m_bundle.containsKey(AlarmMsgDBTableRow.C_MSG_CHECKED_INT))
			m_bundle.putInt(AlarmMsgDBTableRow.C_MSG_CHECKED_INT, 0);
		if (!m_bundle.containsKey(AlarmMsgDBTableRow.C_MSG_TITLE_TEXT)) 
			m_bundle.putString(AlarmMsgDBTableRow.C_MSG_TITLE_TEXT, "");
		if (!m_bundle.containsKey(AlarmMsgDBTableRow.C_MSG_TYPE_INT)) 
			m_bundle.putInt(AlarmMsgDBTableRow.C_MSG_CONTENT_TEXT, AlarmMsgDBTable.MSG_TYPE_ALARM);
		if (!m_bundle.containsKey(AlarmMsgDBTableRow.C_MSG_USER_TEXT)) 
			m_bundle.putString(AlarmMsgDBTableRow.C_MSG_USER_TEXT, "");
	}
	
	public AlarmMsgDBTableRowBuilder setSender(String sender) {
		m_bundle.putString(AlarmMsgDBTableRow.C_MSG_FROM_TEXT, sender);
		return this;
	}
	
	public AlarmMsgDBTableRowBuilder setId(String id) {
		m_bundle.putString(AlarmMsgDBTableRow.C_MSG_ID_TEXT, id);
		return this;
	}
	
	public AlarmMsgDBTableRowBuilder setTitle(String title) {
		m_bundle.putString(AlarmMsgDBTableRow.C_MSG_TITLE_TEXT, title);
		return this;
	}
	
	public AlarmMsgDBTableRowBuilder setContent(String content) {
		m_bundle.putString(AlarmMsgDBTableRow.C_MSG_CONTENT_TEXT, content);
		return this;
	}
	
	public AlarmMsgDBTableRowBuilder setStatus(int status) {
		m_bundle.putInt(AlarmMsgDBTableRow.C_MSG_STATUS_INT, status);
		return this;
	}
	
	public AlarmMsgDBTableRowBuilder setTime(String time) {
		m_bundle.putString(AlarmMsgDBTableRow.C_MSG_TIME_TEXT, time);
		return this;
	}
	
	public AlarmMsgDBTableRowBuilder setReceiver(String receiver) {
		m_bundle.putString(AlarmMsgDBTableRow.C_MSG_USER_TEXT, receiver);
		return this;
	}
	
	public AlarmMsgDBTableRowBuilder setMark(int mark) {
		m_bundle.putInt(AlarmMsgDBTableRow.C_MSG_MARK_INT, mark);
		return this;
	}
	
	public AlarmMsgDBTableRowBuilder setChecked(int checked) {
		m_bundle.putInt(AlarmMsgDBTableRow.C_MSG_CHECKED_INT, checked);
		return this;
	}
	
	public AlarmMsgDBTableRowBuilder setType(int type) {
		 m_bundle.putInt(AlarmMsgDBTableRow.C_MSG_TYPE_INT, type);
		return this;
	}

	@Override
	public DBTableRow build(final Bundle bundle) {
		if (check(bundle)) {
			m_bundle = bundle;
			return new AlarmMsgDBTableRow(bundle);
		}
		return null;
	}
}
