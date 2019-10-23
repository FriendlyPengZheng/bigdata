package myTest;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TreeMa {

	public static void main(String[] args) {
		int a = 1;
		int b = 1;
		System.out.println("李磊".hashCode());
		// TODO Auto-generated method stub
		TreeMap<Integer, String> set = new TreeMap<Integer, String>();
		set.put(4, "a");
		set.put(2,"c");
		set.put(3, "g");
		set.put(3, "c");
		Iterator<Integer> it = set.keySet().iterator();
		while(it.hasNext()){
			System.out.println(set.get(it.next()));
			//TreeMap键有序 不可重复 ，后来者覆盖前者值
		}
		ConcurrentMap<Integer, String> concurrentMap = new ConcurrentHashMap<Integer, String>(); 
	}

}
