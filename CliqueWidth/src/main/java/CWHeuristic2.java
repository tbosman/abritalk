import java.util.ArrayList;
import java.util.Random;

import toools.set.DefaultIntSet;
import toools.set.IntSet;
import toools.set.IntSets;
import grph.Grph;
import grph.algo.topology.GridTopologyGenerator;
import grph.algo.topology.PetersonGraph;
import grph.in_memory.InMemoryGrph;
import CliqueWidth.CliqueWidth.PartitionTree;
import CliqueWidth.CliqueWidth.tools.UFPartition;


public class CWHeuristic2 {

	public CWHeuristic2() {
		// TODO Auto-generated constructor stub
	}

	Grph g;
	UFPartition<Integer> components;
	UFPartition<Integer> groups;
	UFPartition<Integer> gPartition;
	int[] K; //group sizes

	ArrayList<IntSet> N;
	PartitionTree pTree; 

	int cwdUB = 0;

	public void init(Grph gIn) {
		g = gIn;
		components = new UFPartition<Integer>();
		components.makeSets(g.getVertices().toIntegerArrayList());

		groups = components.clone();
		
		gPartition = groups.clone();

		K = new int[g.getVertices().getGreatest()+1];
		N = new ArrayList<IntSet>(g.getVertices().getGreatest()+1);

		for(int i=0; i<K.length;i++) {
			K[i] = 1;
			IntSet completeVertices = new DefaultIntSet();
//			completeVertices.addAll(gIn.getNeighbours(i).clone());
			for(int n : gIn.getNeighbours(i).toIntArray()) {
				completeVertices.add(n);
			}
			N.add(completeVertices);

		}

		cwdUB = 0;

		pTree = new PartitionTree(); 
		pTree.createLeafs(g.getVertices());
	}

	public void updateGPartition() {
		gPartition = new UFPartition<Integer>();
		gPartition.makeSets(groups.getRoots());
		for(int g:groups.getRoots()) {
			for(int g2: groups.getRoots()) {
			if(components.find(g) == components.find(g2)) {
				gPartition.union(g,g2);
			}
			}
		}
	}
	
	public IntSet getTrivialVertices(IntSet X) {
		IntSet intersection = new DefaultIntSet();
		IntSet union = new DefaultIntSet();
		union.addAll(N.get(X.getGreatest()));
		for(int v : X.toIntArray()) {
			union.addAll(N.get(v));
			intersection = IntSets.intersection(intersection, N.get(v));			
		}
		return IntSets.difference(union, intersection);
	}
	
	public IntSet getComponents(IntSet X) {
		IntSet comps = new DefaultIntSet();
		for(int v : X.toIntArray()) {
			comps.add(components.find(v));
		}
		return comps;
	}
	
	public int getComponentMergeWidth(IntSet X) {
		int width = 0;
		for(int c : X.toIntArray()) {
			width += K[c];
		}
		return width;
	}
	
	public IntSet getGroups(IntSet X) {
		IntSet grps = new DefaultIntSet();
		for(int v : X.toIntArray()) {
			grps.add(groups.find(v));
		}
		return grps;
	}
	
	public void run(Grph g) {
		init(g);
		int numRuns = g.getVertices().size();
		for(int i=0; i<numRuns-1;i++) {
			
			// Fix pendants 
			
			for(int v : groups.getRoots()) {
				IntSet adjGroups = new DefaultIntSet();
				for(int n : N.get(v).toIntArray()) {
					adjGroups.add(groups.find(n));
				}
				if(adjGroups.size() == 1) {
					if(K[components.find(v)] == 1 ) {
						System.out.println(v+" is pendant");
						int pendantTo = adjGroups.getGreatest();
						System.out.println("Singleton comp, merging to "+pendantTo); 
						
						components.union(pendantTo, v);
						N.get(pendantTo).removeAll(IntSets.intersection(N.get(pendantTo), N.get(v)));
						N.get(v).clear();
						K[components.find(v)] += 1;
					}
				}
			}
			
			
			
			int minWidth = Integer.MAX_VALUE;
			int minU=-1;
			int minV=-1;

			Integer[] groupHeads = groups.getRoots().toArray(new Integer[1]);
			for(int ii=0;ii<groupHeads.length-1;ii++) {
				for(int iii=ii+1; iii<groupHeads.length;iii++) {
					int u = groupHeads[ii];
					int v = groupHeads[iii];
					IntSet nUmV = IntSets.difference(N.get(u), N.get(v));
					IntSet nVmU = IntSets.difference(N.get(v), N.get(u));
					IntSet cUmV = new DefaultIntSet();
					cUmV.add(components.find(u));
					IntSet cVmU = new DefaultIntSet();
					cVmU.add(components.find(v));
					for(int n : nUmV.toIntArray()) {
						cUmV.add(components.find(n));
					}
					for(int n : nVmU.toIntArray()) {
						cVmU.add(components.find(n));
					}
					boolean cDisjoint = false;
					if(IntSets.intersection(cUmV, cVmU).size() == 0 || IntSets.union(cUmV, cVmU).size()==1) {
						cDisjoint = true;
					}

					int costUV = cDisjoint? -1:0;
					
					for(int n : IntSets.union(cVmU, cUmV).toIntArray()) {
						costUV += K[n];
					}
					//Adjustment for groups with no complete vertices
					IntSet newCompGroups = new DefaultIntSet();
					for(int vc : IntSets.union(cVmU, cUmV).toIntArray()) {
						for(int vcc : components.getBlock(vc)) {
							newCompGroups.add(vcc);
						}
					}
					newCompGroups = getGroups(newCompGroups);
					
					for(int n : newCompGroups.toIntArray()) {
						if(N.get(n).size() == 0) {
							costUV -= 1;
						}
						
					}
					if(N.get(u).size() == 0 && N.get(v).size() == 0) {
						costUV += 1; //adjust for double counting
					}
					
					
//					int costUV2 = 0; 
//					IntSet secComps = getComponents(getTrivialVertices(IntSets.union(cUmV, cVmU)));
//					int secWidth = getComponentMergeWidth(secComps);
//					costUV2 = secWidth;
					
					
					IntSet vGroup = new DefaultIntSet();
					for(int vg : groups.getBlock(v)) {
						vGroup.add(vg);
					}
					
					if((u==21 && v==51) || (u==21 && v==51)) {
						int a=1;
					}
					
					int maxCompsize = 0; 
					for(int c: IntSets.union(cUmV, cVmU).toIntArray()) {
						maxCompsize = Math.max(maxCompsize, K[c]);
					}
					if(costUV < maxCompsize && false) {
						minWidth = costUV;
						minU = u;
						minV = v;

						break;
					}
					
					if(N.get(u).size()==0 && N.get(v).size() ==0 && IntSets.union(cVmU, cUmV).size() == 1) {
						minWidth = costUV;
						minU = u;
						minV = v;

						break;
					}
					
					if(costUV<minWidth || 
							//( costUV<=minWidth && N.get(u).contains(vGroup)   ) 
							(costUV<=minWidth && IntSets.intersection(cVmU, cUmV).size() > 0) //In this case resultant component has width= minWidth-1
							){
						minWidth = costUV;
						minU = u;
						minV = v;

						
					}

				}
			}



			{
				if(minWidth >3) {
					int a=1;
					for(int gr: gPartition.getRoots()) {
						System.out.println(gPartition.getBlock(gr));
					}
				}
				
				
				
				//merging minU, minV
				System.out.println("Merging "+minU+"-"+minV+",\t width="+minWidth);
				int u = minU;
				int v = minV;
				
				if(minU==13 && minV==28) {
					int a = 1;
				}
				
				
				

				//Finding comps again, todo do smarter
				IntSet nUmV = IntSets.difference(N.get(u), N.get(v));
				IntSet nVmU = IntSets.difference(N.get(v), N.get(u));
				IntSet cUmV = new DefaultIntSet();
				cUmV.add(components.find(u));
				IntSet cVmU = new DefaultIntSet();
				cVmU.add(components.find(v));
				for(int n : nUmV.toIntArray()) {
					cUmV.add(components.find(n));
				}
				for(int n : nVmU.toIntArray()) {
					cVmU.add(components.find(n));
				}

				
				IntSet compUnion = IntSets.union(cVmU, cUmV);
	
				
				if(nUmV.size()==0 && nVmU.size()==0){
					System.out.println("True twin");
				}else if(nUmV.contains(v) && nVmU.contains(u) && nUmV.size() == groups.getBlock(v).size() && nVmU.size() == groups.getBlock(u).size()) {
					System.out.println("False twin");
				}else {
					IntSet grps = getGroups(compUnion);
					boolean twinWPendant = true;
					for(int grp : grps.toIntArray()) {
						if(grp == u || grp == v || N.get(grp).size() == 0) {
							
						}else {
							twinWPendant = false;
						}
						
						
					}
					if(twinWPendant) {
						System.out.println("Twin with pendant ");
					}else if(grps.size()==3 && (N.get(u).size()==0 && N.get(v).size()==1)  && (N.get(v).size()==0 && N.get(u).size()==1) ){
						System.out.println("Pendant-to-pendant ");
					}else {
						
					
					for(int gr: gPartition.getRoots()) {
						System.out.println(gPartition.getBlock(gr));
					}
					int a=1;
					}

				}
				
				
				
				int newCompWidth = -1;
				for(int n : compUnion.toIntArray()) {
					newCompWidth += K[n];
				}
				
				for(int c : compUnion.toIntArray()) {
					components.union(components.find(u), c);
				}

				groups.union(groups.find(u), groups.find(v));

				cwdUB = Math.max(cwdUB, minWidth);
				
				
				
				

				
				if(newCompWidth >2 ) {
					for(int gr: gPartition.getRoots()) {
						System.out.println(gPartition.getBlock(gr));
					}
					int a=1;
				}
				//updating stuff
				K[components.find(u)] = newCompWidth;
				N.set(groups.find(u), IntSets.intersection(N.get(u), N.get(v)  )  );
				
				
				// DBEGU
				updateGPartition();
				
				contractVertices(u,v);
				
				checkContraction(u, v);
			}
		}
		
		System.out.print("Final width="+cwdUB);

	}
	
	private void checkContraction(int u, int v ) {
		ArrayList<Integer> newGroupVertices = groups.getBlock(u);
		for(int i=0; i<newGroupVertices.size()-1;i++ ){
			for(int j=i+1; i<newGroupVertices.size();i++) {
				int w = newGroupVertices.get(i);
				int x = newGroupVertices.get(j);
				for(int y : g.getVertices().toIntArray()) {
					if(g.areEdgesAdjacent(w, y) && !g.areEdgesAdjacent(x, y)) {
						if(!(groups.find(w) == groups.find(y) )) {
							System.out.println("Neighbourhood property VIOLATION: "+w+"~"+y+","+x+"!~"+y);
						}
					}
					if(g.areEdgesAdjacent(x, y) && !g.areEdgesAdjacent(w, y)) {
						if(!(groups.find(x) == groups.find(w) )) {
							System.out.println("Neighbourhood property VIOLATION: "+x+"~"+y+","+w+"!~"+y);
						}
					}
				}
				
			}
		}

	}
	
	private void contractVertices(int u, int v) {
		IntSet vN = g.getNeighbours(v);
		for(int n : vN.toIntArray()) {
//			if(!g.areVerticesAdjacent(u, n) && n != u) {
//				g.addUndirectedSimpleEdge(u, n);
//			}
			int e = g.getEdgesConnecting(v, n).getGreatest();
			g.removeEdge(e);
		}
		
		for(int n : g.getNeighbours(u).toIntArray()) {
			if(!vN.contains(n) && n!=v) {
//				g.removeEdge(u,n);
				int e = g.getEdgesConnecting(u, n).getGreatest();
				g.removeEdge(e);
			}
		}
			
		
		g.removeVertex(v);
		
		g.getVertexLabelProperty().setValue(u, g.getVertexLabelProperty().getValueAsString(u)+","+g.getVertexLabelProperty().getValueAsString(v));

		
	}
	
	public static void main(String... args) {
		Grph g = new InMemoryGrph();
		g.ensureNVertices(5);
		g.clique();

		g = PetersonGraph.petersenGraph(5, 2);
		
		
//			g = new Paley13Generator().paley13Generator();
//			g = new ChvatalGenerator().chvatalGenerator();
		//	g = PetersonGraph.petersenGraph(20, 3);
		g = new InMemoryGrph();
		int n=4;
		GridTopologyGenerator gt = new GridTopologyGenerator();
		gt.setWidth(n);
		gt.setHeight(n);
		gt.compute(g);
			for(int i : g.getVertices().toIntArray()) {
				g.getVertexLabelProperty().setValue(i, ""+i);
			}
			g.display();
			
		new CWHeuristic2().run(g);
		g = new DHGenerator(150, 0.2, 0.4).run();

		DHGenerator gen = new DHGenerator(80, 0.2, 0.4); 

		for(int ii=0; ii<1; ii++) {
			gen.rnd = new Random(ii);
			g = gen.run();

			for(int i : g.getVertices().toIntArray()) {
				g.getVertexLabelProperty().setValue(i, ""+i);
			}
			
			g.display();

			
		}




	}
}







