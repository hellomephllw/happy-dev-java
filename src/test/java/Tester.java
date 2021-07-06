import com.happy.util.CollectionUtil;
import com.happy.util.RegexUtil;

import java.io.File;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: happy
 * @date: 2018-11-15
 */
public class Tester {

    public static void main(String[] args) {
        try {
            System.out.println(RegexUtil.match(".+\\sorder\\sby\\s.+\\slimit\\s.+", "abc"));
            String str = "select (select * from b order by aa limit 1,1) from test where a=1 order by name limit 1,1;";
            System.out.println(RegexUtil.match(".+\\sorder\\sby\\s.+\\slimit\\s.+", str));
            System.out.println(RegexUtil.match(".+(\\sorder\\sby\\s.+\\slimit\\s.+)$", str));
            String[] results = str.split("\\sorder\\sby\\s.+\\slimit\\s\\d+,\\d+");
            System.out.println(results.length);
            System.out.println(results[0]);
            System.out.println(results[1]);
//            System.out.println(results[2]);

            System.out.println("============");
            String[] fragments = str.split("\\s+");
            int count = 0;
            List<String> newFragments = new LinkedList<>();
            for (String fragment : fragments) {
                if (fragment.equals("order")) ++count;
                if (count > 1) break;
                newFragments.add(fragment);
                System.out.println(fragment);
            }

            System.out.println(String.join(" ", newFragments));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
