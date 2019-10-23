package message_push_client;

import java.util.ArrayList;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;

import com.baidu.android.pushservice.CustomPushNotificationBuilder;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class BDCloudPushClient {
	private static String API_KEY = "gA8IMDOc2R3HCH3uVNrYPk9H";
	
	private static BDCloudPushClient s_Instance = null;
	public static BDCloudPushClient Instance() {
		return s_Instance;
	}
	
	public static BDCloudPushClient InitInstance(Context context) {
		if (null == s_Instance) 
			s_Instance = new BDCloudPushClient(context);
		return s_Instance;
	}
	
	private Context m_context = null;
	
	private BDCloudPushClient(Context context) {
		m_context = context;
	}
	
	public void setNotification(Resources resource, String pkgName, int icon) {
		
        CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
        		m_context, resource.getIdentifier("notification_custom_builder", "layout", pkgName), 
        		resource.getIdentifier("notification_icon", "id", pkgName), 
        		resource.getIdentifier("notification_title", "id", pkgName), 
        		resource.getIdentifier("notification_text", "id", pkgName));
        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        cBuilder.setStatusbarIcon(icon);
        cBuilder.setLayoutDrawable(resource.getIdentifier("simple_notification_icon", "drawable", pkgName));
		PushManager.setNotificationBuilder(m_context, 1, cBuilder);
	}
	
	public void startWork() {
		//启动百度云推送client service
		PushManager.startWork(m_context,
				PushConstants.LOGIN_TYPE_API_KEY,
				API_KEY);
	}
	
	public void bindTag(ArrayList<String> tags) {
		PushManager.setTags(m_context, tags);
	}
}
