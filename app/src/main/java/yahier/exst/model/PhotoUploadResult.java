package yahier.exst.model;

import java.io.Serializable;

/**
 * 上传图片返回的结果
 * @author ruilin
 *
 */
public class PhotoUploadResult implements Serializable {
	private static final long serialVersionUID = 3671813263014547223L;
	
	public String filename;
	public int index;
	public String small;
	public String large;
	public String ori;
}