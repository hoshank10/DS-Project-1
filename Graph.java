// Name : Hoshank Mali
// ID : 801254416
// Graph.java

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Scanner;

class GraphException extends RuntimeException {
	public GraphException(String name) {
		super(name);
	}
}

// Class to represent a Vertex
class Vertex {
	public String name;
	public List<Edge> adj; // list of adjacent vertices
	public Vertex prev; // maintain previous vertex for finding shortest path
	public boolean status; // maintain if a vertex is available or not
	public Double dist; // distance to reach a vertex
	public boolean visited; // maintain if vertex is already visited during reachable

	public Vertex(String nm) {
		name = nm;
		adj = new LinkedList<Edge>();
		reset();
		status = true;
		dist = Graph.INFINITY;
		visited = false;
	}

	public void reset() {
		dist = Graph.INFINITY;
		prev = null;
		visited = false;
	}

//get set methods
	public boolean getStatus() {
		return this.status;
	}

	public boolean isVisited() {
		return this.visited;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public double getDist() {
		return this.dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}
}

//class to represent a Edge
class Edge {
	public double cost; // cost of an edge
	public Vertex dest; // destination of an edge
	public boolean status; // maintain if an edge is available or not

	public Edge(Vertex dest, double cost) {
		this.dest = dest;
		this.cost = cost;
		this.status = true;
	}

//get set methods
	public String getDest() {
		return dest.name;
	}

	public double getCost() {
		return cost;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean st) {
		this.status = st;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
}

//class Graph
public class Graph {
	public static final Double INFINITY = Double.MAX_VALUE;
	private Map<String, Vertex> vertexMap = new HashMap<String, Vertex>(); // Map of vertices
	boolean flag = true;

	// Add new edge to graph
	public void addEdge(String sourceName, String destName, double cost) {
		Vertex v = getVertex(sourceName); // create source vertex if does not exist
		Vertex w = getVertex(destName); // create destination vertex if does not exist

		Iterator<Edge> listIterator = v.adj.listIterator();
		int flag = 0;
		while (listIterator.hasNext()) {
			Edge edge = listIterator.next();
			if (w.name.equals(edge.dest.name)) { // check if edge already exists
				edge.cost = cost; // update cost of already existing edge
				flag = 1;
			}
		}
		if (flag == 0) // Edge does not exist
			v.adj.add(new Edge(w, cost));
	}

	// Dijktras Algorithm
	public void Dijktras(String source, String dest) {
		clearAll(); // reset all vertices
		PriorityQueue<Vertex> pq = new PriorityQueue<>(new comp()); // min heap priority queue
		List<String> visited = new ArrayList<String>();
		Vertex start = vertexMap.get(source);
		if (start == null) { // source vertex does not exist in the graph
			System.out.println("Source vertex not found");
			return;
		} else if (!start.getStatus()) { // source vertex is down
			System.out.println(start.name + " is down");
			return;
		} else {
			// set distance of source vertex to 0 and previous as null
			start.dist = Double.valueOf(0);
			start.prev = null;
		}
		pq.add(start);
		while (!pq.isEmpty()) {
			Vertex v = pq.remove();
			visited.add(v.name);
			ListIterator<Edge> i = v.adj.listIterator();
			while (i.hasNext()) {
				Edge e = (Edge) i.next();
				if (e.getStatus()) {
					String sDesti = e.getDest();
					Vertex vDestn = vertexMap.get(sDesti);
					if (vDestn.getStatus()) {
						if (vDestn.getDist() > (v.getDist() + e.getCost())) {
							vDestn.setDist(v.getDist() + e.getCost()); // update distance of vertex
							vDestn.prev = v; // update previous of vertex
							System.out.println(vDestn.name + " " + vDestn.prev.name);
						}
						if (!visited.contains(vDestn.name) && !pq.contains(vDestn)) {
							pq.add(vDestn);
						}
					}
				}
			}
		}
		printPath(dest);
		if (flag) {// print cost only if printPath executes correctly
			double fc = vertexMap.get(dest).getDist();
			BigDecimal bd = new BigDecimal(fc).setScale(2, RoundingMode.HALF_UP);
			System.out.print(" " + bd);
			System.out.println();
		}
	}

	public void reachable() {
		TreeSet<String> treeSet = new TreeSet<String>();
		System.out.println();
		for (String key : vertexMap.keySet()) { // add available vertices to treeset
			if (vertexMap.get(key).getStatus())
				treeSet.add(key);
		}
		Iterator<String> i = treeSet.iterator();
		while (i.hasNext()) {
			String vertex = i.next();
			System.out.println(vertex);
			breadthFirstSearch(vertex);
		}
	}

	// Breadth First Search algorithm to print all reachable vertices from a given
	// vertex
	// BFS gives time complexity O(V+E)
	// Running for V vertices. total time complexity is O(V(V+E))
	public void breadthFirstSearch(String vertex) {
		clearAll(); // reset all vertices
		TreeSet<String> treeSet = new TreeSet<String>();
		PriorityQueue<String> pq = new PriorityQueue<String>();
		Vertex v = vertexMap.get(vertex);
		v.visited = true; // set visited as true for this vertex
		pq.add(v.name);
		while (pq.size() > 0) {
			String ve = pq.remove();
			ListIterator<Edge> i = vertexMap.get(ve).adj.listIterator();
			while (i.hasNext()) {
				Edge e = (Edge) i.next();
				if (e.getStatus() && (!e.dest.isVisited()) && (e.dest.getStatus())) {
					treeSet.add(e.getDest());
					pq.add(e.getDest());
					e.dest.setVisited(true);
				}
			}
		}
		for (String s : treeSet) {
			System.out.println(" " + s);
		}
	}

	// print all vertices and edges
	public void print() {
		TreeSet<String> treeSet = new TreeSet<String>();
		System.out.println();
		for (String key : vertexMap.keySet()) {
			treeSet.add(key);
		}
		Iterator<String> i = treeSet.iterator();
		while (i.hasNext()) {
			String s = i.next();
			System.out.print(s);
			if (!vertexMap.get(s).getStatus())
				System.out.print(" DOWN"); // print DOWN if vertex is down
			System.out.println();
			TreeSet<String> adj = new TreeSet<String>();
			HashMap<String, Double> cost = new HashMap<String, Double>(); // maintain vertex name and cost of edge
			HashMap<String, Boolean> status = new HashMap<String, Boolean>(); // maintain vertex name and status of edge
			for (Edge e : vertexMap.get(s).adj) {
				adj.add(e.dest.name);
				cost.put(e.dest.name, e.cost);
				status.put(e.dest.name, e.status);
			}
			for (String si : adj) {
				System.out.print("  " + si + " " + cost.get(si)); // print all the adjacent vertex in the current vertex
																	// list
				if (status.get(si) == false)
					System.out.print(" DOWN");
				System.out.println();
			}
		}
	}

	private Vertex getVertex(String vertexName) {
		Vertex v = vertexMap.get(vertexName);
		if (v == null) {
			v = new Vertex(vertexName); // add new vertex if vertex is null
			vertexMap.put(vertexName, v);
		}
		return v;
	}

	// Vertex Down
	public void vertexDown(String v) {
		Vertex ve = vertexMap.get(v);
		if (ve == null) {
			System.out.println("vertex not found");
			return;
		}
		if (ve.status) {
			ve.setStatus(false);
		} else {
			System.out.println(ve.name + " is already Down");
		}
	}

	// Vertex Up
	public void vertexUp(String v) {
		Vertex ve = vertexMap.get(v);
		if (ve == null) {
			System.out.println("vertex not found");
			return;
		}
		if (!ve.status) {
			ve.setStatus(true);
		} else {
			System.out.println(ve.name + " is already Up");
		}
	}

	// Edge Down
	public void edgeDown(String src, String dest) {
		Vertex s = vertexMap.get(src);
		Vertex d = vertexMap.get(dest);
		boolean done = false;
		if (s == null || d == null) {
			System.out.println("Source/Destination vertex not found");
			return;
		}
		ListIterator<Edge> i = s.adj.listIterator();
		while (i.hasNext()) {
			Edge e = (Edge) i.next();
			if (e.getDest().equals(dest)) {
				if (e.getStatus()) {
					e.setStatus(false);
					done = true;
				} else {
					System.out.println("Edge is already Down");
					done = true;
				}
			}
		}
		if (!done) {
			System.out.println("No such Edge exists");
		}
	}

	// Edge Up
	public void edgeUp(String src, String dest) {
		Vertex s = vertexMap.get(src);
		Vertex d = vertexMap.get(dest);
		boolean done = false;
		if (s == null || d == null) {
			System.out.println("Source/Destination vertex not found");
			return;
		}
		ListIterator<Edge> i = s.adj.listIterator();
		while (i.hasNext()) {
			Edge e = (Edge) i.next();
			if (e.getDest().equals(dest)) {
				if (!e.getStatus()) {
					e.setStatus(true);
					done = true;
				} else {
					System.out.println("Edge is already Up");
					done = true;
				}
			}
		}
		if (!done) {
			System.out.println("No such Edge exists");
		}
	}

	// Delete Edge
	public void deleteEdge(String src, String dest) {
		Vertex s = vertexMap.get(src);
		Vertex d = vertexMap.get(dest);
		boolean done = false;
		if (s == null || d == null) {
			System.out.println("Source/Destination vertex not found");
			return;
		}
		ListIterator<Edge> i = s.adj.listIterator();
		while (i.hasNext()) {
			Edge e = (Edge) i.next();
			if (e.getDest().equals(dest)) {
				s.adj.remove(e);
				done = true;
			}
		}
		if (!done) {
			System.out.println("No such Edge exists");
		}
	}

	// used to print path after Dijkstras Algorithm
	public void printPath(String destName) {
		Vertex w = vertexMap.get(destName);
		if (w == null) {
			flag = false;
			System.out.println("Destination vertex not found");
		} else if (w.dist == INFINITY) {
			System.out.println(destName + " is unreachable");
			flag = false;
		} else {
			printPath(w);
		}
	}

	private void printPath(Vertex dest) {
		if (dest.prev != null) {
			printPath(dest.prev);
			System.out.print(" to ");
		}
		System.out.print(dest.name);
	}

	/**
	 * Initializes the vertex output info prior to running any shortest path
	 * algorithm.
	 */
	private void clearAll() {
		for (Vertex v : vertexMap.values())
			v.reset();
	}

	/**
	 * Process a request; return false if end of file.
	 */
	public static boolean processRequest(Scanner in, Graph g) {
		try {
			String command, src, dest;
			Double cost;
			String query = in.nextLine();
			StringTokenizer tokens = new StringTokenizer(query);
			switch (tokens.countTokens()) {
			case 1:
				command = tokens.nextToken();
				if (command.equals("print")) {
					g.print();
				} else if (command.equals("reachable")) {
					g.reachable();
				} else if (command.equals("quit")) {
					System.exit(0);
				} else {
					System.out.println("Invalid Query");
				}
				break;

			case 2:
				command = tokens.nextToken();
				src = tokens.nextToken();
				if (command.equals("vertexdown")) {
					g.vertexDown(src);
				} else if (command.equals("vertexup")) {
					g.vertexUp(src);
				} else {
					System.out.println("Invalid Query");
				}
				break;

			case 3:
				command = tokens.nextToken();
				src = tokens.nextToken();
				dest = tokens.nextToken();
				if (command.equals("deleteedge")) {
					g.deleteEdge(src, dest);
				} else if (command.equals("edgedown")) {
					g.edgeDown(src, dest);
				} else if (command.equals("edgeup")) {
					g.edgeUp(src, dest);
				} else if (command.equals("path")) {
					g.Dijktras(src, dest);
				} else {
					System.out.println("Invalid Query");
				}
				break;

			case 4:
				command = tokens.nextToken();
				src = tokens.nextToken();
				dest = tokens.nextToken();
				cost = Double.parseDouble(tokens.nextToken());
				if (command.equals("addedge")) {
					g.addEdge(src, dest, cost);
				} else {
					System.out.println("Invalid Query");
				}
				break;

			default:
				System.out.println("Invalid Query");
				break;
			}
		} catch (NoSuchElementException e) {
			System.out.print(e);
			return false;
		} catch (GraphException e) {
			System.err.println(e);
		}
		return true;
	}

	public static void main(String[] args) {
		Graph g = new Graph();
		try {
			FileReader fin = new FileReader(args[0]);
			Scanner graphFile = new Scanner(fin);

			// Read the edges and insert
			String line;
			while (graphFile.hasNextLine()) {
				line = graphFile.nextLine();
				StringTokenizer st = new StringTokenizer(line);

				try {
					if (st.countTokens() != 3) {
						System.err.println("Skipping ill-formatted line " + line);
						continue;
					}
					String source = st.nextToken();
					String dest = st.nextToken();
					double cost = Double.parseDouble(st.nextToken());
					g.addEdge(source, dest, cost);
					g.addEdge(dest, source, cost);
				} catch (NumberFormatException e) {
					System.err.println("Skipping ill-formatted line " + line);
				}
			}

		} catch (IOException e) {
			System.err.println(e);
		}

		System.out.println("File read...");
		System.out.println(g.vertexMap.size() + " vertices");

		Scanner in = new Scanner(System.in);
		while (processRequest(in, g))
			;
	}

	class comp implements Comparator<Vertex> {

		@Override
		public int compare(Vertex o1, Vertex o2) {
			// TODO Auto-generated method stub
			return o1.dist.compareTo(o2.dist);
		}

	}
}
