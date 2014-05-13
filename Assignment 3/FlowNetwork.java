//Hao Ge (Harvey) Yang
//260465315

package a3posted;

import java.util.LinkedList;
import java.util.HashSet;

public class FlowNetwork {

	//   The data structures follow what I presented in class.  Use three graphs which 
	//   represent the capacities, the flow, and the residual capacities.
	
	Graph capacities;      		// weights are capacities   (G)
	Graph flow;            		// weights are flows        (f)
	Graph residualCapacities;   // weights are determined by capacities (graph) and flow (G_f)
	
	//   Constructor.   The input is a graph that defines the edge capacities.
	
	public FlowNetwork(Graph capacities){
				
		this.capacities    = capacities;
		
		//  The flow and residual capacity graphs have the same vertices as the original graph.
		
		flow               = new Graph( capacities.getVertices() );
		residualCapacities = new Graph( capacities.getVertices() );
		
		//  Initialize the flow and residualCapacity graphs.   The flow is initialized to 0.  
		//  The residual capacity graph has only forward edges, with weights identical to the capacities. 

		for (String u : flow.getVertices()){
			for (String v : capacities.getEdgesFrom(u).keySet() ){
				
				//  Initialize the flow to 0 on each edge
				
				flow.addEdge(u, v, new Double(0.0));
				
				//	Initialize the residual capacity graph G_f to have the same edges and capacities as the original graph G (capacities).
				
				residualCapacities.addEdge(u, v, new Double( capacities.getEdgesFrom(u).get(v) ));
			}
		}
	}

	/*
	 * Here we find the maximum flow in the graph.    There is a while loop, and in each pass
	 * we find an augmenting path from s to t, and then augment the flow using this path.
	 * The beta value is computed in the augment method. 
	 */
	
	public void  maxFlow(String s,  String t){
		
		LinkedList<String> path;
		double beta;
		while (true){
			path = this.findAugmentingPath(s, t);
			if (path == null)
				break;
			else{
				beta = computeBottleneck(path);
				augment(path, beta);				
			}
		}	
	}
	
	/*
	 *   Use breadth first search (bfs) to find an s-t path in the residual graph.    
	 *   If such a path exists, return the path as a linked list of vertices (s,...,t).   
	 *   If no path from s to t in the residual graph exists, then return null.  
	 */
	
	public LinkedList<String>  findAugmentingPath(String s, String t){

		LinkedList<String> newAugmentPath = new LinkedList<String>();	//creates a LinkedList to store the path
		String source = s;		//source of path
		String terminal = t;	//the terminal of the path
		String parentVertex;	//parent vertex of the current
				
		residualCapacities.bfs(source);		//performs breadth first search from the source to organize the graph
		newAugmentPath.addFirst(terminal);	//add terminal first to the LinkedList since we are going from terminal to source
		
		String currentVertex = t;			//set current vertex to be the terminal, we will traverse back
		while (residualCapacities.getParent(currentVertex)!=null){	//while loop goes through all vertices as long as the vertex has a parent
			parentVertex = residualCapacities.getParent(currentVertex);	//get parent
			newAugmentPath.addFirst(parentVertex);	//add parent to ahead in the LinkedList since we are traversing back
			currentVertex = parentVertex;	//set the parent to be the new currentVertex
			if (parentVertex.equals(source)){	//if we have reached the source, terminate loop, since there is no more parent
				break;
			}
		}
		
		if (newAugmentPath.getFirst().equals(source)){	//check to see that the source has been reached, hence a valid path has been generated
			return newAugmentPath;
		}
		else {
			return null;
		}
	}
	
	/*
	 *   Given an augmenting path that was computed by findAugmentingPath(), 
	 *   find the bottleneck value (beta) of that path, and return it.
	 */
	
	public double computeBottleneck(LinkedList<String>  path){ //this works

		double beta = Double.MAX_VALUE;

		//  Check all edges in the path and find the one with the smallest weight in the
		//  residual graph.   This will be the new value of beta.

		String currentVertex;		//the current vertex
		String nextVertex;			//the next vertex in the path
		double capacityResidual;	//stores the capacity of the residual graph
		
		int first = 0;
		while (first < path.size()-1){
			currentVertex = path.get(first);	//gets the current vertex in the path
			int next = first+1;
			nextVertex = path.get(next);		//gets the next vertex in the path following first
			capacityResidual = residualCapacities.getEdgesFrom(currentVertex).get(nextVertex);	//gets the capacity weight from current to next
			
			beta = Math.min(beta, capacityResidual); //set bottleneck to be equal to the smallest residual capacity
			first = first+1;
		}
		return beta;
	}
	
	//  Once we know beta for a path, we recompute the flow and update the residual capacity graph.
	//  Once we know beta for a path, we recompute the flow and update the residual capacity graph.

	public void augment(LinkedList<String>  path,  double beta){
		String currentVertex;
		String nextVertex;
		
		for (int i = 0; i < path.size()-1; i++){
			
			double currentFlow = 0.0;	//gets the current flow weight
			double weightBackward = 0.0;	//backward edge			
			double weightForward = 0.0;		//forward edge
			double currentCapacity = 0.0;	//holds the capacity
			
			currentVertex = path.get(i);	//gets the current vertex
			nextVertex = path.get(i+1);		//gets the adjacent vertex following i in the path

			if (flow.getEdgesFrom(nextVertex).containsKey(currentVertex)){	//if this is true, backwards edge is present
				currentFlow = currentFlow - flow.getEdgesFrom(nextVertex).get(currentVertex);
				flow.addEdge(nextVertex, currentVertex, currentFlow+beta);	//backward edge added to flow
				weightForward = flow.getEdgesFrom(nextVertex).get(currentVertex);	//forward edge for residual
				currentFlow = flow.getEdgesFrom(nextVertex).get(currentVertex);
				currentCapacity = capacities.getEdgesFrom(nextVertex).get(currentVertex);
				weightBackward = currentCapacity - currentFlow;	//backward edge for residual
			}
			
			if (flow.getEdgesFrom(currentVertex).containsKey(nextVertex)){	//if currentVertex has adjacent nextVertex, we get the flow of the edge
				currentFlow = flow.getEdgesFrom(currentVertex).get(nextVertex);
				flow.addEdge(currentVertex, nextVertex, currentFlow+beta);	//add the edge to the flow after updating with bottleneck
				weightBackward = flow.getEdgesFrom(currentVertex).get(nextVertex);	//for residual graph
				currentFlow = flow.getEdgesFrom(currentVertex).get(nextVertex);
				currentCapacity = capacities.getEdgesFrom(currentVertex).get(nextVertex);	//gets the capacity
				weightForward = currentCapacity - currentFlow;	//the residual edge is updated by subtracting the new flow from capacity
			}

			residualCapacities.addEdge(currentVertex, nextVertex, weightForward);	//update forward capacity
			residualCapacities.addEdge(nextVertex, currentVertex, weightBackward);	//update backward capacity

			//we remove all edges that have weight 0
			if (currentFlow==0.0){	//no flow, remove both forward and backward
				flow.getEdgesFrom(currentVertex).remove(nextVertex);
				flow.getEdgesFrom(nextVertex).remove(currentVertex);
			}
			
			if (weightForward == 0.0){
				residualCapacities.getEdgesFrom(currentVertex).remove(nextVertex);
			}
			
			if (weightBackward == 0.0){
				residualCapacities.getEdgesFrom(nextVertex).remove(currentVertex);
			}
			
		}

	}
	//  This just dumps out the adjacency lists of the three graphs (original with capacities,  flow,  residual graph).
	
	public String toString(){
		return "capacities\n" + capacities + "\n\n" + "flow\n" + flow + "\n\n" + "residualCapacities\n" + residualCapacities;
	}
	
}