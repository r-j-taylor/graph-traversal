/*
 * Ryan Taylor
 * November 15, 2019
 */
 
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
 
public class GraphTraversal {
 
    public static void main(String[] args) {
        
        if (args.length != 1) {
            System.out.println("Wrong number of arguments! Please pass in the file name, and only the file name.");
            System.exit(-1);
        }
 
        // Setup input scanner
        File inputFile = new File(args[0]);
        Scanner scanner = null;
        try {
            scanner = new Scanner(inputFile);
            scanner.useDelimiter("\\s+");
        } catch (FileNotFoundException e) {
            System.out.println("Could not find \"" + args[0] + "\", please try a different file name.");
            System.exit(-1);
        }
 
        // Create input array
        ArrayList<String> input = new ArrayList<>();
        while (scanner.hasNext()) {
            input.add(scanner.next());
        }
        scanner.close();

        String[] rideInfo = input.toArray(new String[input.size()]);
        Hashtable<Character, GraphNode> graph = createGraph(rideInfo);

        System.out.print("\nYour itinerary is as follows: ");
        findItinerary(graph, rideInfo);
        System.out.println();

        System.exit(0);
    }
 
    private static void findItinerary(Hashtable<Character, GraphNode> graph, String[] input) {
        
        Character startingRide = '!';
        int maxTime = 0;
        for (String rideInfo : input) { // For each available ride,
            Character ride = rideInfo.charAt(0);
            int tempTime = findItineraryHelper(graph, ride, graph.get(ride)); // Find an itinerary with that ride as the head.
 
            if (tempTime > maxTime) { // If this new itinerary has the new longest ride time,
                // Save it.
                maxTime = tempTime;
                startingRide = ride;
            }
        }
 
        if (startingRide == '!') {
            System.out.println("\nThere was a problem creating your itinerary, sorry!\n");
            System.exit(-1);
        }
        System.out.println(graph.get(startingRide).sequence);
    }
 
    private static int findItineraryHelper(Hashtable<Character, GraphNode> graph, Character ride, GraphNode node) {
        
        if (node.mostTime != 0) { // If a best sequence has already been found for this ride,
            // Use it.
            return node.mostTime;
        }else if (node.connectedNodes.length == 0) { // Else, if this ride is a last possible ride,
            // Consider it alone to be its best sequence.
            node.mostTime = node.rideTime;
            node.sequence += ride;
            return node.mostTime;
        } else { // Else,
            int max = 0;
            
            for (Character availableRide : node.connectedNodes) { // For each ride after the given ride,
                GraphNode nextNode = graph.get(availableRide);
                int time = findItineraryHelper(graph, availableRide, nextNode); // Find the best sequence for the second ride.
                if (time > max) { // If the new itinerary has the longest ride time,
                    // Save it.
                    max = time;
                    node.sequence = ride + ", " + nextNode.sequence;
                }
            }
 
            // Add this ride's time to the total time and return.
            node.mostTime = max + node.rideTime;
            return node.mostTime;
        }
    }
 
    private static Hashtable<Character, GraphNode> createGraph(String[] input) {
 
        // Graph is represented as a hashtable with the keys as the individual nodes of the
        // graph and the values being the vertices, stored as an array in the GraphNode object.
        Hashtable<Character, GraphNode> graph = new Hashtable<>();

        for (String ride1 : input) { // For each ride,
            ArrayList<Character> connectedNodes = new ArrayList<>();
            int ride1Start = Integer.parseInt(ride1.substring(ride1.indexOf('[') + 1, ride1.indexOf(',')));
            int ride1End = Integer.parseInt(ride1.substring(ride1.indexOf(',') + 1, ride1.indexOf(')')));
 
            for (String ride2 : input) { // For every other ride,
                int ride2Start = Integer.parseInt(ride2.substring(ride2.indexOf('[') + 1, ride2.indexOf(',')));
                if (ride1End <= ride2Start &&
                    !connectedNodes.contains(ride2.charAt(0)) &&
                    ride1.charAt(0) != ride2.charAt(0)) { // If the second ride is a valid successor of the first,
                    // Create the connection.
                    connectedNodes.add(ride2.charAt(0));
                }
            }
 
            // Add the node and vertices to the graph.
            GraphNode newNode = new GraphNode(ride1End - ride1Start, connectedNodes.toArray(new Character[connectedNodes.size()]));
            graph.put(ride1.charAt(0), newNode);
        }
 
        return graph;
    }
    
    // Object to represent a node and its vertices. Also contains relevant
    // info on the best sequence for this node, for optimization purposes.
    static class GraphNode {
        
        Character[] connectedNodes;
        int rideTime;
        int mostTime = 0;
        String sequence = "";
 
        GraphNode(int rideTime, Character[] connectedNodes) {
            this.rideTime = rideTime;
            this.connectedNodes = connectedNodes;
        }
    }
}
