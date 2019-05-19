import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.DES;
import org.junit.Test;

/**
 * @author keray
 * @date 2019/05/19 14:23
 */
public class AESTest {

    private static final AES AES = new AES("1111111111111111".getBytes());

    @Test
    public void desTest() {
        System.out.println(AES.encryptBase64("1234"));
        System.out.println(AES.decryptStr(AES.encryptBase64("1234")));
    }
}
