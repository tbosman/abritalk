package CliqueWidth.CliqueWidth;

import grph.Grph;
import grph.in_memory.InMemoryGrph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import CliqueWidth.CliqueWidth.UnionTree.NodeType;
import toools.set.DefaultIntSet;
import toools.set.IntSet;

public class UnionTree {

	private int idCounter = -1; 
	private HashMap<Integer, Node> leafs = new HashMap<Integer, Node>();	
	
	public enum NodeType {LEAF, INNER};
	public class Node{
		private Node parent; 
		NodeType type; 
		final int id; 
		
		
		
		private HashSet<Node> children = new HashSet<Node>(); 
		
		public HashSet<Node> getChildren() {
			return children;
		}

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
			if(this.parent!=this) {
				this.parent.children.remove(this);
			}
			this.parent = parent;
			parent.children.add(this);
		}	
		
		public boolean isAncestor(Node node) {
			if(node == this) {
				return true;
			}else if(this.parent == this) {
				return false; 
			}else {
				return this.parent.isAncestor(node);
			}
		}

		public NodeType getType() {
			return this.type;
		}

		public int getId() {
			return id;
		}

		public Node getRoot() {
			if(this.parent == this) {
				return this;
			}else {
				return this.parent.getRoot();
			}
			
		}
		
	}
		
	
	public ArrayList<Node> getInnerNodes(){
		HashSet<Node> activeInnerNodes = new HashSet<Node>();
		HashSet<Node> innerNodes = new HashSet<Node>();
		for(Node lNode : this.leafs.values()) {
			activeInnerNodes.add(lNode.parent);			
			innerNodes.add(lNode.parent);
		}
		
		while(activeInnerNodes.size()>0) {
			HashSet<Node> newNodes = new HashSet<Node>();
			for(Node n : activeInnerNodes) {
				if(!innerNodes.contains(n.getParent()) ) {
					newNodes.add(n.getParent());
					innerNodes.add(n.getParent());
				}
				
			}
			activeInnerNodes = newNodes;
		}
		return new ArrayList<Node>(innerNodes);
	}
	
	
	public IntSet getLeavesInSubtree(Node node) {
		IntSet leafSet = new DefaultIntSet(); 
		for(Node lNode : this.leafs.values()) {
			if(lNode.isAncestor(node)) {
				leafSet.add(lNode.id);
			}
		}
		return leafSet;
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
		parentStack.push(node);
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
		if(leafs.size()==1) {
			return;
		}
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
			if(subRoots.size()==1){
				leastCANotFound = true;
				oldCA = subRoots.iterator().next();
			}else{				
				leastCANotFound = false;
			}			
		}
		
		//reattach subtrees rooted subRoots to new common ancestors
		Node newCA = new Node(); 
		newCA.setParent(oldCA);
		for(Node n:subRoots){
			n.setParent(newCA);
		}
		
		
	
		
		
	}
	
	public Grph toGrph() {
		Grph g = new InMemoryGrph();
		for(int v : leafs.keySet()) {
			g.addVertex(v);
			g.getVertexColorProperty().setValue(v,2);
		}
		
		Node root = leafs.values().iterator().next().getRoot();	
		addChildrenToGrph(root, g);
		return g;
	}
	
	private void addChildrenToGrph(Node node, Grph g) {
		if(!g.containsVertex(node.getId()));
		HashSet<Node> children = node.getChildren();
		for(Node cNode : children) {
			if(!g.containsVertex(cNode.getId())) {
				g.addVertex(cNode.getId());
			}
			g.addDirectedSimpleEdge(node.getId(), cNode.getId());
			addChildrenToGrph(cNode, g);
		}
	}
}
