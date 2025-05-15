public abstract class Beverage {
    protected String name;
    protected int prepareTime;

    public Beverage(String name, int prepareTime) {
        this.name = name;
        this.prepareTime = prepareTime;
    }

    public String getName() {
        return name;
    }

    public int getPrepareTime() {
        return prepareTime;
    }
}
