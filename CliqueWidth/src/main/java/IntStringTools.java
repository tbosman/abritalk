import toools.set.DefaultIntSet;
import toools.set.IntSet;

public class IntStringTools {

	public static long intsetToLong(IntSet X){
		if(X.size()>60){
			throw new Error("Set is to big"); 
		}
		long string = 0L; 
		for(int i : X.toIntArray()){
			string = string | 1L << i;
		}
		return string; 
	}

	public static int intsetToInt(IntSet X){
		if(X.size()>60){
			throw new Error("Set is to big"); 
		}
		int string = 0; 
		for(int i : X.toIntArray()){
			string = string | 1 << i;
		}
		return string; 
	}

	public static IntSet intToIntSet(int x){
	
		IntSet set = new DefaultIntSet();
		for (int i = 31; i >= 0; i--) {
			if((x & (1L << i)) != 0){
				set.add(i);
			}
		}
		return set;
	}
	
	public static int nextBitPermutation(int v){
		if(v == 0){
			return 1;
		}
		int w;
		int t = (v | (v - 1)) + 1;  
		w = t | ((((t & -t) / (v & -v)) >> 1) - 1);  
		return w;
	}

}
