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
            File file = new File(FileUtil.getLocalRootAbsolutePath() + "/.gitignore");
            System.out.println(file);
            System.out.println(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
