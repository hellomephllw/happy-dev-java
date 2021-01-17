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
            String str = "a_b";
            System.out.println(str.split("_")[0]);
            System.out.println("a".split("_")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
