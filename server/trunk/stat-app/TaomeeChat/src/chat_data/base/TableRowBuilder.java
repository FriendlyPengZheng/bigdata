package chat_data.base;

import android.os.Bundle;

public abstract class TableRowBuilder {
	protected Bundle m_bundle = new Bundle();
	
	public abstract DBTableRow build(final Bundle bundle);
	
	public DBTableRow build() {
		return build(m_bundle);
	}
	
	public abstract boolean check(Bundle bundle);
	
	public boolean check() {
		return check(m_bundle);
	}
	
	protected abstract void fillDefaultValues();
}
