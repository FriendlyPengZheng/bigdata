package com.taomee.tms.mgr.module;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;






//import org.apache.catalina.startup.UserDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.ctl.ConfigService;

@Service
public class UserService {
	
	private String powerSystem = "http://am-server.taomee.net/index.php";
	private String[] admin;
 
	@Autowired
	HttpSession httpSession;
	@Autowired
	private CookieUtils cookie;
	
	
	UserService() {
		ConfigService config = new ConfigService();
		this.powerSystem = config.getPowerSystem();
		this.admin = config.getAdmin();
	}
	
	public int login(String userName, String passWord) {
		if ("".equals(userName) || userName == null || "".equals(passWord) || passWord == null) {
			return -1;
		}
		//TODO 权限系统验证
		httpSession.setAttribute("user_id", "283");
		httpSession.setAttribute("user_name", userName);
		//httpSession.setAttribute("is_admin", "1"); //TODO
		String[] authids = {
				"24138770016",
				"24141190016",
				"24140640016",
				"24141350016",
				"24127270016",
				"24135790016",
				"24135770016",
				"24135780016",
				"24135800016",
				"24134770016",
				"24135760016",
				"24135760018",
				"24127219999",
				"-3",
				"-2",
				"-1",
				"0",
				"24138969999",
				"24127259999",
				"24139929999",
				"24128289999",
				"24127389999",
				"24127409999",
				"24127650016",
				"24127610016",
				"24127379999",
				"24127620016",
				"24127630016",
				"24127640016",
				"24127660016",
				"24127670016",
				"24127680016",
				"24127690016",
				"24127700016",
				"24127710016",
				"24127419999",
				"24127720016",
				"24127730016",
				"24127740016",
				"24127420016",
				"24127439999",
				"24127750016",
				"24127449999",
				"24127299999",
				"24127319999",
				"24127289999",
				"24127329999",
				"24127330016",
				"24127349999",
				"24127359999",
				"24127490016",
				"24127450016",
				"24127460016",
				"24127470016",
				"24127480016",
				"24127500016",
				"24127510016",
				"24127520016",
				"24127530016",
				"24127540016",
				"24127550016",
				"24127560016",
				"24127570016",
				"24127580016",
				"24127590016",
				"24127390016",
				"24127300016",
				"24127760016",
				"24127600016",
				"24127740018",
				"24127580018",
				"24138030016",
				"24128299999",
				"24128309999",
				"24128310016",
				"24128329999",
				"24128330016",
				"24128349999",
				"24128350016",
				"24128360016",
				"24128370016",
				"24128380016",
				"24128390016",
				"24128400016",
				"24128410016",
				"24128420016",
				"24128530016",
				"24128430016",
				"24128440016",
				"24128450016",
				"24127760018",
				"24127600018",
				"24128430018",
				"24128450018",
				"24138020016",
				"24136139999",
				"24138789999",
				"24138799999",
				"24138810016",
				"24138820016",
				"24138830016",
				"24138840016",
				"24138850016",
				"24136130016",
				"24137139999",
				"24137130016",
				"24137869999",
				"24137870016",
				"24137880016",
				"24127440016",
				"24127350016",
				"24138860016",
				"24138809999",
				"24138870016",
				"24138880016",
				"24138890016",
				"24138900016",
				"24138910016",
				"24138920016",
				"24138930016",
				"24138940016",
				"24138950016",
				"24138960016",
				"24127370016",
				"24127280016",
				"24128290016",
				"24140150016",
				"24140020016",
				"24139980016",
				"24139990016",
				"24140000016",
				"24140010016",
				"24140030016",
				"24140040016",
				"24140070016",
				"24140080016",
				"24140090016",
				"24140069999",
				"24140100016",
				"24140120016",
				"24140130016",
				"24140140016",
				"24140170016",
				"24140050016",
				"24140180016",
				"24140140018",
				"24140180018",
				"24140210016",
				"24139960016",
				"24139979999",
				"24140590016",
				"24140590018",	
				"24140470016",
				"24140470018",
		};
		Arrays.sort(authids);
		httpSession.setAttribute("user_auth_list", authids);
		String is_admin = "0";
		for(String id : authids) {
			if(id.equals("-2")) {
				is_admin = "1";
				break;
			}
		}
		httpSession.setAttribute("is_admin", is_admin); //TODO
		return 1;
	}
	
	public String[] getAuthority() {
		return (String[]) httpSession.getAttribute("user_auth_list");
	}
	
	public void setAuthority(String[] authority) {
		if(authority == null || authority.length == 0){
			//String[]  authoList = fetchAuthority();
			ArrayList<String>  authoList = fetchAuthority();
			if(authoList == null || authoList.size() == 0) {
				return;
			}
			String isAdmin = getIsAdmin();
			if(isAdmin != null && isAdmin.equals("1")) {
				authoList.add("-2");
			}
			authoList.add("-1");
			authoList.add("-3");
			String[] authStingList = new String[authoList.size()];
			for(int i = 0; i < authoList.size(); i++){
				authStingList[i] = authoList.get(i);
			}
			httpSession.setAttribute("user_auth_list", authStingList);
		} else {
			httpSession.setAttribute("user_auth_list", authority);
		}
	}
	
	public String getUserName() {
		return (String) httpSession.getAttribute("user_name");
	}
	
	public void setUserName(String userName) {
		httpSession.setAttribute("user_name", userName);
	}
	
	public String getUserId() {
		return (String) httpSession.getAttribute("user_id");
	}
	
	public void setUserId(String userId) {
		httpSession.setAttribute("user_id", userId);
		for(String id: admin) {
			if(id.equals(userId)) {
				this.SetIsAdmin("1");
				break; 
			}
		}
	}
	
	public String getIsAdmin() {
		return (String) httpSession.getAttribute("is_admin");
	}
	
	public void SetIsAdmin(String isAdmin) {
		httpSession.setAttribute("is_admin", isAdmin);
	}
	
	public String getIsGuest() {
		String userName = (String) httpSession.getAttribute("user_name");
		return userName.indexOf("guest_") < 0 ? "0" : "1";
	}
	
	/*
	 * get user info
	 */
	public Map<String, String> getUserInfo() {
		Map<String,String> userInfo = new HashMap<String, String>();
		userInfo.put("current_admin_name", this.getUserName());
		userInfo.put("current_admin_id", this.getUserId());
		userInfo.put("is_super_admin", this.getIsAdmin());
		userInfo.put("is_guest", this.getIsGuest());
		return userInfo;
	}
	
	public void logout() {
		httpSession.invalidate();
	}
	
	public Boolean checkSession() {
		String userName = getUserName();
		if(userName == null || userName.isEmpty()) {
			return false;
		}
		String[] authority = this.getAuthority();
		if(authority == null || authority.length == 0) {
			return false;
		}
		return true;
	}
	
	
	private String _request(String cmd, Map<String, String> extr) {
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		String queryString = "cmd="+ cmd + "&client_ip=" + addr.getHostAddress();
		for(Map.Entry<String, String> entry : extr.entrySet()) {
			queryString += "&";
			queryString += entry.getKey();
			queryString += "=";
			queryString += entry.getValue();
		}
		System.out.println("queryString:" + queryString);
		return HttpRequest.sendGet(powerSystem, queryString);
	}
	
	/*public String[] fetchAuthority() {
		Map<String, String> extr = new HashMap<String, String>();
		extr.put("user_id", this.getUserId());
		extr.put("system_id", "24");
		String data = this._request("1003", extr);
		System.out.println("@@@@@@autho:" + data);
		
		if(data == null || data.isEmpty() || data.equals("false")){
			return null;
		}
		
		JSONObject dataObject = JSONObject.parseObject(data);
		String result = dataObject.getString("result");
		
		if(result == null || result.isEmpty() || !result.equals("0")) {
			return null;
		}
		
		JSONArray authoObj = dataObject.getJSONArray("data");
		if(authoObj == null || authoObj.isEmpty()){
			return null;
		}
		
		String[] autoList = new String[authoObj.size()];
		//ArrayList<String>  autoList = new ArrayList<String> ();
		for(int i = 0; i < authoObj.size(); i++) {
			JSONObject info = authoObj.getJSONObject(i);
			if(info == null || info.isEmpty()){
				continue;
			}
			autoList[i] = info.getString("id");
			//System.out.println("@@@autoList["+i+"]:"+ autoList.get(i));
		}
		
		return autoList;
		
	}*/
	
	public ArrayList<String> fetchAuthority() {
		Map<String, String> extr = new HashMap<String, String>();
		extr.put("user_id", this.getUserId());
		extr.put("system_id", "24");
		String data = this._request("1003", extr);
		System.out.println("@@@@@@autho:" + data);
		
		if(data == null || data.isEmpty() || data.equals("false")){
			return null;
		}
		
		JSONObject dataObject = JSONObject.parseObject(data);
		String result = dataObject.getString("result");
		
		if(result == null || result.isEmpty() || !result.equals("0")) {
			return null;
		}
		
		JSONArray authoObj = dataObject.getJSONArray("data");
		if(authoObj == null || authoObj.isEmpty()){
			return null;
		}
		
		//String[] autoList = new String[authoObj.size()];
		ArrayList<String>  autoList = new ArrayList<String> ();
		for(int i = 0; i < authoObj.size(); i++) {
			JSONObject info = authoObj.getJSONObject(i);
			if(info == null || info.isEmpty()){
				continue;
			}
			autoList.add(info.getString("id"));
			//System.out.println("@@@autoList["+i+"]:"+ autoList.get(i));
		}
		
		return autoList;
		
	}
}
