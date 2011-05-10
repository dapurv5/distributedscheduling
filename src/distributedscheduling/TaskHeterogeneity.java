package distributedscheduling;

/**
 *
 * @author apurv verma
 */
public enum TaskHeterogeneity {
     TEST (5),
     LOW (100) ,
     HIGH (3000);

     private int h;

     private TaskHeterogeneity(int h){
        this.h=h;
     }

     public int getNumericValue(){
        return h;
     }
}
