package CliqueWidth.CliqueWidth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import CliqueWidth.CliqueWidth.tools.UFPartition;
import toools.set.DefaultIntSet;
import toools.set.IntSet;

public class PartitionTree {

	private HashMap<Integer, Node<UFPartition<Integer>>> leafs = new HashMap<Integer, Node<UFPartition<Integer>>>();
	
	private class IntPair{
		private final int first; 
		private final int second;
		/**
		 * @param first
		 * @param second
		 */
		public IntPair(int first, int second) {
			super();
			this.first = first;
			this.second = second;
		}
	}
	private class Node<T>{
		private final T e; 
		private Node<T> parent;
		private ArrayList<IntPair> stagedLabelMerge = new ArrayList<IntPair> ();//to execute when disjoint merged into parent
		Node(T e){
			this.e = e;
			setParent(this); 
		}
		public Node<T> getParent() {
			return parent;
		}
		public Node<T> getRoot(){
			if(getParent().equals(this)){
				return this;
			}else{
				return parent.getRoot();
			}
		}
		public void setParent(Node<T> parent) {
			this.parent = parent;
		}
		public T getE() {
			return e;
		}
		
	}
	
	public PartitionTree() {

	}
	
	public void createLeafs(IntSet vertices) {
		for(int i: vertices.toIntArray()) {	
			leafs.put(i, new Node<UFPartition<Integer>>(UFPartition.<Integer>fromElement(i)));
		}		
	}
	private void executeStagedLabelMerge(ArrayList<IntPair> stagedLabelMerge) {
		for(IntPair vPair : stagedLabelMerge) {
			int u = vPair.first;
			int v = vPair.second;
			labelMerge(u,v);
		}
	}
	
	public void stageLabelMerge(int u, int v) {
		if(!leafs.get(u).getRoot().equals(leafs.get(v).getRoot()) ) {
			throw new Error("Merging labels from different components");
		}
		Node<UFPartition<Integer>> comp =  leafs.get(u).getRoot();
		
		comp.stagedLabelMerge.add(new IntPair(u,v)); 
	}
	public void disjointMerge(IntSet vertices) {
		Set<Node<UFPartition<Integer>>> rootNodes = new HashSet<Node<UFPartition<Integer>>>();
		for(int i : vertices.toIntArray()) {
			rootNodes.add(leafs.get(i).getRoot());
		}
		UFPartition<Integer> newPartition = new UFPartition<Integer>();
		Node<UFPartition<Integer>> newNode = new Node<UFPartition<Integer>>(newPartition);
		for(Iterator<Node<UFPartition<Integer>>> cNodeIt = rootNodes.iterator(); cNodeIt.hasNext();) {
			
			Node<UFPartition<Integer>> cNode = cNodeIt.next();
			cNode.setParent(newNode);
			if(cNode.stagedLabelMerge.size()>0) {
				newNode.stagedLabelMerge.addAll(cNode.stagedLabelMerge);				
			}
			newNode.getE().mergeDisjoint(cNode.getE());
			
		}
		executeStagedLabelMerge(newNode.stagedLabelMerge);
	}
	
	public void labelMerge(int u, int v) {
		if(!leafs.get(u).getRoot().equals(leafs.get(v).getRoot()) ) {
			throw new Error("Merging labels from different components");
		}
		Node<UFPartition<Integer>> comp =  leafs.get(u).getRoot();
		comp.getE().union(u, v);
	}
	

}
