package CliqueWidth.CliqueWidth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import toools.set.DefaultIntSet;
import toools.set.IntSet;

public class UnionTree {

	private int idCounter = -1; 
	private HashMap<Integer, Node> leafs;	
	
	private enum NodeType {LEAF, INNER};
	private class Node{
		private Node parent; 
		NodeType type; 
		final int id; 
		
		Node(int id){
			this.id = id;
			this.parent = this;
			type = NodeType.LEAF;			
		}
		
		Node(){
			this(idCounter++);
			type = NodeType.INNER;			
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}	
		
	}
	
	
	
	
	
	public void addLeaves(IntSet ids){
		idCounter = ids.getGreatest()+1;
		Node r = new Node();
		for(int v: ids.toIntArray()){
			Node n = new Node(v);
			n.setParent(r);
			leafs.put(v, n);			
		}		
		
	}
	
	private Stack<Node> parentStack(Node node){
		Stack<Node> parentStack =  new Stack<Node>();
		Node cnode = node; 
		while(cnode.parent != cnode){
			cnode = cnode.parent; 
			parentStack.push(cnode);
		}
		return parentStack;
	}
	
	private Node getLeaf(int v){
		return leafs.get(v);
	}
	
	//create new child below lowest common ancestor of leafs and attach subtrees containing leafs to it 
	public void makeCommonAncestor(IntSet leafs){
		ArrayList<Stack<Node>> parentStacks = new ArrayList<Stack<Node>>(); 
	
		for(int v : leafs.toIntArray()){
			parentStacks.add(parentStack(getLeaf(v)));
		}
		
		Node oldCA = parentStacks.get(0).peek();
		HashSet<Node> subRoots = new HashSet<Node>(); 
		boolean leastCANotFound = true;		
		while(leastCANotFound){
			subRoots.clear();
			for(Stack<Node> pStack : parentStacks){
				subRoots.add(pStack.pop());				
			}
			if(subRoots.size()>1){
				leastCANotFound = false;
			}else{
				oldCA = subRoots.iterator().next();
			}			
		}
		
		//reattach subtrees rooted subRoots to new common ancestors
		Node newCA = new Node(); 
		newCA.setParent(oldCA);
		for(Node n:subRoots){
			n.setParent(newCA);
		}
		
		
	
		
		
	}
	
}
