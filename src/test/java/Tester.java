import com.google.common.base.CaseFormat;
import com.llw.util.CollectionUtil;
import com.llw.util.FileUtil;
import com.llw.util.MathUtil;
import com.llw.util.PagingUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.File;
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
//            String str = "jdbc:mysql://localhost:3306/demo?characterEncoding=utf8&useSSL=true";
//            String[] fragments = str.split("\\?")[0].split("/");
//            System.out.println(fragments[fragments.length - 1]);

            System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "test"));
            System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "test_data"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
