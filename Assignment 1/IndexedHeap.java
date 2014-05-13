//NAME: Hao Ge (Harvey) Yang
//ID:   260465315

package a1posted;
import java.util.ArrayList;
import java.util.HashMap;

/*
 *   Here is the posted code for Assignment 1 in COMP 251  Winter 2014.
 * 
 *   Written by Michael Langer.
 *   This basic heap implementation is a modified version of Wayne and Sedgewick's code 
 *   (from their book, see link from their Coursera Algorithms course website).
 *   See other comments at the top of the Heap.java class.
 */

public class IndexedHeap{   

	private ArrayList<Double>    priorities;
	private ArrayList<String>  	 names;     //   Think of this as a map:  indexToNames

	/*  
	 * 	This is not just a heap;  it is an indexed heap!  To index directly into the heap,
	 *  we need a map. 
	 */
	
	private HashMap<String,Integer>  nameToIndex;    

	// constructor

	public IndexedHeap(){
		
		//  A node in the heap keeps track of a object name and the priority of that object. 
		
		names = new ArrayList<String>();
		priorities = new ArrayList<Double>();

		/*
		 * Fill the first array slot (index 0) with dummy values, so that we can use usual 
		 * array-based heap parent/child indexing.   See my COMP 250 notes if you don't know 
		 * what that means.
		 */
								   
		names.add( null );    	
		priorities.add( 0.0 );      

		//  Here is the map that we'll need when we want to change the priority of an object.
		
		nameToIndex  = new HashMap<String,Integer>();
	}

	private int parent(int i){     
		return i/2;
	}
	    		
	private int leftChild(int i){ 
	    return 2*i;
	}
	
	private int rightChild(int i){ 
	    return 2*i+1;
	}
	
	private boolean is_leaf(int i){
		return (leftChild(i) >= priorities.size()) && (rightChild(i) >= priorities.size());
	}
	
	private boolean oneChild(int i){ 
	    return (leftChild(i) < priorities.size()) && (rightChild(i) >= priorities.size());
	}
	
	/* 
	 *  The upHeap and downHeap methods use the swap method which you need to implement.
	 */
	
	private void upHeap(int i){
		if (i > 1) {   // min element is at 1, not 0
			if ( priorities.get(i) < priorities.get(parent(i)) ) {

				swap(parent(i),i);
				upHeap(parent(i));
			}
		}
	}

	private void downHeap(int i){

		// If i is a leaf, heap property holds
		if ( !is_leaf(i)){

			// If i has one child...
			if (oneChild(i)){
				//  check heap property
				if ( priorities.get(i) > priorities.get(leftChild(i)) ){
					// If it fails, swap, fixing i and its child (a leaf)
					swap(i, leftChild(i));
				}
			}
			else	// i has two children...

				// check if heap property fails i.e. we need to swap with min of children

				if  (Math.min( priorities.get(leftChild(i)), priorities.get(rightChild(i))) < priorities.get(i)){ 

					//  see which child is the smaller and swap i's value into that child, then recurse

					if  (priorities.get(leftChild(i)) < priorities.get(rightChild(i))){
						swap(i,   leftChild(i));
						downHeap( leftChild(i) );
					}
					else{
						swap(i,  rightChild(i));
						downHeap(rightChild(i));
					}
				}
		}
	}	

	public boolean contains(String name){
		if (nameToIndex.containsKey( name ))
			return true;
		else
			return false;
	}
	
	public int sizePQ(){
		return priorities.size()-1;   //  not to be confused with the size() of the underlying ArrayList, which included a dummy element at 0
	}

	public boolean isEmpty(){
		return sizePQ() == 0;   
	}
	
	public double getPriority(String name){
		if  (!contains( name ))
			throw new IllegalArgumentException("nameToIndex map doesn't contain key " + String.valueOf(name));
		return priorities.get( nameToIndex.get(name) );	
	}
	
	public double getMinPriority(){
		return priorities.get(1);	
	}

	public String nameOfMin(){
		return names.get(1);
	}

	/*
	 *   Implement all methods below
	 */
	
	/*
	 *   swap( i, j) swaps the values in the nodes at indices i and j in the heap.   
	 */

	private void swap(int i, int j){

		/* This then swaps the object names in the names arraylist */
		String interim;
		interim = names.get(i);
		names.set(i, names.get(j));
		names.set (j, interim);
		
		
		/* This then swaps the priorities in the priorities arraylist */
		double temporary;
		temporary = priorities.get(i);
		priorities.set(i, priorities.get(j));
		priorities.set(j, temporary);
		
		
		
		/*Updates the HashMap nameToIndex */
		String name1 = names.get(i);
		String name2 = names.get(j);
		
		int temporaryIndex1 = nameToIndex.get(name1);
		int temporaryIndex2 = nameToIndex.get(name2);
		
		nameToIndex.put(name1,temporaryIndex2);
		nameToIndex.put(name2,temporaryIndex1); 
		
		

	}

	
	
	//  returns (and removes) the name of the element with lowest priority value, and updates the heap
	
	public String removeMin(){
		if (sizePQ()==0) throw new IndexOutOfBoundsException ("Priority queue underflow");
		if (priorities.size() > 2){  //  we have more than one element in the heap
			String minimum = nameOfMin(); //stores the minimum string
			names.set(1, names.remove(names.size() -1)); //sets the last element of the ArrayList to priority 1
			priorities.set(1,  priorities.remove( priorities.size() - 1 )); //does the same for the priorities ArrayList
			nameToIndex.put(minimum,1); //updates the map
			downHeap(1); //updates the heap
			return minimum;
		}
		else {//  priorities.size == 2  i.e.  it can't be less than 2 since we would then be asking to remove the dummy element at 0.
			priorities.remove(1);
			String oldMin = names.get(1);
			names.remove(1);
			return oldMin;
		}
	}	

	/*
	 * There are two add methods.  The first assumes a specific priority.  That's the one
	 * you need to implement.   The second gives a default priority of Double.POSITIVE_INFINITY	  
	 */
	
	public void  add(String name, double priority){

		if  (contains( name ))
			throw new IllegalArgumentException("Trying to add " + String.valueOf(name) + ", but its already there.");

			priorities.add(new Double(priority)); //adds an element space to the end of the ArrayList
			int sizeOfArrayList = priorities.size(); //gets the size of the ArrayList
			priorities.set(sizeOfArrayList-1, priority); //sets the element just added to the priority
			nameToIndex.put(name, sizeOfArrayList-1); //puts the new name and priority into the nameToIndex map
			names.add(new String(name));  //adds an element space to the end of the ArrayList
			names.set(sizeOfArrayList-1,name); //sets the element just added to the string name
			upHeap(sizeOfArrayList-1); //updates the heap
		}
		
		//----------------------- ADD YOUR CODE HERE  ----------------------------
		
	
	public void  add(String name){
		add(name, Double.POSITIVE_INFINITY);
	}

	/*
	 *   If new priority is different from the current priority then change the priority (and possibly modify the heap). 
	 *   If the name is not there, then throw an exception.
	 */
	
	public void changePriority(String name, double priority){

		if  (!contains( name )) //throws exception if priority of string is not there
			throw new IllegalArgumentException("Trying to change priority of " + String.valueOf(name) + ", but its not there.");
		int index= names.indexOf(name); //gets the index of the existing string from the names ArrayList
		priorities.set(index, priority); //sets priority of the index element in the priority ArrayList to the new priority
		names.set(index, name); //sets the name of the element to the new String name, not particularly useful
		nameToIndex.put(name, index); 
		upHeap(index);
		downHeap(index);
		

		//-----------------------  ADD YOUR CODE HERE ----------------------------

	} 
	
}
