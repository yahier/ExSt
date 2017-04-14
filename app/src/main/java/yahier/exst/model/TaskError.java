package yahier.exst.model;

import java.io.Serializable;

/**
 * Created by tnitf on 2016/4/12.
 */
public class TaskError extends com.stbl.base.library.task.TaskError implements Serializable {

    public TaskError(String msg) {
        super(msg);
    }

    public TaskError(int code, String msg) {
        super(code, msg);
    }

    public TaskError(String strCode, String msg) {
        super(strCode, msg);
    }

    public String getMessage() {
        if (msg == null) {
            return "";
        }
        return msg;
    }

}
