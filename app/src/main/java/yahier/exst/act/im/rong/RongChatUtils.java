package yahier.exst.act.im.rong;


public class RongChatUtils {
	
	private static String conversationPrivate = "/conversation/private";
	private static String conversationGroup = "/conversation/group";
	
	/** 是否是群聊 */
	public static boolean isChatGroup(String value){
		return value.equals(conversationGroup);
	}

}
