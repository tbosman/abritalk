import java.util.Iterator;

import toools.set.DefaultIntSet;
import toools.set.IntSet;

/**
 * Iterator over subsets of {1,...,n}
 */
public class PowerSetIterator implements Iterator<IntSet>{
	/**
	 * 
	 */
	final int maxIdx;
	int cString = 0; 
	boolean done = false; 
	PowerSetIterator(int n){
		maxIdx = n-1; 
	}
	public boolean hasNext() {
		return !done;
		/*System.out.println((byte)cString);
		System.out.println(1L << maxIdx+1);
		return cString < 1L << maxIdx+1;*/
	}

	private IntSet longToIntSet(long x){

		IntSet set = new DefaultIntSet();
		for (int i = maxIdx; i >= 0; i--) {
			if((x & (1L << i)) != 0){
				set.add(i);
			}
		}
		return set;
	}
	public IntSet next() {
		IntSet set = longToIntSet(cString);
		cString = IntStringTools.nextBitPermutation(cString);
		if(cString >= 1L << (maxIdx+1)){
			if(Integer.bitCount(cString)<= maxIdx){
				cString = (1 << (Integer.bitCount(cString) + 1)) -1; //Gives a bitstring with last bitcount(cString)+1 bits set to one
			}else{
				this.done = true;
			}
		}else{
			
		}
		return set;
//		return longToIntSet(cString++);			
	}
	public void remove() {
		// TODO Auto-generated method stub

	}
	
	/** Sets this iterator so iteration will start from first set of cardinality k
	 * 
	 * @param k
	 */
	public void resetToCardinality(int k){
		int nextSetString = (1 << k)-1;
	}
	
	public static void main(String... args){
		PowerSetIterator it = new PowerSetIterator(4); 
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}

}