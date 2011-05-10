package distributedscheduling;

/**
 *
 * @author apurv verma
 */
public enum MachineHeterogeneity {
    TEST (5),
    LOW (10) ,
    HIGH (1000);

    private int h;

    private MachineHeterogeneity(int h){
        this.h=h;
    }

    public int getNumericValue(){
        return h;
    }
}
