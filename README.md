# Campus Navigator

This project is a Java application that helps organize a university club fair and provides a navigation assistant for visitors.  
It combines **graph algorithms** and **regular expressions** to solve two related problems:

1. **Club Fair Setup Planning**  
   - Reads booth setup tasks from an XML file.  
   - Calculates earliest possible start and end times for each task.  
   - Produces a schedule with the minimum overall project duration.  

2. **Campus Navigator App**  
   - Reads campus navigation data from a `.dat` file using regular expressions.  
   - Combines walking and golf cart routes to compute the fastest path.  
   - Outputs the total travel time in minutes and step-by-step directions.  

## 📂 File Structure
- `Main.java` – Entry point of the program  
- `Project.java` – Represents a project with tasks  
- `Task.java` – Represents a setup task with duration and dependencies  
- `ClubFairSetupPlanner.java` – Logic for scheduling tasks  
- `Point.java` – Represents 2D coordinates on the campus map  
- `Station.java` – Represents a golf cart station  
- `CartLine.java` – Represents a golf cart line with stations  
- `RouteDirection.java` – Represents a navigation step in the route  
- `CampusNavigatorNetwork.java` – Reads `.dat` input using regex, builds the network  
- `CampusNavigatorApp.java` – Computes the fastest route and prints directions  
