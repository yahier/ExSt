package yahier.exst.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 卖家订单
 * @author ruilin
 *
 */
public class SellerOrderItem<T> implements Serializable {

	public String url;
	public OrderState state;
	
	public long id;
	public long orderid;
	public int orderstate;
	public String orderstatename;
	public T products;
	public ArrayList<OrderProduct> productsList;
	public Address addressinfo;
	
	public float realpayamount;
	public String shopname;
	public String createtime;
	public String modifytime;
}

//"id": 942,
//"orderid": 31512241404537660,
//"orderstate": 29999,
//"products": [
//    {
//        "goodsid": 1450835532602221,
//        "skuid": 2450835532602221,
//        "imgurl": "http://120.25.150.37:10099/",
//        "price": 43.00,
//        "count": 1
//    },
//    {
//        "goodsid": 1450868559252221,
//        "skuid": 2450868559252221,
//        "imgurl": "http://120.25.150.37:10099/",
//        "price": 324.00,
//        "count": 1
//    }
//],
//"addressinfo": {
//    "addressid": 426,
//    "userid": 14471346108766,
//    "provinceid": "1",
//    "provincename": "",
//    "cityid": "1",
//    "cityname": "",
//    "districtid": "1",
//    "postcode": "1",
//    "address": "1",
//    "username": "11",
//    "phone": "11",
//    "isdefault": 0
//},
//"realpayamount": 367.00,
//"shopname": "",
//"createtime": 1450937093,
//"modifytime": 1450961627
//},
