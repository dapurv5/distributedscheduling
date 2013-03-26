package com.googlecode.distributedscheduling;

import java.util.Arrays;
import static java.lang.System.out;

/**
 * @author apurv verma
 */
/*
 * The strategy used for the choosing the timing of the mapping-events is that when the meta-task set accumulates
 * a predefined number of 'META_TASK_SIZE' tasks.
 */
public class ArrivalGenerator {

    /*Stores the arrival time for each task*/
    int[] arrival_time;

    /*lambda denotes number of arrivals per unit time interval. */
    double lambda;

    public ArrivalGenerator(int NUM_TASKS, double Lambda){
        arrival_time=new int[NUM_TASKS];
        lambda=Lambda;
    }

    public ArrivalGenerator ArrivalGenerator(int NUM_TASKS, double Lambda){
        arrival_time=new int[NUM_TASKS];
        lambda=Lambda;
        return this;
    }

    private void generateArrival(){
        RandomGenerator r= new RandomGenerator(lambda);
        arrival_time[0]=r.nextPoisson();
        for(int i=1;i<arrival_time.length;i++){
            arrival_time[i]=arrival_time[i-1]+r.nextPoisson();
        }
    }

    public int[] getArrival(){
        generateArrival();
        return arrival_time;
    }

    @Override
    public String toString(){
        String s=Arrays.toString(this.getArrival());
        return s;
    }

    public static void main(String...args){
        int LAMBDA=3;
        int NUM_TASKS=50;
        out.println( Arrays.toString(new ArrivalGenerator(NUM_TASKS,LAMBDA).getArrival()) );
    }
}
