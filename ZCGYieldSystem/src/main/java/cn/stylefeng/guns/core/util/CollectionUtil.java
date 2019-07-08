package cn.stylefeng.guns.core.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CollectionUtil {

	/**
	 * int数组转字符串数组
	 * @param as
	 * @return
	 */
	static public String[] switchArray(Integer[] as) {
		if(as == null) {
			return new String[] {};
		}
		String[] result = new String[as.length];
		for(int i = 0 ,len = as.length ;i < len ;++i) {
			result[i] = String.valueOf(as[i]);
		}
		return result;
	}
	
	/**
	 * 	数组中包含指定元素
	 * @param ele 元素
	 * @param arrays 数组
	 * @return
	 */
	static public boolean contains(String[] arrays ,String ele) {
		if(Objects.isNull(arrays) || arrays.length == 0) {
			return false;
		}
		for(int i = 0 ,len = arrays.length ;i < len ;++i) {
			if((ele == null && arrays[i] == null) || (ele != null && ele.equals(arrays[i]))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 查找集合的交集
	 * @param a
	 * @param b
	 * @return
	 */
	static public int[] intersection(int[] a ,int[] b) {
		if(Objects.nonNull(a) && Objects.nonNull(b)) {
			return a.length > b.length ? intersection0(b, a)
					: intersection0(a, b);
		}
		
		return new int[] {};
	}
	
	static private int[] intersection0(int[] minArray ,int[] maxArray) {
		int[] temp = new int[minArray.length];
		int count = 0;
		
		for(int i = 0 ,minLen = minArray.length ;i < minLen ;++i) {
			int currentVal = minArray[i];
			if(!contains(currentVal, temp ,0 ,count)) {
				if(contains(currentVal ,maxArray ,0 ,maxArray.length)) {
					temp[count++] = currentVal;
				}
			}
		}
		
		int[] result0 = new int[count];
		System.arraycopy(temp, 0, result0, 0, count);
		
		return result0;
	}
	
	/**
	 * 使用二分算法查询集合的交集
	 * @param a
	 * @param b
	 * @return
	 */
	static public int[] intersectionByBinarySearch(int[] a ,int[] b) {
		if(Objects.nonNull(a) && Objects.nonNull(b)) {
			return a.length > b.length ? intersectionByBinarySearch0(b, a)
					: intersectionByBinarySearch0(a, b);
		}
		
		return new int[] {};
	}
	
	static private int[] intersectionByBinarySearch0(int[] minLengthArray ,int[] maxLengthArray) {
		Set<Integer> tempResult = new HashSet<Integer>();
		
		Arrays.sort(minLengthArray);
		Arrays.sort(maxLengthArray);
		
		for(int i = 0 ,len = minLengthArray.length ;i < len ;++i) {
			int currentVale = minLengthArray[i];
			
			if(Arrays.binarySearch(maxLengthArray, currentVale) >= 0) {
				tempResult.add(Integer.valueOf(currentVale));
			}
		}
		
		return setToArray(tempResult);
	}
	
	/**
	 * 多线程的方式，应该有调用者实现多线程
	 * @param a
	 * @param b
	 * @return
	 */
	static public int[] intersectionByMultithread(int[] a ,int[] b) {
		if(Objects.nonNull(a) && Objects.nonNull(b)) {
			return a.length > b.length ? intersectionByMultithread0(b, a)
					: intersectionByMultithread0(a, b);
		}
		
		return new int[] {};
	}
	
	static public int[] intersectionByMultithread0(int[] minLengthArray ,int[] maxLengthArray) {
		int threadNumber = Runtime.getRuntime().availableProcessors();
		int maxLength = maxLengthArray.length;
		int sliceArrayBaseLength = maxLength / threadNumber;
		
		if(sliceArrayBaseLength <= 1) {return intersectionByBinarySearch0(minLengthArray, maxLengthArray);}
		
		ConcurrentLinkedQueue<Object> tempResult = new ConcurrentLinkedQueue<>();
		
		for(int i = 0 ;i < threadNumber ;++i) {
			int sliceArrayStartIndex = sliceArrayBaseLength * i;
			int sliceArrayEndIndex = 
					sliceArrayBaseLength + sliceArrayBaseLength + sliceArrayStartIndex > maxLength ?
							maxLength : sliceArrayBaseLength + sliceArrayStartIndex;
			
			int currentSliceArrayLength = sliceArrayEndIndex - sliceArrayStartIndex;
					
			int[] sliceArray = new int[currentSliceArrayLength];
			System.arraycopy(maxLengthArray, sliceArrayStartIndex, sliceArray, 0, currentSliceArrayLength);
			
			new Thread(()->{
				tempResult.add(intersectionByBinarySearch(minLengthArray, sliceArray));
			}).start();
		}
		
		while(tempResult.size() != threadNumber) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		
		int[] result = new int[0];
		while(!tempResult.isEmpty()) {
			result = combineAndRemoveRepeating(result, (int[])tempResult.poll());
		}
		
		return result;
	}
	
	/**
	 * 查询array中是否包含val值
	 * @param val
	 * @param array
	 * @param offset
	 * @param len
	 * @return
	 */
	static public boolean contains(int val ,int[] array ,int offset,int len) {
		checkNull(array);
		checkIndex(offset, len, array.length);
		
		int i = offset;
		for(;i < len ;++i) {
			if(val == array[i]) {
				return true;
			}
		}
		
		return false;
	}
	
	static private void checkNull(Object ...obj) {
		if(Objects.isNull(obj)) {
			throw new NullPointerException("array is null");
		}
		
		for(int i = 0 ,len = obj.length ;i < len ;++i) {
			if(Objects.isNull(obj[i])) {
				throw new NullPointerException("array is null");
			}
		}
	}
	
	static private void checkIndex(int offset ,int srcLen ,int arrayLen) {
		if(offset < 0 || srcLen < offset || srcLen > arrayLen) {
			throw new RuntimeException("index is error ,offset = " + offset + " ;len = " + srcLen);
		}
	}

	/**
	 *  将数组a和数组b组合为新的数组
	 * @param a
	 * @param b
	 * @return
	 */
	public static int[] combine(int[] a, int[] b) {
		checkNull(a,b);
		
		int[] result = new int[a.length + b.length];
		
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		
		return result;
	}
	
	/**
	 * 查询数组a 和 数组b的非交集
	 * @param a
	 * @param b
	 * @return
	 */
	static public int[] nonredundant(int[] a, int[] b) {
		checkNull(a,b);
		
		return a.length > b.length ? nonredundant0(b, a) : nonredundant0(a, b);
	}
	
	static private int[] nonredundant0(int[] minLengthArray, int[] maxLengthArray) {
		Set<Integer> tempResult = new HashSet<>();
		
		Arrays.sort(minLengthArray);
		Arrays.sort(maxLengthArray);
		
		for(int i = 0 ,len = maxLengthArray.length ;i < len ;++i) {
			if(Arrays.binarySearch(minLengthArray, maxLengthArray[i]) < 0) {
				tempResult.add(maxLengthArray[i]);
			}
		}
		
		for(int i = 0 ,len = minLengthArray.length ;i < len ;++i) {
			if(Arrays.binarySearch(maxLengthArray, minLengthArray[i]) < 0) {
				tempResult.add(minLengthArray[i]);
			}
		}
		
		return setToArray(tempResult);
	}
	
	/**
	 * 合并数组并去掉重复的数据
	 * @param a
	 * @param b
	 * @return
	 */
	static public int[] combineAndRemoveRepeating(int[] a ,int[] b) {
		checkNull(a,b);
		
		Set<Integer> resultSet = new HashSet<>();
		
		copyToSet(a, resultSet);
		copyToSet(b, resultSet);
		
		return setToArray(resultSet);
	}
	
	static private void copyToSet(int[] src ,Set<Integer> dest) {
		for(int i = 0 ,len = src.length ;i < len ;++i) {
			dest.add(Integer.valueOf(src[i]));
		}
	}
	
	static private int[] setToArray(Set<Integer> src) {
		
		if(Objects.nonNull(src) && !src.isEmpty()) {
			int[] result = new int[src.size()];
			int writeIndex = 0;
			
			Iterator<Integer> iter = src.iterator();
			while(iter.hasNext()) {
				result[writeIndex++] = iter.next().intValue();
			}
			
			return result;
		}
		
		return new int[] {};
	}
	
	static public<T> boolean constains(List<T> list ,T t) {
		if(list == null || list.isEmpty()) {
			return false;
		}
		for(int i = 0 ,len = list.size() ;i < len ;++i) {
			T tmp = list.get(i);
			if(tmp == t || (tmp != null && tmp.equals(t))) {
				return true;
			}
		}
		return false;
	} 
}
