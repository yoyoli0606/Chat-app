package hk.edu.cuhk.ie.iems5722;

/**
 * Created by HH on 2016/2/27.
 */
public class Room {
    private String name;
    private String id;
    public Room(String name ,String id){
        this.name = name;
        this.id = id;
    }
    protected String getName(){
        return this.name;
    }
    protected String getId(){
        return this.id;
    }
}
