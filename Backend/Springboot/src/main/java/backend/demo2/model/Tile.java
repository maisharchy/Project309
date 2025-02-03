package backend.demo2.model;

public class Tile {

    private int id;
    private String color;
    private boolean flipped;
    private boolean active;

    public Tile(int id, String color) {
        this.id = id;
        this.color = color;
        this.flipped = false;
        this.active = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void flip() {
        this.flipped = !this.flipped;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
