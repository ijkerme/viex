package start.module.myvim;

import java.awt.Component;
import java.awt.event.KeyListener;
import start.module.myvim.quicklist.PopupListener;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import start.module.myvim.handler.CommandMode;
import start.module.myvim.handler.action.ActionFactory;
import start.module.myvim.sp.StatusLineElementProviderImpl;
import start.module.myvim.sp.StatusLineMgr;
import start.module.myvim.utilities.VIEXPairTable;
import static start.module.myvim.utilities.ComponentUtils.*;

/**
 *
 * @author xiaomj
 */
public class VIEXInitialSetting implements LookupListener {
    
    private ActionFactory af;
    
    private static final Logger logger = Logger.getLogger("VIEXInitialSetting");
    
    private static EVIKeyListener viListener = new EVIKeyListener();
    private static PopupListener popListener = new PopupListener();
 
    //private PropertyChangeListener propertyChanged = new EVIPropertyChangeListener();
    
    private static TopComponentRegistryListener tcrl = new TopComponentRegistryListener();
    
    private CommandMode cm;
    
    /**
     *
     */
    public static boolean ISATTACHED = false;
    
    /**
     *
     */
    public void attach() {
        ISATTACHED = true;
        af = ActionFactory.getInstance();
        af.initActions();
        changeStatusBar();
        addKeymapToAllPanes();
        VIEXPairTable.getInstance().initialize();
    }
    
    /**
     *
     */
    public void detach() {
        ISATTACHED = false;
        af = null;
        changeStatusBar();
        removeKeymapFromAllPanes();
    }
    
    private void addKeymapToAllPanes() {
        TopComponent.Registry r = TopComponent.getRegistry();
        r.addPropertyChangeListener(tcrl);
        addLookupListener();
        //Set comps = TopComponent.getRegistry().getOpened(); //get all window components in netbeans
        TopComponent comp = TopComponent.getRegistry().getActivated();
        if (null == comp)
            return ;
        //logger.info("TopComponent:" + comp.getName());
        Node[] arr = comp.getActivatedNodes();
        if (arr == null)
            //continue ;
            return ;
        for (int j = 0; j < arr.length; j++) {
            EditorCookie ec = (EditorCookie) arr[j].getCookie(EditorCookie.class); //just only open editor with editable.
            if (ec != null) {
                JEditorPane[] panes = getOpenedPanes(ec);
                if (panes != null) {
                    for (int i = 0; i < panes.length; i++) {
                        addEVIKeymap(panes[i]);
                        //eviStatusBarChange(panes[i]);
                        KeyListener[] kls = panes[i].getKeyListeners();
                        for (int f = 0; f < kls.length; f++)
                            if (kls[f] instanceof EVIKeyListener)
                                continue ; //prevent registry key listener more than twice
                        //logger.warning("add listener");
                        panes[i].addKeyListener(viListener);
                        panes[i].addFocusListener(popListener);
                    }
                }
            }
        }
    }
    
    private void removeKeymapFromAllPanes() {
        TopComponent.Registry r = TopComponent.getRegistry();
        r.removePropertyChangeListener(tcrl);
        removeLookupListener();
        Set comps = TopComponent.getRegistry().getOpened(); //get all window component in netbeans
        for (Iterator ite = comps.iterator(); ite.hasNext();) {
            TopComponent comp = (TopComponent)ite.next();
            Node[] arr = comp.getActivatedNodes();
            if (arr == null)
                continue ;
            for (int j = 0; j < arr.length; j++) {
                EditorCookie ec = (EditorCookie) arr[j].getCookie(EditorCookie.class);
                if (ec != null) {
                    JEditorPane[] panes = getOpenedPanes(ec);
                    if (panes != null) {
                        for (int i = 0; i < panes.length; i++) {
                            removeEVIKeymap(panes[i]);
                            //eviStatusBarChange(panes[i]);
                            panes[i].removeKeyListener(viListener);
                            panes[i].removeFocusListener(popListener);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * EVIKeyListener
     *
     *  @return EVI\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD
     */
    public static EVIKeyListener getKeyListener() {
        return viListener;
    }
    
    public static PopupListener getPopupListener() {
        return popListener;
    }

    private void addLookupListener() {
        Lookup.Result result = Utilities.actionsGlobalContext().lookup(new Lookup.Template(Object.class));
        result.addLookupListener(this);
        result.allItems();
    }

    private void removeLookupListener() {
        Lookup.Result result = Utilities.actionsGlobalContext().lookup(new Lookup.Template(Object.class));
        result.removeLookupListener(this);
        result.allItems();
    }

    public void resultChanged(LookupEvent arg0) {
        logger.log(Level.FINE, "Changed Settings");
    }

    private void changeStatusBar() {
        StatusLineElementProviderImpl status = StatusLineMgr.getStatusLine();
        if (status != null) {
            Component label = status.getStatusLineElement();
            if (label != null) {
                ((JLabel)label).setText("-VIEX-");
                label.setVisible(!label.isVisible());
            }
        }
    }

}
