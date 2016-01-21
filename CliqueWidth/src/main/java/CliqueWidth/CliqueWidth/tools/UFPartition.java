package CliqueWidth.CliqueWidth.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Simple Union-Find implementation
 * @author tbn530
 *
 */
public class UFPartition<T> {
	
	private class Node<T>{
		private final T e; 
		Node<T> parent;
		private int subTreeSize = 1; 
		Node(T e){
			this.e = e;
			this.parent = this; 
		}
		public Node<T> getParent() {
			return parent;
		}
		
		public Node<T> getRoot(){
			if(getParent().equals(this)){
				return this;
			}else{				
				this.parent = parent.getRoot();
				return this.parent;
			}
		}
		public void setParent(Node<T> parent) {
			this.parent = parent;
			parent.subTreeSize += this.subTreeSize;
		}
		public T getE() {
			return e;
		}
		public int getSubTreeSize() {
			return subTreeSize;
		}
		
		public void treeShrunk(int numRemoved) {
			subTreeSize -= numRemoved;
			assert subTreeSize >0; 
			if(parent!=this)parent.treeShrunk(numRemoved);
		}

		
		
	}
	
	HashMap<T, Node<T>> nodes = new HashMap<T, Node<T>>();
	
	public void makeSet(T e){
		// TODO assert no double elements
		nodes.put(e, new Node<T>(e));
	}
	
	public void makeSets(Collection<T> eArr) {
		for(T e : eArr) {
			makeSet(e);
		}
	}
	
	public void remove(T e) {
		Node<T> node = getNode(e);
		node.parent.treeShrunk(1);
//		node.parent = node;
		nodes.remove(node.getE());
		
		
	}

	
	public T find(T e){
		Node<T> node = getNode(e); 
		if(node == null){
			//TODO throw exception
		}
		return node.getRoot().getE();
	}
	
	private Node<T> getNode(T e){
//		Iterator<UFPartition<T>.Node<T>> it = nodes.iterator();
//		Node<T> node;
//		while(it.hasNext()){
//			node = it.next();
//			if(node.getE().equals(e)){ 
//				return node;
//			}
//		}
		if(nodes.containsKey(e)) {
			return nodes.get(e);
		}
		return null; 
	}
	
	public void union(T e1, T e2){	
		if(getNode(e1).getRoot() == getNode(e2).getRoot()) {
			return;
		}
		Node<T> b1 = getNode(e1); 
		getNode(e2).getRoot().setParent(b1);			
	}
	public void union(T... eArr) {
		Node<T> b1 = getNode(eArr[0]);
		for(int i=1;i<eArr.length;i++) {
			getNode(eArr[i]).setParent(b1);
		}
		
	}
	
	public int size(T e){
		if(getNode(e) == null){
			System.out.println("wtf");
		}
		return getNode(e).getRoot().getSubTreeSize();
	}

	public static <U> UFPartition<U> fromElement(U e){
		UFPartition<U> p = new UFPartition<U>();
		p.makeSet(e);
		return p;
	}
	
	/**
	 * Creates new partition by disjoint union of this and p2, elements are cloned
	 * @param p2
	 * @return
	 */
	public void mergeDisjoint(UFPartition<T> p2){
		p2 = p2.clone();
		for(Node<T> n : p2.nodes.values()) {
			assert !nodes.containsKey(n.getE());
			nodes.put(n.getE(), new Node<T>(n.getE()));
		}
		for(Node<T> n : p2.nodes.values()) {
			Node<T> newNode = getNode(n.getE());
			Node<T> newNodeRoot = getNode(n.getRoot().getE());
			newNode.setParent(newNodeRoot);
			nodes.put(n.getE(), new Node<T>(n.getE()));
		}
		
	}
	/**
	 * Deep copy
	 */
	public UFPartition<T> clone(){
		UFPartition<T> np = new UFPartition<T>();
		for(Node<T> n : this.nodes.values()) {
			np.nodes.put(n.getE(), new Node<T>(n.getE()));
		}
		for(Node<T> n : this.nodes.values()) {
			Node<T> newNode = np.getNode(n.getE());
			Node<T> newNodeRoot = np.getNode(n.getRoot().getE());
			newNode.setParent(newNodeRoot);
			np.nodes.put(n.getE(), new Node<T>(n.getE()));
		}
		return np;
	}
	
	public String toString(){ 
		String out = "";
		for(Node n : nodes.values()){
			out += "n: "+n.getE()+",p: "+n.getParent().getE()+", r:"+n.getRoot().getE()+"/ ";
		}
		return out;
	}
	
	
	public ArrayList<T> getBlock(T e){
		T root = find(e);
		ArrayList<T> block = new ArrayList<T>();
		for(Node<T> node : nodes.values()){
			if(node.getRoot().e.equals(root)){
				block.add(node.getE());
			}
		}
		return block;
		
	}
	
	public String toMathString(){//String in set notation 
		HashMap<T, ArrayList<T>> blocksByIdx = new HashMap<T, ArrayList<T>>(); 
		
		for(Node<T> node : nodes.values()){
			if(!blocksByIdx.containsKey(node.getRoot().e)){
				blocksByIdx.put(node.getRoot().e, new ArrayList<T>());
				
			}
			blocksByIdx.get(node.getRoot().e).add(node.e);
		}
		
		
		
		String mathString = "{";
		
		for(ArrayList<T> block : blocksByIdx.values()){
			mathString += "{"+block.get(0);
			for(int i =1; i< block.size();i++){
				mathString +=", "+block.get(i);
			}
			mathString += "}"; 
					
		}
		
		 mathString += "}";
		
		return mathString;
	}
	
	public ArrayList<T> getRoots() {
		HashSet<T> roots = new HashSet<T>();
		for(T node : nodes.keySet()) {
			roots.add(find(node));
		}
		return new ArrayList<T>(roots);
	}
	
	public Collection<T> values(){
//		ArrayList<T> v
//		return nodes.values();
		return null;
		//TODO
	}
}
