package space.ikostylev.scanningcodeapp;

class Product {
    private String name;
    private String info;
    private float cost;
    private long barCode;

    public Product(String name, String info, float cost, long barCode) {
        this.name = name;
        this.info = info;
        this.cost = cost;
        this.barCode = barCode;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public long getBarCode() {
        return barCode;
    }

    public void setBarCode(long barCode) {
        this.barCode = barCode;
    }
}
