package alarm_client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import stat_app_proto.AppLoginRequest.StatAppLoginRequest;
import stat_app_proto.AppLoginRequest.StatAppLoginResponse;
import stat_app_proto.AppPullMsg.AppPullMsgRequest;
import stat_app_proto.AppPullMsg.AppPullMsgResponse;
import android.util.Log;
import chat_base.ChatUtils;

public class AlarmTcpClient {
	private static final String TAG = "TCP_CLIENT";
	private String m_ServerHost = null;
	private int m_Port = -1;
	private int m_timeout = 5000; //ms
	
	public enum TcpClientStatus {
		SOCKET_INVALID,
		SOCKET_INITING,
		SOCKET_WORKING,
		SOCKET_ERROR
	}
	private interface TcpCallback {}
	
	public AlarmTcpClient(final String host, final int port) {
		m_ServerHost = host;
		m_Port = port;
	}
	
	/**
	 * TcpClient.send() result callback
	 * @author lance
	 *
	 */
	public interface SendResultCallback extends TcpCallback{
		/**
		 * @param result : true -> success, false -> failed
		 */
		void onSendCompleted(boolean result);
	}
	
	/**
	 * Send Message Request By TCP Connect
	 * @param msg [IN] : message will be send
	 * @param callback [OUT] : send result callback
	 * @return true or false
	 */
	public boolean send(final String msg, final SendResultCallback callback) {
		if (null == msg || msg.length() == 0 || null == callback) {
			return false;
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Socket socket = new Socket(m_ServerHost, m_Port);
					socket.setSoTimeout(m_timeout);
					OutputStream outStream = socket.getOutputStream();
					outStream.write(msg.getBytes(), 0, msg.length());
					outStream.flush();
					socket.close();
				}
				catch (Exception e) {
					e.printStackTrace();
					callback.onSendCompleted(false);
				}
				callback.onSendCompleted(true);
			}
		};
		postRequest(runnable);
		
		return true;
	}
	
	/**
	 * TCPClient.recv() result callback
	 * @author lance
	 *
	 */
	public interface RecvResultCallback extends TcpCallback{
		/**
		 * @param recvedStr : Received String
		 */
		void onRecvcompleted(String recvedStr);
	}

	/**
	 * 消息发送并接受返回数据
	 * @param msg 要发送的内容
	 * @param callback 返回数据的回调接口对象
	 * @return
	 */
	public boolean sendMsgAndRecvResponse(final String msg, final RecvResultCallback callback) {
		if (null == msg || msg.length() == 0)
			return false;
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Socket socket = new Socket(m_ServerHost, m_Port);
					socket.setSoTimeout(m_timeout);
					
					OutputStream outStream = socket.getOutputStream();
					outStream.write(msg.getBytes(), 0, msg.length());
					outStream.flush();
					
					String recvString = new String();
					InputStream inStream = socket.getInputStream();
					byte buffer[] = new byte[1024];
					int ret;
					while ((ret = inStream.read(buffer, 0, buffer.length)) != -1) {
						Log.i(TAG, "recv : " + ret + "\t" + new String(buffer, 0, ret));
						if (0 == ret)
							break;
						recvString += new String(buffer, 0, ret);
					}
					
					callback.onRecvcompleted(recvString);
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
					callback.onRecvcompleted(null);
				}
			}
		};
		postRequest(runnable);
		
		return true;
	}
	
	/**
	 * 登录请求的返回状态回调接口类
	 * @author lance
	 *
	 */
	public interface LoginResponseCallback {
		/**
		 * 登录请求的返回状态回调接口
		 * @param response 服务器返回消息
		 */
		void onResponse(final StatAppLoginResponse response);
	}
	
	/**
	 * APP 登录请求
	 * @param request 登录信息，包含用户名，密码，token，设备类型
	 * @param callback 服务器返回的状态回调接口对象
	 * @return
	 */
	public boolean sendLoginRequest(final StatAppLoginRequest request, final LoginResponseCallback callback) {
		
		if (null == request || null == callback)
			return false;
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
//					Log.d(TAG, "Send Login Request");
					Socket socket = new Socket(m_ServerHost, m_Port);
					socket.setSoTimeout(m_timeout);
					
					int pkglen = 4 + 4 + request.getSerializedSize();
					OutputStream sendStream = socket.getOutputStream();
					sendStream.write(ChatUtils.packHeader(pkglen, ChatUtils.POROTO_REGISTER));
					request.writeTo(sendStream);
					sendStream.flush();
					
					InputStream recvStream = socket.getInputStream();
					byte buffer[] = new byte[8];
					int recvedLen = 0;
					int ret = 0;
					do {
						ret = recvStream.read(buffer, recvedLen, 8-recvedLen);
						if (ret == -1) {
							callback.onResponse(null);
							socket.close();
							return;
						}
						recvedLen += ret;
					} while(recvedLen < 8);
//					Log.d(TAG, "pkglen : " + ChatUtils.toHH(buffer)[0] + ", protoId : " + ChatUtils.toHH(buffer)[1]);
					
					int resLen = ChatUtils.toHH(buffer)[0] - 8;
					byte[] resBuffer = new byte[resLen];
					recvedLen = 0;
					do {
						ret = recvStream.read(resBuffer, recvedLen, resLen - recvedLen);
						if (ret == -1) {
							callback.onResponse(null);
							socket.close();
							return;
						}
						recvedLen += ret;
					} while (recvedLen < resLen);
					
					StatAppLoginResponse response = StatAppLoginResponse.parseFrom(resBuffer);
					callback.onResponse(response);
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResponse(null);
				}
			}
		};
		postRequest(runnable);
		
		return true;
	}
	
	/**
	 * APP 拉取信息请求的相应回调接口类
	 * @author lance
	 *
	 */
	public interface PullMsgResponseCallback {
		/**
		 * APP 拉取请求的应答回调接口
		 * @param response 应答信息，包含消息的个数和各个消息的具体内容（标题，内容）
		 */
		void onResponse(final AppPullMsgResponse response);
	}
	
	/**
	 * 发送拉取信息请求
	 * @param request 拉取信息的请求
	 * @param callback 服务器返回数据的回调接口对象
	 * @return
	 */
	public boolean sendPullMsgRequest(final AppPullMsgRequest request, final PullMsgResponseCallback callback) {
		if (null == request || null == callback) {
			return false;
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
//					Log.d(TAG, "Send Pull Msg Request");
					Socket socket = new Socket(m_ServerHost, m_Port);
					socket.setSoTimeout(m_timeout);
					
					int pkglen = 4 + 4 + request.getSerializedSize();
					OutputStream sendStream = socket.getOutputStream();
					sendStream.write(ChatUtils.packHeader(pkglen, ChatUtils.POROTO_PULLMSG));
					request.writeTo(sendStream);
					sendStream.flush();
					
					InputStream recvStream = socket.getInputStream();
					byte buffer[] = new byte[8];
					int recvedLen = 0;
					int ret = 0;
					do {
						ret = recvStream.read(buffer, recvedLen, 8-recvedLen);
						if (ret == -1) {
							callback.onResponse(null);
							socket.close();
							return;
						}
						recvedLen += ret;
					} while(recvedLen < 8);
					
					int resLen = ChatUtils.toHH(buffer)[0] - 8;
					byte[] resBuffer = new byte[resLen];
					recvedLen = 0;
					do {
						ret = recvStream.read(resBuffer, recvedLen, resLen - recvedLen);
						if (ret == -1) {
							callback.onResponse(null);
							socket.close();
							return;
						}
						recvedLen += ret;
					} while (recvedLen < resLen);
					
					AppPullMsgResponse response = AppPullMsgResponse.parseFrom(resBuffer);
//					Log.d(TAG, "Recv : " + response.getMsgCount());
					
					callback.onResponse(response);
					socket.close();
				}
				catch (Exception e) {
					e.printStackTrace();
					callback.onResponse(null);
				}
			}
		};
		postRequest(runnable);
		return true;
	}
	
	private void postRequest(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.start();
	}
}
