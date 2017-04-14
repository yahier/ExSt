package yahier.exst.util;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MyHandler extends DefaultHandler {
	final String tagProvice = "State";
	final String tagCity = "City";
	final String tagArea = "Region";

	List<String> list_province;
	List<String> list_city;
	List<String> list_area;

	public List<String> code_list_province;
	public List<String> code_list_city;
	public List<String> code_list_area;

	// 用来保证 只解析xml文件中的一部分。
	boolean isIn = false;
	// 选中的省份 初始为null，也用来判断用户 有没有选好省份
	public static String provinceName = "";
	public static String cityName = "";

	int type = 1;
	boolean isInProvince = false;
	boolean isInCity = false;

	public MyHandler(int type) {
		this.type = type;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		list_province = new ArrayList<String>();
		list_city = new ArrayList<String>();
		list_area = new ArrayList<String>();

		code_list_province = new ArrayList<String>();
		code_list_city = new ArrayList<String>();
		code_list_area = new ArrayList<String>();

	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (type == 1) {
			if (qName.equals("State")) {
				String name = attributes.getValue("Name");
				list_province.add(name);
				name = attributes.getValue("id");
				code_list_province.add(name);

			}
		} else if (type == 2) {
			if (qName.equals("State")) {
				String name = attributes.getValue("Name");
				if (name.equals(provinceName)) {
					isIn = true;
				}
			}

			if (isIn) {
				if (qName.equals("City")) {
					String name = attributes.getValue("Name");
					list_city.add(name);

					name = attributes.getValue("id");
					code_list_city.add(name);
				}
			}
		} else if (type == 3) {
			if (qName.equals("State")) {
				String name = attributes.getValue("Name");
				if (name.equals(provinceName)) {
					isInProvince = true;
				}
			}

			if (isInProvince) {
				if (qName.equals("City")) {
					String name = attributes.getValue("Name");
					if (name.equals(cityName)) {
						isInCity = true;
					}
				}
			}

			if (isInCity) {
				if (qName.equals("Region")) {
					String name = attributes.getValue("Name");
					list_area.add(name);
					name = attributes.getValue("id");
					code_list_area.add(name);
				}
			}
		}

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);

		if (type == 2) {
			if (isIn) {
				if (qName.equals("State")) {
					isIn = false;
				}
			}
		}

		if (type == 3) {
			if (isInCity) {
				if (qName.equals("City")) {
					isInProvince = false;
					isInCity = false;
				}
			}
		}
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	/**
	 * 得到解析后 的省份数据
	 */
	public List getProvinceList() {
		return list_province;
	}

	/**
	 * 得到解析后的 城市数据
	 */
	public List getCityList() {
		return list_city;
	}

	/**
	 * 得到解析后的 区域数据
	 */
	public List getAreaList() {
		//System.out.println("区域的数目是   " + list_area.size());
		return list_area;
	}
}
