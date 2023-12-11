import java.util.*;

public class Itemset<T>{
    private HashSet<T> items;

    public Itemset(){
        this.items = new HashSet<T>();
    }

    public Itemset(HashSet<T> items){
        this.items = items;
    }

    public Itemset(Itemset<T> itemset){
        this.items = itemset.items;
    }

    public void setItems(HashSet<T> items){
        this.items = items;
    }

    public HashSet<T> getItems(){
        return this.items;
    }

}
