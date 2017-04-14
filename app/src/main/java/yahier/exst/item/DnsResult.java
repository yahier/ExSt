package yahier.exst.item;

import java.util.List;

/**
 * Created by lenovo on 2016/4/23.
 */

/**
 * 解析的dns结果
 */
public class DnsResult {
    String host;
    int ttl;
    List<String> ips;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List ips) {
        this.ips = ips;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
