import java.util.Arrays;
import java.util.Iterator;

import org.graphstream.algorithm.generator.PetersenGraphGenerator;

import grph.Grph;
import grph.algo.topology.ErdosRenyiRandomizedScheme;
import grph.algo.topology.GridTopologyGenerator;
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
		public void remove() {
			// TODO Auto-generated method stub
			
		}

	}
	public IntSet getAtomicCore(IntSet X, Grph g){
		IntSet Xcopy = X.clone();
		int[] XArr = Xcopy.toIntArray(); 
		Arrays.sort(XArr);
		IntSet atomicCore = new DefaultIntSet(); 
		for(int u : XArr){
			int[] XcopyArr = Xcopy.toIntArray();
			Arrays.sort(XcopyArr);
			for(int v: XcopyArr){
				if(v <= u){
					continue;
				}
				if(IntSets.difference(g.getNeighbours(u),X).equals(IntSets.difference(g.getNeighbours(v),X))) {
					atomicCore.addAll(u,v);
					atomicCore.addAll(IntSets.difference(g.getNeighbours(u), g.getNeighbours(v)));
					atomicCore.addAll(IntSets.difference(g.getNeighbours(v), g.getNeighbours(u)));
					Xcopy.remove(v);
					break;
				}			

			}
		}
//		System.out.println("X: "+X);
//		System.out.println("Core: "+atomicCore);
		return atomicCore;		
	}

	public boolean isAtomicCore(IntSet X, Grph g){
		return getAtomicCore(X,g).equals(X);
	}


	public Iterator<IntSet> powerSetIterator(int n){
		return new powerSetIterator(n);
	}

	public int nCr(int n, int r){
		r = Math.max(r, n-r);
		int result = 1;
		for(int i=n; i>r; i--){
			result *= i;			
		}
		for(int i=1; i<=n-r; i++){
			result /= i; 
		}
		return result;
	}

	public long intsetToLong(IntSet X){
		if(X.size()>60){
			throw new Error("Set is to big"); 
		}
		long string = 0L; 
		for(int i : X.toIntArray()){
			string = string | 1L << i;
		}
		return string; 
	}

	public int intsetToInt(IntSet X){
		if(X.size()>60){
			throw new Error("Set is to big"); 
		}
		int string = 0; 
		for(int i : X.toIntArray()){
			string = string | 1 << i;
		}
		return string; 
	}

	private IntSet intToIntSet(int x){

		IntSet set = new DefaultIntSet();
		for (int i = 31; i >= 0; i--) {
			if((x & (1L << i)) != 0){
				set.add(i);
			}
		}
		return set;
	}

	public IntSet merge(IntSet X, int u, int v, Grph g){
		IntSet xNew = new DefaultIntSet(); 
		xNew.addAll(X);
		xNew.addAll(IntSets.difference(g.getNeighbours(u), g.getNeighbours(v)));
		xNew.addAll(IntSets.difference(g.getNeighbours(v), g.getNeighbours(u)));
		xNew.addAll(u,v);
		return xNew;

	}

	public void calcAtomicCore(Grph g){
		IntSet setStringSet = new DefaultIntSet(); 
		IntSet newStringSet = new DefaultIntSet();

		//Init
		int[] vArr = g.getVertices().toIntArray();
		Arrays.sort(vArr);
		for(int u : vArr){
			for(int v : vArr){
				if(u<= v){
					continue; 
				}
				IntSet X = IntSets.difference(g.getNeighbours(u), g.getNeighbours(v));
				X.addAll(IntSets.difference(g.getNeighbours(v), g.getNeighbours(u)));
				X.addAll(u,v);
				if(isAtomicCore(X, g)){
					newStringSet.add(intsetToInt(X));
				}
				System.out.println("X: "+X+"-"+intToIntSet(intsetToInt(X))) ;

			}
		}


		while(!newStringSet.isEmpty()){
			int string = newStringSet.toIntArray()[0];
			newStringSet.remove(string);
			setStringSet.add(string);
			IntSet X = intToIntSet(string);
//			System.out.println("routine 2, X: "+X);//#DBG
			IntSet atoms = AtomTools.atomHeads(X,g);
			int[] mergeCandidates = IntSets.union(atoms, IntSets.difference(g.getVertices(), X)).toIntArray() ;
			Arrays.sort(mergeCandidates);
			for(int u : mergeCandidates){
				for(int v: mergeCandidates){
					if(u<=v){
						continue;
					}
					IntSet newX = merge(X,u,v, g);
					if(!setStringSet.contains(intsetToInt(newX))){
						
						newStringSet.add(intsetToInt(newX));
					}	
					
//					if(isAtomicCore(newX, g)){
//					
//					}else{
//						throw new Error("this shouldnt happen");
//					}
				}
			}
//			System.out.println("Size: "+setStringSet.size());

		}
		
		System.out.println("Size: "+setStringSet.size());
		
	}





	public int start(Grph g){
		int[] acPerSize = new int[g.getVertices().size()+1];
		long numAC = 0;
		long numTot = 0; 
		int n = g.getVertices().size();
		powerSetIterator it = new powerSetIterator(n);
		while(it.hasNext()){
			numTot++;
			IntSet X = it.next();
			if(isAtomicCore(X, g)){
				acPerSize[X.size()] += 1; 
				numAC++;
//				System.out.println("routine 1, X:"+X);//#DBG
			}
		}

		System.out.println("NumAC: "+numAC); 
		System.out.println("Out of: "+Math.pow(2,n));
		System.out.println("check: "+numTot);
		for(int i=0; i<= g.getVertices().size(); i++){
			System.out.println("|X|="+i+"\t "+ acPerSize[i]+"/ "+nCr(g.getVertices().size(), i));
		}
		return (int) numAC;
	}
	public static void main(String... args){


		Grph g = new PetersonGraph().petersenGraph(5, 2);
		g = new ChvatalGenerator().chvatalGenerator();
		g = new Paley13Generator().paley13Generator();
		//		g = new MCGeeGenerator().run();

//		g = new DHGenerator(15, 0.2, 0.4).run();
		//		
		//
		//		GridTopologyGenerator gt = new GridTopologyGenerator(); 
		//		g.clear();
		//		
		//		
		//		
		//		gt.setHeight(4);
		//		gt.setWidth(4);
		//		gt.compute(g);

		
		
//		int maxSize = 15;
//		int runs =10; 
//		double[] avReduction = new double[maxSize+1];
//		for(int n=1;n<maxSize+1;n++) {
//			for(int i=0; i<runs;i++) {
//				g = new RandomGraphGenerator(n, 0.75, i).compute();
////				DHGenerator dhg = new DHGenerator(n, 0.2, 0.4);
////				dhg.rnd.setSeed(i);
////				g = dhg.run();
//				
//
//				avReduction[n] += new AtomicCore().start(g);
//			}
//			avReduction[n] /= runs;
//			avReduction[n] /= Math.pow(2,n);
//		}
//		
//		System.out.println("Final results");
//		for(int i=1; i<maxSize+1;i++) {
//			System.out.println(i+" & "+avReduction[i]+"\\\\");			
//		}
		

//		System.out.println(g);
//
		new AtomicCore().start(g);
		
//		new AtomicCore().calcAtomicCore(g);
//
//
//
//
//		System.out.println(""+(1L << 2));
//		g.display();
	}

}
