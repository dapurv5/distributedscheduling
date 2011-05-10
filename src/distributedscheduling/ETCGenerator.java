package distributedscheduling;

import java.util.Arrays;
import java.util.Random;
import static java.lang.System.out;

/**
 * @author apurv verma
 * @version 0.0
 */

/*
 * This class generates an ETC(Expected Time Completion) matrix to be used in the simulation.
 * e[i,j] represents the Expected time of execute the ith task ( t[i] ) on the jth machine ( m[j])
 */
public class ETCGenerator{

    /*The number of machines in the Heterogenous Computing suite*/
    int m;

    /*The number of tasks in the Heterogenous Computing suite*/
    int n;

    /*The Expected Time to Complete matrix*/
    int [][]e;

    /*Task Heterogeneity*/
    int T_t;

    /*Machine Heterogeneity*/
    int T_m;

    public ETCGenerator(int NUM_MACHINES, int NUM_TASKS, TaskHeterogeneity TASK_HETEROGENEITY, MachineHeterogeneity MACHINE_HETEROGENEITY) {
        m=NUM_MACHINES;
        n=NUM_TASKS;
        e=new int[n][m];/*e[i][j] represents the time taken to complete the ith task on the jth machine.*/
        T_t=TASK_HETEROGENEITY.getNumericValue();
        T_m=MACHINE_HETEROGENEITY.getNumericValue();
    }

    public ETCGenerator ETCEngine(int NUM_MACHINES, int NUM_TASKS, TaskHeterogeneity TASK_HETEROGENEITY, MachineHeterogeneity MACHINE_HETEROGENEITY) {
        m=NUM_MACHINES;
        n=NUM_TASKS;
        e=new int[n][m];/*e[i][j] represents the time taken to complete the ith task on the jth machine.*/
        T_t=TASK_HETEROGENEITY.getNumericValue();
        T_m=MACHINE_HETEROGENEITY.getNumericValue();
        return this;
    }

    private void generateETC(){
        Random rt=new Random();
        Random rm=new Random();
        int q[]=new int[n];

        for(int i=0;i<n;i++){
            int N_t=rt.nextInt(T_t);
            q[i]=N_t;
        }
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                int N_m=rm.nextInt(T_m);
                e[i][j]=q[i]*N_m +1;
            }
        }
    }

    public int[][] getETC(){
        generateETC();
        return e;
    }

    @Override
    public String toString(){
        String s=Arrays.deepToString(this.getETC());
        return s;
    }



    public static void main(String...args){
        TaskHeterogeneity TH = null;
        MachineHeterogeneity MH=null;
        int ee[][]=new ETCGenerator(3,10,TH.LOW,MH.LOW).getETC();
        out.println(Arrays.deepToString(ee));
    }
}