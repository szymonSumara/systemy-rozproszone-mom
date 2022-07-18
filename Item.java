public enum Item {
    OXYGEN("OXYGEN"),
    SHOES("SHOES"),
    BACKPACK("BACKPACK");


    private final String name;

    private Item(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

