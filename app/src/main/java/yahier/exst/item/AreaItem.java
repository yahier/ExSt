package yahier.exst.item;

import java.io.Serializable;

public class AreaItem implements Serializable {
	String districtname;
	String adcode;

	public String getDistrictname() {
		return districtname;
	}

	public void setDistrictname(String districtname) {
		this.districtname = districtname;
	}

	public String getAdcode() {
		return adcode;
	}

	public void setAdcode(String adcode) {
		this.adcode = adcode;
	}

}
