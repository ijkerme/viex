package start.module.myvim.test;

import start.module.myvim.utilities.TextUtil;
import static java.lang.System.*;

/**
 *
 * @author jker
 */
public class TestTextUtil {

    public static void main(String args[]) {
        testN2A();
    }

    public static void testN2A() {
        TextUtil tu = new TextUtil();
        out.println(tu.native2Ascii("abc"));

    }
}
