import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class DFS {
    List<String>    nodes;

    public DFS(Task[] task) {
        nodes = new ArrayList<>();
        for (Task tsk : task) {
            if (tsk.cntPredecessors == 0) {
                DFSUtil(tsk.id, task);
            }
            else {
                if (!tsk.visited) {
                    DFSUtil(tsk.id, task);
                }
            }
        }
    }

    private void DFSUtil(int id, Task[] task) {
        task[id-1].visited = true;
        nodes.add(task[id-1].name);

        ListIterator<Task> i = task[id-1].outEdges.listIterator();
        while (i.hasNext()) {
            int n = i.next().id;
            if (!task[n-1].completed){
                if (!task[n-1].visited) {
                    DFSUtil(n, task);
                }
                else {
                    System.out.println("Not realizable, graph contains cycle: " + nodes);
                    System.exit(1);
                }
            }
        }
        task[id-1].completed = true;
        nodes.clear();
    }
}

class topologicalSort {

    public topologicalSort(Task[] task) {
        topologicalSortList(task);
    }

    public static ArrayList<Task> topologicalSortList(Task[] task) {
        Stack<Task> S = new Stack<>();
        ArrayList<Task> topoSorted = new ArrayList<>();
        for (Task t : task) {
            if (t.cntPredecessors == 0) {
                S.push(t);
                topoSorted.add(t);
            }
        }
        while(!S.empty()) {
            Task n = S.pop();
            for (Task tn : n.outEdges) {
                tn.cntPredecessors -= 1;
                if (tn.cntPredecessors <= 0) {
                    S.push(tn);
                    topoSorted.add(tn);
                }
            }
        }
        for (Task t : task) {
            t.cntPredecessors = t.orgPred;
        }
        return topoSorted;
    }
}

class optimalTimeSchedule {

    public optimalTimeSchedule(Task [] task, String filename) {
        ArrayList<Task> sorted = topologicalSort.topologicalSortList(task);
        String newfilename = filename.replace("inputs", "outputs");
        newfilename = newfilename.replace(".txt", "");

        try {
        File oTS = new File(newfilename + "_optimal_time_schedule.txt");
        FileWriter writer = new FileWriter(newfilename + "_optimal_time_schedule.txt");
        int maxEnd = 0;
        for (Task t : task) {
            int end = t.earliestStart+t.time;
            if (maxEnd < t.earliestStart + t.time) {
                maxEnd = t.earliestStart + t.time;
            }
            writer.write("Task: " + t.name + "| Start: " + t.earliestStart + "| End: " + end + "\n");

        }
        writer.write("-------------------------------\n");
        writer.write("* Optimal project time is " + maxEnd + " *");
        writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

class projectTimeSchedule {

    public projectTimeSchedule(Task [] task, String filename) {
        ArrayList<Task> sorted = topologicalSort.topologicalSortList(task);
        PriorityQueue<Integer> timeQueue = new PriorityQueue<>();
        String newfilename = filename.replace("inputs", "outputs");
        newfilename = newfilename.replace(".txt", "");


        for (Task t : sorted) {
            if (!timeQueue.contains(t.latestStart)) {
                timeQueue.add(t.latestStart);
            }
            if (!timeQueue.contains(t.latestStart + t.time)) {
                timeQueue.add(t.latestStart + t.time);
            }
        }

        int currentStaff = 0;
        try {
            File oTS = new File(newfilename + "_time_schedule.txt");
            FileWriter writer = new FileWriter(newfilename + "_time_schedule.txt");
            while(!timeQueue.isEmpty()) {
                writer.write("Time: " + timeQueue.peek() + "\n");
                for (Task t : sorted) {
                    if (t.latestStart == timeQueue.peek()) {
                        writer.write("Starting: " + t.name + "\n");
                        currentStaff += t.staff;
                    }
                    if ((t.latestStart + t.time) == timeQueue.peek()) {
                        writer.write("Finished: " + t.name + "\n");
                        currentStaff -= t.staff;
                    }
                }
                writer.write("Current staff: " + currentStaff + "\n");
                writer.write("-------------------------------\n");
                timeQueue.poll();
            }
            writer.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

class nodeInfo {

    public nodeInfo(Task [] task, String filename) {
        ArrayList<Task> sorted = topologicalSort.topologicalSortList(task);
        String newfilename = filename.replace("inputs", "outputs");
        newfilename = newfilename.replace(".txt", "");

        try {
        File oTS = new File(newfilename + "_node_info.txt");
        FileWriter writer = new FileWriter(newfilename + "_node_info.txt");
        for (Task t : task) {
            writer.write("Identity number: " + t.id + "\n");
            writer.write("\t Task: " + t.name + "\n");
            writer.write("\t Time: " + t.time + "\n");
            writer.write("\t Staff: " + t.staff + "\n");
            writer.write("\t Earliest starting time: " + t.earliestStart + "\n");
            writer.write("\t Slack: " + t.slack + "\n");
            for (int i = t.outEdges.size() - 1; i >= 0; i--) {
                writer.write("\t Dependendt tasks: " + t.outEdges.get(i).id + "\n");
            }
            writer.write("-------------------------------\n");
        }
        writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

class setLatestStart {

    public setLatestStart (Task[] task) {
        int end = 0;
        for (Task t : task) {
            if (end < t.earliestStart + t.time) {
                end = t.earliestStart + t.time;
            }
        }
        ArrayList<Task> sorted = topologicalSort.topologicalSortList(task);
        for (int i = sorted.size()-1; i >= 0; i--) {
            if (sorted.get(i).outEdges.size() == 0) {
                sorted.get(i).latestStart = end - sorted.get(i).time;
            }
            else {
                int late = sorted.get(i).outEdges.get(0).latestStart;
                for (int j = sorted.get(i).outEdges.size()-1; j >= 0; j-- ) {
                    if (late > sorted.get(i).outEdges.get(j).latestStart) {
                        late = sorted.get(i).outEdges.get(j).latestStart;
                    }
                }
                sorted.get(i).latestStart = late -sorted.get(i).time;
                if (sorted.get(i).latestStart < sorted.get(i).earliestStart) {
                    sorted.get(i).latestStart = sorted.get(i).earliestStart;
                }
            }
        }
    }
}
