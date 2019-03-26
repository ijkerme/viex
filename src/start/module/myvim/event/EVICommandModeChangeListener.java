package start.module.myvim.event;

import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorUtilities;
import org.netbeans.editor.BaseKit;
import org.netbeans.editor.EditorUI;
import org.netbeans.editor.StatusBar;
import org.netbeans.editor.Utilities;
import org.openide.util.NbBundle;
import start.module.myvim.handler.CommandMode;
import start.module.myvim.handler.action.quicklist.QuickNavagatorWorker;
import start.module.myvim.handler.action.quicklist.QuickNavagatorFactory;
import start.module.myvim.quicklist.VIEXPopup;
import start.module.myvim.state.EditorState;
import start.module.myvim.state.Mode;
import static start.module.myvim.state.Mode.*;
import start.module.myvim.state.VIEXRepository;
import start.module.myvim.utilities.EVIConstant;
import start.module.myvim.utilities.OverwriteCaret;

/**
 * Listener for command mode changed
 *
 * @author jker
 */
public class EVICommandModeChangeListener implements CommandModeChangeListener {
    
    private static Caret overCaret = new OverwriteCaret();
    
    public void modeChanged(CommandModeChangeEvent e) {
        //System.err.println("mode is changed");
        final JTextComponent component = (JTextComponent)e.getSource();
        if (component != null) {
            if (null != Utilities.getEditorUI(component)) {
                StatusBar sb = Utilities.getEditorUI(component).getStatusBar();
                final JLabel label = sb.getCellByName(EVIConstant.EVI);
                if (null != label) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            //CommandMode mode = CommandModeOperator.getOperator().get(component);
                            EditorState estate = VIEXRepository.getRespository().getEditorState(component);
                            if (estate != null) {
                                CommandMode mode = estate.getCommandMode();
                                label.setText(mode.getDescription());
                            }
                        }
                    });
                }
            }
            if (e.getOldMode() == MISC_MODE) {
            // if has task is executing then cancel...
                QuickNavagatorWorker work = QuickNavagatorFactory.getNavagatorFactory().getNavagatorWorker();
                if (work != null) {
                    //System.out.println("cancel task...");
                    //System.out.println("work:" + work);
                    if (work.isRunning())
                        work.cancel();
                    else {
                        VIEXPopup.getPopup().hide();
                    }
                    //System.out.println(work.isRunning());
                }
            }
            switchCursor(component, e.getNewMode());
        }
    }
    
    private void switchCursor(JTextComponent editorUI, Mode mode) {
        //EditorUI editorUI = org.netbeans.editor.Utilities.getEditorUI(editor);
        if (editorUI != null) {
            Boolean overwrite = (Boolean) editorUI.getClientProperty(EditorUtilities.CARET_OVERWRITE_MODE_PROPERTY);
            if (mode == COMMAND_MODE) {
                if (overwrite == null) {
                    editorUI.putClientProperty(EditorUtilities.CARET_OVERWRITE_MODE_PROPERTY, true);
                    //changeTypingModeText(editorUI, EVIConstant.NONE);
                } else if (!overwrite) {
                    editorUI.putClientProperty(EditorUtilities.CARET_OVERWRITE_MODE_PROPERTY, true);
                    //changeTypingModeText(editorUI, EVIConstant.NONE);
                }
                //changeTypingModeText(editorUI, EVIConstant.NONE);
            } else if (mode == INSERT_MODE) {
                /*if (overwrite == null) {
                    editorUI.putProperty(EditorUI.OVERWRITE_MODE_PROPERTY, false);
                    changeTypingModeText(editorUI, getText(StatusBar.INSERT_LOCALE));
                } else */
                if (overwrite != null && overwrite) {
                    editorUI.putClientProperty(EditorUtilities.CARET_OVERWRITE_MODE_PROPERTY, false);
                    //changeTypingModeText(editorUI, getText(StatusBar.INSERT_LOCALE));
                }
            }
        }
    }
    
    private String getText(String bundleKey) {
        ResourceBundle bundle = NbBundle.getBundle(BaseKit.class);
        return bundle.getString(bundleKey);
    }
    
    private void changeTypingModeText(final EditorUI editorUI, final String text) {
        final StatusBar sb = editorUI.getStatusBar();
        if (sb != null) {
            if (SwingUtilities.isEventDispatchThread()) {
                sb.getCellByName(StatusBar.CELL_TYPING_MODE).setText(text);
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        sb.getCellByName(StatusBar.CELL_TYPING_MODE).setText(text);
                    }
                });
            }
        }
    }
    
    /*public void selectCaret(JTextComponent component, Mode mode) {
     
        Caret newCaret = null;
        EVIKeyListener kl = ComponentUtil.getEVIKeyListener();
        Caret oldCaret = kl.getOriginCaret(); //ExtCaret
        Caret curCaret = component.getCaret(); //
     
        if (oldCaret == null || curCaret == null) {
            return ;
        }
     
        if (mode == COMMAND_MODE) {
            if (curCaret != overCaret) {
                newCaret = overCaret;
                changeCaret(newCaret, component);
            }
        } else if (mode == INSERT_MODE) {
            if (curCaret != oldCaret) {
                newCaret = oldCaret;
                changeCaret(newCaret, component);
            }
        }
    }
     
    private void changeCaret(Caret newCaret, JTextComponent component) {
        Caret curCaret = component.getCaret();
        int dot = curCaret.getDot();
        int blink = curCaret.getBlinkRate();
        boolean visible = curCaret.isVisible();
        if (visible) {
            curCaret.setVisible(false);
        }
        component.setCaret(newCaret);
        newCaret.setDot(dot);
        newCaret.setBlinkRate(blink);
        newCaret.setVisible(true);
    }*/
    
}
