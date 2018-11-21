import com.llw.util.CollectionUtil;
import com.llw.util.MathUtil;
import com.llw.util.PagingUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @discription:
 * @author: llw
 * @date: 2018-11-15
 */
public class Tester {

    public static void main(String[] args) {
        try {
//            System.out.println(MathUtil.subtract(1, 2, 3));
            PageRequest page = PagingUtil.buildJpaPageRequest(
                    1,
                    1,
                    CollectionUtil.stringMap().put("id", "desc"),
                    CollectionUtil.stringMap().put("name", "desc")
            );
            System.out.println(page);

//            Map<String, String> map = new HashMap<>();
//            map.put("name", "aaa");
//            map.put("age", "bbb");
//            System.out.println(map.entrySet().iterator().next());
//            System.out.println(map.values());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
