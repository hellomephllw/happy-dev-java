import com.llw.util.CollectionUtil;
import com.llw.util.EncryptionUtil;
import com.llw.util.IpUtil;
import com.llw.util.NetUtil;

/**
 * @description:
 * @author: llw
 * @date: 2018-11-15
 */
public class Tester {

    public static void main(String[] args) {
        try {
//            String ipStr = "1.1.200.200";
//            long ipLong = IpUtil.ipToLong(ipStr);
//            System.out.println(ipLong);
//            System.out.println(IpUtil.longToIp(ipLong));

            String result = NetUtil.sendHttpRequest(
                    CollectionUtil.fieldMap()
                            .put("url", "http://localhost:8080/communal/area/provinces")
                            .put("type", "post")
                            .put("contentType", "application/x-www-form-urlencoded")
                            .put("body", "name=123&age=55")
                            .build());

            System.out.println(result);
        } catch (Exception e) {
            System.out.println("=====");
            e.printStackTrace();
        }
    }

}

class Temp {

    public static void exe() {
        try {
            throw new Exception("错误");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
