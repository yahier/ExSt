package yahier.exst.api.data;

import java.io.Serializable;

/**
 * Created by meteorshower on 16/3/3.
 */
public class LiveRoomToken implements Serializable {

    /**
     * usersig : eJxljs1Og0AYRfc8BWGrsTMDwzAmLhooLbVEq0VjN4TAUL80DH-TSjG*e5XYSOL6nNx7PjVd143N6vkmSdPyIFWsTpUw9FvdQMb1H6wqyOJExWaT-YOiq6ARcZIr0QwQU0oJQmMHMiEV5HAxLGoyCyFiWzZmI6-N9vFw9qt9ryDKLD5WYDfAcBa5wdqFR49PI3qlQijwMnC2EyR94i72pX-k0Xtfrz5gabPam0yD3cuhxZvugW*7uTNb8JLJ1ye-r5UvMs5P86K-z2UoUdp6b3ejSwWFuHTbDsEMj4OOommhlINAEKaYmD-ZyNC*tDOduF2C
     * sdkappid : 1400005749
     * accounttype : 3306
     * identifier : 14537400264617
     */

    private String usersig;
    private String sdkappid;
    private String accounttype;
    private String identifier;

    public void setUsersig(String usersig) {
        this.usersig = usersig;
    }

    public void setSdkappid(String sdkappid) {
        this.sdkappid = sdkappid;
    }

    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUsersig() {
        return usersig;
    }

    public String getSdkappid() {
        return sdkappid;
    }

    public String getAccounttype() {
        return accounttype;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "LiveRoomToken{" +
                "accounttype='" + accounttype + '\'' +
                ", usersig='" + usersig + '\'' +
                ", sdkappid='" + sdkappid + '\'' +
                ", identifier='" + identifier + '\'' +
                '}';
    }
}
