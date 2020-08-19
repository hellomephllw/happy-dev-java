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
            List<String> list = new ArrayList<>();
            list.add("a");
            list.add("b");
            list.add("c");

            String c = list.get(2);
            list.remove(c);
            list.add(0, c);

            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
