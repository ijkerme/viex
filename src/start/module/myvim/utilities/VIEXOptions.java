package start.module.myvim.utilities;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.table.DefaultTableModel;
import org.openide.util.NbPreferences;

/**
 *
 * @author jker
 */
public class VIEXOptions {

    public static final String GENERAL = "General";
    public static final String CHAR_PAIR = "CharPair";
    public static final String FILE_TEMPLATE = "FileTemplate";

    
    private static Preferences prefs = getPreferences();

    /**
     *  Make a osgi framework lib with specified values
     *
     * @param values
     */
    public static Preferences getPreference(String name) {
        return prefs.node(name);
    }

    /**
     *  Remove a osgi framework lib
     *
     * @param name  osgi lib name
     */
    public static void removePreference(String name) {
        Preferences prefers = getPreferences();
        Preferences node = prefers.node(name);
        try {
            node.removeNode();
        } catch (BackingStoreException ex) {
        }
    }


    public static void loadTablePref(Preferences node, DefaultTableModel model) {
        try {
            int row = 0;
            int col = 0;
            String[] keys = node.keys();
            for (String k : keys) {
                String v = node.get(k, null);
                if (v != null) {
                    if (row >= model.getRowCount()) {
                        model.insertRow(row, new Object[] {k, v});
                    } else {
                        col = 0;
                        model.setValueAt(k, row, col);
                        col++;
                        model.setValueAt(v, row, col);
                    }
                    row++;
                }
            }
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *  Get osgi preferences for module
     *
     * @return
     */
    public static Preferences getPreferences() {
        Preferences prefers = NbPreferences.forModule(VIEXOptions.class);
        return prefers;
    }

    public static String getVIEXOption(String node, String name, String def) {
        Preferences pref = getPreference(node);
        return pref.get(name, def);
    }

    public static void putVIEXOption(String node, String name, String value) {
        Preferences pref = getPreference(node);
        pref.put(name, value);
    }

    public static boolean getBoolean(String node, String name) {
        Preferences pref = getPreference(node);
        return pref.getBoolean(name, true);
    }

    public static void putBoolean(String node, String name, boolean value) {
        Preferences pref = getPreference(node);
        pref.putBoolean(name, value);
    }

    public static double getDouble(String node, String name, double def) {
        Preferences pref = getPreference(node);
        return pref.getDouble(name, def);
    }

    public static void putDouble(String node, String name, double value) {
        Preferences pref = getPreference(node);
        pref.putDouble(name, value);
    }

    public static int getInt(String node, String name, int def) {
        Preferences pref = getPreference(node);
        return pref.getInt(name, def);
    }

    public static void putInt(String node, String name, int value) {
        Preferences pref = getPreference(node);
        pref.putInt(name, value);
    }

    public static float getFloat(String node, String name, float def) {
        Preferences pref = getPreference(node);
        return pref.getFloat(name, def);
    }

    public static void putFloat(String node, String name, float value) {
        Preferences pref = getPreference(node);
        pref.putFloat(name, value);
    }
    
}
