package myTest;

public class ReturnTest {

	
	public void fun(int a){
		if(a ==1){
			//业务处理
			System.out.println("部分1");
		}else{
			return;
		}
		//业务处理
		System.out.println("部分2");
	}
	
	public static void main(String[] args) {
		ReturnTest returnTest = new ReturnTest();
		returnTest.fun(2);
	}
}
