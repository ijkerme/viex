package start.module.myvim.utilities;

import java.io.File;
import java.util.Enumeration;
import java.util.StringTokenizer;
import org.openide.util.Enumerations;

/**
 *
 * @author jker
 */
public class VIEXUtil {
    
    public enum VERTION {
        FIVE, SIX
    };
    
    public static VERTION getNBVertion(String nbdirs) {
        
        Enumeration<Object> more;
        if (nbdirs != null) {
            more = new StringTokenizer(nbdirs, File.pathSeparator);
        } else {
            more = Enumerations.empty();
        }
        while (more.hasMoreElements()) {
            String s = (String) more.nextElement();
            if (s.endsWith("ide7"))
                return VERTION.FIVE;
            if (s.endsWith("ide8"))
                return VERTION.SIX;
        }
        return VERTION.FIVE;
    }
    
    public static String getNbdirs() {
        return System.getProperty("netbeans.dir");
    }
    
    public static String toHexValue(String s) {
        if (null == s)
            return "";
        String hexs = "";
        byte[] bytes = s.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            String v = Integer.toHexString(bytes[i]);
            //logger.info("Hex Value:" + v);
            if (v.length() > 2)
                hexs += v.substring(v.length() - 3);
            else
                hexs += v;
        }
        return hexs;
    }

}
