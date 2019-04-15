import com.llw.util.CollectionUtil;
import com.llw.util.EncryptionUtil;
import com.llw.util.IpUtil;
import com.llw.util.NetUtil;

import java.util.*;

/**
 * @description:
 * @author: llw
 * @date: 2018-11-15
 */
public class Tester {

    public static void main(String[] args) {
        try {
            List list = new ArrayList();
            list.add(1);
            list.add(2);
            list.add(1, "a");
            list.add(1, "b");

            System.out.println(list);

            List l = new LinkedList();

            Map map = new HashMap();
            System.out.println(map.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
