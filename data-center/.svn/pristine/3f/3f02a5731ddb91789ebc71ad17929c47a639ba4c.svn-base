package chat_setting;

import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import chat_data.base.DBTable;

public class SettingDBTable extends DBTable{
	public static String TABLE_NAME	= "settings";
	private static String C_SETTING_NAME	= "key";
	private static String C_SETTING_VALUE	= "value";
	
	public static int BELL_ONCE 	= 0x0001;
	public static int SHAKE_FLAG	= 0x0002;
	public static int API_KEY		= 0x0003;
	public static int SECRET_KEY	= 0x0004;
	public static int SERVER_IP		= 0x0005;
	public static int SERVER_PORT	= 0x0006;
	public static int ALARM_TIME	= 0x0007;
	public static int ALARM_COUNT	= 0x0008;
	public static int MSG_SHOW_COUNT= 0x0009;
	public static int MSG_REFRESH	= 0x000A;
	public static int MSG_PULL_WAIT	= 0x000B;
	public static int BD_TOKEN		= 0x000C;
	public static int USER_NAME		= 0x000D;
	public static int MOBILE_NUMBER	= 0x000E;
	public static int APP_MODE		= 0x0010;

	@Override
	public int createTable(SQLiteDatabase db) {
		String createMsgTableSql = "Create table if not exists " 		//不存在时创建
				+ TABLE_NAME											//表名
				+ " (_id integer primary key autoincrement, " 			//0
				+ C_SETTING_NAME + " integer, "							//1
				+ C_SETTING_VALUE + " TEXT "								//2
				+");";
		db.execSQL(createMsgTableSql);
		return 0;
	}
	
	public String getSettingValue(int key) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ").append(C_SETTING_VALUE).append(" from ").append(TABLE_NAME)
			.append(" where ").append(C_SETTING_NAME).append(" = ").append(key);
		Cursor cursor = m_database.rawQuery(sql.toString(), null);
		String value = "";
		if (null != cursor && cursor.moveToFirst())
			value = cursor.getString(0);
		cursor.close();
		return value;
	}
	
	public void setSetingValue(int key, String value) {
		ContentValues cv = new ContentValues();
		cv.put(C_SETTING_VALUE, value);
		String where = C_SETTING_NAME + " = ?";
		String[] args = {
				String.valueOf(key)
		};
		int count = m_database.update(TABLE_NAME, cv, where, args);
		if (count == 0) {
			cv.put(C_SETTING_NAME, String.valueOf(key));
			m_database.insert(TABLE_NAME, null, cv);
		}
	}
	
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
		return null;
	}
}
