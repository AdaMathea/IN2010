import java.util.*;

class Task {
    int           id, time, staff;
    String        name;
    int           earliestStart, latestStart, slack;
    List<Task>    outEdges;
    int           cntPredecessors = 0;
    int           orgPred;
    int           prevNodes = 0;
    boolean       visited, completed, critical;

    public Task(int id) {
        this.id = id;
        latestStart = 0;
        outEdges = new ArrayList<>();
        visited = false;
        completed = false;
        critical = false;
    }

    public void addEdge(Task task) {
        outEdges.add(task);
    }

    public void setEarliestStart (int startTime) {
        if (cntPredecessors > prevNodes) {
            if (earliestStart < startTime) {
                earliestStart = startTime;
            }
            prevNodes++;
        }

        if (cntPredecessors == prevNodes) {
            for (Task t : outEdges) {
                t.setEarliestStart(earliestStart + time);
            }
        }
    }
}