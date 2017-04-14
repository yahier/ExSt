package yahier.exst.model;

import java.io.Serializable;

/**
 * Created by tnitf on 2016/3/12.
 */
public class SearchHistory implements Serializable {

    private static final long serialVersionUID = 475583791667081633L;

    private int id;
    private String word;
    private long time;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
