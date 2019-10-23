package chat_data.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import chat_data.alarm_msg.AlarmMsgDBTable;
import chat_setting.SettingDBTable;

public class LocalDatabaseHelper {
	
	private static final String DB_NAME = "TaomeeChat.db";		///< 数据库名字
	private static final int DB_VERSION = 1;					///< 数据库版本

	private LocalDB m_db = null;
	private AlarmMsgDBTable m_alarmMsgTable = null;
//	private AccountDBTable m_accountTable = null;
	private SettingDBTable m_settingTable = null;
	
	public LocalDatabaseHelper(Context context) {
		m_alarmMsgTable = new AlarmMsgDBTable();
//		m_accountTable = new AccountDBTable();
		m_settingTable = new SettingDBTable();
		DBTable[] tables = {m_alarmMsgTable, m_settingTable};
		m_db = new LocalDB(context, tables);
//		m_accountTable.initialize(m_db.getWritableDatabase());
		m_alarmMsgTable.initialize(m_db.getWritableDatabase());
		m_settingTable.initialize(m_db.getWritableDatabase());
	}
	
	public AlarmMsgDBTable getAlarmMsgTable() {
		return m_alarmMsgTable;
	}
	
//	public AccountDBTable getAccountDBTable() {
//		return m_accountTable;
//	}
	
	public SettingDBTable getSettingDBTable() {
		return m_settingTable;
	}

	private class LocalDB extends SQLiteOpenHelper {
		private DBTable[] m_tables;

		public LocalDB(Context context, DBTable[] tables) {
			super(context, DB_NAME, null, DB_VERSION);
			m_tables = tables;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			for (DBTable table : m_tables) {
				table.createTable(db);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			for (DBTable table : m_tables) {
				table.upGrade(db, oldVersion, newVersion);
			}
		}
	}
}
