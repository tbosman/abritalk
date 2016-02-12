import java.util.Arrays;

import grph.Grph;
import toools.set.DefaultIntSet;
import toools.set.IntSet;
import toools.set.IntSets;

public class AtomTools {

	
	public static IntSet atomHeads(IntSet X, Grph g){
		IntSet atomHeads =new DefaultIntSet();
		int[] xArr = X.toIntArray();
		Arrays.sort(xArr);
		for(int u : xArr){			
			boolean isAtomHead = true; 
			for(int v: xArr){
				if(u <= v){
					continue;
				}
				
				if(IntSets.difference(g.getNeighbours(u),X).equals(IntSets.difference(g.getNeighbours(v),X))) {
					isAtomHead = false;
					break;
				}				
			}
			if(isAtomHead) {
				atomHeads.add(u);
			}
		}
		return atomHeads;
	}
}
