package start.module.myvim.state;

import java.util.HashMap;
import java.util.Map;
import javax.swing.text.JTextComponent;

/**
 *
 * @author jker
 */
public class VIEXRepository {
    
    private Map<JTextComponent, EditorState> repository = new HashMap<JTextComponent, EditorState>();

    private static VIEXRepository instance = new VIEXRepository();

    private VIEXRepository(){
        
    }

    public static VIEXRepository getRespository() {
        return instance;
    }

    public void addEditorState(JTextComponent component, EditorState estate) {
        repository.put(component, estate);
    }

    public void removeEditorState(JTextComponent component) {
        repository.remove(component);
    }
    
    public EditorState getEditorState(JTextComponent component) {
        return repository.get(component);
    }

    public void clean() {
        repository.clear();
    }
    
}
