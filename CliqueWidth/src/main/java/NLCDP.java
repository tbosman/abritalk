
import grph.Grph;
import grph.algo.topology.GridTopologyGenerator;
import grph.algo.topology.PetersonGraph;
import grph.in_memory.InMemoryGrph;
import toools.set.DefaultIntSet;
import toools.set.IntSet;
import toools.set.IntSets;

public class NLCDP {
	
	
	private Grph g;
	private int UB;
	private boolean isUBSet;
	private int LB;
	private boolean isLBSet;
	
	

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
		for(int i=from; i<=to; i++){
			minWidth = Math.min(minWidth, minWidthsPerCardinality[i]);
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
		
		while(subsetIterator.hasNext()){			
			
			
			
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
				continue;
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
					
					int w1 = widths.get(IntStringTools.intsetToInt(AtomicCore.getAtomicCore(subsubSet,g)));
					int w2 = widths.get(IntStringTools.intsetToInt(AtomicCore.getAtomicCore(subsubSetCompl,g)));
					if(isUBSet){					
						if(w1 > this.UB || w2 > this.UB){
							continue;
						}
					}
					int mu = new GreedySplit().getSplitWidth(subsubSet,  subsubSetCompl, g);
//					int w1 = widths.get(IntStringTools.intsetToInt(subsubSet));
//					int w2 = widths.get(IntStringTools.intsetToInt(subsubSetCompl));
					int width = Math.max(mu, w1);
					width = Math.max(width, w2);
					if(width < widths.get(curSetString)){
						if(width<32 && curSet.size()>9){
							System.out.println("Set: "+curSet+"width: "+width);
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
					int solwidth = Math.max(widths.get(IntStringTools.intsetToInt(AtomicCore.getAtomicCore(curSet,g))),IntStringTools.intsetToInt(AtomicCore.getAtomicCore(curSetCompl,g)));
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
				if(widths.get(curSetString) < minWidthPerCardinality[curSet.size()]){
					minWidthPerCardinality[curSet.size()] = widths.get(curSetString);
				}
				
				if(this.UB<= this.LB) {
					widths.set(IntStringTools.intsetToInt(g.getVertices()), this.UB);
					break;
				}
			}
			
			
			
		}
		
		int nlcwidth = widths.get((1 << g.getVertices().size())-1);
		int V = (1 << g.getVertices().size())-1;
		System.out.println(Integer.toBinaryString(V));
		System.out.println(IntStringTools.intToIntSet(V));
		System.out.println("final width: "+nlcwidth);
		
	}
	
	public static void main(String... args){
		Grph g;
//		g = new PetersonGraph().petersenGraph(5,2);
//		g = new ChvatalGenerator().chvatalGenerator();
//		g = new Paley13Generator().paley13Generator();
//		g = new MCGeeGenerator().run();
		g = new FlowerSnarkGenerator().run(5);
//		GridTopologyGenerator GT = new GridTopologyGenerator();
//		GT.setHeight(4);
//		GT.setWidth(4);
//		g= new InMemoryGrph();
//		GT.compute(g);
		NLCDP dp = new NLCDP(g);
		dp.setUpperBound(8);
		dp.setLowerBound(2);
		dp.start(); 
	}
}
