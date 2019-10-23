package myTest;

public class paraList {

	static void test(String...strings){
		System.out.println(strings[0]);
	}
	public static void main(String[] args) {
		String list = "a;b;c;d;e;f";
		String[] str = list.split(";");
		test(str);
	}

}
