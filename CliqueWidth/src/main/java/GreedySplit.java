import java.util.ArrayList;

import grph.Grph;
import toools.set.DefaultIntSet;
import toools.set.IntSet;
import toools.set.IntSets;

public class GreedySplit {



	private ArrayList<IntSet> calculateSplits(IntSet X, Grph g){
		ArrayList<IntSet> splits = new ArrayList<IntSet>(); 

		IntSet currentGroups = new DefaultIntSet();
		IntSet currentComponent = new DefaultIntSet();

		ArrayList<IntSet> N = new ArrayList<IntSet>();
		for(int v=0; v<g.getVertices().size();v++){
			N.add(g.getNeighbours(v).clone());			
		}


		//init
		{
		int minCost = Integer.MAX_VALUE;
		int minU=-1;
		int minV=-1; 

		IntSet nonGroups = IntSets.difference(X, currentComponent); 
		for(int u : nonGroups.toIntArray()){
			for(int v: nonGroups.toIntArray()){
				if(u>=v)
					continue;
				int costUV = getMergeCost(u, v, currentGroups, nonGroups, N);
				if(costUV < minCost){
					minCost = costUV; 
					minU = u; 
					minV = v; 					
				}
			}
		}
		
		merge(minU, minV, currentComponent, currentGroups, nonGroups, N);
		System.out.println("Merging: "+minU+"-"+minV+",\t newCompsize: "+currentComponent.size()+" ("+minCost+")");

		}
		
		
		

		while(currentComponent.size()< X.size()){
			int minCost = Integer.MAX_VALUE;
			int minU=-1;
			int minV=-1; 

			IntSet nonGroups = IntSets.difference(X, currentComponent); 
			for(int u : currentGroups.toIntArray()){
				for(int v: nonGroups.toIntArray()){
					int costUV = getMergeCost(u, v, currentGroups, nonGroups, N);
					if(costUV < minCost){
						minCost = costUV; 
						minU = u; 
						minV = v; 					
					}
				}
			}
			
			merge(minU, minV, currentComponent, currentGroups, nonGroups, N);
			
			System.out.println("Merging: "+minU+"-"+minV+",\t newCompsize: "+currentComponent.size()+" ("+minCost+")");
		}
		
		return null;
		

	}

	private void merge(int v1, int v2, IntSet component, IntSet groups, IntSet nonGroups, ArrayList<IntSet> N){
		IntSet X = IntSets.difference(N.get(v1), N.get(v2));
		X.addAll(IntSets.difference(N.get(v2), N.get(v1)));
		X.addAll(v1, v2);
		X = IntSets.difference(X, groups);
		assert !IntSets.union(groups, nonGroups).contains(X);

		groups.addAll(X);
		int[] groupVertices = groups.toIntArray();

		for(int  v: IntSets.union(groups, nonGroups).toIntArray()){
			N.set(v, IntSets.difference(N.get(v), X));			
		}

		for(int i =0; i<groupVertices.length-1;i++){
			int u = groupVertices[i];
			for(int j=i+1; j<groupVertices.length;j++){				
				int v = groupVertices[j];
				if(setsEqualMod(N.get(u), N.get(v), X)){
					groups.remove(u);
					break;
				}
			}
		}

		component.addAll(X);	

	}


	private boolean setsEqualMod(IntSet A, IntSet B, IntSet M){
		IntSet Am = IntSets.difference(A, M);
		IntSet Bm = IntSets.difference(B, M);
		return (Am.equals(Bm));
	}

	private int getMergeCost(int v1, int v2,  IntSet groups, IntSet nonGroups, ArrayList<IntSet> N){
		IntSet X = IntSets.difference(N.get(v1), N.get(v2));
		X.addAll(IntSets.difference(N.get(v2), N.get(v1)));
		X.addAll(v1, v2);
		if(!IntSets.union(groups, nonGroups).contains(X)){
			return Integer.MAX_VALUE;
		}

		int[] newVertices = IntSets.union(groups, X).toIntArray();
		int cost = newVertices.length;
		for(int i =0; i<newVertices.length-1;i++){
			int u = newVertices[i];
			for(int j=i+1; j<newVertices.length;j++){				
				int v = newVertices[j];
				if(setsEqualMod(N.get(u), N.get(v), X)){
					cost--;
					break;
				}
			}
		}
		return cost;


	}
	
	
	
	public static void main(String... args){
		Grph g = new MCGeeGenerator().run();
		
		DHGenerator DHG = new DHGenerator(150, 0.2,0.4);
		g = DHG.run();
		new GreedySplit().calculateSplits(g.getVertices(), g);
	}

}
