
import java.io.IOException;

import grph.Grph;
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

public class NLCDP {
	
	
	private Grph g;
	private int UB = Integer.MAX_VALUE;
	private boolean isUBSet =true;
	private int LB = 0;
	private boolean isLBSet = true;
	
	

	public NLCDP(Grph g){
		init(g);
	}
	
	public void init(Grph g){
		this.g = g;
	}
	
	public void setUpperBound(int UB){
		this.UB = UB;
		this.isUBSet = true;
	}
	
	public void setLowerBound(int LB){
		this.LB = LB;
		this.isLBSet = true;
	}
	
	
	
	public IntSet intsetIndexProjection(IntSet X, int[] idcs){
		assert X.getGreatest()<= idcs.length;
		IntSet XPr = new DefaultIntSet();
		for(int v : X.toIntArray()){
			XPr.add(idcs[v]);
		}
		return XPr;
	}
	
	public int intervalLowerBound(int from, int to, int[] minWidthsPerCardinality){
		if(to-from < 1){
			return 0;
		}
		int minWidth = Integer.MAX_VALUE;
		for(int i=from; i<to; i++){
			minWidth = Math.min(minWidth, minWidthsPerCardinality[i]);
		}
		if(minWidth == Integer.MAX_VALUE){
			minWidth = from; 
		}
		return minWidth;
	}
	
	
	public void start(){
		PowerSetIterator subsetIterator = new PowerSetIterator(g.getVertices().size());
		
		CompactIntArray widths = new CompactIntArray(32, 1 << (g.getVertices().size()));
		int[] minWidthPerCardinality = new int[g.getVertices().size()+1];
		minWidthPerCardinality[0] = 1;
		minWidthPerCardinality[1] = 1;
		for(int i=3;i<minWidthPerCardinality.length;i++){minWidthPerCardinality[i] = Integer.MAX_VALUE;};
		
		int largestSetSeen = 0; //cardinality sets of current iteration of DP
		GreedySplit GS = new GreedySplit(); 
		
		while(subsetIterator.hasNext()){			
			
			if(this.UB<= this.LB) {
				widths.set(IntStringTools.intsetToInt(g.getVertices()), this.UB);
				break;
			}
			
			IntSet curSet = subsetIterator.next();
			int curSetString = IntStringTools.intsetToInt(curSet);
//			System.out.println(IntStringTools.intToIntSet(curSetString));
			
			if(curSet.size()>largestSetSeen){
				if(largestSetSeen % 2 ==0){
					int lb = intervalLowerBound(largestSetSeen/2, largestSetSeen, minWidthPerCardinality);
					System.out.println("Updating interval LB ["+largestSetSeen/2+","+largestSetSeen+"],  bound: "+lb+" old bound: "+this.LB);
					if(!isLBSet || lb > this.LB){
						this.LB = lb;
						this.isLBSet = true;						
					}
				}
				largestSetSeen++; 
				
			}
			
			if(curSet.size() < 2){
				widths.set(curSetString,curSet.size());				
			}else 	if(!AtomicCore.isAtomicCore(curSet, g)){
				//do nothing; 
				int coreString = IntStringTools.intsetToInt(AtomicCore.getAtomicCore(curSet, g));
				IntSet atoms = AtomTools.atomHeads(curSet, g);
				int width = Math.max(widths.get(coreString), atoms.size());
				if(width < minWidthPerCardinality[curSet.size()]){
					minWidthPerCardinality[curSet.size()] = width;
				}
			}else{
				widths.setMax(curSetString);
				int curSetLB = AtomTools.atomHeads(curSet, g).size();
				if(isUBSet){					
					if(curSetLB > this.UB){
						continue;
					}
				}				
				int[] idxProjection = curSet.toIntArray();
				PowerSetIterator subsubsetIterator = new PowerSetIterator(curSet.size());
				while(subsubsetIterator.hasNext()){
					
					IntSet subsubSet = intsetIndexProjection(subsubsetIterator.next(), idxProjection);
					if(!curSet.contains(subsubSet)){
						System.out.println("this shouldnt happen");
					}
					IntSet subsubSetCompl = IntSets.difference(curSet, subsubSet);
					IntSet subsubSetCore =AtomicCore.getAtomicCore(subsubSet,g);
					IntSet subsubSetComplCore =AtomicCore.getAtomicCore(subsubSetCompl,g); 
					
					int w1 = widths.get(IntStringTools.intsetToInt(subsubSetCore));
					int w2 = widths.get(IntStringTools.intsetToInt(subsubSetComplCore));
					if(isUBSet){					
						if(w1 > this.UB || w2 > this.UB){
							continue;
						}
					}
					
					int mu = new GreedySplit().getSplitWidth(subsubSet,  subsubSetCompl, g);
					/*int mu = GS.getSplitWidth(subsubSetCore, subsubSetComplCore,g);
					mu += curSet.size() - subsubSetCore.size() - subsubSetComplCore.size();
					if(mu != GS.getSplitWidth(subsubSet, subsubSetCompl, g)){
						System.out.println("oh-oh");
					}*/
//					int w1 = widths.get(IntStringTools.intsetToInt(subsubSet));
//					int w2 = widths.get(IntStringTools.intsetToInt(subsubSetCompl));
					int width = Math.max(mu, w1);
					width = Math.max(width, w2);
					if(width < widths.get(curSetString)){
						if(widths.get(curSetString) < minWidthPerCardinality[curSet.size()]){
							minWidthPerCardinality[curSet.size()] = widths.get(curSetString);
						}
						if(width<32 && curSet.size()>9){

							System.out.println("Set: "+curSet+"\t width: "+width+"\t Best LB-UB: "+this.LB+"-"+this.UB+"\t Set size:"+curSet.size());


						}
						widths.set(curSetString, width);
						if(width <= curSetLB) {
							continue;
						}
						if(isLBSet){
							if(width <= this.LB){
								continue;
							}							
						}
					}
					
				}
				
				
				
				if(curSet.size()>=g.getVertices().size()/2.0) {
					IntSet curSetCompl = IntSets.difference(g.getVertices(), curSet);
					
					int curSetComplString = IntStringTools.intsetToInt(curSetCompl);
					if(AtomicCore.isAtomicCore(curSetCompl, g) && widths.get(curSetComplString) == 0){//not yet set 
						continue;
					}
					int solwidth = Math.max(widths.get(IntStringTools.intsetToInt(AtomicCore.getAtomicCore(curSet,g))),IntStringTools.intsetToInt(AtomicCore.getAtomicCore(curSetCompl,g)));
					if(solwidth >= this.UB){
						continue;
					}
					solwidth = Math.max(new GreedySplit().getSplitWidth(curSet, curSetCompl, g), solwidth);
					if(solwidth <= this.LB) {
						widths.set(IntStringTools.intsetToInt(g.getVertices()), solwidth);
						break;
					}else if(!isUBSet || solwidth <= this.UB) {
						this.UB = solwidth;
						isUBSet = true;
					}
				}
				if(widths.get(curSetString) == 0){
					System.out.println("Zero width: "+curSet);
				}
				
				
				
				
			}
			
			
			
		}
		
		int nlcwidth = widths.get((1 << g.getVertices().size())-1);
		int V = (1 << g.getVertices().size())-1;
		System.out.println(Integer.toBinaryString(V));
		System.out.println(IntStringTools.intToIntSet(V));
		System.out.println("final width: "+nlcwidth);
		
	}
	
	public static void main(String... args) throws ParseException, IOException, GraphBuildException{
		Grph g;
//		g = new PetersonGraph().petersenGraph(5,2);
//		g = new ChvatalGenerator().chvatalGenerator();
		g = new Paley13Generator().paley13Generator();
//		g = new MCGeeGenerator().run();
//		g = new FlowerSnarkGenerator().run(5);
//		GridTopologyGenerator GT = new GridTopologyGenerator();
//		GT.setHeight(4);
//		GT.setWidth(4);
//		g= new InMemoryGrph();
//		GT.compute(g);
		
		
		
		g = new DimacsReader().readGraph(new RegularFile("graphs/queen8_12.col"));
		if(!g.containsVertex(0) && g.containsVertex(1)) {//vertex indexing starting at 1, relabel max vertex to 0
			int maxV = g.getVertices().getGreatest();
			g.addVertex(0);
			IntSet mNeighbours = g.getNeighbours(maxV);
			for(int n: mNeighbours.toIntArray()) {
				g.addUndirectedSimpleEdge(0, n);
			}
			g.removeEdge(maxV);
		}
		
		
		NLCDP dp = new NLCDP(g);
		dp.setUpperBound(20);
		dp.setLowerBound(2);
		dp.start(); 
	}
}
