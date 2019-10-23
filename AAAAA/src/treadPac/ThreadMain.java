package treadPac;

public class ThreadMain {

	
	  
	public static void main(String[] args) {
		/*RunableTest r1 = new RunableTest("t1");
		RunableTest r2 = new RunableTest("t2");
		r1.start();
		r2.start();*/
		for (int i = 0; i < 10; i++) {
			new ThreadTest(i,"t"+i).start();
			System.out.println("starting");
		}
	}

}
