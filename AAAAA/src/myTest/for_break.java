package myTest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class for_break {

	
	public static void main(String[] args) throws ParseException {
		/*int i = 0;
		for(i = 0; i<5; i++){
			System.out.println(i);
		}*/
		
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date d = df.parse("20190215");
        int time = (int) (d.getTime()/1000);
		System.out.println(time);
		Date da = new Date();
		da.getDate();
	}
}

