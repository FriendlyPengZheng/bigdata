package treadPac;

public class ThreadTest extends Thread{

	private int count;
	private String name;
	private static int num = 5;
	
	
	public ThreadTest(int count, String name) {
		this.count = count;
		this.name = name;
		System.out.println("creating thread "+count+name);
	}

	@Override
	public void run() {
		while(true){
			System.out.println("Thread " +
					count + "(" + name+ ")");
			if(--num==0){
				break;
			}
		}
	}
	
}
