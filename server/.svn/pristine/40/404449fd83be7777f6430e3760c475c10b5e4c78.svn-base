package chat_data.alarm_msg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import alarm_ui.AlarmMsgOperatorBar;
import alarm_ui.AlarmMsgUIController;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import chat_data.base.DBTable;
import chat_data.base.DBTableRow;

public class AlarmMsgDBTable extends DBTable {
	public static final String TABLE_NAME 		= "t_alarm_msg";		///< 消息表名
	
	public static final int MSG_TYPE_ALARM		= 0x0001;
	
	public static final int MSG_TYPE_CHAT_1		= 0xA001;
	public static final int MSG_TYPE_CHAT_N		= 0xA002;
	@Override
	public int createTable(SQLiteDatabase db) {
		String createMsgTableSql = "Create table if not exists " 		//不存在时创建
				+ TABLE_NAME										//表名
				+ " (_id integer primary key autoincrement, " 		//0
				+ AlarmMsgDBTableRow.C_MSG_FROM_TEXT + " TEXT, "		//1
				+ AlarmMsgDBTableRow.C_MSG_ID_TEXT + "  TEXT, "			//2
				+ AlarmMsgDBTableRow.C_MSG_TITLE_TEXT + " TEXT, "		//3
				+ AlarmMsgDBTableRow.C_MSG_CONTENT_TEXT + " TEXT, "		//4
				+ AlarmMsgDBTableRow.C_MSG_TIME_TEXT + " integer, "		//5
				+ AlarmMsgDBTableRow.C_MSG_STATUS_INT + " integer, "	//6
				+ AlarmMsgDBTableRow.C_MSG_MARK_INT + " integer, "		//7
				+ AlarmMsgDBTableRow.C_MSG_CHECKED_INT + " integer, "	//8
				+ AlarmMsgDBTableRow.C_MSG_USER_TEXT + " TEXT, "		//9
				+ AlarmMsgDBTableRow.C_MSG_TYPE_INT + " integer "		//10
				+");";
		db.execSQL(createMsgTableSql);
		return 0;
	}
	
	public static final String[] ColumnTypes = {
		"long",		//_id
		"String",	//from
		"String",	//id
		"String",	//title
		"String",	//content
		"String",	//time
		"int",		//status
		"int",		//mark
		"int",		//checked
		"String",	//user
		"int"		//type
	};
	
	@Override
	public int upGrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
	
	@Override
	protected Set<String> getKeyColumns() {
		HashSet<String> keySet = new HashSet<String>();
		keySet.add(AlarmMsgDBTableRow.C_MSG_ID_TEXT);
		return keySet;
	}
	
	public ArrayList<DBTableRow> getAllMsg(int count) {
		String[] condition = {
				AlarmMsgDBTableRow.C_MSG_STATUS_INT + " >= 0"
		};
		return quary(condition, AlarmMsgDBTableRow.C_MSG_TIME_TEXT, true, count, ColumnTypes);
	}
	
	public ArrayList<DBTableRow> getUnreadMsg(int count) {
		String[] condition = {
				AlarmMsgDBTableRow.C_MSG_STATUS_INT + " = 0"
		};
		
		return quary(condition, AlarmMsgDBTableRow.C_MSG_TIME_TEXT, false, count, ColumnTypes);
	}
	
	public ArrayList<DBTableRow> getReadMsg(int count) {
		String[] condition = {
			AlarmMsgDBTableRow.C_MSG_STATUS_INT + " > 0"
		};
		return quary(condition, AlarmMsgDBTableRow.C_MSG_TIME_TEXT, true, count, ColumnTypes);
	}
	
	public ArrayList<String> getNeedPullMsg() {
		String[] columns = {
				AlarmMsgDBTableRow.C_MSG_ID_TEXT
		};
		
		String condition = AlarmMsgDBTableRow.C_MSG_STATUS_INT + " < 0";
		Cursor cursor = m_database.query(TABLE_NAME, columns, condition, null, null, null, null);
		ContentValues cv = new ContentValues();
		cv.put(AlarmMsgDBTableRow.C_MSG_STATUS_INT, String.valueOf(-2));
		m_database.update(TABLE_NAME, cv, condition, null);
		
		ArrayList<String> list = new ArrayList<String>();
		if (cursor == null || !cursor.moveToFirst())
			return list;
		do {
			list.add(cursor.getString(0));
		} while(cursor.moveToNext());
		
		return list;
	}
	
	public int deletePullFailedMsg() {
		return m_database.delete(TABLE_NAME, AlarmMsgDBTableRow.C_MSG_STATUS_INT + " < -1", null);
	}
	
	private static final int unread = 0;
	private static final int read	= 1;
	private static final int marked = 2;
	private static final int all 	= 3;
	private static final int pull	= 4;
	
	public ArrayList<DBTableRow> getAllMsgMore(long _id, boolean direction, int count) {
		return getMoreMsg(_id, all, direction, count);
	}
	
	public ArrayList<DBTableRow> getMoreReadMsg(long _id, boolean direction, int count) {
		return getMoreMsg(_id, read, direction, count);
	}
	
	public ArrayList<DBTableRow> getMoreUnreadMsg(long _id, boolean direction, int count) {
		return getMoreMsg(_id, unread, direction, count);
	}
	
	public ArrayList<DBTableRow> getMoreMarkedMsg(long _id, boolean direction, int count) {
		return getMoreMsg(_id, marked, direction, count);
	}
	
	public ArrayList<DBTableRow> getMoreNeedPullMsg(long _id, boolean direction, int count) {
		return getMoreMsg(_id, pull, direction, count);
	}
	
	private ArrayList<DBTableRow> getMoreMsg(long _id, int where, boolean direction, int count) {
		String condition0, condition1;
		if (direction) 
			condition0 = "_id > " + _id;
		else 
			condition0 = "_id < " + _id;
		
		switch (where) {
		case unread:
			condition1 = AlarmMsgDBTableRow.C_MSG_STATUS_INT + " = 0";
			break;
		case read:
			condition1 = AlarmMsgDBTableRow.C_MSG_STATUS_INT + " > 0";
			break;
		case marked:
			condition1 = AlarmMsgDBTableRow.C_MSG_MARK_INT + " > 0";
			break;
		case all:
			condition1 = AlarmMsgDBTableRow.C_MSG_STATUS_INT + " >= 0";
			break;
		case pull:
			condition1 = AlarmMsgDBTableRow.C_MSG_STATUS_INT + " < 0";
			break;
		default:
			condition1 = "";
		}
		String[] condition = {
				condition0,
				condition1
		};
		return quary(condition, AlarmMsgDBTableRow.C_MSG_TIME_TEXT, !direction, count, ColumnTypes);
	}
	
	public ArrayList<DBTableRow> getMarkedMsg(int count) {
		String[] condition = {
			AlarmMsgDBTableRow.C_MSG_MARK_INT + " > 0"
		};
		return quary(condition, AlarmMsgDBTableRow.C_MSG_TIME_TEXT, false, count, ColumnTypes);
	}
	
	public ArrayList<DBTableRow> getNeedPullMsg(int count) {
		String[] condition = {
				AlarmMsgDBTableRow.C_MSG_STATUS_INT + " < 0"
			};
		return quary(condition, AlarmMsgDBTableRow.C_MSG_TIME_TEXT, false, count, ColumnTypes);
	}
	
	public int markMsgAsRead(String msgId) {
		ContentValues cv = new ContentValues();
		cv.put(AlarmMsgDBTableRow.C_MSG_STATUS_INT, 1);
		String selection = AlarmMsgDBTableRow.C_MSG_ID_TEXT + " = ?";
		String args[] = {msgId};
		return m_database.update(TABLE_NAME, cv, selection, args);
	}
	
	public int updateMsgMark(String msgId, int mark) {
		ContentValues cv = new ContentValues();
		cv.put(AlarmMsgDBTableRow.C_MSG_MARK_INT, mark);
		String selection = AlarmMsgDBTableRow.C_MSG_ID_TEXT + " = ?";
		String args[] = {msgId};
		return m_database.update(TABLE_NAME, cv, selection, args);
	}
	
	public int setMsgCheckedStatus(String msgId, boolean checked) {
		int state = (checked ? 1 : 0);
		ContentValues cv = new ContentValues();
		cv.put(AlarmMsgDBTableRow.C_MSG_CHECKED_INT, state);
		
		if (null != msgId) {
			String selection = AlarmMsgDBTableRow.C_MSG_ID_TEXT + " = ?";
			String args[] = {msgId};
			return m_database.update(TABLE_NAME, cv, selection, args);
		}
		else 
			return m_database.update(TABLE_NAME, cv, null, null);
	}
	
	public int doOnCheckedRows(int action, int param, int flag) {
		ContentValues cv = new ContentValues();
		cv.put(AlarmMsgDBTableRow.C_MSG_CHECKED_INT, 0);
		String where = AlarmMsgDBTableRow.C_MSG_CHECKED_INT + " = 1 ";
		switch (action) {
		case AlarmMsgOperatorBar.ACTION_MARKED_READ:
			cv.put(AlarmMsgDBTableRow.C_MSG_STATUS_INT, 1);
			return m_database.update(TABLE_NAME, cv, where, null);
			
		case AlarmMsgOperatorBar.ACTION_DELETE_MARKED:
			return m_database.delete(TABLE_NAME, where, null);
			
		case AlarmMsgOperatorBar.ACTION_MARKED_UNREAD:
			cv.put(AlarmMsgDBTableRow.C_MSG_STATUS_INT, 0);
			return m_database.update(TABLE_NAME, cv, where, null);
			
		case AlarmMsgOperatorBar.ACTION_MARKED_ALL:
			cv.put(AlarmMsgDBTableRow.C_MSG_CHECKED_INT, param);
			if (0 != flag && param != 0) {
				switch (flag) {
				case AlarmMsgUIController.ALARM_MSG_MARKED:
					where = AlarmMsgDBTableRow.C_MSG_MARK_INT + " > 0 ";
					break;
				case AlarmMsgUIController.ALARM_MSG_READ:
					where = AlarmMsgDBTableRow.C_MSG_STATUS_INT + " > 0 ";
					break;
				case AlarmMsgUIController.ALARM_MSG_UNREAD:
					where = AlarmMsgDBTableRow.C_MSG_STATUS_INT + " = 0 ";
					break;
				default:
					where = AlarmMsgDBTableRow.C_MSG_STATUS_INT + " >= 0 ";
				}
			}
			return m_database.update(TABLE_NAME, cv, where, null);
			
		case AlarmMsgOperatorBar.ACTION_MARKED_CARE:
			cv.put(AlarmMsgDBTableRow.C_MSG_MARK_INT, 1);
			return m_database.update(TABLE_NAME, cv, where, null);
			
		case AlarmMsgOperatorBar.ACTION_DISMISS_CARE:
			cv.put(AlarmMsgDBTableRow.C_MSG_MARK_INT, 0);
			return m_database.update(TABLE_NAME, cv, where, null);
			
		default:
			return m_database.update(TABLE_NAME, cv, null, null);
		}
	}
	
	public int removeMsg(String msgId) {
		String selection = AlarmMsgDBTableRow.C_MSG_ID_TEXT + " = ?";
		String args[] = {msgId};
		return m_database.delete(TABLE_NAME, selection, args);
	}
	
	public int getUnreadMsgCount() {
		return getMsgCount(AlarmMsgDBTableRow.C_MSG_STATUS_INT + " = 0");
	}
	
	public int getAllMsgCount() {
		return getMsgCount(AlarmMsgDBTableRow.C_MSG_STATUS_INT + " >= 0");
	}
	
	public int getReadMsgCount() {
		return getMsgCount(AlarmMsgDBTableRow.C_MSG_STATUS_INT + " > 0");
	}
	
	public int getMarkedCount() {
		return getMsgCount(AlarmMsgDBTableRow.C_MSG_MARK_INT + " > 0");
	}
	
	public int getCheckedCount() {
		return getMsgCount(AlarmMsgDBTableRow.C_MSG_CHECKED_INT + " > 0");
	}
	
	public int getPullCout() {
		return getMsgCount(AlarmMsgDBTableRow.C_MSG_STATUS_INT + " < 0");
	}
	
	public int getMsgCount(String condition) {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from ").append(TABLE_NAME);
		if (null != condition && condition.length() != 0)
			sb.append(" where ").append(condition);
		Cursor cursor = m_database.rawQuery(sb.toString(), null);
		if (null != cursor && cursor.moveToFirst())
			return cursor.getInt(0);
		else 
			return 0;
	}
}
