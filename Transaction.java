import java.util.*;

public class Transaction<T> extends HashMap<T, Double>{
    private HashMap<T, Double> itemsAndProbabilities;

    public Transaction(){
        this.itemsAndProbabilities = new HashMap<T, Double>();
    }

    public Transaction(HashMap<T, Double> itemsAndProbabilities){
        this.itemsAndProbabilities = itemsAndProbabilities;
    }

    public Transaction(Transaction<T> transaction){
        this.itemsAndProbabilities = transaction.itemsAndProbabilities;
    }

    public HashMap<T, Double> getItemsAndProbabilities(){
        return this.itemsAndProbabilities;
    }
}
