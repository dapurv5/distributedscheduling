package distributedscheduling;

import java.util.Vector;
import java.util.Iterator;
import static java.lang.System.out;

/**
 * @author apurv verma
 */
public class SchedulingEngine {

    Heuristic h;
    SimulatorEngine sim;
    
    public SchedulingEngine(SimulatorEngine sim,Heuristic heuristic){
        h=heuristic;
        this.sim=sim;
    }

    public void schedule(Vector<Task> metaSet,int currentTime){

        /*If any machine has zero assigned tasks then set mat[] for that machine to be the current time.*/
        for(int i=0;i<sim.m;i++){
            if(sim.p[i].isEmpty()){
                sim.mat[i]=currentTime;
            }
        }

        if(h==Heuristic.MET)
            schedule_MET(metaSet,currentTime);
        else if(h==Heuristic.MCT)
            schedule_MCT(metaSet,currentTime);
        else if(h==Heuristic.MinMin)
            schedule_MinMin(metaSet,currentTime);
        else if(h==Heuristic.Sufferage)
            schedule_Sufferage(metaSet,currentTime);
        else if(h==Heuristic.MinMean)
            schedule_MinMean(metaSet,currentTime);
        else if(h==Heuristic.MaxMin)
            schedule_MaxMin(metaSet,currentTime);
        else if(h==Heuristic.MinVar)
            schedule_MinVar(metaSet,currentTime);
        else if(h==Heuristic.NovelI)
            schedule_NovelI(metaSet, currentTime);
    }

    private void schedule_MET(Vector<Task> metaSet,int currentTime){

        int minExecTime=Integer.MAX_VALUE;
        int machine=0;

        for(int i=0;i<metaSet.size();i++){
            Task t=metaSet.elementAt(i);
            for(int j=0;j<sim.m;j++){
                if( sim.etc[t.tid][j] < minExecTime){
                    minExecTime=sim.etc[t.tid][j];
                    machine=j;
                }
            }
         sim.mapTask(t, machine);
    //out.println("Adding task "+t.tid+" to machine "+machine+". Completion time = "+t.cTime+" @time "+currentTime);//////
        }
        //out.println("________Return from schedule_________");///////////////
    }

    private void schedule_MCT(Vector<Task> metaSet,int currentTime){
       
        int minComplTime=Integer.MAX_VALUE;
        int machine=0;

        for(int i=0;i<metaSet.size();i++){
            Task t=metaSet.elementAt(i);
            for(int j=0;j<sim.m;j++){
                if( sim.etc[t.tid][j] + sim.mat[j] < minComplTime){
                    minComplTime=sim.etc[t.tid][j] + sim.mat[j];
                    machine=j;
                }
            }
         sim.mapTask(t, machine);
    //out.println("Adding task "+t.tid+" to machine "+machine+". Completion time = "+t.cTime+" @time "+currentTime);//////
        }
        //out.println("________Return from schedule_________");///////////////
    }

    private void schedule_MinMin(Vector<Task> metaSet, int currentTime){

        /*We do not actually delete the task from the meta-set rather mark it as removed*/
        boolean[] isRemoved=new boolean[metaSet.size()];

        /*Matrix to contain the completion time of each task in the meta-set on each machine.*/
        int c[][]=schedule_MinMinHelper(metaSet);
        int i=0;

        int tasksRemoved=0;
        do{
            int minTime=Integer.MAX_VALUE;
            int machine=-1;
            int taskNo=-1;
            /*Find the task in the meta set with the earliest completion time and the machine that obtains it.*/
            for(i=0;i<metaSet.size();i++){
                if(isRemoved[i])continue;
                for(int j=0;j<sim.m;j++){
                    if(c[i][j]<minTime){
                        minTime=c[i][j];
                        machine=j;
                        taskNo=i;
                    }
                }
            }           
            Task t=metaSet.elementAt(taskNo);
            sim.mapTask(t, machine);

            /*Mark this task as removed*/
            tasksRemoved++;
            isRemoved[taskNo]=true;
            //metaSet.remove(taskNo);

            /*Update c[][] Matrix for other tasks in the meta-set*/
            for(i=0;i<metaSet.size();i++){
                if(isRemoved[i])continue;
                else{
                    c[i][machine]=sim.mat[machine]+sim.etc[metaSet.get(i).tid][machine];
                }
            }            

        }while(tasksRemoved!=metaSet.size());
    }


    private void schedule_MaxMin(Vector<Task> metaSet, int currentTime){

        /*We do not actually delete the task from the meta-set rather mark it as removed*/
        boolean[] isRemoved=new boolean[metaSet.size()];

        /*Matrix to contain the completion time of each task in the meta-set on each machine.*/
        int c[][]=schedule_MinMinHelper(metaSet);
        int i=0;

        /*Minimum Completion Time of the ith task in the meta set*/
        int[] minComplTime=new int[metaSet.size()];
        int[] minComplMachine=new int[metaSet.size()];

        int tasksRemoved=0;
        do{
            int minTime=Integer.MAX_VALUE;
            int machine=-1;
            int taskNo=-1;
            /*Find the task in the meta set with the earliest completion time and the machine that obtains it.*/
            for(i=0;i<metaSet.size();i++){
                if(isRemoved[i])continue;
                for(int j=0;j<sim.m;j++){
                    if(c[i][j]<minTime){
                        minTime=c[i][j];
                        machine=j;                        
                    }
                }
                minComplTime[i]=minTime;
                minComplMachine[i]=machine;
                minTime=Integer.MAX_VALUE;
                machine=-1;
            }

            /*Find the task which has the maximum minimum completion time*/
            int maxMinComplTime=Integer.MIN_VALUE;
            for(int l=0;l<metaSet.size();l++){
                if(maxMinComplTime<minComplTime[l]){
                    maxMinComplTime=minComplTime[l];
                    taskNo=l;
                }
            }
            Task t=metaSet.elementAt(taskNo);
            machine=minComplMachine[taskNo];
            sim.mapTask(t, machine);

            /*Mark this task as removed*/
            tasksRemoved++;
            isRemoved[taskNo]=true;
            //metaSet.remove(taskNo);

            /*Update c[][] Matrix for other tasks in the meta-set*/
            for(i=0;i<metaSet.size();i++){
                if(isRemoved[i])continue;
                else{
                    c[i][machine]=sim.mat[machine]+sim.etc[metaSet.get(i).tid][machine];
                }
            }

        }while(tasksRemoved!=metaSet.size());
    }

    /*This function is a helper of schedule_MinMin() and schedule_MaxMin()*/
    private int[][] schedule_MinMinHelper(Vector<Task> metaSet){
        int c[][]=new int[metaSet.size()][sim.m];
        int i=0;
        for(Iterator it=metaSet.iterator();it.hasNext();){
            Task t=(Task)it.next();
            for(int j=0;j<sim.m;j++){
                c[i][j]=sim.mat[j]+sim.etc[t.tid][j];
            }
            i++;
        }
        return c;
    }

    private void schedule_Sufferage(Vector<Task> metaSet, int currentTime) {

        /*We don't directly add the tasks to the p[] matrix of simulator rather add in this copy first*/
        Vector<TaskWrapper> pCopy[]=new Vector[sim.m];
        
        /*Copy of mat matrix*/
        int[] matCopy=new int[sim.m];
        for(int i=0;i<sim.m;i++){
            matCopy[i]=sim.mat[i];
            /*Also initialize the processors Copy , pCopy[]*/
            pCopy[i]=new Vector<TaskWrapper>(4);
        }

        /*assigned[j] =true tells that machine j has been assigned a task.*/
        boolean assigned[]=new boolean[sim.m];

        /*We do not actually delete the task from the meta-set rather mark it as removed*/
        boolean[] isRemoved=new boolean[metaSet.size()];

        /*Matrix to contain the completion time of each task in the meta-set on each machine.*/
        int c[][]=schedule_MinMinHelper(metaSet);
        int i=0;
        /*Sufferage value of all tasks*/
        int[] sufferage=new int[metaSet.size()];

        int tasksRemoved=0;
        do{
            int minTime1=Integer.MAX_VALUE;
            int minTime2=Integer.MAX_VALUE;
            int machine1=-1;
            int machine2=-1;
            
            /*For tasks in the meta set,find machine on which it has the earliest and 2nd earliest completion time*/
            for(i=0;i<metaSet.size();i++){
                if(isRemoved[i])continue;
                /*Earliest completion time machine*/
                for(int j=0;j<sim.m;j++){
                    if(c[i][j]<minTime1){
                        minTime1=c[i][j];
                        machine1=j;
                    }
                }
                /*2nd earliest completion time machine*/
                for(int j=0;j<sim.m;j++){
                    if(j!=machine1 && c[i][j]<minTime2 ){
                        minTime2=c[i][j];
                        machine2=j;
                    }
                }
                sufferage[i]=minTime2-minTime1;
                Task t=metaSet.elementAt(i);
                if(!assigned[machine1]){
                    mapTaskCopy(t,machine1,pCopy,matCopy,i);
                    /*Mark this task as removed*/
                    tasksRemoved++;
                    isRemoved[i]=true;
                    //metaSet.remove(taskNo);
                                        
                }
                else{
                    for(Iterator it=pCopy[machine1].iterator();it.hasNext();){
                        TaskWrapper tw=(TaskWrapper)it.next();
                        if(sufferage[tw.getIndex()] < sufferage[i]){
                            Task task=tw.getTask();
                            int index=tw.getIndex();
                            /*Unassign this task from machine1*/
                            pCopy[machine1].remove(tw);

                            /*Update matCopy[] matrix*/
                            matCopy[machine1]-=sim.etc[task.tid][machine1];

                            /*Add it back to the meta set*/
                            tasksRemoved--;
                            isRemoved[index]=false;
                            
                            /*Assign the current task to the machine*/
                            mapTaskCopy(t,machine1,pCopy,matCopy,i);
                            
                            /*Mark this task as removed*/
                            tasksRemoved++;
                            isRemoved[i]=true;

                        }

                    }
                }
                /*Update c[][] Matrix for other tasks in the meta-set*/
                for(i=0;i<metaSet.size();i++){
                    if(isRemoved[i])continue;
                    else{
                        c[i][machine1]=matCopy[machine1]+sim.etc[metaSet.get(i).tid][machine1];
                    }
                }
            }                                                            

        }while(tasksRemoved!=metaSet.size());

        /*Copy matCopy[] and pCopy[] back to original matrices*/
        for(i=0;i<sim.m;i++){
            for(int j=0;j<pCopy[i].size();j++){
                TaskWrapper tbu=pCopy[i].elementAt(j);
                sim.mapTask(tbu.getTask(), i);                
            }
        }
        /*By doing this we are preserving the order in which tasks should have been mapped to the machines*/
        System.arraycopy(matCopy, 0, sim.mat, 0, sim.m);
    }


    /*This function is a helper of schedule_Sufferage()*/
    private void mapTaskCopy(Task t, int machine, Vector<TaskWrapper> pCopy[], int mat[],int index){
        t.set_eTime(sim.etc[t.tid][machine]);
        t.set_cTime( mat[machine]+sim.etc[t.tid][machine] );

        TaskWrapper tw=new TaskWrapper(index,t);
        pCopy[machine].add(tw);
        mat[machine]=t.cTime;
    }

    private void schedule_MinMean(Vector<Task> metaSet, int currentTime) {

         /*We don't directly add the tasks to the p[] matrix of simulator rather add in this copy first*/
        Vector<TaskWrapper> pCopy[]=new Vector[sim.m];

        /*Copy of mat matrix*/
        int[] matCopy=new int[sim.m];
        for(int i=0;i<sim.m;i++){
            matCopy[i]=sim.mat[i];
            /*Also initialize the processors Copy , pCopy[]*/
            pCopy[i]=new Vector<TaskWrapper>(4);
        }

        /*First schedule that tasks according to min-min*/
        schedule_MinMinCopy(metaSet,currentTime,pCopy,matCopy);

        /*Find avg completion time for each machine*/
        long sigmaComplTime=0;
        long avgComplTime=0;
        for(int i=0;i<sim.m;i++)
            sigmaComplTime+=matCopy[i];
        avgComplTime=sigmaComplTime/sim.m;
        int k=0;        
        /*Reshufffle tasks from machines which have higher completion time than average to lower compl time machines*/
        for(int i=0;i<sim.m;i++){
            if(matCopy[i]<=avgComplTime)continue;
            k=0;
            while(k+1<=pCopy[i].size()){
                TaskWrapper tw=pCopy[i].elementAt(k);
                Task t=tw.getTask();
                /*Remap this task to another machine with completion time less than average completion time
                 such that the difference of the new completion time of the machine and the average comple-
                 -tion time becomes the minimum.
                 This is analogous to best-fit algorithm
                */
                int delta=Integer.MIN_VALUE;
                int machine=i;
                for(int j=0;j<sim.m;j++){
                    if(j==i || matCopy[j]>=avgComplTime)continue;
                    if(( matCopy[j]+sim.etc[t.tid][j] < avgComplTime) && Math.abs( matCopy[j]+sim.etc[t.tid][j] - avgComplTime) > delta){
                        delta= (int) Math.abs( matCopy[j]+sim.etc[t.tid][j] - avgComplTime);
                        machine=j;
                    }
                }
                /*Map the task to the new machine*/
                if(machine!=i){
                    pCopy[i].remove(tw);
                    matCopy[i]-=sim.etc[t.tid][i];
                    mapTaskCopy(t,machine,pCopy,matCopy,tw.getIndex());

                    /*Note that the new avg completion time may be different from the old one*/
                    //sigmaComplTime-=sim.etc[t.tid][i];
                    //sigmaComplTime+=sim.etc[t.tid][machine];
                    //avgComplTime=sigmaComplTime/sim.m;
                    /*Not included because it increases makespan slightly*/
                }
                k++;
            }
        }
        /*Copy matCopy[] and pCopy[] back to original matrices*/
        for(int i=0;i<sim.m;i++){
            for(int j=0;j<pCopy[i].size();j++){
                TaskWrapper tbu=pCopy[i].elementAt(j);
                sim.mapTask(tbu.getTask(), i);
            }
        }
        /*By doing this we are preserving the order in which tasks should have been mapped to the machines*/
        System.arraycopy(matCopy, 0, sim.mat, 0, sim.m);
    }


    private void schedule_MinMinCopy(Vector<Task> metaSet, int currentTime, Vector<TaskWrapper>[] pCopy, int[] matCopy){

        /*We do not actually delete the task from the meta-set rather mark it as removed*/
        boolean[] isRemoved=new boolean[metaSet.size()];

        /*Matrix to contain the completion time of each task in the meta-set on each machine.*/
        int c[][]=schedule_MinMinCopyHelper(metaSet,matCopy);
        int i=0;

        int tasksRemoved=0;
        do{
            int minTime=Integer.MAX_VALUE;
            int machine=-1;
            int taskNo=-1;
            /*Find the task in the meta set with the earliest completion time and the machine that obtains it.*/
            for(i=0;i<metaSet.size();i++){
                if(isRemoved[i])continue;
                for(int j=0;j<sim.m;j++){
                    if(c[i][j]<minTime){
                        minTime=c[i][j];
                        machine=j;
                        taskNo=i;
                    }
                }
            }
            Task t=metaSet.elementAt(taskNo);
            this.mapTaskCopy(t,machine,pCopy,matCopy,taskNo);

            /*Mark this task as removed*/
            tasksRemoved++;
            isRemoved[taskNo]=true;
            //metaSet.remove(taskNo);

            /*Update c[][] Matrix for other tasks in the meta-set*/
            for(i=0;i<metaSet.size();i++){
                if(isRemoved[i])continue;
                else{
                    c[i][machine]=matCopy[machine]+sim.etc[metaSet.get(i).tid][machine];
                }
            }

        }while(tasksRemoved!=metaSet.size());
    }

    /*This function is a helper of schedule_MinMin()*/
    private int[][] schedule_MinMinCopyHelper(Vector<Task> metaSet, int[] matCopy){
        int c[][]=new int[metaSet.size()][sim.m];
        int i=0;
        for(Iterator it=metaSet.iterator();it.hasNext();){
            Task t=(Task)it.next();
            for(int j=0;j<sim.m;j++){
                c[i][j]=matCopy[j]+sim.etc[t.tid][j];
            }
            i++;
        }
        return c;
    }

    private void schedule_MinVar(Vector<Task> metaSet, int currentTime) {
         /*We don't directly add the tasks to the p[] matrix of simulator rather add in this copy first*/
        Vector<TaskWrapper> pCopy[]=new Vector[sim.m];

        /*Copy of mat matrix*/
        int[] matCopy=new int[sim.m];
        for(int i=0;i<sim.m;i++){
            matCopy[i]=sim.mat[i];
            /*Also initialize the processors Copy , pCopy[]*/
            pCopy[i]=new Vector<TaskWrapper>(4);
        }

        /*First schedule that tasks according to min-min*/
        schedule_MinMinCopy(metaSet,currentTime,pCopy,matCopy);

        /*Find avg completion time for each machine*/
        long sigmaComplTime=0;
        long avgComplTime=0;
        for(int i=0;i<sim.m;i++)
            sigmaComplTime+=matCopy[i];
        avgComplTime=sigmaComplTime/sim.m;
            
        
        int k=0;
        /*Reshuffle tasks so that the variance decreases*/
        for(int i=0;i<sim.m;i++){
            if(matCopy[i]<=avgComplTime)continue;
            k=0;
            while(k+1<=pCopy[i].size()){
                TaskWrapper tw=pCopy[i].elementAt(k);
                Task t=tw.getTask();                
                int deltaVar=0;
                int minDeltaVar=Integer.MAX_VALUE;

                long newSigmaComplTime=sigmaComplTime;
                long newAvgComplTime=avgComplTime;

                int machine=i;
                int delta=Integer.MIN_VALUE;
                for(int j=0;j<sim.m;j++){
                    if(j==i || matCopy[j]>=avgComplTime)continue;
                    deltaVar=0;

                    newSigmaComplTime-=sim.etc[t.tid][i];
                    newSigmaComplTime+=sim.etc[t.tid][j];
                    newAvgComplTime=newSigmaComplTime/sim.m;

                    deltaVar-=(int) Math.pow((matCopy[i] - avgComplTime),2);
                    deltaVar+=(int) Math.pow((matCopy[i]-sim.etc[t.tid][i]-newAvgComplTime),2);
                    deltaVar-=(int) Math.pow((matCopy[j]-avgComplTime),2);
                    deltaVar+=(int) Math.pow((matCopy[j]+sim.etc[t.tid][j]-newAvgComplTime),2);

                    if(( matCopy[j]+sim.etc[t.tid][j] < avgComplTime) && Math.abs( matCopy[j]+sim.etc[t.tid][j] - avgComplTime) > delta && deltaVar<0 && deltaVar<minDeltaVar){
                        minDeltaVar=deltaVar;
                        delta= (int) Math.abs( matCopy[j]+sim.etc[t.tid][j] - avgComplTime);
                        machine=j;
                    }

                    newSigmaComplTime=sigmaComplTime;
                    newAvgComplTime=avgComplTime;

                }
                /*Map the task to the new machine*/
                if(machine!=i){
                    pCopy[i].remove(tw);
                    matCopy[i]-=sim.etc[t.tid][i];
                    mapTaskCopy(t,machine,pCopy,matCopy,tw.getIndex());
                    /*Note that the new avg completion time may be different from the old one*/
                    sigmaComplTime-=sim.etc[t.tid][i];
                    sigmaComplTime+=sim.etc[t.tid][machine];
                    avgComplTime=sigmaComplTime/sim.m;

                }
                k++;
            }
        }
        /*Copy matCopy[] and pCopy[] back to original matrices*/
        for(int i=0;i<sim.m;i++){
            for(int j=0;j<pCopy[i].size();j++){
                TaskWrapper tbu=pCopy[i].elementAt(j);
                sim.mapTask(tbu.getTask(), i);
            }
        }
        /*By doing this we are preserving the order in which tasks should have been mapped to the machines*/
        System.arraycopy(matCopy, 0, sim.mat, 0, sim.m);
    }


    private void schedule_NovelI(Vector<Task> metaSet, int currentTime){
        
    }
}
