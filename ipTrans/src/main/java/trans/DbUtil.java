package trans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class DbUtil {

	private static String USER = "root";
	private static String PWD = "root";
	private static String HOST = "jdbc:mysql://10.1.1.34:3306/db_ip_distribution_15_Q2?characterEncoding=UTF-8";

	private static Connection conn = null;
	private static PreparedStatement ps = null;
	private static ResultSet rs = null;
	
	public static String selectCityCode(String provinceName,String cityName){
		Map<Integer, String> map = new HashMap<Integer, String>();
		String cityCodeAndName = null;
		if("".equals(cityName) || cityName == null){
			return null;
		}
		String sql;
		sql = "select city_code,city_name from t_city_code where city_name like ?";
		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			if(cityName.equals(provinceName) && 
				("上海".equals(provinceName)
				||"北京".equals(provinceName)
				||"天津".equals(provinceName)
				||"重庆".equals(provinceName))){
				ps.setString(1, "市辖区"+"("+provinceName+")");
			}else{
				ps.setString(1, cityName+"%"+"("+provinceName+")");
			}
			rs = ps.executeQuery();
			System.out.println(ps.toString());
			int i = 0;
			while(rs.next()){
				i++;
				String name = rs.getString(2).split("[(]")[0];
				String code = rs.getString(1);
				String value = code+"#"+name;
				map.put(i, value);
			}
			if(i==0){
				cityCodeAndName = null;
			}
			if(i==1){
				cityCodeAndName = map.get(1);
			}
			if(i>=2){
				boolean isCity = false;
				boolean isRegion = false;
				int m = 0;
				int n = 0;
				int k = 0;
				for(int j = 1; j <= map.size(); j++){
					String cn = map.get(j);
					char[] cns = cn.toCharArray();
					int size = cns.length;
					if(cns[size-1]=='市'){
						isCity = true;
						m = j;
					}else if(cns[size-1]=='区'){
						isRegion = true;
						n = j;
					}else{
						k = j;
					}
				}
				if(isCity){
					cityCodeAndName = map.get(m);
				}else if(isRegion){
					cityCodeAndName = map.get(n);
				}else{
					cityCodeAndName = map.get(k);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close();
		}
		return cityCodeAndName;
	}
	
	public static String selectProvinceCode(String provinceName){
		StringBuffer provinceCode = new StringBuffer();
		String sql = "select province_code,province_remark from t_province_code where province_name =?";
		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, provinceName);
			rs = ps.executeQuery();
			System.out.println(ps.toString());
			int i = 0;
			while(rs.next()){
				i++;
				provinceCode.append(rs.getString(1));
				provinceCode.append("#");
				provinceCode.append(rs.getString(2));
			}
			if(i==0){
				return null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			close();
		}
		return provinceCode.toString();
	}
	
	public static String selectCode2(String countryCn){
		String code2 = null;
		if("保留地址".equals(countryCn)){
			code2 = "IA";
		}else{
			String sql = "select c2code from t_code_country where country_cn = ?";
			try {
				conn = getConnection();
				ps = conn.prepareStatement(sql);
				ps.setString(1, countryCn);
				rs = ps.executeQuery();
				System.out.println(ps.toString());
				while(rs.next()){
					code2 = rs.getString(1);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				close();
			}
		}
		return code2;
	}
	
	public static Connection getConnection(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(HOST, USER, PWD);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	public static void close(){
		try {
			rs.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
