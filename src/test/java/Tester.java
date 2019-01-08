import com.llw.util.EncryptionUtil;

/**
 * @description:
 * @author: llw
 * @date: 2018-11-15
 */
public class Tester {

    public static void main(String[] args) {
        try {
            String key = "1234567890";
            String result = EncryptionUtil.desEncode("12345", key);

            System.out.println(result);

            String source = EncryptionUtil.desDecode(result, key);

            System.out.println(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
