package myTest;

import java.util.Arrays;

import org.junit.Test;

public class mergeTest {

	private int[] arrB;
	public void merge(int[] arrA ,int low, int mid, int high){
		
		arrB = arrA.clone();
		int i =0, j=0, k=low;
		for(i = low, j = mid; i <= mid-1 && j<=high; k++){
			if(arrB[i] <= arrB[j]){
				arrA[k] = arrB[i++];
			}else{
				arrA[k] = arrB[j++];
			}
		}
		
		while(i < mid){
			arrA[k++] = arrB[i++];
		}
		
		while(j < high){
			arrA[k++] = arrB[j++];
		}
	}
	
	public void mergeSort(int[] arrA, int low, int high){
		if(low < high){
			int mid = (low+high)/2;
			mergeSort(arrA, low, mid);
			mergeSort(arrA, mid+1, high);
			merge(arrA, low, mid+1, high);
			System.out.println(Arrays.toString(arrA));
		}
	}
	@Test
	public void testSort() {
		int [] arrA = {100, 90, 80, 76, 86, 53, 47,33};
		arrB = new int[arrA.length];
		mergeSort(arrA, 0, arrA.length-1);
		System.out.println(Arrays.toString(arrA));
	}

}
