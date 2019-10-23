package myTest;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.ReentrantLock;

public class CallableTest implements Callable<Integer>{

	static int sum = 0;
	StringBuilder sb = new StringBuilder();
	Map<String, String> map = new ConcurrentHashMap<String, String>();
	/*private static */ReentrantLock lock = new ReentrantLock();
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		CallableTest callableTest = new CallableTest();
		FutureTask<Integer> task = new FutureTask<Integer>(callableTest);
		FutureTask<Integer> task2 = new FutureTask<Integer>(callableTest);
		FutureTask<Integer> task3 = new FutureTask<Integer>(callableTest);
		new Thread(task).start();/*
		new Thread(task2).start();
		new Thread(task3).start();*/
	}

	@Override
	public Integer call() throws Exception {
		// TODO Auto-generated method stub
		lock.lock();
		try{
			for(int i =0; i <= 50; i++){
				sum += i;
				Thread.sleep(10);
				System.out.println(Thread.currentThread().getName()+"遍历"+i+".....sum="+sum);
			}
		}finally{
			lock.unlock();
		}
		return sum;
	}

}
