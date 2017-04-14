package yahier.exst.model.express;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import com.stbl.stbl.util.JSONHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 物流信息
 * @author ruilin
 *
 */
public class ExpressInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3787217977529776338L;

	public long orderid;
	public String expressno;
	public String companyname;
	public String companycode;
	public String expresstext;	// need to be parsed
	public String express; //快递信息
	
	public ArrayList<Station> stationList;

	public void toStationList(String express){
		if (express == null || express.equals("{}")) return;
		try {
			JSONObject expressObj = new JSONObject(express);
			JSONObject lastresult = expressObj.optJSONObject("lastresult");
			JSONArray data = lastresult.optJSONArray("data");
			stationList = JSONHelper.getList(data.toString(),Station.class);
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public Station getLastStation() {
		if (null != stationList && stationList.size() > 0) {
			return stationList.get(0);
		}
		return null;
	}

//	public static ArrayList<Station> parseStation(String expresstext) {
//		if (null == expresstext) return null;
//		Kuaidi100 kuaidi = JSONHelper.getObject(expresstext, Kuaidi100.class);
//		if (null != kuaidi && null != kuaidi.data) {
//			Collections.reverse(kuaidi.data); // 反转顺序
//			return kuaidi.data;
//		} else {
//			return null;
//		}
//	}

}
