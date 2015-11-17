import java.io.IOException;
import java.util.Iterator;

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
		
		while(gIn.getVertices().size()>1) {
			int[] toGroup = findCheapestLabelMerge();
			int u = toGroup[0];
			int v = toGroup[1];
			
			System.out.println("Merging: "+u+"-"+v);
			try {
				if(false)
				System.in.read();
//				int a =1;
			} catch (IOException e) {
				e.printStackTrace();
			}
			executeLabelMerge(u, v);
			System.out.println(components);
		}
		
		
		return null;//TODO return
	}
	
	public void executeLabelMerge(int u, int v) {
		IntSet reqV = getRequiredVertices(u,v);				
		IntSet reqC = getRequiredComponents(reqV);
		System.out.println("Required width: "+getRequiredWidth(reqC));
		if(reqC.size() > 1) {
			pTree.disjointMerge(reqC);
			int[] toMergeComponents = reqC.toIntArray();
			for(int i=1; i<toMergeComponents.length;i++){
				components.union(toMergeComponents[0], toMergeComponents[i]);
			}		
			
		}
		if(reqV.size() == 2 || reqC.size() == 1) {
			//can merge labels immediately after merging components because no required vertices 
			//or components do not need to be merged 
			pTree.labelMerge(u, v);
		}else {
			pTree.stageLabelMerge(u, v);
		}
		g.getVertexLabelProperty().setValue(Math.min(u,v), g.getVertexLabelProperty().getValueAsString(Math.min(u,v))+","+g.getVertexLabelProperty().getValueAsString(Math.max(u,v)));
//		g.removeEdge(u,v);;
		contractVertices(Math.min(u,v), Math.max(u,v));
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
				int w = getRequiredWidth(reqC) - (reqV.size()==2?1:0);
				if(w<minW) {
					minW =w;
					cu = u;
					cv = v;
				}
			}
				
		}
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
	
	private int getRequiredWidth(IntSet reqComponents) {
		int width =0;
		for(int component : reqComponents.toIntArray()) {
			width += components.size(component);
		}
		return width; 
	}
	
	public static void main(String... args) {
		Grph g = new InMemoryGrph();
		g.ensureNVertices(5);
		g.clique();
		
		g = PetersonGraph.petersenGraph(5, 2);
		for(int i : g.getVertices().toIntArray()) {
			g.getVertexLabelProperty().setValue(i, ""+i);
		}
		
		
		new CWHeuristic().run(g);
	}
	
}
