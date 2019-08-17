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
            Pattern p = Pattern.compile("@[a-zA-Z0-9]+");
            Matcher m = p.matcher("select * from @FamilyName where id >= floor(((select max(id) from @FamilyName) - (select min(id) from @FamilyName) + 1) * rand()) + (select min(id) from @FamilyName) limit 1;");

            while(m.find()) {
                System.out.println(m.group());//group方法返回由以前匹配操作所匹配的输入子序列。
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
