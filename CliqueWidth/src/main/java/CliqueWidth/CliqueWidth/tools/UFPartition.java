package CliqueWidth.CliqueWidth.tools;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Simple Union-Find implementation
 * @author tbn530
 *
 */
public class UFPartition<T> {
	
	private class Node<T>{
		private final T e; 
		Node<T> parent;
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
	
	ArrayList<Node<T>> nodes = new ArrayList<Node<T>>();
	
	public void makeSet(T e){
		// TODO assert no double elements
		nodes.add(new Node<T>(e));
	}
	

	
	public T find(T e){
		Node<T> node = getNode(e); 
		if(node == null){
			//TODO throw exception
		}
		return node.getRoot().getE();
	}
	
	private Node<T> getNode(T e){
		Iterator<UFPartition<T>.Node<T>> it = nodes.iterator();
		Node<T> node;
		while(it.hasNext()){
			node = it.next();
			if(node.getE().equals(e)){
				return node;
			}
		}
		return null; 
	}
	
	public void Union(T e1, T e2){		
		getNode(e2).setParent(getNode(e1));	
		
	}


}
