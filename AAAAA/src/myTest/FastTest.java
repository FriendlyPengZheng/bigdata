package myTest;

import org.junit.Test;

public class FastTest {
	
	public void fsort(int[] arr, int left, int right){
		
		if(left >= right){
			return;
		}
		int x = arr[left];
	
		int i = left;
		int j = right;
		
		while(i<j){

			//将小于基准书的数移到左边
			while(arr[j]>=x && j>i){
				j--;
			}
			
			//将大于基准数的数移到右边
			while(arr[i]<=x && i<j){
				i++;
			}
			
			//i和j指向的数 交换位置
			if(i<j){
				int tmp = arr[j];
				arr[j] = arr[i];
				arr[i] = tmp;
			}	
		}
		
		//将基准数 和
		arr[left] = arr[i];
		arr[i] = x;
		
		fsort(arr, left, i-1);
		fsort(arr, i+1, right);

		for(int m:arr){
			System.out.print(m+"\t");;
		}
		System.out.println();
	}
	@Test
	public void test(){
		int[] a = new int[]{32,21,17,20,62,34,12,49,24,54,33};
		fsort(a, 0, a.length-1);
	}
}
