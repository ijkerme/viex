package start.module.myvim.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import static start.module.myvim.utilities.VIEXOptions.*;

/**
 * The collection of the pair of chars for command '
 * 
 *
 * @author jker
 */
public class VIEXPairTable {

    public static enum CATEGORY {
        CHAR_PAIR, TEMPLATE_PAIR
    }
    private static final Map<String, String> pairs = new HashMap();
    private static final Map<String, String> templateMap = new HashMap();

    static {
        pairs.put("'", "'");
        pairs.put("\"", "\"");

        // Map implementated by the netbeans options for later
        templateMap.put("c", "Templates/Classes/Class.java");
        templateMap.put("i", "Templates/Classes/Interface.java");
        templateMap.put("e", "Templates/Classes/Enum.java");
        templateMap.put("a", "Templates/Classes/AnnotationType.java");
        templateMap.put("p", "Templates/Classes/Empty.java");
        templateMap.put("m", "Templates/Classes/Main.java");
        // Web
        templateMap.put("h", "Templates/JSP_Servlet/Html.html");
        templateMap.put("x", "Templates/JSP_Servlet/XHtml.xhtml");
        templateMap.put("j", "Templates/JSP_Servlet/JSP.jsp");
        templateMap.put("s", "Templates/JSP_Servlet/Servlet.java");
        templateMap.put("f", "Templates/JSP_Servlet/SimpleFilter.java");
    }

    private static VIEXPairTable instance;

    public static VIEXPairTable getInstance() {
        if (instance == null) {
            instance = new VIEXPairTable();
        }
        return instance;
    }

    public void initialize() {
        // Get chars pairs from the netbeans options
        load(CATEGORY.CHAR_PAIR);
        load(CATEGORY.TEMPLATE_PAIR);
    }

    private void load(CATEGORY cat) {
        Preferences node = null;
        try {
            if (cat == CATEGORY.CHAR_PAIR)
                node = getPreference(CHAR_PAIR);
            else
                node = getPreference(FILE_TEMPLATE);
            String[] keys = node.keys();
            for (String k : keys) {
                addPair(cat, k, node.get(k, ""));
            }
        } catch (BackingStoreException ex) {
            //
        }
    }

    public Map<String, String> getCharsPairMap() {
        return pairs;
    }

    public Map<String, String> getTeplateNameMap() {
        return templateMap;
    }

    /**
     *  Add a chars pairs in map
     * 
     * @param chars
     * @param mchars
     */
    public void addPair(CATEGORY cat, String key, String value) {
        if (cat == CATEGORY.CHAR_PAIR)
            pairs.put(key, value);
        else
            templateMap.put(key, value);
    }

    public String getPairValue(CATEGORY cat, String key) {
        if (cat == CATEGORY.CHAR_PAIR)
            return pairs.get(key);
        else
            return templateMap.get(key);
    }

}
