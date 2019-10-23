package treadPac;

public class RunableTest implements Runnable{

	private Thread t;
	private String threadName;
	
	
	public RunableTest() {
		
	}

	public RunableTest(String threadName) {
		this.threadName = threadName;
		System.out.println("creating..." + threadName);
	}


	@Override
	public void run() {
		System.out.println("running..." + threadName);
		for(int i = 1; i <= 30; i++){
			System.out.println("Thread: " + threadName + ", " + i);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Thread .." +  threadName + " exiting.");
	}

	public void start () {
		System.out.println("Starting ..." +  threadName );
		if (t == null) {
			t = new Thread (this, threadName);
			t.start ();
		}
	}
}
