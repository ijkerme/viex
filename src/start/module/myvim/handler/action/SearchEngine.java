package start.module.myvim.handler.action;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import start.module.myvim.utilities.VIEXBundle;
import start.module.myvim.utilities.VIEXOptions;

/**
 * Example:
 *      <pre>
 *          SearchEngine engine = SearchEngine.getEngine();
 *          engine.setDirection(SearchEngine.NEXT);
 *          engine.setTarget(JTextComponent);
 *          engine.tracking(pattern, offset);
 *      </pre>
 *
 *
 * @author jker
 */
public class SearchEngine {
    
    public static final String NEXT = "next";
    public static final String PREV = "prev";
    
    //
    private static String direction;
    
    //
    private boolean found;
    
    //
    private int start;
    
    //
    private int end;
    
    private JTextComponent target;
    
    private static SearchEngine engine;
    
    private SearchEngine(){}
    
    public static SearchEngine getEngine() {
        if (engine == null) {
            engine = new SearchEngine();
        }
        direction = NEXT;
        return engine;
    }

    public boolean isIgnoreCase() {
        return VIEXOptions.getBoolean(VIEXOptions.GENERAL, VIEXBundle.getMessage("OptionsIgnoreCase"));
    }
    
    //\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD
    public void setDirection(String directionm) {
        this.direction = directionm;
    }
    
    public void setSource(JTextComponent targetm) {
        this.target = targetm;
    }
    
    public boolean isFound() {
        return this.found;
    }
    
    public int getStart() {
        return this.start;
    }
    
    public int getEnd() {
        return this.end;
    }
    
    public void tracking(String pattern, int offset) {
        if (target != null) {
            Document doc = target.getDocument();
            int docLength = doc.getLength();
            if (offset > docLength) {
                resetEngine();
                return;
            }
            if (!isValidPattern(pattern)) {
                resetEngine();
                return;
            }
            if (NEXT.equals(direction)) {
                matcher(pattern, offset, docLength - offset);
                if (!found){
                    matcher(pattern, 0, offset);
                }
            } else {
                matcher(pattern, 0, offset);
                if (!found) {
                    matcher(pattern, offset, docLength - offset);
                }
            }
        }
    }
    
    private void matcher(String pattern, int os, int len) {
        String data = null;
        try {
            data = target.getDocument().getText(os, len);
            Pattern p = null;
            if (isIgnoreCase())
                p = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
            else 
                p = Pattern.compile(pattern, Pattern.MULTILINE);
            Matcher m = p.matcher(data);
            if (m.find()) {
                do {
                    target.setCaretPosition(os + m.start());
                    start = os + m.start();
                    end = os + m.end();
                    found = true;
                    if (NEXT.equals(direction))
                        break;
                } while (m.find());
            } else {
                resetEngine();
            }
        } catch (BadLocationException ex) {
            //ex.printStackTrace();
        }
    }
    
    private boolean isValidPattern(String pattern) {
        try {
            Pattern p = Pattern.compile(pattern);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    private void resetEngine() {
        found = false;
        start = -1;
        end = -1;
    }
    
}
