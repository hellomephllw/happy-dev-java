import com.llw.util.EncryptionUtil;
import com.llw.util.IpUtil;

/**
 * @description:
 * @author: llw
 * @date: 2018-11-15
 */
public class Tester {

    public static void main(String[] args) {
        try {
            String ipStr = "1.1.200.200";
            long ipLong = IpUtil.ipToLong(ipStr);
            System.out.println(ipLong);
            System.out.println(IpUtil.longToIp(ipLong));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
