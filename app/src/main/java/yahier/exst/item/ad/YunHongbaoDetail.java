package yahier.exst.item.ad;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28.
 */

public class YunHongbaoDetail implements Serializable{
    String code;
    String message;
    YunHongbaoData data;
    String request_id;

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public YunHongbaoData getData() {
        return data;
    }

    public void setData(YunHongbaoData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
