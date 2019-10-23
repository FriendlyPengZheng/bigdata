package alarm_client;

import java.util.ArrayList;

import stat_app_proto.AppPullMsg.AppPullMsgResponse;
import stat_app_proto.AppPullMsg.MsgBody;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import chat_data.alarm_msg.AlarmMsgDBTable;
import chat_data.alarm_msg.AlarmMsgDBTableRow;
import chat_data.alarm_msg.AlarmMsgDBTableRowBuilder;
import chat_data.base.DBTableRow;

public class AlarmMsgCenter {
	private AlarmMsgDBTable m_msgTable;
	
	public AlarmMsgCenter(AlarmMsgDBTable msgTable) {
		m_msgTable = msgTable;
	}
	
	public ArrayList<DBTableRow> getAllMsg(int count) {
		return m_msgTable.getAllMsg(count);
	}
	
	public ArrayList<DBTableRow> getUnreadMsg(int count) {
		return m_msgTable.getUnreadMsg(count);
	}
	
	public ArrayList<DBTableRow> getReadMsg(int count) {
		return m_msgTable.getReadMsg(count);
	}
	
	public ArrayList<DBTableRow> getmarkedMsg(int count) {
		return m_msgTable.getMarkedMsg(count);
	}
	
	public ArrayList<DBTableRow> getPullMsg(int count) {
		return m_msgTable.getNeedPullMsg(count);
	}
	
	public ArrayList<DBTableRow> getAllMsgMore(long _id, boolean direction, int count) {
		return m_msgTable.getAllMsgMore(_id, direction, count);
	}
	
	public ArrayList<DBTableRow> getMoreReadMsg(long _id, boolean direction, int count) {
		return m_msgTable.getMoreReadMsg(_id, direction, count);
	}
	
	public ArrayList<DBTableRow> getMoreUnreadMsg(long _id, boolean direction, int count) {
		return m_msgTable.getMoreUnreadMsg(_id, direction, count);
	}
	
	public ArrayList<DBTableRow> getMoreMarkedMsg(long _id, boolean direction, int count) {
		return m_msgTable.getMoreMarkedMsg(_id, direction, count);
	}
	
	public ArrayList<DBTableRow> getMorePullMsg(long _id, boolean direction, int count) {
		return m_msgTable.getMoreNeedPullMsg(_id, direction, count);
	}
	
	public int getAllMsgCount() {
		return  m_msgTable.getAllMsgCount();
	}
	
	public int getReadMsgCount() {
		return  m_msgTable.getReadMsgCount();
	}
	
	public int getMarkedCount() {
		return  m_msgTable.getMarkedCount();
	}
	
	public int getCheckedCount() {
		return  m_msgTable.getCheckedCount();
	}
	
	public int getPullCount() {
		return m_msgTable.getPullCout();
	}
	
	public int doActionOnRows(int action, int param, int flag) {
		return m_msgTable.doOnCheckedRows(action, param, flag);
	}
	
	public int markMsgAsRead(String msgId) {
		return m_msgTable.markMsgAsRead(msgId);
	}
	
	public int setMsgCheckedStatus(String msgId, boolean checked) {
		return m_msgTable.setMsgCheckedStatus(msgId, checked);
	}
	
	public int updateMarkStatus(String msgId, int mark) {
		return m_msgTable.updateMsgMark(msgId, mark);
	}
	
	public int removeMsg(String msgId) {
		return m_msgTable.removeMsg(msgId);
	}
	
	public void cleanAllMsg() {
		m_msgTable.clear();
	}
	
	public int getUnreadMsgCount() {
		return m_msgTable.getUnreadMsgCount();
	}
	
	public ArrayList<String> getNeedPullMsg() {
		return m_msgTable.getNeedPullMsg();
	}
	
	public int deletePullFailedMsg() {
		return m_msgTable.deletePullFailedMsg();
	}
	
	public void recvedMsg(String title, String content) {
		AlarmMsgPuller.pullAlarmMsg();
		try {
			byte[] msgBytes = Base64.decode(content, Base64.DEFAULT);
			
			AppPullMsgResponse response = AppPullMsgResponse.parseFrom(msgBytes);
			recvedMsg(response);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int recvedMsg(AppPullMsgResponse response) {
		if (null == response) {
			return 0;
		}
		
		if (response.getMsgCount() !=0) {
			for (int i=0; i<response.getMsgCount(); ++i) {
				MsgBody msg = response.getMsg(i);
				
				AlarmMsgDBTableRowBuilder builder = new AlarmMsgDBTableRowBuilder();
				builder.setSender("Alarm Server")
						.setId(msg.getMsgId())
						.setTitle(msg.getTitle())
						.setContent(msg.getContent())
						.setTime(getTimeFromId(msg.getMsgId()))
						.setReceiver(AlarmClient.m_UserName);
				switch (response.getRet()) {
				case 0:
					builder.setStatus(0);
					break;
				case 1:
					continue;
				case 2:
				case 3:
					builder.setStatus(-1);
					break;
				default:
					continue;
				}
				AlarmMsgDBTableRow row = AlarmMsgDBTableRow.cast(builder.build());
				m_msgTable.updateOrInsert(row);
			}
		}
		else {
			int del = m_msgTable.deletePullFailedMsg();
//			Log.d("11111111",  "pull failed : " + del);
			return 0-del;
		}
		return response.getMsgCount();
	}
	
	public void onEnd() {
		m_msgTable.onEnd();
	}
	
	private String getTimeFromId(String id) {
		String[] ret = id.split(":",3);
		int index = Integer.valueOf(ret[1]);
		return ret[0] + String.valueOf(index%1000+1000).substring(1, 4);
	}
	
}
