public class Item<T>{
    private T value;

    public Item(){
        this.value = null;
    }

    public Item(T value){
        this.value = value;
    }

    // clone ??
    public Item(Item<T> item){
        this.value = item.value;
    }

    // clone ??
    public void setItem(T value) {
        this.value = value;
    }

    public T getItem(){
        return this.value;
    }
}