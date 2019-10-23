package trans;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class ReadFIle {
	private static String span = null;
	private static String start = null;
	private static String end = null;
	private static String country = null;
	private static String province = null;
	private static String city = null;
	private static String isp = null;
	private static String startIp = null;
	private static String endIp = null;
	private static String flagCountry = null;
	
	public static void readFile(){
		File file = new File("D:\\city_ip");
		InputStream in = null;
		InputStreamReader inReader = null;
		BufferedReader bufferedReader = null;
		try {
			in = new FileInputStream(file);
			inReader = new InputStreamReader(in);
			bufferedReader = new BufferedReader(inReader);
			String line = null;
			int i = 0;
			int j = 0;
			Map<String, String> map= new HashMap<String, String>();
			StringBuffer sbCN = new StringBuffer();
			StringBuffer sbCoutry = new StringBuffer();
			while((line = bufferedReader.readLine()) != null){
				String[] items = line.split("#");
				span = items[0];
				start = items[1];
				end = items[2];
				country = items[3];
				province = items[4];
				city = items[5];
				isp = items[6];
				startIp = items[7];
				endIp = items[8];
				String start1 = null;
				String end1 = null;
				String startIp1 = null;
				String endIp1 = null;
				if(map.isEmpty()){
					String valueString = start+"#"+end+"#"+startIp+"#"+endIp;
					map.put(country, valueString);
					flagCountry = country;
				}else{
					if(map.containsKey(country)){
						//获取出value同当前IP区间拼接
						String[] values = map.get(country).split("#");
						start1 = values[0];
						end1 = values[1];
						startIp1 = values[2];
						endIp1 = values[3];
						if((Long.parseLong(end1)+1L) == Long.parseLong(start)){
							String valueString = start1+"#"+end+"#"+startIp1+"#"+endIp;
							map.put(country, valueString);
						}else{
							new Throwable("ip is not continuous");
						}
						flagCountry = country;
					}else{
						i++;
						//输出map，同时清空map，然后将当前内容写入map
						String[] values = null;
						if(map.get(flagCountry)==null){
							System.out.println(flagCountry);
						}
							values = map.get(flagCountry).split("#");
							sbCoutry.append("("+i+",");
							sbCoutry.append(values[0]+",");
							sbCoutry.append(values[1]+",");
							String code2 = DbUtil.selectCode2(flagCountry);
							sbCoutry.append("'"+code2+"'"+",");
							sbCoutry.append("'"+values[2]+"'"+",");
							sbCoutry.append("'"+values[3]+"'"+"),");
							if(i%8000==0){
								sbCoutry.append("\r\n");
								sbCoutry.append("INSERT INTO t_country_ip VALUES ");
							}
							map.clear();
							String valueString = start+"#"+end+"#"+startIp+"#"+endIp;
							map.put(country,valueString);
							flagCountry = country;
					}
				}
				//格式化t_city_ip
				if("中国".equals(country)){
					j++;
					String provinceCodeAndRemark = DbUtil.selectProvinceCode(province);
					if(provinceCodeAndRemark != null){
						sbCN.append("("+j+",");
						sbCN.append(span+",");
						sbCN.append(start+",");
						sbCN.append(end+",");
						String[] provinceCodeAndRemarks = provinceCodeAndRemark.split("#");
						String remark = provinceCodeAndRemarks[1];
						if("直辖市".equals(remark)){
							remark = "市";
						}
						if("自治区".equals(remark)){
							remark = "";
						}
						sbCN.append("'"+province+remark+"'"+",");
						String provinceCode = provinceCodeAndRemarks[0];
						sbCN.append("'"+provinceCode+"'"+",");
						
						String cityName = null;
						String cityCode = null;
						String cityCodeAndName = DbUtil.selectCityCode(province, city);
						if(cityCodeAndName==null){
							cityName = "";
							cityCode = "";
						}else{
							String[] cityCodeAndNames = DbUtil.selectCityCode(province, city).split("#");
							cityName = cityCodeAndNames[1];
							cityCode = cityCodeAndNames[0];
						}
						sbCN.append("'"+cityName+"'"+",");
						sbCN.append("'"+cityCode+"'"+",");
						
						sbCN.append("'"+isp+"'"+",");
						sbCN.append("'"+startIp+"'"+",");
						sbCN.append("'"+endIp+"'"+"),");
						if(j%8000==0){
							sbCN.append("\r\n");
							sbCN.append("INSERT INTO t_city_ip VALUES ");
						}
					}
					//System.out.println(sb.toString());
				}
			}
			writeFile(sbCoutry.toString(),new File("D:\\sqlCountry"));
			writeFile(sbCN.toString(),new File("D:\\sqlCN"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bufferedReader.close();
				inReader.close();
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void writeFile(String sqlString,File file){
		//File file = new File("D:\\sqlString");
		OutputStream out = null;
		OutputStreamWriter oStreamWriter = null;
		BufferedWriter writer = null;
		try {
			out = new FileOutputStream(file,true);
			oStreamWriter = new OutputStreamWriter(out);
			writer = new BufferedWriter(oStreamWriter);
			writer.write(sqlString);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				writer.close();
				oStreamWriter.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
