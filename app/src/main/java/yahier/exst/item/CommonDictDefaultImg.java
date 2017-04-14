package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/7/15.
 */
public class CommonDictDefaultImg implements Serializable{

    String discussion;
    String assistant;
    String group;
    String user;


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDiscussion() {
        return discussion;
    }

    public void setDiscussion(String discussion) {
        this.discussion = discussion;
    }

    public String getAssistant() {
        return assistant;
    }

    public void setAssistant(String assistant) {
        this.assistant = assistant;
    }
}
