package alarm_client;

import java.util.ArrayList;

import android.util.Log;

public class AlarmMsgPuller {
	private static boolean m_state = false;
	
	public static boolean checkState() {
		return m_state;
	}
	
	public static void pullAlarmMsg() {
//		Log.d("", "===================================================================");
		if (m_state)
			return;
		m_state = true;
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					sleep(10 * 1000);
//					Log.d("", "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					m_state = false;
					ArrayList<String> idList = AlarmClient.Instance().getMsgCenter().getNeedPullMsg();
					Log.d("", "pull list : " + idList.size());
					AlarmClient.Instance().sendPullMsgRequest(idList);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.run();
			}
		};
		thread.start();
	}
}
