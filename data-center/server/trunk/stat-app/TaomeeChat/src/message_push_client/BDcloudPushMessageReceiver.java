package message_push_client;

import main_taomeechat.TaomeeChatMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.baidu.android.pushservice.PushConstants;

public class BDcloudPushMessageReceiver extends BroadcastReceiver {
	
	private static final String TAG = "BDC_RECV";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = null;
		Bundle bundle = new Bundle();
		String title = null, msg = null;
		
		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			//获取消息内容
			String message = intent.getExtras().getString(
					PushConstants.EXTRA_PUSH_MESSAGE_STRING);

//			Log.i(TAG, "onMessage: " + message);
			JSONObject contentJson = null;
			
			try {
				contentJson = new JSONObject(message);
				if (contentJson.has("title"))
					title = contentJson.getString("title");
				else 
		        	title = "no title";
				if (contentJson.has("description")) 
					msg = contentJson.getString("description");
				else 
		        	msg = "";
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
			
			action = MsgPushUtils.ACTION_MESSAGE;
		}
		
		else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
			//处理绑定等方法的返回数据
			//获取方法
			final String method = intent
					.getStringExtra(PushConstants.EXTRA_METHOD);
			//方法返回错误码。若绑定返回错误（非0），则应用将不能正常接收消息。
			//绑定失败的原因有多种，如网络原因，或access token过期。
			//请不要在出错时进行简单的startWork调用，这有可能导致死循环。
			//可以通过限制重试次数，或者在其他时机重新调用来解决。
			int errorCode = intent.getIntExtra(PushConstants.EXTRA_ERROR_CODE, PushConstants.ERROR_SUCCESS);
			String content = "";
			if (intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT) != null) {
				//返回内容
				content = new String(
					intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));
			}
			
			
			bundle.putString(MsgPushUtils.RESPONSE_METHOD, method);
			bundle.putInt(MsgPushUtils.RESPONSE_ERRCODE, errorCode);
			bundle.putString(MsgPushUtils.RESPONSE_CONTENT, content);
			action = MsgPushUtils.ACTION_RESPONSE;
		}
		
		else if (intent.getAction().equals(PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) {
			title = intent
					.getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);
			msg = intent
					.getStringExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT);
			
			action = MsgPushUtils.ACTION_CLICK;
		}
		
		if (action != null) {
			if (null != title)
				bundle.putString("title", title);
			if (null != msg)
				bundle.putString("msg", msg);
			postActionIntent(context, action, bundle);
		}
	}
	
	private void postActionIntent(Context context, String action, Bundle bundle) {
		Intent aIntent = new Intent(action);
		if (bundle!=null) 
			aIntent.putExtras(bundle);
		if (!TaomeeChatMainActivity.RUNNING_FLAG && action.equals(MsgPushUtils.ACTION_MESSAGE)) {
//			aIntent.setClass(context, TaomeeChatBackgroundService.class);
			context.startService(aIntent);
		}
		else {
			aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			aIntent.setClass(context, TaomeeChatMainActivity.class);
			context.startActivity(aIntent);
		}
	}
}
