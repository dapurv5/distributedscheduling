package com.googlecode.distributedscheduling;

import java.util.Vector;
import java.util.Comparator;
import java.util.PriorityQueue;
import static java.lang.System.out;


/**
 * @author apurv verma
 */
public class SimulatorEngine {

    /*p[i] represents all tasks submitted to the ith machine*/
    public PriorityQueue<Task> p[];   

    /*Comparator for tasks*/
    private Comparator<Task> comparator;

    /*The total number of tasks*/
    int n;

    /*The number of machines*/
    int m;

    /*The poisson arrival rate*/
    double lambda;

    /*Meta-task set size*/
    int S;

    /*Arrival time of tasks*/
    public int arrivals[];

    /*ETC matrix*/
    public int etc[][];

    /*Machine availability time, the time at which machine i finishes all previously assigned tasks.*/
    public int mat[];

    private SchedulingEngine eng;

    TaskHeterogeneity TH;
    MachineHeterogeneity MH;
    
    /*For calculating avg completion time*/
    long sigma;

    /*For calculating makespan*/
    long makespan;

    public SimulatorEngine(int NUM_MACHINES, int NUM_TASKS, double ARRIVAL_RATE, int metaSetSize,Heuristic HEURISTIC, TaskHeterogeneity th, MachineHeterogeneity mh){
        
        sigma=0;
        makespan=0;

        MH=mh;
        TH=th;
        n=NUM_TASKS;
        S=metaSetSize;
        m=NUM_MACHINES;
        lambda=ARRIVAL_RATE;
        comparator=new TaskComparator();
        p=new PriorityQueue[m];      
        eng=new SchedulingEngine(this,HEURISTIC);

        for(int i=0;i<p.length;i++)
            p[i]=new PriorityQueue<Task>(5,comparator);
        
        generateRandoms();
        mat=new int[m];
    }

    private void generateRandoms(){
        arrivals=new ArrivalGenerator(n,lambda).getArrival();
        etc=new ETCGenerator(m,n,TH,MH).getETC();
    }

    public void newSimulation(boolean generateRandoms){
        makespan=0;
        sigma=0;
        if(generateRandoms)
            generateRandoms();
        for(int i=0;i<m;i++){
            mat[i]=0;
            p[i].clear();
        }
    }

    public void setHeuristic(Heuristic h){
        this.eng.h=h;
    }

    public long getMakespan() {
        return makespan;
    }

    public int[] getArrivals() {
        return arrivals;
    }

    public int[][] getEtc() {
        return etc;
    }

    public void mapTask(Task t, int machine){
        t.set_eTime(etc[t.tid][machine]);
        t.set_cTime( mat[machine]+etc[t.tid][machine] );
        p[machine].offer(t);
        mat[machine]=t.cTime;
    }

    


    public void simulate(){
        /*tick represents the current time*/
        int tick=0;

        Vector<Task> metaSet=new Vector<Task>(S);
        int i1=0;
        int i2=S;

        /*Initialization*/
        /*Add the first S tasks to the meta set and schedule them*/
        for(int i=i1;i<i2;i++){
            Task t=new Task(arrivals[i],i);
            metaSet.add(t);
        }
        i1=i2;
        i2=(int) min(i1+S, arrivals.length);
        /*Set tick to the time of the first mapping event*/
        tick=arrivals[i1-1];
        eng.schedule(metaSet,tick);

        /*Set tick to the time of the next mapping event*/
        tick=arrivals[i2-1];

        /*Simulation Loop*/
        do{

            /*Set the current tick value*/
            if(i2==i1){
                tick=Integer.MAX_VALUE;                
                /*Remove all the completed tasks from all the machines*/
                removeCompletedTasks(tick);
                break;
            }
            else{
                /*The time at which the next mapping event takes place*/
                tick=arrivals[i2-1];
                /*Remove all the completed tasks from all the machines*/
                removeCompletedTasks(tick);
            }
            /**/
            
            /*Collect next S OR (i2-i1) tasks to the meta set and schedule them*/
            metaSet=new Vector<Task>(i2-i1);

            for(int i=i1;i<i2;i++){
                Task t=new Task(arrivals[i],i);
                metaSet.add(t);
            }
            eng.schedule(metaSet, tick);
            /**/

            /*Set values for next iteration.*/
            i1=i2;
            i2=(int) min(i1+S, arrivals.length);
            /**/

        }while(!discontinueSimulation());
    }

    private void removeCompletedTasks(int currentTime){
        for(int i=0;i<this.m;i++){
            if(!p[i].isEmpty()){
                Task t=p[i].peek();               
                while(t.cTime<=currentTime){                 
                    sigma+=t.cTime;
                    makespan=max(makespan,t.cTime);
                    //out.println("Removing task "+t.tid+" at time "+currentTime);////////////////////////
                    t=p[i].poll();
                    if(!p[i].isEmpty())
                        t=p[i].peek();
                    else
                        break;
                }
            }
        }
    }

    private boolean discontinueSimulation(){
        boolean result=true;
        for(int i=0;i<this.m && result;i++)
            result=result && p[i].isEmpty();
        return result;
    }

    private long max(long a,long b){
        if(a>b)
            return a;
        else
            return b;
    }

    private long min(long a,long b){
        if(a<b)
            return a;
        else
            return b;
    }

   
}
