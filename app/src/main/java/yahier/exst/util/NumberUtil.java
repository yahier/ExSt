package yahier.exst.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtil {

	/**
	 * 判断是否手机号码
	 * @param phoneNo
	 * @return
	 */
	public static boolean isPhoneNo(String phoneNo) {
		Pattern p = Pattern.compile("^(1)\\d{10}$");
		Matcher m = p.matcher(phoneNo);
		System.out.println(m.matches() + "---");
		return m.matches();
	}
}
