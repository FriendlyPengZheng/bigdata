package chat_data.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public abstract class DBTable {
	protected SQLiteDatabase m_database = null;
	
	public void initialize(SQLiteDatabase db) {
		m_database = db;
	}
	
	public abstract int createTable(SQLiteDatabase db);
	
	public int dropTable() {
		try {
			String sql = "drop table " + getTableName();
			m_database.execSQL(sql);
		}
		catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
		return 0;
	}
	
	public abstract int upGrade(SQLiteDatabase db, int oldVersion, int newVersion);
	
	public abstract String getTableName();
	
	public boolean insert(DBTableRow row) {
		if (null == m_database) 
			return false;
		
		ContentValues cv = getValuesFromRow(row);
		return (-1 != m_database.insert(getTableName(), null, cv));
	}
	
	public int update(Map<String, Object> condition, Map<String, Object> newValues) {
		if (null == m_database) 
			return 0;
		
		return m_database.update(getTableName(), getValuesFromMap(newValues), getConditionString(condition), null);
	}
	
	protected int update(Map<String, Object> condition, DBTableRow row) {
		if (null == m_database)
			return 0;
		return m_database.update(getTableName(), getValuesFromRow(row), getConditionString(condition), null);
	}
	
	public int delete(Map<String, Object> condition) {
		if (null == m_database) 
			return 0;
		return m_database.delete(getTableName(), getConditionString(condition), null);
	}
	
	public int clear() {
		if (null == m_database)
			return 0;
		return m_database.delete(getTableName(), "1", null);
	}
	
	public ArrayList<DBTableRow> quary(String[] condition, String orderBy, boolean order, int limit, String[] types) {
		if (condition == null || null == m_database)
			return null;
		
		String sql = "select * from " + getTableName();
		if (null != condition)
			for (int i=0; i<condition.length; ++i) {
				if (condition[i].length() == 0)
					continue;
				
				if (0 == i)
					sql += " where ";
				else
					sql += " and ";
				sql += condition[i];
			}
		
		if (null != orderBy) 
			sql += " order by " + orderBy;
		if (order) 
			sql += " desc ";
		else 
			sql += " asc ";
		if (limit > 0) 
			sql += " limit " + limit;
		
//		Log.d("", sql);
		
		Cursor cursor = m_database.rawQuery(sql, null);
		if (null == cursor || cursor.getCount() == 0) 
			return new ArrayList<DBTableRow>();
		
		int step = 0;
		if (order) {
			cursor.moveToLast();
			step = -1;
		}
		else {
			cursor.moveToFirst();
			step = 1;
		}
		
		ArrayList<DBTableRow> list = new ArrayList<DBTableRow>();
		do {
			Bundle bundle = new Bundle();
			for(int i=0; i<cursor.getColumnCount(); ++i) {
				if (i >= types.length)
					bundle.putString(cursor.getColumnName(i), cursor.getString(i));
				else if (types[i].equals("String"))
					bundle.putString(cursor.getColumnName(i), cursor.getString(i));
				else if (types[i].equals("int"))
					bundle.putInt(cursor.getColumnName(i), cursor.getInt(i));
				else if (types[i].equals("long"))
					bundle.putLong(cursor.getColumnName(i), cursor.getLong(i));
				else
					bundle.putString(cursor.getColumnName(i), cursor.getString(i));
			}
			list.add(DBTableRow.build(bundle));
		} while(cursor.move(step));
		cursor.close();
		return list;
	}
	
	public int updateOrInsert(DBTableRow row) {
		if (null == m_database) 
			return 0;
		
		int ret = m_database.update(getTableName(), getValuesFromRow(row), getConditionStrFromRowByKey(getKeyColumns(), row), null);
		if (0 == ret)
			if (-1 != m_database.insert(getTableName(), null, getValuesFromRow(row)))
				return 1;
			else 
				return 0;
		
		return ret;
	}
	
	protected abstract Set<String> getKeyColumns();
	
	protected String getConditionStrFromRowByKey(Set<String> keys, DBTableRow row) {
		Iterator<String> it = keys.iterator();
		boolean first = true;
		String condition = new String();
		while (it.hasNext()) {
			String key = it.next();
			if (!first) 
				condition += " and ";
			condition += key + " = '" + row.getValues().get(key).toString() + "'";
		}
		return condition;
	}
	
	protected ContentValues getValuesFromRow(DBTableRow row) {
		ContentValues cv = new ContentValues();
		Bundle bundle = row.getValues();
		Set<String> keys = bundle.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			cv.put(key, bundle.get(key).toString());
		}
		return cv;
	}
	
	protected ContentValues getValuesFromMap(Map<String, Object> map) {
		ContentValues cv = new ContentValues();
		Set<String> keys = map.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			cv.put(key, map.get(key).toString());
		}
		return cv;
	}
	
	protected String getConditionString(Map<String, Object> map) {
		String condition = new String();
		Set<String> keys = map.keySet();
		Iterator<String> it = keys.iterator();
		boolean first = true;
		while (it.hasNext()) {
			String key = it.next();
			if (!first) 
				condition += " and ";
			condition += key + " = " + map.get(key).toString();
		}
		return condition;
	}
	
	public void onEnd() {
		if (null != m_database) {
			m_database.close();
			m_database = null;
		}
	}
	
//	public abstract ChatTableRowInfoBuilder getRowInfoBuilder();
//	public abstract ChatTableRowInfo getNewInfo();
}
