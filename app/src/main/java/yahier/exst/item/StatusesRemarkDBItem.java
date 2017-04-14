package yahier.exst.item;

/**
 * Created by lenovo on 2016/7/26.
 */
public class StatusesRemarkDBItem {
    long statusesId;
    long commentId;
    long user1Id;
    String user1Name;
    long user2Id;
    String user2Name;
    String remarkContent;
    long remarkTime;

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getUser2Name() {
        return user2Name;
    }

    public void setUser2Name(String user2Name) {
        this.user2Name = user2Name;
    }

    public long getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(long user2Id) {
        this.user2Id = user2Id;
    }

    public String getUser1Name() {
        return user1Name;
    }

    public void setUser1Name(String user1Name) {
        this.user1Name = user1Name;
    }

    public long getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(long user1Id) {
        this.user1Id = user1Id;
    }

    public long getStatusesId() {
        return statusesId;
    }

    public void setStatusesId(long statusesId) {
        this.statusesId = statusesId;
    }

    public long getRemarkTime() {
        return remarkTime;
    }

    public void setRemarkTime(long remarkTime) {
        this.remarkTime = remarkTime;
    }

    public String getRemarkContent() {
        return remarkContent;
    }

    public void setRemarkContent(String remarkContent) {
        this.remarkContent = remarkContent;
    }
}
