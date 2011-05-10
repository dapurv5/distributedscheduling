package distributedscheduling;

/**
 *
 * @author apurv verma
 */
public class TaskWrapper {

    /*index represents that index of this task in the metaset*/
    private int i;

    /*The task which this wrapper is wrapping*/
    private Task t;

    public TaskWrapper(int index, Task task){
        i=index;
        t=task;
    }

    public int getIndex() {
        return i;
    }

    public Task getTask() {
        return t;
    }



}
