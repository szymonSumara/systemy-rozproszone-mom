import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        Supplier s1 = new Supplier("A",Arrays.asList(Item.BACKPACK, Item.OXYGEN));
        Supplier s2 = new Supplier("B",Arrays.asList(Item.SHOES, Item.OXYGEN));
        Team t1 = new Team("C", Arrays.asList(Item.BACKPACK,Item.BACKPACK, Item.OXYGEN, Item.OXYGEN, Item.OXYGEN));
        Team t2 = new Team("D", Arrays.asList(Item.SHOES,Item.SHOES, Item.SHOES,  Item.OXYGEN));
        Admin a = new Admin();


        s1.run();
        s2.run();
        t1.run();
        t2.run();
        a.run();


        try {
            a.join();
            s1.join();
            s2.join();
            t1.join();
            t2.join();
        }catch (InterruptedException exception){

        }
    }
}
