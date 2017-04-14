package yahier.exst.model.express;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 快递100
 * @author ruilin
 *
 */
public class Kuaidi100 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2902080585589450207L;
	public String message;
	public String status;
	public String state;
	public ArrayList<Station> data;
}
