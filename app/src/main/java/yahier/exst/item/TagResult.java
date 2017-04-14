package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

public class TagResult implements Serializable {
	List<Tag> trades;
	List<Tag> professions;
	List<Tag> others;

	public List<Tag> getTrades() {
		return trades;
	}

	public void setTrades(List<Tag> trades) {
		this.trades = trades;
	}

	public List<Tag> getProfessions() {
		return professions;
	}

	public void setProfessions(List<Tag> professions) {
		this.professions = professions;
	}

	public List<Tag> getOthers() {
		return others;
	}

	public void setOthers(List<Tag> others) {
		this.others = others;
	}

}
