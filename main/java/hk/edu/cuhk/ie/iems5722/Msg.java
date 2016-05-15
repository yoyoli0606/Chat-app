package hk.edu.cuhk.ie.iems5722;

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;

    private String content;
    private String time;
   // private String namename;
    private int type;

    public Msg(String content, String time, int type) {
        this.content = content;
        this.time =time;
        this.type = type;
        //this.namename = name;
    }

    public String getContent() {
        return content;
    }

    public String getTime(){
        return time;
    }

    public int getType() {
        return type;
    }
}
