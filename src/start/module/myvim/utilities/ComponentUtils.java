package start.module.myvim.utilities;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JEditorPane;
import org.openide.cookies.EditorCookie;
import start.module.myvim.VIEXInitialSetting;
import start.module.myvim.event.EVICommandModeChangeListener;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import org.netbeans.editor.EditorUI;
import org.netbeans.editor.Utilities;
import org.openide.awt.StatusLineElementProvider;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import start.module.myvim.handler.CommandMode;
import start.module.myvim.handler.action.NBActionFacade;
import start.module.myvim.highlight.EVIHighlightPainter;
import start.module.myvim.state.EditorState;
import static start.module.myvim.state.Mode.*;
import static start.module.myvim.state.CommandModeState.*;
import static start.module.myvim.state.VIEXRepository.*;

/**
 *
 * @author jker
 */
public class ComponentUtils {
    
    private static final Logger logger = Logger.getLogger("ComponentUtil");
    
    private static final int VISIABLE_COLS = 30;
    private static final String PADDING_CHARS = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    
    private static Action bskeyAction = null;
    
    private static EVICommandModeChangeListener cmcl = new EVICommandModeChangeListener();
    
    public static EVICommandModeChangeListener getCommandModeChangeListener() {
        return cmcl;
    }
    
    /**
     * Find most active JTextComponent component in window.
     * <tt>Warning:</tt> if it be could't work then other functionals also get errors.
     *
     * @return  JTextComponent, return <tt>null</tt> if most active JTextComponent not found.
     */
    public static JTextComponent getMostActiveComponent() {
        TopComponent tc = TopComponent.getRegistry().getActivated();
        if (tc != null) {
            /*MultiViewHandler viewHandler = MultiViews.findMultiViewHandler(tc);
            if (viewHandler != null) {
                MultiViewPerspective[] views = viewHandler.getPerspectives();
                viewHandler.requestActive(views[views.length - 1]); // For listening web.xml MultiViewEditor
            }*/
            Node[] nodes = tc.getActivatedNodes();
            if (nodes == null)
                return null;
            for (int i = 0; i < nodes.length; i++) {
                EditorCookie ec = (EditorCookie)nodes[i].getCookie(EditorCookie.class);  //not enough, EditorCookie
                if (ec != null) {
                    try {
                        JEditorPane[] panes = getOpenedPanes(ec);    // throws exception in netbeans 6.0
                        //JEditorPane[] panes = ec.getOpenedPanes();
                        if (panes != null) {
                            return panes[0];
                        }
                    } catch (Exception e) {
                        // do nothing
                    }
                }
            }
        }

        return null;
        //return EditorRegistry.lastFocusedComponent();  //for netbeans 6.0 has error!!!!!!
        //return Registry.getMostActiveComponent();
    }
    
    
    public static JEditorPane[] getOpenedPanes(final EditorCookie ec) {
        if(SwingUtilities.isEventDispatchThread())
            return ec.getOpenedPanes();
        else {
            final Vector v = new Vector();
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        v.add(ec.getOpenedPanes());
                    }
                });
            }catch(InvocationTargetException ite) {
                //do nothing
            }catch(InterruptedException ie) {
                //do nothing
            }
            if(v.size() == 1)
                return (JEditorPane[])v.get(0);
            else
                return null;
        }
    }
    
    public static JTextComponent getTextComponent(ActionEvent e) {
        if (e != null) {
            Object o = e.getSource();
            if (o instanceof JTextComponent) {
                return (JTextComponent) o;
            }
        }
        return null;
    }

    public static void updateStatusBarInfo(final String value) {
        final JTextComponent component = getMostActiveComponent();
        if (null == component || null == value)
            return ;
        if (null == Utilities.getEditorUI(component))
            return ;
        StatusLineElementProvider status = Lookup.getDefault().lookup(StatusLineElementProvider.class);
        Component statusLabel = status.getStatusLineElement();
        if (statusLabel == null)
            return ;
        final JLabel label = (JLabel)statusLabel;
        EditorState es = getRespository().getEditorState(component);
        if (es == null)
            return ;
        final CommandMode mode = es.getCommandMode();
        if (mode != null) {
            final String text = mode.getDescription() + getSubString(value);
            if (SwingUtilities.isEventDispatchThread())
                label.setText(text);
            else
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            label.setText(text);
                        }
                    });
                } catch (InvocationTargetException ex) {
                    //ex.printStackTrace();
                } catch (InterruptedException ex) {
                    //ex.printStackTrace();
                }
        }
    }
    
    public static void eviStatusBarInfo(JTextComponent component, final String value) {
        org.netbeans.editor.Utilities.setStatusText(component, "--VIEX--" + value);
    }
    
    private static String getSubString(String s) {
        if (s.length() <= VISIABLE_COLS)
            return s;
        return s.substring(s.length() - VISIABLE_COLS);
    }
    
    // Removes only our owner highlights
    public static void removeHighlights(JTextComponent component) {
        if (component == null)
            return ;
        Highlighter hilite = component.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();
        for (int i=0; i<hilites.length; i++) {
            if (hilites[i].getPainter() instanceof EVIHighlightPainter) {
                hilite.removeHighlight(hilites[i]);
                component.repaint();
            }
        }
    }
    
    /**
     *
     * @param component
     */
    public static void addEVIKeymap(JTextComponent component) {
        if (component == null)
            throw new NullPointerException("component be can't null") ;
        //logger.log(Level.INFO, "add evi keymap..." + component.getName());
        if (isNullMode(component)) {
            EditorState estate = new EditorState();
            estate.getCommandMode().addCommandModeChangeListener(cmcl);
            getRespository().addEditorState(component, estate);
        }
        if (!isCommandMode(component)) {
            addKeymap(component);
            switchMode(COMMAND_MODE, component);
            //updateStatusBarInfo("");
        }
    }
    
    /**
     *
     * @param component
     */
    public static void removeEVIKeymap(JTextComponent component) {
        if (component == null)
            throw new NullPointerException("component be can't null");
        //logger.log(Level.INFO, "remove evi keymap..." + component.getName());
        if (isNullMode(component))
            return ;
        if (!isInsertMode(component)) {
            switchMode(INSERT_MODE, component);
            removeKeymap(component);
            //updateStatusBarInfo("");
        }
        if (!VIEXInitialSetting.ISATTACHED) {//Can clean just only when ISATTACHED is false.
            getRespository().getEditorState(component).getCommandMode().removeCommandModeChangeListener(cmcl);
            getRespository().removeEditorState(component);
        }
    }
    
    /**
     *
     * @param component
     */
    public static void addKeymap(final JTextComponent component) {
        if (component == null)
            throw new NullPointerException("component be can't null");
        Keymap parent = component.getKeymap();
        if (parent != null && !parent.getName().equals(EVIConstant.EVI_KEYMAP)) {
            final Keymap newmap = getRespository().getEditorState(component).getKeymap(parent);
            if (SwingUtilities.isEventDispatchThread())
                component.setKeymap(newmap);
            else {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {  // ????? deadlock, netbeans UI can be dead??
                        public void run() {
                            component.setKeymap(newmap);
                        }
                    });
                } catch (InvocationTargetException ex) {
                    //ex.printStackTrace();
                } catch (InterruptedException ex) {
                    //ex.printStackTrace();
                }
            }
            //logger.info("Keymap:" + component.getKeymap());
            //logger.info("Parent Keymap" + component.getKeymap().getResolveParent());
        }
    }
    
    /**
     *
     * @param component
     */
    public static void removeKeymap(final JTextComponent component) {
        if (component == null)
            throw new NullPointerException("component be can't null");
        Keymap keymap = component.getKeymap();
        if (keymap != null && keymap.getName().equals(EVIConstant.EVI_KEYMAP)) {
            final Keymap parent = component.getKeymap().getResolveParent();
            if (SwingUtilities.isEventDispatchThread()) {
                component.setKeymap(parent);
            } else {
                SwingUtilities.invokeLater(new Runnable() {              // some above,,,????
                    public void run() {
                        component.setKeymap(parent);
                    }
                });
            }
            //logger.info("Keymap:" + component.getKeymap());
            //logger.info("Parent keymap:" + component.getKeymap().getResolveParent());
        }
    }
    
    public static boolean isNullMode(JTextComponent component) {
        return getRespository().getEditorState(component) == null ? true : false;
    }
    
    private static void addEVIKeyAction(Keymap newmap) {
        KeyStroke ekey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke ikey = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0);
        KeyStroke bkey = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
        Action a = new NBActionFacade.NopAction();
        newmap.addActionForKeyStroke(ekey, a);  //added to vk_enter empty action
        newmap.addActionForKeyStroke(ikey, a);  //added to vk_insert empty action
        newmap.addActionForKeyStroke(bkey, a);  //added to vk_back_space empty action
    }
    
    private static void resetComponentOverwriteMode(JTextComponent component) {
        EditorUI editorUI = org.netbeans.editor.Utilities.getEditorUI(component);
        editorUI.putProperty(EditorUI.OVERWRITE_MODE_PROPERTY, false);
    }

    
}
