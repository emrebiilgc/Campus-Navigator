import java.io.Serializable;
import java.util.*;

public class Project implements Serializable {
    static final long serialVersionUID = 33L;
    private final String name;
    private final List<Task> tasks;

    public Project(String name, List<Task> tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    /**
     * @return the total duration of the project in days
     */
    public int getProjectDuration() {
        int[] earliestStart = getEarliestSchedule();
        int maxDuration = 0;
        for (int i = 0; i < earliestStart.length; i++) {
            int completion = earliestStart[i] + tasks.get(i).getDuration();
            if (completion > maxDuration) {
                maxDuration = completion;
            }
        }
        return maxDuration;
    }

    /**
     * Schedule all tasks within this project such that they will be completed as early as possible.
     *
     * @return An integer array consisting of the earliest start days for each task.
     */
    public int[] getEarliestSchedule() {
        int taskCount = tasks.size();
        int[] earliestStart = new int[taskCount];
        int[] dependencyCount = new int[taskCount];
        Map<Integer, List<Integer>> dependencyGraph = new HashMap<>();

        for (Task currentTask : tasks) {
            for (int prerequisite : currentTask.getDependencies()) {
                dependencyGraph.computeIfAbsent(prerequisite, key -> new ArrayList<>()).add(currentTask.getTaskID());
                dependencyCount[currentTask.getTaskID()]++;
            }
        }

        Queue<Integer> readyTasks = new LinkedList<>();
        for (int i = 0; i < taskCount; i++) {
            boolean noDependencies = dependencyCount[i] == 0;
            if (noDependencies) {
                readyTasks.offer(i);
            }
        }

        do {
            if (readyTasks.isEmpty()) break;
            int current = readyTasks.poll();
            int finishTime = earliestStart[current] + tasks.get(current).getDuration();

            List<Integer> dependents = dependencyGraph.get(current);
            boolean hasDependents = dependents != null && !dependents.isEmpty();
            if (hasDependents) {
                for (int dependent : dependents) {
                    earliestStart[dependent] = Math.max(earliestStart[dependent], finishTime);
                    dependencyCount[dependent]--;
                    boolean readyToSchedule = dependencyCount[dependent] <= 0;
                    if (readyToSchedule) {
                        readyTasks.offer(dependent);
                    }
                }
            }
        } while (true);

        return earliestStart;
    }


    public static void printlnDash(int limit, char symbol) {
        for (int i = 0; i < limit; i++) System.out.print(symbol);
        System.out.println();
    }

    /**
     * Some free code here. YAAAY! 
     */
    public void printSchedule(int[] schedule) {
        int limit = 65;
        char symbol = '-';
        printlnDash(limit, symbol);
        System.out.println(String.format("Project name: %s", name));
        printlnDash(limit, symbol);

        // Print header
        System.out.println(String.format("%-10s%-45s%-7s%-5s","Task ID","Description","Start","End"));
        printlnDash(limit, symbol);
        for (int i = 0; i < schedule.length; i++) {
            Task t = tasks.get(i);
            System.out.println(String.format("%-10d%-45s%-7d%-5d", i, t.getDescription(), schedule[i], schedule[i]+t.getDuration()));
        }
        printlnDash(limit, symbol);
        System.out.println(String.format("Project will be completed in %d days.", tasks.get(schedule.length-1).getDuration() + schedule[schedule.length-1]));
        printlnDash(limit, symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;

        int equal = 0;

        for (Task otherTask : ((Project) o).tasks) {
            if (tasks.stream().anyMatch(t -> t.equals(otherTask))) {
                equal++;
            }
        }

        return name.equals(project.name) && equal == tasks.size();
    }

}
