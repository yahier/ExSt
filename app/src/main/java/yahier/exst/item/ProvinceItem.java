package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

public class ProvinceItem implements Serializable {
	String provincename;
	String adcode;
	List<CityItem> citys;

	public String getProvincename() {
		return provincename;
	}

	public void setProvincename(String provincename) {
		this.provincename = provincename;
	}

	public String getAdcode() {
		return adcode;
	}

	public void setAdcode(String adcode) {
		this.adcode = adcode;
	}

	public List<CityItem> getCitys() {
		return citys;
	}

	public void setCitys(List<CityItem> citys) {
		this.citys = citys;
	}

}
