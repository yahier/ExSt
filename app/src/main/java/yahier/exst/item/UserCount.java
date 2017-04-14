package yahier.exst.item;

import java.io.Serializable;

public class UserCount implements Serializable {
	int tudi_count;
	int attention_count;
	int fans_count;

	public int getTudi_count() {
		return tudi_count;
	}

	public void setTudi_count(int tudi_count) {
		this.tudi_count = tudi_count;
	}

	public int getAttention_count() {
		return attention_count;
	}

	public void setAttention_count(int attention_count) {
		this.attention_count = attention_count;
	}

	public int getFans_count() {
		return fans_count;
	}

	public void setFans_count(int fans_count) {
		this.fans_count = fans_count;
	}

}
