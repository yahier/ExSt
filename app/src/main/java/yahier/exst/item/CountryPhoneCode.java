package yahier.exst.item;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/3/13.
 * 国家电话前缀
 */
public class CountryPhoneCode implements Serializable {
    int id;
    String country;
    String prefix;
    String area;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return "CountryPhoneCode{" +
                "area='" + area + '\'' +
                ", id=" + id +
                ", country='" + country + '\'' +
                ", prefix='" + prefix + '\'' +
                '}';
    }
}
