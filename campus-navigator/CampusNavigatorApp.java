import java.io.Serializable;
import java.util.*;

public class CampusNavigatorApp implements Serializable {
    static final long serialVersionUID = 99L;

    public HashMap<Station, Station> predecessors = new HashMap<>();
    public HashMap<Set<Station>, Double> times = new HashMap<>();

    public CampusNavigatorNetwork readCampusNavigatorNetwork(String filename) {
        CampusNavigatorNetwork network = new CampusNavigatorNetwork();
        network.readInput(filename);
        return network;
    }


    /**
     * Calculates the fastest route from the user's selected starting point to
     * the desired destination, using the campus golf cart network and walking paths.
     * @return List of RouteDirection instances
     */

    public List<RouteDirection> getFastestRouteDirections(CampusNavigatorNetwork network) {
        List<RouteDirection> routeDirections = new ArrayList<>();

        List<Station> points = new ArrayList<>();
        Map<Station, List<Station>> adjacencyList = new HashMap<>();
        Map<String, Boolean> shuttleLinks = new HashMap<>();

        points.add(network.startPoint);
        points.add(network.destinationPoint);

        int lineIndex = 0;
        while (lineIndex < network.lines.size()) {
            CartLine cart = network.lines.get(lineIndex);
            for (Station st : cart.cartLineStations) {
                points.add(st);
            }
            lineIndex++;
        }

        for (Station st : points) {
            adjacencyList.putIfAbsent(st, new ArrayList<>());
        }

        int i = 0;
        while (i < points.size()) {
            int j = 0;
            while (j < points.size()) {
                Station s1 = points.get(i);
                Station s2 = points.get(j);
                if (!s1.equals(s2)) {
                    adjacencyList.get(s1).add(s2);
                    double walkTime = estimateTravelTime(s1.coordinates, s2.coordinates, network.averageWalkingSpeed);
                    Set<Station> timeKey = new HashSet<>(Arrays.asList(s1, s2));
                    times.put(timeKey, walkTime);
                }
                j++;
            }
            i++;
        }

        int cartLineIndex = 0;
        if (network.lines.size() > 0) {
            do {
                CartLine cart = network.lines.get(cartLineIndex);
                List<Station> stops = cart.cartLineStations;

                int idx = 0;
                for (; idx < stops.size() - 1; idx++) {
                    Station start = stops.get(idx);
                    Station end = stops.get(idx + 1);
                    adjacencyList.get(start).add(end);
                    adjacencyList.get(end).add(start);
                    double cartSpeed = network.averageCartSpeed;
                    double travelTime = estimateTravelTime(start.coordinates, end.coordinates, cartSpeed);
                    Set<Station> timeKey = new HashSet<>(Arrays.asList(start, end));
                    times.put(timeKey, travelTime);
                    shuttleLinks.put(start.toString() + ":" + end.toString(), true);
                    shuttleLinks.put(end.toString() + ":" + start.toString(), true);
                }

                cartLineIndex++;
            } while (cartLineIndex < network.lines.size());
        }

        Map<Station, Double> shortestDist = new HashMap<>();
        Map<Station, Station> predecessors = new HashMap<>();
        for (Station station : points) {
            shortestDist.put(station, Double.MAX_VALUE);
        }
        shortestDist.put(network.startPoint, 0.0);

        PriorityQueue<Station> queue = new PriorityQueue<>(Comparator.comparingDouble(shortestDist::get));
        queue.add(network.startPoint);

        do {
            if (queue.isEmpty()) {
                break;
            }
            Station current = queue.poll();
            List<Station> neighbors = adjacencyList.getOrDefault(current, new ArrayList<>());

            int nIndex = 0;
            while (nIndex < neighbors.size()) {
                Station neighbor = neighbors.get(nIndex);
                Set<Station> key = new HashSet<>(Arrays.asList(current, neighbor));
                if (times.containsKey(key)) {
                    double altDist = shortestDist.get(current) + times.get(key);
                    if (altDist < shortestDist.get(neighbor)) {
                        shortestDist.put(neighbor, altDist);
                        predecessors.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
                nIndex++;
            }
        } while (!queue.isEmpty());

        List<Station> finalPath = new ArrayList<>();

        for (Station step = network.destinationPoint; step != null; step = predecessors.get(step)) {
            finalPath.add(step);
        }


        Collections.reverse(finalPath);

        int pathIndex = 0;
        while (pathIndex < finalPath.size() - 1) {
            Station origin = finalPath.get(pathIndex);
            Station dest = finalPath.get(pathIndex + 1);
            Set<Station> timeKey = new HashSet<>(Arrays.asList(origin, dest));
            double duration = times.getOrDefault(timeKey, estimateTravelTime(origin.coordinates, dest.coordinates, network.averageWalkingSpeed));
            boolean viaCart = shuttleLinks.containsKey(origin.toString() + ":" + dest.toString());
            routeDirections.add(new RouteDirection(origin.toString(), dest.toString(), duration, viaCart));
            pathIndex++;
        }

        return routeDirections;
    }

    /**
     * Function to print the route directions to STDOUT
     */
    public void printRouteDirections(List<RouteDirection> routeSteps) {
        double accumulatedDuration = 0.0;
        for (int i = 0; i < routeSteps.size(); i++) {
            accumulatedDuration += routeSteps.get(i).duration;
        }
        System.out.println("The fastest route takes " + Math.round(accumulatedDuration) + " minute(s).");
        System.out.println("Directions\n----------");

        int stepNumber = 1;
        int idx = 0;
        while (idx < routeSteps.size()) {
            RouteDirection step = routeSteps.get(idx);
            String mode = step.cartRide ? "Ride the cart" : "Walk";
            System.out.printf("%d. %s from \"%s\" to \"%s\" for %.2f minutes.%n",
                    stepNumber++, mode, step.startStationName, step.endStationName, step.duration);
            idx++;
        }
    }

    private double estimateTravelTime(Point firstPoint, Point secondPoint, double speed) {
        double dx = firstPoint.x - secondPoint.x;
        double dy = firstPoint.y - secondPoint.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance / speed;
    }

}
