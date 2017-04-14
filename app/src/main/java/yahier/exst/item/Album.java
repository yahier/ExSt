package yahier.exst.item;

import java.io.Serializable;

/**
 * 相册
 * 
 * @author lenovo
 * 
 */
public class Album implements Serializable {
	int albumid;
	String albumname;
	String albumdesc;
	long createtime;

	public int getAlbumid() {
		return albumid;
	}

	public void setAlbumid(int albumid) {
		this.albumid = albumid;
	}

	public String getAlbumname() {
		return albumname;
	}

	public void setAlbumname(String albumname) {
		this.albumname = albumname;
	}

	public String getAlbumdesc() {
		return albumdesc;
	}

	public void setAlbumdesc(String albumdesc) {
		this.albumdesc = albumdesc;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

}
