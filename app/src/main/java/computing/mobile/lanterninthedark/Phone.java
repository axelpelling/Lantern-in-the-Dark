package computing.mobile.lanterninthedark;

/**
 * Created by Axel on 2014-12-03.
 */
public class Phone {

    private int x;
    private int y;

    private int id;

    private boolean played;

    public Phone(int id){
        x = -1;
        y = -1;
        this.id = id;
        played  = false;
    }

    public Phone(int id, int x, int y){
        this.x = x;
        this.y = y;
        this.id = id;
        played  = false;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString(){
        return "ID: " + id + " x: " + x + " y: " + y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getPlayed(){
        return played;
    }

    public void setPlayed(boolean input){
        played = input;
    }

}
