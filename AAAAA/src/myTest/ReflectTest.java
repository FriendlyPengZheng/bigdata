package myTest;

import java.lang.reflect.Method;

public class ReflectTest {

	public static void main(String[] args) {
		
		String str = "aa";
		Class<? extends String> strClass= str.getClass();
		try {
			Method method = strClass.getDeclaredMethod("substring");
			method.setAccessible(true);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
