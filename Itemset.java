import java.util.*;

public class Itemset<T>{
    private HashSet<T> set;
    private double u;

    public Itemset(){
        this.set = new HashSet<T>();
        this.u = 0.0;
    }

    public Itemset(HashSet<T> set){
        this.set = set;
        this.u = 0.0;
    }

    public HashSet<T> getSet(){
        return this.set;
    }

    public void setU(double u){
        this.u = u;
    }

    public double getU(){
        return this.u;
    }
}
