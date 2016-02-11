import java.util.Arrays;
import java.util.Iterator;

import org.graphstream.algorithm.generator.PetersenGraphGenerator;

import grph.Grph;
import grph.algo.topology.PetersonGraph;
import toools.set.DefaultIntSet;
import toools.set.IntSet;
import toools.set.IntSets;

public class AtomicCore {

	/**
	 * Iterator over subsets of {1,...,n}
	 */
	private class powerSetIterator implements Iterator<IntSet>{
		final int maxIdx;
		int cString = 0; 
		powerSetIterator(int n){
			maxIdx = n-1; 
		}
		public boolean hasNext() {
			return cString < 1L << maxIdx+1;
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
			return longToIntSet(cString++);			
		}
		
	}
	public IntSet getAtomicCore(IntSet X, Grph g){
		IntSet Xcopy = X.clone();
		int[] XArr = Xcopy.toIntArray(); 
		Arrays.sort(XArr);
		IntSet atomicCore = new DefaultIntSet(); 
		for(int u : XArr){
			for(int v: Xcopy.toIntArray()){
				if(v <= u){
					continue;
				}
				if(IntSets.difference(g.getNeighbours(u),Xcopy).equals(IntSets.difference(g.getNeighbours(v),Xcopy))) {
					atomicCore.addAll(u,v);
					atomicCore.addAll(IntSets.difference(Xcopy, IntSets.intersection(g.getNeighbours(u), g.getNeighbours(v))));
					Xcopy.remove(v);
					break;
				}			
				
			}
		}
		System.out.println("X: "+X);
		System.out.println("Core: "+atomicCore);
		return atomicCore;		
	}
	
	public boolean isAtomicCore(IntSet X, Grph g){
		return getAtomicCore(X,g).equals(X);
	}
	
	
	public Iterator<IntSet> powerSetIterator(int n){
		return new powerSetIterator(n);
	}
	
	public void start(Grph g){
		long numAC = 0;
		long numTot = 0; 
		int n = g.getVertices().size();
		powerSetIterator it = new powerSetIterator(n);
		while(it.hasNext()){
			numTot++;
			if(isAtomicCore(it.next(), g)){
				numAC++;
			}
		}
		
		System.out.println("NumAC: "+numAC); 
		System.out.println("Out of: "+Math.pow(2,n));
		System.out.println("check: "+numTot);
	}
	public static void main(String... args){
		
		
		Grph g = new PetersonGraph().petersenGraph(5, 2);
		g = new ChvatalGenerator().chvatalGenerator();
		g = new Paley13Generator().paley13Generator();
		g = new MCGeeGenerator().run();

		new AtomicCore().start(g);
		
		

		
		System.out.println(""+(1L << 2));
		g.display();
	}
	
}
