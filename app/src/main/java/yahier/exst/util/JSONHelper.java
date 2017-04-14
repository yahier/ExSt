package yahier.exst.util;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

public class JSONHelper {
	private static final String TAG = "JSONHelper";

	/*
	 * private static final GsonBuilder gsonb; static { gsonb = new
	 * GsonBuilder(); // gsonb.registerTypeAdapter(Date.class, new
	 * JsonDeserializer<Date>() { // // @Override // public Date
	 * deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext
	 * context) throws JsonParseException { // String date =
	 * json.getAsJsonPrimitive().getAsString(); // String JSONDateToMilliseconds
	 * = "\\/(Date\\((.*?)(\\+.*)?\\))\\/"; // Pattern pattern =
	 * Pattern.compile(JSONDateToMilliseconds); // Matcher matcher =
	 * pattern.matcher(date); // String result = matcher.replaceAll("$2"); //
	 * try { // return new Date(new Long(result)); // } catch (Exception e) { //
	 * throw new RuntimeException(e); // } // } // }); }
	 */
	public static <T> ArrayList<T> getList(String JSONString, Class<T> classOfT) {
		if (null == JSONString)
			return null;
		ArrayList<T> array = null;
		try {
			array = (ArrayList<T>) JSON.parseArray(JSONString, classOfT);
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (NumberFormatException e){
			e.printStackTrace();
		}catch (ClassCastException e){
			e.printStackTrace();
		}
		return array;
		/*
		 * ArrayList<T> data = new ArrayList<T>(); JSONArray array = null; try {
		 * array = new JSONArray(JSONString); } catch (final JSONException e) {
		 * Log.e(TAG, "Error in getList - " + e); }
		 * 
		 * if (array != null) { data = (ArrayList<T>)
		 * JSON.parseArray(JSONString, classOfT); if (list.size() > 0) {
		 * LogUtil.logE("orderId3:" + list.get(0).orderid); }
		 * 
		 * final Gson gson = gsonb.create(); for (int i = 0; i < array.length();
		 * i++) { try { final JSONObject object = array.getJSONObject(i); final
		 * T entity = gson.fromJson(object.toString(), classOfT);
		 * data.add(entity); } catch (final JSONException e) { LogUtil.logE(TAG,
		 * "Error in getList - " + e); } } }
		 * 
		 * return data;
		 */
	}

	public static <T> T getObject(String JSONString, Class<T> classOfT) {
		T entity = null;
		try {
			entity = JSON.parseObject(JSONString, classOfT);
		} catch (JSONException e) {
			e.printStackTrace();
		}
//		LogUtil.logE("json---:"+JSONString+"-------classoft --:"+classOfT+"-----  entity--:"+entity);
		return entity;
	}

	public static <T> T getObject(String JSONString, Type classOfT) {
		T entity = null;
		try {
			entity = JSON.parseObject(JSONString, classOfT);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		/*
		 * final Gson gson = gsonb.create(); try { //entity =
		 * gson.fromJson(JSONString, classOfT); entity =
		 * JSON.parseObject(JSONString, classOfT); } catch (Exception e) {
		 * LogUtil.logE("JsonHelper :JsonPraseError! "+e.getMessage()); return
		 * null; }
		 */
		return entity;
	}

	public static String getStringFromObject(Object src) {
		return JSON.toJSONString(src);
		/*
		 * final Gson gson = gsonb.create(); String result = gson.toJson(src);
		 * return result;
		 */
	}
//
	/**
	 * 融云消息的解析
	 *
	 * @JSONObject json
	 * @return
	 */
	public static String getExParams(JSONObject jsonOri) {
		JSONObject json;
		try {
			json = new JSONObject(jsonOri.toString());
			return json.getString("ex_params");
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}
