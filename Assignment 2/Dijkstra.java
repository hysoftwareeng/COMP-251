//Hao Ge (Harvey) Yang
//260465315


package a2posted;

import java.util.HashMap;
import java.util.HashSet;


public class Dijkstra {

	private IndexedHeap  pq;	
	private static int edgeCount = 0;               //  Use this to give names to the edges.										
	private HashMap<String,Edge>  edges = new HashMap<String,Edge>();

	private HashMap<String,String>   parent;
	private HashMap<String,Double>   dist;  //  This is variable "d" in lecture notes
	private String 					 startingVertex;	
	
	HashSet<String>  setS      ;
	HashSet<String>  setVminusS;

	public Dijkstra(){
		pq    		= new IndexedHeap()  ;		
		setS        = new HashSet<String>();
		setVminusS  = new HashSet<String>();		
		parent  = new HashMap<String,String>();
		dist 	= new HashMap<String,Double>();
	}
	
	/*
	 * Run Dijkstra's algorithm from a vertex whose name is given by the string s.
	 */
	
	public void dijkstraVertices(Graph graph, String s){
		
		//  temporary variables
		
		String u;	
		double  distToU,
				costUV;		
		
		HashMap<String,Double> uAdjList;		
		initialize(graph,s);
		
		parent.put( s, null );
		pq.add(s, 0.0);   // shortest path from s to s is 0.
		this.startingVertex = s;

		//  --------- BEGIN: ADD YOUR CODE HERE  -----------------------		
		while (pq.isEmpty() == false){
			u = pq.removeMin();
			setS.add(u);         //add u to set S
			setVminusS.remove(u);  //remove u from set V/S
			
			//Need to then visit each edge existing vertex_u
			uAdjList = graph.getAdjList().get(u);  //store all the adjacent edges into the adjacency list
			for (String v : uAdjList.keySet()){
				if (setVminusS.contains(v)) {
					costUV = uAdjList.get(v);     //gets the cost of UV
					distToU = dist.get(u) + costUV;   
					
					if (!pq.contains(v)){     //if the vertex v is not contained in the priority queue, add it and make it equal to infinity
						pq.add(v, Double.POSITIVE_INFINITY);
					}
					
					if (distToU < pq.getPriority(v)){  //compare distance, if the distance of new path is smaller, update v's parent to be u
						pq.changePriority(v, distToU); //and update the priority queue
						dist.put(v, distToU);
						parent.put(v, u);
					}
				}
			}
		}
		//  --------- END:  ADD YOUR CODE HERE  -----------------------
	}
	
	
	public void dijkstraEdges(Graph graph, String startingVertex){

		//  Makes sets of the names of vertices,  rather than vertices themselves.
		//  (Could have done it either way.)
		
		//  temporary variables
		
		Edge e;
		String u;
		String v;
		double tmpDistToV;
		
		initialize(graph, startingVertex);

		//  --------- BEGIN: ADD YOUR CODE HERE  -----------------------
		pqAddEdgesFrom(graph,startingVertex);  //first add all the adjancet edges leaving starting vertex s. 
		
		while (pq.isEmpty() == false){
			tmpDistToV = pq.getMinPriority();  //this gets the current shortest path to v
			String minEdge = pq.removeMin();  //removes the edge with smallest distance
			e = edges.get(minEdge);           
			u = e.getStartVertex();
			v = e.getEndVertex();
				
			if ((setS.contains(v) == false)) {   //check to see both vertices are not in set S, hence the edge is a crossing edge
				setS.add(v);					 //add to set S and remove from setVminusS.
				setVminusS.remove(v);
				parent.put(v,u);                 //make v's parent equal to u.
				dist.put(v, tmpDistToV);         //update the distance
				pqAddEdgesFrom(graph, v);        //now we add to the priorty queue all the edges leaving v. 
			}
		}
		//  --------- END:  ADD YOUR CODE HERE  -----------------------

	}
	
	/*
	 *   This initialization code is common to both of the methods that you need to implement so
	 *   I just factored it out.
	 */

	private void initialize(Graph graph, String startingVertex){
		//  initialization of sets V and VminusS,  dist, parent variables
		//

		for (String v : graph.getVertices()){
			setVminusS.add( v );
			dist.put(v, Double.POSITIVE_INFINITY);
			parent.put(v, null);
		}
		this.startingVertex = startingVertex;

		//   Transfer the starting vertex from VminusS to S and  

		setVminusS.remove(startingVertex);
		setS.add(startingVertex);
		dist.put(startingVertex, 0.0);
		parent.put(startingVertex, null);
	}

    /*  
	 *  helper method for dijkstraEdges:   Whenever we move a vertex u from V\S to S,  
	 *  add all edges (u,v) in E to the priority queue of edges.
	 *  
	 *  For each edge (u,v), if this edge gave a shorter total distance to v than any
	 *  previous paths that terminate at v,  then this edge will be removed from the priority
	 *  queue before these other vertices. 
	 *  
	 */
	
	public void pqAddEdgesFrom(Graph g, String u){
		double distToU = dist.get(u); 
		for (String v : g.getAdjList().get(u).keySet()  ){  //  all edges of form (u, v) 
			
			edgeCount++;
			Edge e = new Edge( edgeCount, u, v );
			edges.put( e.getName(), e );
			pq.add( e.getName() ,  distToU + g.getAdjList().get(u).get(v) );
		}
	}

	// -------------------------------------------------------------------------------------------
	
	public String toString(){
		String s = "";
		s += "\nRan Dijkstra from vertex " + startingVertex + "\n";
		for (String v : parent.keySet()){
			s += v + "'s parent is " +   parent.get(v) ;
			s += "   and pathlength is "  + dist.get(v) + "\n" ;
		}
		return s;
	}

	//  This class is used only to keep track of edges in the priority queue. 
	
	private class Edge{
		
		private String edgeName;
		private String u, v;
		
		Edge(int i, String u, String v){
			this.edgeName = "e_" + Integer.toString(i);
			this.u = u;
			this.v = v;
		}
		
		public String getName(){
			return edgeName;
		}
		
		String getStartVertex(){
			return u;
		}

		String getEndVertex(){
			return v;
		}
		
		public String toString(){
			return 	edgeName + " : " + u + " " + v;
		}
	}
	

}
