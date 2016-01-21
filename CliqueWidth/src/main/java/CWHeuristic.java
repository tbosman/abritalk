import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import toools.set.DefaultIntSet;
import toools.set.IntSet;
import toools.set.IntSets;
import grph.Grph;
import grph.algo.topology.PetersonGraph;
import grph.in_memory.InMemoryGrph;
import CliqueWidth.CliqueWidth.PartitionTree;
import CliqueWidth.CliqueWidth.tools.UFPartition;


public class CWHeuristic {
	Grph g;
	UFPartition<Integer> components;
	PartitionTree pTree; 
	
	int maxW = 0; 
	public CWHeuristic() {
	}

	public void init(Grph gIn) {
		g = gIn;
		components = new UFPartition<Integer>();
		components.makeSets(g.getVertices().toIntegerArrayList());
		pTree = new PartitionTree(); 
		pTree.createLeafs(g.getVertices());
	}
	public PartitionTree run(Grph gIn) {
		
		init(gIn); 
	
		gIn.displayGraphstream_0_4_2();
		
		
		System.out.println("Running LB Algo");
		
//		VCLowerBound LB = new VCLowerBound(g, components);
//		LB.run();
		
		
		try {
//			if(false)
			System.in.read();
//			int a =1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		while(gIn.getVertices().size()>1) {
			int[] toGroup = findCheapestLabelMerge();
			int u = toGroup[0];
			int v = toGroup[1];
			
			System.out.println("Merging: "+u+"-"+v);
/*			try {
//				if(false)
				System.in.read();
//				int a =1;
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			executeLabelMerge(u, v);
//			System.out.println(components);
			
//			System.out.println("Running LB Algo");
//			
//			LB = new VCLowerBound(g, components);
//			LB.run();
//			

			
//			try {
////				if(false)
//				System.in.read();
////				int a =1;
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			
		}
		
		
		System.out.println("## Finished, cwd: "+this.maxW);
		
		return null;//TODO return
	}
	
	public void executeLabelMerge(int u, int v) {
		IntSet reqV = getRequiredVertices(u,v);				
		IntSet reqC = getRequiredComponents(reqV);
		System.out.println("Required width: "+getRequiredWidth(u,v, reqV, reqC));
		if(reqC.size() > 1) {
			pTree.disjointMerge(reqC);
			int[] toMergeComponents = reqC.toIntArray();
			Arrays.sort(toMergeComponents);
			for(int i=1; i<toMergeComponents.length;i++){
				components.union(toMergeComponents[0], toMergeComponents[i]);
			}		
			
		}
		if((reqV.size() == 2 && ( !g.areVerticesAdjacent(u, v)) ) || reqC.size() == 1) {
			//can merge labels immediately after merging components because no required vertices 
			//or components do not need to be merged 
			pTree.labelMerge(u, v);
		}else {
			pTree.stageLabelMerge(u, v);
		}
		g.getVertexLabelProperty().setValue(Math.min(u,v), g.getVertexLabelProperty().getValueAsString(Math.min(u,v))+","+g.getVertexLabelProperty().getValueAsString(Math.max(u,v)));
//		g.removeEdge(u,v);;
		
		//Calculate edges that must be removed from completness neighbourhood
		IntSet neighboursRemove = IntSets.difference(g.getNeighbours(Math.min(u, v)), g.getNeighbours(Math.max(u, v)));;
		
		
		contractVertices(Math.min(u,v), Math.max(u,v));
		for(int n:neighboursRemove.toIntArray()) {
			g.removeEdge(Math.min(u,v), n);
		}
		//
		components.remove(Math.max(u, v));
		
	}
	
	
	private void contractVertices(int u, int v) {
		IntSet vN = g.getNeighbours(v);
		for(int n : vN.toIntArray()) {
			if(!g.areVerticesAdjacent(u, n) && n != u) {
				g.addUndirectedSimpleEdge(u, n);
			}
			int e = g.getEdgesConnecting(v, n).getGreatest();
			g.removeEdge(e);
		}
		g.removeVertex(v);
		
	}
	
	public int[] findCheapestLabelMerge() {
		int cu = 0, cv = 0, minW = Integer.MAX_VALUE; 
		int[] vertices = g.getVertices().toIntArray();
		for(int i = 0; i<vertices.length-1;i++) {
			for(int j=i+1; j<vertices.length;j++) {
				int u = vertices[i];
				int v = vertices[j];
				IntSet reqV = getRequiredVertices(u,v);				
				IntSet reqC = getRequiredComponents(reqV);
				int w = getRequiredWidth(u, v, reqV, reqC); // - (reqC.size() == 1 || !(reqV.size()==2  && g.areVerticesAdjacent(u, v))   ?1:0);
				if(w<minW) {
					minW =w;
					cu = u;
					cv = v;
				}
			}
				
		}
		
		this.maxW = Math.max(minW, this.maxW);
		return new int[] {cu, cv};
	}
	
	private IntSet getRequiredVertices(int u, int v) {
		IntSet reqVertices = new DefaultIntSet(); 
		reqVertices.addAll(u, v);
		IntSet nU = g.getNeighbours(u);
		IntSet nV = g.getNeighbours(v);
		reqVertices.addAll(IntSets.difference(IntSets.union(g.getNeighbours(u), g.getNeighbours(v)), IntSets.intersection(g.getNeighbours(u), g.getNeighbours(v)) ) );
						
		return reqVertices;		
	}
	
	private IntSet getRequiredComponents(IntSet reqVertices) {
		IntSet reqComponents = new DefaultIntSet();
		for(int i : reqVertices.toIntArray()) {
			reqComponents.add(components.find(i));
		}
		return reqComponents;
	}
	
	private int getRequiredWidth(int u, int v, IntSet reqVertices, IntSet reqComponents) {
		int width =0;
		for(int component : reqComponents.toIntArray()) {
			width += components.size(component);
		}
		
		width -= (reqComponents.size() == 1 || (reqVertices.size()==2  && !g.areVerticesAdjacent(u, v))   ?1:0);
		
		return width; 
	}
	
	
	
	public static void main(String... args) {
		Grph g = new InMemoryGrph();
		g.ensureNVertices(5);
		g.clique();
		
		g = PetersonGraph.petersenGraph(5, 2);
//		g = new Paley13Generator().paley13Generator();
//		g = new ChvatalGenerator().chvatalGenerator();
//		g = PetersonGraph.petersenGraph(20, 3);
		g = new DHGenerator(20, 0.2, 0.4).run();
		
		DHGenerator gen = new DHGenerator(100, 0.2, 0.4); 
		
		for(int ii=0; ii<5; ii++) {
			gen.rnd = new Random(ii);
		g = gen.run();
			
		for(int i : g.getVertices().toIntArray()) {
			g.getVertexLabelProperty().setValue(i, ""+i);
		}
		
		
		new CWHeuristic().run(g);
		
		}
	}
	
}
