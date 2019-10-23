package myTest;

public class InnternTest {

	public static void main(String[] args) {

		String str1 = new String("1") + new String ("2");
		//str1.intern();
		String str2 = "12";
		int i = 0;
		String str3 = new String("12");
		String str4 = "12";
		String str5 = "1" + "2";
		System.out.println(str4 == str5);
		System.out.println(str2 == str4);
		System.out.println(str1 == str2);
		System.out.println(str1 == str3);
		InnternTest innternTest = new InnternTest();
		System.out.println(innternTest.changeValue(str4,i));
		System.out.println(i);
		System.out.println(str4);
	}

	String changeValue(String string,int i){
		string = "firendly";
		i = 4;
		//string = new String("peng");
		return string;
	}
}
