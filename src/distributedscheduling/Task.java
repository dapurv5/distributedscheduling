package distributedscheduling;

/**
 *
 * @author apurv verma
 */

/*
 * Every task has been taken to be of batch-mode type. i.e. The tasks assemble in the meta-task set and are mapped at
 * the mapping events.
 */

public class Task {

    int tid;
    
    /*The arrival time, as given by the ArrivalGenerator*/
    int aTime;
    
    /*The completion time*/
    int cTime;

    /*The time required to execute the task on the machine on which it has been scheduled*/
    int eTime;

    public Task(int arrivalTime, int task_id){
        tid=task_id;
        aTime=arrivalTime;
    }

    public int get_aTime() {
        return aTime;
    }

    public void set_aTime(int aTime) {
        this.aTime = aTime;
    }

    public int get_cTime() {
        return cTime;
    }

    public void set_cTime(int cTime) {
        this.cTime = cTime;
    }

    public int get_eTime() {
        return eTime;
    }

    public void set_eTime(int eTime) {
        this.eTime = eTime;
    }


   
}
