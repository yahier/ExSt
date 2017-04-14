package yahier.exst.utils;

public class StringUtils {
	
	/**
	 * 是否为Empty
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value){
		if(value == null)
			return true;
		if(value.trim().equals(""))
			return true;
		return false;
	}

	/** 解析Url参数 */
	public static String analysisUrlParam(String url,String param){
		try{
			String cutUrl = url.substring(url.indexOf("?")+1, url.length());
			String[] arrays = cutUrl.split("&");
			for(String str : arrays){
				if(str.startsWith(param)){
					String value = str.substring(str.indexOf("=")+1, str.length());
					return value;
				}
			}
		}catch(Exception e){

		}
		return "";

	}
	
}
