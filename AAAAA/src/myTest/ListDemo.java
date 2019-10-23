package myTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListDemo {

	private List<Integer> nums = new ArrayList<Integer>(Arrays.asList(0,0,1,2,3,4,0,5,8,0,0));
	
	public void numQuest(){
		int k = 0;
		Integer z = new Integer(0);
		while(k < nums.size()){
			if(nums.get(k).equals(z))
				nums.remove(k);
			k++;
		}
		System.out.println(nums);
	}
	
	public static void main(String[] args) {
		new ListDemo().numQuest();
	}
}
