package myTest;

public class FinallyTest {

	public static void main(String[] args) {
		System.out.println(getValue());
	}
	
	@SuppressWarnings(value = { "finally" })
	private static int getValue(){
		try{
			return 0;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return 1;
		}
		
	}
}
