package com.googlecode.distributedscheduling;

import java.text.DecimalFormat;
import java.util.Arrays;
import static java.lang.System.out;
/**
 *
 * @author apurv verma
 */
public class Main {

     public static void main(String...args){

         long t1=System.currentTimeMillis();

        /*Specify the parameters here*/
        int NUM_MACHINES=29;
        int NUM_TASKS=500;
        double ARRIVAL_RATE=19;
        int metaSetSize=29;

        Heuristic h=null;
        TaskHeterogeneity TH=null;
        MachineHeterogeneity MH=null;

        Heuristic HEURISTIC=null;
        TaskHeterogeneity th=TH.HIGH;
        MachineHeterogeneity mh=MH.HIGH;

        int no_of_simulations=4000;

        /*Specify the parameters here*/        

        Heuristic[] htype=Heuristic.values();
        long sigmaMakespan[]=new long[htype.length];
        long avgMakespan=0;
        SimulatorEngine se=new SimulatorEngine(NUM_MACHINES, NUM_TASKS, ARRIVAL_RATE, metaSetSize,null,th, mh);

        for(int i=0;i<no_of_simulations;i++){
              
            se.newSimulation(true);
            
            for(int j=0;j<htype.length;j++){                
                se.setHeuristic(htype[j]);
                //out.println(Arrays.deepToString(se.getEtc()));//////////
                //out.println(Arrays.toString(se.getArrivals()));/////////
                se.simulate();
                //out.println("Makespan ="+se.getMakespan() +"strategy:"+htype[j].toString());///////////////
                sigmaMakespan[j]+=se.getMakespan();
                se.newSimulation(false);
            }
         }

        for(int j=0;j<htype.length;j++){
            avgMakespan=sigmaMakespan[j]/no_of_simulations;

            String hName=htype[j].toString();
            String tmp=(String.format("%9s",hName));

            DecimalFormat myFormatter = new DecimalFormat("00000000");
            String output=myFormatter.format(avgMakespan);

            out.println("Avg makespan for "+tmp+" heuristic for "+no_of_simulations+ " simulations is =  "+output);
         }

        long t2=System.currentTimeMillis();
        out.println("Total time taken in the simulation = "+(t2-t1)/1000+" sec.");
    }
}
