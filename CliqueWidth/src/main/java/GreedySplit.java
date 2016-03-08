import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;







import CliqueWidth.CliqueWidth.UnionTree;
import CliqueWidth.CliqueWidth.UnionTree.Node;
import CliqueWidth.CliqueWidth.UnionTree.NodeType;
import grph.Grph;
import grph.VertexPair;
import grph.algo.topology.GridTopologyGenerator;
import grph.algo.topology.PetersonGraph;
import grph.in_memory.InMemoryGrph;
import grph.io.DimacsReader;
import grph.io.GraphBuildException;
import grph.io.ParseException;
import toools.io.file.RegularFile;
import toools.set.DefaultIntSet;
import toools.set.IntSet;
import toools.set.IntSets;

public class GreedySplit {



	private boolean doCWD =true;


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
					int costUV = getMergeCost(u, v, currentComponent, X, N, g);
					if(costUV < minCost){
						minCost = costUV; 
						minU = u; 
						minV = v; 					
					}
				}
			}

			if(minCost < Integer.MAX_VALUE) {
				merge(minU, minV, currentComponent, currentGroups, nonGroups, N);
				
				if(splits.size()>0) {
				System.out.println("Merging: "+minU+"-"+minV+",\t newCompsize: "+currentComponent.size()+" ("+minCost+")");
				System.out.println("Real width lb: "+getSplitWidth(splits.get(splits.size()-1), IntSets.difference(currentComponent, splits.get(splits.size()-1)),g ) ) ;
				}

				splits.add(currentComponent.clone());
				
				IntSet trueGroups = getAtomHeads(currentComponent,g);
				
				System.out.println(""+trueGroups+"-"+currentGroups);
			}else {
				int w = getSubsetWidth(X, g);
				System.out.println("No group merges possible at all. Adding remaining vertices one by one");
				for(int v: X.toIntArray()) {
					currentComponent.add(v);
					splits.add(currentComponent.clone());

				}
			}
		}




		while(currentComponent.size()< X.size()){
			int minCost = Integer.MAX_VALUE;
			int minU=-1;
			int minV=-1; 

			IntSet nonGroups = IntSets.difference(X, currentComponent); 
			for(int u : currentGroups.toIntArray()){
				for(int v: IntSets.union(currentGroups,nonGroups).toIntArray()){
					if(u <= v) {
						continue;
					}
					int costUV = getMergeCost(u, v, currentComponent, X, N,g);
					if(costUV < minCost){
						minCost = costUV; 
						minU = u; 
						minV = v; 					
					}
				}
			}
			if(minCost < Integer.MAX_VALUE) {
				merge(minU, minV, currentComponent, currentGroups, nonGroups, N);

				if(splits.size()>0) {
				System.out.println("Merging: "+minU+"-"+minV+",\t newCompsize: "+currentComponent.size()+" ("+minCost+")");
				System.out.println("Real width lb: "+getSplitWidth(splits.get(splits.size()-1), IntSets.difference(currentComponent, splits.get(splits.size()-1)),g ) ) ;
				System.out.println(splits.get(splits.size()-1)+"="+ IntSets.difference(currentComponent, splits.get(splits.size()-1)) );
				}

				splits.add(currentComponent.clone());
				
				IntSet trueGroups = getAtomHeads(currentComponent,g);
				
				System.out.println(""+trueGroups+"-"+currentGroups);
			}else {
				System.out.println("No group merges possible anymore. Adding remaining vertices to the lot");
				splits.add(X);
				break;
			}
		}

		return splits;


	}

	private void merge(int v1, int v2, IntSet component, IntSet groups, IntSet nonGroups, ArrayList<IntSet> N){
		IntSet X = IntSets.difference(N.get(v1), N.get(v2));
		X.addAll(IntSets.difference(N.get(v2), N.get(v1)));
		X.addAll(v1, v2);
		X = IntSets.difference(X, groups);
		assert IntSets.union(groups, nonGroups).contains(X);

		groups.addAll(X);
		int[] groupVertices = groups.toIntArray();

		for(int  v: IntSets.union(groups, nonGroups).toIntArray()){
			N.set(v, IntSets.difference(N.get(v), X));			
		}
		component.addAll(X);	
		for(int i =0; i<groupVertices.length-1;i++){
			int u = groupVertices[i];
			for(int j=i+1; j<groupVertices.length;j++){				
				int v = groupVertices[j];
				if(setsEqualMod(N.get(u), N.get(v), component)){
					groups.remove(u);
					break;
				}
			}
		}
		
		

	

	}


	private boolean setsEqualMod(IntSet A, IntSet B, IntSet M){
		IntSet Am = IntSets.difference(A, M);
		IntSet Bm = IntSets.difference(B, M);
		return (Am.equals(Bm));
	}

	private int getMergeCost(int v1, int v2, IntSet currentComponent, IntSet Universe, ArrayList<IntSet> N, Grph g) {
		IntSet X = IntSets.difference(N.get(v1), N.get(v2));
		X.addAll(IntSets.difference(N.get(v2), N.get(v1)));
		X.addAll(v1, v2);
		if(!Universe.contains(X)){
			return Integer.MAX_VALUE;
		}
		X = IntSets.difference(X, currentComponent); 
		if(doCWD) {
			return getCWDSplitWidth(currentComponent, X, g);
		}
		
		return getSplitWidth(currentComponent, X, g);
	}
	/*
	private int getMergeCost(int v1, int v2,  IntSet groups, IntSet nonGroups, ArrayList<IntSet> N, Grph g){
		IntSet X = IntSets.difference(N.get(v1), N.get(v2));
		X.addAll(IntSets.difference(N.get(v2), N.get(v1)));
		X.addAll(v1, v2);
		if(!IntSets.union(groups, nonGroups).contains(X)){
			return Integer.MAX_VALUE;
		}
		
		IntSet newGroups = IntSets.union(groups, X);
		IntSet addedVertices = IntSets.difference(newGroups, groups);
		//
		int sw =  getSplitWidth(groups, addedVertices, g);
		System.out.println(""+groups+"-"+addedVertices+" W:"+sw);
		return sw;
		

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

	
	}*/

	public int getSubsetWidth(IntSet X, Grph g) {
		int width = X.size(); 
		for(int u : X.toIntArray()) {
			for(int v : X.toIntArray()) {
				if(v <= u) {
					continue;
				}
				//				System.out.println("N["+u+": "+IntSets.difference(g.getNeighbours(u),X));
				//				System.out.println("N["+v+": "+IntSets.difference(g.getNeighbours(v),X));
				if(IntSets.difference(g.getNeighbours(u),X).equals(IntSets.difference(g.getNeighbours(v),X))) {
					width--;
					break;
				}
			}

		}
		assert width >=1;
		return width;
	}

	public boolean subSetIsAtomicGroup(IntSet X, Grph g) {

		return getSubsetWidth(X,g)==1;
	}

	public void finishKTreeSplits(UnionTree kTree, Grph g) {

		for(Node iNode : kTree.getInnerNodes()) {

//			System.out.println("InnerNode id: "+iNode.getId());
			for(Node cNode: iNode.getChildren()) {
//				System.out.println("Cnode id: "+cNode.getId()+"\t Type:"+cNode.getType() );
			}

		}

		boolean notFinished = true;
		while(notFinished) {
			notFinished = false;
			ArrayList<Node> innerNodes = kTree.getInnerNodes();

			for(Node iNode : innerNodes) {
				IntSet X = kTree.getLeavesInSubtree(iNode);
				if(iNode.getChildren().size()>2 && !subSetIsAtomicGroup(X, g)) {

					for(Node cNode : iNode.getChildren()) {
						assert cNode.getType() == NodeType.LEAF;
					}

					System.out.println("Extending split at node id: "+iNode.getId()+" containing:");
					System.out.println(""+X);
					ArrayList<IntSet> splits = calculateSplits(X, g);
					if(splits.size()>1) { //Proper split found
						extendTreeThroughLaminarSplit(kTree, splits, g);
						notFinished = true;
					}
				}
			}
		}
	}
	
	public int getBestSplitCutoff(ArrayList<IntSet> splits, Grph g) {
		return splits.size()-1;
//		
//		int nVertices = splits.get(splits.size()-1).size();
//		if(splits.size() < 4) {
//			return splits.size()-1;
//		}
//		int minWidth = Integer.MAX_VALUE;
//		int cumWidth = 0;
//		int bestSplit = splits.size()-1;
//		for(int i=0;i<splits.size();i++) {
//			if(splits.get(i).size()<(nVertices/3.0)) {
//				continue;
//			}
//			int cWidth = getSplitWidth(splits.get(i), IntSets.difference(splits.get(splits.size()-1),splits.get(i)), g );
//			int cWidth2 = getSplitWidth(splits.get(i-1), IntSets.difference(splits.get(i),splits.get(i-1)), g );
//			cumWidth = Math.max(cumWidth, cWidth2);
//			int currentWidth = Math.max(cWidth, cumWidth);
//			if(currentWidth < minWidth) {
//				minWidth = cWidth;
//				bestSplit = i;
//			}
//		}
//		return bestSplit;
	}

	public void extendTreeThroughLaminarSplit(UnionTree kTree, ArrayList<IntSet> splits, Grph g) {
		//Init		

		
//		for(int i=0; i<splits.size()-1; i++) {
		for(int i=0; i<splits.size()-1; i++) {
			if(splits.get(i).size()>1) {
				kTree.makeCommonAncestor(splits.get(i));
			}
			
			IntSet addedVertices = IntSets.difference(splits.get(i+1), splits.get(i));

			
			if(addedVertices.size()>1) {
				kTree.makeCommonAncestor(addedVertices);
			}
			
		}
		

	}
	public UnionTree createUTreeFromSplits(ArrayList<IntSet> splits, Grph g) {
		UnionTree kTree = new UnionTree(); 
		kTree.addLeaves(g.getVertices());;

		extendTreeThroughLaminarSplit(kTree, splits, g);
		
		
		//Init
		/*kTree.makeCommonAncestor(splits.get(0));
		if(splits.get(0).size()==1) {
			System.err.println("singleton splitset");
		}

		for(int i=1; i<splits.size(); i++) {
			IntSet addedVertices = IntSets.difference(splits.get(i), splits.get(i-1));
			if(addedVertices.size()>1) {
				kTree.makeCommonAncestor(addedVertices);
			}
			kTree.makeCommonAncestor(splits.get(i));
		}*/


		return kTree;
	}

	public IntSet getAtomHeads(IntSet X, Grph g) {
		IntSet atomHeads = new DefaultIntSet();
		int[] Xarr = X.toIntArray();
		Arrays.sort(Xarr);
		for(int u : Xarr) {
			boolean isAtomHead = true; 
			for(int v : X.toIntArray()) {
				if(v <= u) {
					continue;
				}
				//				System.out.println("N["+u+": "+IntSets.difference(g.getNeighbours(u),X));
				//				System.out.println("N["+v+": "+IntSets.difference(g.getNeighbours(v),X));
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
	
	public boolean pathPropertyBlocks(int v1, int v2, int v3, int v4, Grph g) {
		
		boolean block = true; 
		
		block = block && g.areVerticesAdjacent(v1, v3);
		block = block && g.areVerticesAdjacent(v1, v4);
		block = block && (g.areVerticesAdjacent(v2, v3) ^ g.areVerticesAdjacent(v2, v4));
		if(block) {
			return true;
		}
		
		block = block && g.areVerticesAdjacent(v2, v3);
		block = block && g.areVerticesAdjacent(v2, v4);
		block = block && (g.areVerticesAdjacent(v1, v3) ^ g.areVerticesAdjacent(v1, v4));
		if(block) {
			return true;
		}
		return false;		
		
	}
	public int getCWDSplitWidth(IntSet X1, IntSet X2, Grph g) {
		IntSet atomHeadsOne = getAtomHeads(X1,g);
		IntSet atomHeadsOther = getAtomHeads(X2, g );
		IntSet X = IntSets.union(X1, X2);
		int width = atomHeadsOne.size()+atomHeadsOther.size();
		
		HashSet<IntSet> contractedPairs = new HashSet<IntSet>();
		for(int u : atomHeadsOne.toIntArray()) {
			
			for(int v : atomHeadsOther.toIntArray() ) {
				//check if edge or path property is violated
				if(g.getNeighbours(u).contains(v) || g.getNeighbours(v).contains(u) ){
					continue; //Edge property violated
				}
				if(!g.getNeighbours(u).contains(IntSets.difference(g.getNeighbours(v), X2))){
					continue;//Neighbourhood property violated
				}
				if(!g.getNeighbours(v).contains(IntSets.difference(g.getNeighbours(u), X1))){
					continue; //Same
				}
				
				if(IntSets.difference(g.getNeighbours(u), X).equals(IntSets.difference(g.getNeighbours(v), X)) ) {
					boolean blocked = false;
					for(IntSet pair: contractedPairs) {
						if(pathPropertyBlocks(u, v, pair.toIntArray()[0], pair.toIntArray()[1],g)) {
							blocked =true;
							break;
						}
					}
					if(!blocked) {
						width--; 
						atomHeadsOther.remove(v);
						contractedPairs.add(IntSets.from(u,v));
						break;
					}
				}
			}
		}
		return width;
	}
	
	public int getSplitWidth(IntSet X1, IntSet X2, Grph g) {
		IntSet atomHeadsOne = getAtomHeads(X1,g);
		IntSet atomHeadsOther = getAtomHeads(X2, g );
		IntSet X = IntSets.union(X1, X2);
		int width = atomHeadsOne.size()+atomHeadsOther.size();
		for(int u : atomHeadsOne.toIntArray()) {			 
			for(int v : atomHeadsOther.toIntArray() ) {
				if(IntSets.difference(g.getNeighbours(u), X).equals(IntSets.difference(g.getNeighbours(v), X)) ) {
					width--; 
					atomHeadsOther.remove(v);
					break;
				}
			}
		}
		return width;
	}
	public int getCWDNodeWidth(UnionTree kTree, Node iNode, Grph g) {
		HashSet<Node> cNodes  = iNode.getChildren();
		
		if(cNodes.size()>2) {
			int w = 0; 
			for(Node cNode : cNodes) {
				w += getSubsetWidth(kTree.getLeavesInSubtree(cNode), g);
			}
			return w;
		}
		
		IntSet X = kTree.getLeavesInSubtree(iNode);
		
		Iterator<Node> cIterator = cNodes.iterator(); 
		Node cNode1 = cIterator.next();
		Node cNode2 = cIterator.next();
		IntSet X1 = kTree.getLeavesInSubtree(cNode1);
		IntSet X2 = kTree.getLeavesInSubtree(cNode2);
		return getCWDSplitWidth(X1, X2, g);
	}
	public int getNodeWidth(UnionTree kTree, Node iNode, Grph g) {
		HashSet<Node> cNodes  = iNode.getChildren();
		
		if(cNodes.size()>2) {
			int w = 0; 
			for(Node cNode : cNodes) {
				w += getSubsetWidth(kTree.getLeavesInSubtree(cNode), g);
			}
			return w;
		}
		
		IntSet X = kTree.getLeavesInSubtree(iNode);
		
		Iterator<Node> cIterator = cNodes.iterator(); 
		Node cNode1 = cIterator.next();
		Node cNode2 = cIterator.next();
		IntSet X1 = kTree.getLeavesInSubtree(cNode1);
		IntSet X2 = kTree.getLeavesInSubtree(cNode2);
		return getSplitWidth(X1, X2, g);
		/*
		IntSet atomHeadsOne = getAtomHeads(kTree.getLeavesInSubtree(cNode1),g);
		IntSet atomHeadsOther = getAtomHeads(kTree.getLeavesInSubtree(cNode2), g );
		int width = atomHeadsOne.size()+atomHeadsOther.size();
		for(int u : getAtomHeads(kTree.getLeavesInSubtree(cNode1), g ).toIntArray() ) {			 
			for(int v : atomHeadsOther.toIntArray() ) {
				if(IntSets.difference(g.getNeighbours(u), X).equals(IntSets.difference(g.getNeighbours(v), X)) ) {
					width--; 
					atomHeadsOther.remove(v);
					break;
				}
			}
		}
		return width;
*/

	}
	public int getKTreeWidth(UnionTree kTree, Grph g) {
		int width = 0; 

		for(Node iNode : kTree.getInnerNodes()) {			
//			int nWidth = getSubsetWidth(kTree.getLeavesInSubtree(iNode), g);
			int nWidth = getNodeWidth(kTree, iNode,g);
			width = Math.max(width, nWidth);
		}
		return width;
	}
	public int getCWDTreeWidth(UnionTree kTree, Grph g) {
		int width = 0; 

		for(Node iNode : kTree.getInnerNodes()) {			
//			int nWidth = getSubsetWidth(kTree.getLeavesInSubtree(iNode), g);
			int nWidth = getCWDNodeWidth(kTree, iNode,g);
			width = Math.max(width, nWidth);
		}
		return width;
	}
	

	public static void main(String... args) throws ParseException, IOException, GraphBuildException{
		Grph g;
//		g = new MCGeeGenerator().run();
//		g = new Paley13Generator().paley13Generator();
//		g = new ChvatalGenerator().chvatalGenerator();
//		g = PetersonGraph.petersenGraph(5, 2);
		
//		g = new FlowerSnarkGenerator().run(5);
		
//		g = new FranklinGraph().run();
//		g= new ClebschGenerator().clebschGenerator();
		
//		g = PetersonGraph.petersenGraph(10, 3);//Is Desargues graph
//		g = new FruchtGenerator().fruchtGenerator();
//		g = new PappusGenerator().pappusGenerator();
//		g = new ShrikhandeGenerator().shrikhandeGenerator();
		
//		DHGenerator DHG = new DHGenerator(50, 0.2,0.4);
//				g = DHG.run();

		
		g = new DimacsReader().readGraph(new RegularFile("graphs/mulsol.i.5.col"));
//		if(!g.containsVertex(0) && g.containsVertex(1)) {//vertex indexing starting at 1, relabel max vertex to 0
//			int maxV = g.getVertices().getGreatest();
//			g.addVertex(0);
//			IntSet mNeighbours = g.getNeighbours(maxV);
//			for(int n: mNeighbours.toIntArray()) {
//				g.addUndirectedSimpleEdge(0, n);
//			}
//			g.removeVertex(maxV);
//		}
		GrphTools.NormalizeVertexRange(g);
		
		System.out.println(g);
		
		
		
//		g = new InMemoryGrph();
		int n=10;
		GridTopologyGenerator gt = new GridTopologyGenerator();
		gt.setWidth(n);
		gt.setHeight(n);
//		g.clear();
//		gt.compute(g);
		GreedySplit GS = new GreedySplit();
		GS.doCWD = false;
		ArrayList<IntSet> splits = GS.calculateSplits(g.getVertices(), g);
		UnionTree kTree = GS.createUTreeFromSplits(splits, g);
		GS.finishKTreeSplits(kTree, g);
		int NLCWidth = GS.getKTreeWidth(kTree, g);
		GS = new GreedySplit();
		GS.doCWD = true;
		splits = GS.calculateSplits(g.getVertices(), g);
		kTree = GS.createUTreeFromSplits(splits, g);
		GS.finishKTreeSplits(kTree, g);
		int CWidth = GS.getCWDTreeWidth(kTree, g);
		System.out.println("Done, final NLC-width: "+NLCWidth );
		System.out.println("Done, final Clique-width: "+CWidth );
//		
//		GS = new GreedySplit();
//		GS.doCWD = true;
//		splits = GS.calculateSplits(g.getVertices(), g);
//		GS.createUTreeFromSplits(splits, g);
//		GS.finishKTreeSplits(kTree, g);
//		System.out.println("Done, final NLC-width: "+GS.getKTreeWidth(kTree, g) );
//		System.out.println("Done, final Clique-width: "+GS.getCWDTreeWidth(kTree, g) );
	
		
		Grph kTGraph = kTree.toGrph();
		kTGraph.display();
		g.display();
	}
	

}
