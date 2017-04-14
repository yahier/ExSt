package yahier.exst.ui.DirectScreen.homeNotify.callback;

import com.tencent.TIMMessage;

import java.util.List;

/**
 * Created by meteorshower on 16/4/10.
 */
public interface MsgRecordListener {

    public void msgError(int errorValue);

    public void msgSuccess(List<TIMMessage> timMessages);
}
