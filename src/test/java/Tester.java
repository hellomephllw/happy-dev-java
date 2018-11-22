import com.llw.util.CollectionUtil;
import com.llw.util.MathUtil;
import com.llw.util.PagingUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: llw
 * @date: 2018-11-15
 */
public class Tester {

    public static void main(String[] args) {
        try {
            String name = "12345.java";
            System.out.println(name.split("\\.")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
