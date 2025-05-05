package com.crm;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrmApplication.class, args);

		int[] original = { 23, 8, 63, 6, 9, 67, 55 };
		System.out.println("Original Array: " + Arrays.toString(original));
		System.out.println();

		runBubbleSort(Arrays.copyOf(original, original.length));
		runSelectionSort(Arrays.copyOf(original, original.length));
		runInsertionSort(Arrays.copyOf(original, original.length));
		runMergeSort(Arrays.copyOf(original, original.length));
		runQuickSort(Arrays.copyOf(original, original.length));
		runHeapSort(Arrays.copyOf(original, original.length));
	}

	// 1. Bubble Sort
	private static void runBubbleSort(int[] arr) {
		long start = System.nanoTime();

		int temp, flag;
		for (int i = 0; i < arr.length; i++) {
			flag = 0;
			for (int j = 0; j < arr.length - 1 - i; j++) {
				if (arr[j] > arr[j + 1]) {
					temp = arr[j];
					arr[j] = arr[j + 1];
					arr[j + 1] = temp;
					flag = 1;
				}
			}
			if (flag == 0)
				break;
		}

		long end = System.nanoTime();
		System.out.println("Bubble Sort Result:  " + Arrays.toString(arr));
		System.out.println("Time taken: " + (end - start) + " ns\n");
	}

	// 2. Selection Sort
	private static void runSelectionSort(int[] arr) {
		long start = System.nanoTime();

		for (int i = 0; i < arr.length - 1; i++) {
			int min = i;
			for (int j = i + 1; j < arr.length; j++) {
				if (arr[j] < arr[min]) {
					min = j;
				}
			}
			int temp = arr[min];
			arr[min] = arr[i];
			arr[i] = temp;
		}

		long end = System.nanoTime();
		System.out.println("Selection Sort Result: " + Arrays.toString(arr));
		System.out.println("Time taken: " + (end - start) + " ns\n");
	}

	// 3. Insertion Sort
	private static void runInsertionSort(int[] array) {
		long start = System.nanoTime();

		for (int i = 1; i < array.length; i++) {
			int key = array[i];
			int j = i - 1;
			while (j >= 0 && array[j] > key) {
				array[j + 1] = array[j];
				j--;
			}
			array[j + 1] = key;
		}

		long end = System.nanoTime();
		System.out.println("Insertion Sort Result: " + Arrays.toString(array));
		System.out.println("Time taken: " + (end - start) + " ns\n");
	}

	// 4. Merge Sort
	private static void runMergeSort(int[] array) {
		long start = System.nanoTime();

		mergeSort(array);

		long end = System.nanoTime();
		System.out.println("Merge Sort Result:    " + Arrays.toString(array));
		System.out.println("Time taken: " + (end - start) + " ns\n");
	}

	private static void mergeSort(int[] array) {
		if (array.length <= 1)
			return;

		int mid = array.length / 2;
		int[] left = Arrays.copyOfRange(array, 0, mid);
		int[] right = Arrays.copyOfRange(array, mid, array.length);

		mergeSort(left);
		mergeSort(right);
		merge(array, left, right);
	}

	private static void merge(int[] array, int[] left, int[] right) {
		int i = 0, j = 0, k = 0;
		while (i < left.length && j < right.length) {
			array[k++] = (left[i] < right[j]) ? left[i++] : right[j++];
		}
		while (i < left.length)
			array[k++] = left[i++];
		while (j < right.length)
			array[k++] = right[j++];
	}

	private static void runQuickSort(int[] array) {
		long start = System.nanoTime();

		quickSort(array, 0, array.length - 1);

		long end = System.nanoTime();
		System.out.println("Quick Sort Result:    " + Arrays.toString(array));
		System.out.println("Time taken: " + (end - start) + " ns\n");
	}

	private static void quickSort(int[] array, int low, int high) {
		if (low < high) {
			int pivotIndex = partition(array, low, high);
			quickSort(array, low, pivotIndex - 1);
			quickSort(array, pivotIndex + 1, high);
		}
	}

	private static int partition(int[] array, int low, int high) {
		int pivot = array[high];
		int i = low - 1;

		for (int j = low; j < high; j++) {
			if (array[j] < pivot) {
				i++;
				int temp = array[i];
				array[i] = array[j];
				array[j] = temp;
			}
		}

		int temp = array[i + 1];
		array[i + 1] = array[high];
		array[high] = temp;

		return i + 1;
	}

	// 6. Heap Sort
	private static void runHeapSort(int[] arr) {
		long start = System.nanoTime();

		int n = arr.length;

		for (int i = n / 2 - 1; i >= 0; i--)
			heapify(arr, n, i);

		for (int i = n - 1; i > 0; i--) {
			int temp = arr[0];
			arr[0] = arr[i];
			arr[i] = temp;

			heapify(arr, i, 0);
		}

		long end = System.nanoTime();
		System.out.println("Heap Sort Result:      " + Arrays.toString(arr));
		System.out.println("Time taken: " + (end - start) + " ns\n");
	}

	private static void heapify(int[] arr, int n, int i) {
		int largest = i;
		int left = 2 * i + 1;
		int right = 2 * i + 2;

		if (left < n && arr[left] > arr[largest])
			largest = left;

		if (right < n && arr[right] > arr[largest])
			largest = right;

		if (largest != i) {
			int swap = arr[i];
			arr[i] = arr[largest];
			arr[largest] = swap;

			heapify(arr, n, largest);
		}
	}

}
