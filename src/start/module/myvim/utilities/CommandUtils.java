package start.module.myvim.utilities;

import java.awt.event.KeyEvent;

/**
 *
 * @author jker
 */
public class CommandUtils {
    
    public static boolean isStartWithColon(String value) {
        return (value != null) && value.startsWith(":");
    }
    
    public static boolean isStartWithSlash(String value) {
        return (value != null) && value.startsWith("/");
    }
  
    public static boolean isEndWithEnter(String value) {
        if (null == value)
            return false;
        int len = value.length();
        return  len > 0 ? value.charAt(len - 1) == KeyEvent.VK_ENTER : false;
    }
    
    public static boolean isVFirstLetter(String value) {
        return (value != null) && value.startsWith("v");
    }
    
    public static boolean isDigitalFirstLetter(String value) {
        return (value != null) && Character.isDigit(value.charAt(0));
    }
    
    public static boolean isEndWithBackspace(String value) {
        int len = value.length();
        if ((value != null) && (len > 0)) {
            return (value.charAt(len - 1) == KeyEvent.VK_BACK_SPACE);
        }
        return false;
    }

    public static boolean isEndWithSpace(String value) {
        if (value == null)
            return false;
        return value.charAt(value.length() - 1) == KeyEvent.VK_SPACE;
    }
    
    public static boolean isEndWithTab(String value) {
        if (value == null)
            return false;
        return value.charAt(value.length() - 1) == KeyEvent.VK_TAB;
    }
}
