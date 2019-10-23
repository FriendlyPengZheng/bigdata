package chat_data.base;

import android.os.Bundle;

public class DBTableRow {
	protected Bundle m_conlumnValueBundle = null;
	
	protected DBTableRow(Bundle bundle) {
		m_conlumnValueBundle = bundle;
	}
	
	public static DBTableRow build(Bundle bundle) {
		return new DBTableRow(bundle);
	}
	
	public final Bundle getValues() {
		return m_conlumnValueBundle;
	}
	
	public long getRowId() {
		return m_conlumnValueBundle.getLong("_id");
	}
	
	public final String getTableName() {
		return "";
	}
	
//	public abstract class TableRowBuilder {
//		protected Bundle m_bundle = new Bundle();
//		
//		public abstract DBTableRow build(final Bundle bundle);
//		
//		public DBTableRow build() {
//			return build(m_bundle);
//		}
//		
//		public abstract boolean check(Bundle bundle);
//		
//		public boolean check() {
//			return check(m_bundle);
//		}
//		
//		protected abstract void fillDefaultValues();
//	}
}
