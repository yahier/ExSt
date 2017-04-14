package yahier.exst.item;

import java.io.Serializable;
import java.util.List;

public class Rank1Group implements Serializable {

	Rank1Item myrankings;
	List<Rank1Item> rankings;

	public Rank1Item getMyrankings() {
		return myrankings;
	}

	public void setMyrankings(Rank1Item myrankings) {
		this.myrankings = myrankings;
	}

	public List<Rank1Item> getRankings() {
		return rankings;
	}

	public void setRankings(List<Rank1Item> rankings) {
		this.rankings = rankings;
	}

}
