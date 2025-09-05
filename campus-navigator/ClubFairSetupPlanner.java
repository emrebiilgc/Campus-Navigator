import java.io.Serializable;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;

public class ClubFairSetupPlanner implements Serializable {
    static final long serialVersionUID = 88L;

    /**
     * Given a list of Project objects, prints the schedule of each of them.
     * Uses getEarliestSchedule() and printSchedule() methods of the current project to print its schedule.
     * @param projectList a list of Project objects
     */

    public void printSchedule(List<Project> projectList) {
        for (Project p : projectList) {
            int[] earliest = p.getEarliestSchedule();
            p.printSchedule(earliest);
        }
    }

    /**
     * TODO: Parse the input XML file and return a list of Project objects
     *
     * @param filename the input XML file
     * @return a list of Project objects
     */
    public List<Project> readXML(String filename) {
        List<Project> projectList = new ArrayList<>();

        try {
            File inputFile = new File(filename);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(inputFile);
            document.getDocumentElement().normalize();

            NodeList projNodes = document.getElementsByTagName("Project");

            int projIndex = 0;
            if (projNodes.getLength() > 0) {
                do {
                    Element projElement = (Element) projNodes.item(projIndex);
                    String projName = projElement.getElementsByTagName("Name").item(0).getTextContent();

                    List<Task> taskCollection = new ArrayList<>();
                    NodeList taskNodes = projElement.getElementsByTagName("Task");

                    int taskIndex = 0;
                    if (taskNodes.getLength() > 0) {
                        while (taskIndex < taskNodes.getLength()) {
                            Element taskElement = (Element) taskNodes.item(taskIndex);

                            int taskIdentifier = Integer.parseInt(taskElement.getElementsByTagName("TaskID").item(0).getTextContent());
                            String taskDesc = taskElement.getElementsByTagName("Description").item(0).getTextContent();
                            int taskDuration = Integer.parseInt(taskElement.getElementsByTagName("Duration").item(0).getTextContent());

                            List<Integer> dependencyIds = new ArrayList<>();
                            NodeList dependencyNodes = taskElement.getElementsByTagName("DependsOnTaskID");

                            int depIndex = 0;
                            if (dependencyNodes.getLength() > 0) {
                                while (depIndex < dependencyNodes.getLength()) {
                                    dependencyIds.add(Integer.parseInt(dependencyNodes.item(depIndex).getTextContent()));
                                    depIndex++;
                                }
                            }

                            taskCollection.add(new Task(taskIdentifier, taskDesc, taskDuration, dependencyIds));
                            taskIndex++;
                        }
                    }

                    projectList.add(new Project(projName, taskCollection));
                    projIndex++;
                } while (projIndex < projNodes.getLength());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return projectList;
    }



}
