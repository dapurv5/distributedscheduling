Distributed-Scheduling-Simulator
--------

Java simulator for calculating the makespan for a set of tasks on a heterogenous distributed computing system.

Heuristics Implemented

- MET (Minimum Execution Time)
- MCT (Minimum Completion Time)
- MaxMin
- Sufferage
- MinMin
- MinMean
- MinVar


### LICENSE
Copyright (c) 2010 Apurv Verma

### BUILD INSTRUCTIONS

Maven-3.0.3 and Java-6+

### QUICKSTART
Here is how you can add a new heuristic and test it against existing heuristics.

1: Go to Heuristic.java . Add your new heuristic say you call it NovelI

2: Go to the class SchedulingEngine.java
Add an else if clause as follows.

```
      else if(h==Heuristic.NovelI)
		schedule_NovelI(metaSet, currentTime); 
```

3: Now add the schedule_NovelI() function to the same class.

```
private void schedule_NovelI(Vector<Task> metaSet, int currentTime){
...PUT YOUR CODE HERE...
}
```

5: Information about individual processors and tasks can be exctracted from the 'sim' object.

For example sim.etc[t.tid][m] represents the expected time of completion of task 't' on the machine 'm'

Similarly sim.mat[m] represents the machine availability time for machine number 'm'.



4: The set of tasks is provided as a Vector. You need to iterate over this vector and for each task 't' in this vector you need to run this function.

```
sim.mapTask(t, machine)
```

Here 't' is the Task that has to be executed.
And machine is the machine number of the machine that this task is being mapped to.


6: This is all you need to do. You can tweak some parameters from the Main.java file I would recommend not to change the 'metaSetSize' by a great amount.

7: The simulator takes some time in running. You can decrease no_of_simulations to get it work faster. 


### RESULTS
MinMean heuristic gives the minimum makespan.


###RESEARCH PAPERS
Dynamic Matching and Scheduling of a Class of Independent Tasks onto Heterogeneous Computing Systems
Muthucumaru Maheswaran, Shoukat Ali , Howard Jay Siegel ,Debra Hensgen, and Richard F. Freund?


A New Heuristic Approach:Min-Mean Algorithm For Scheduling Meta-Tasks On Heterogenous Computing Systems 
Kamalam.G.K and Muralibhaskaran.V


