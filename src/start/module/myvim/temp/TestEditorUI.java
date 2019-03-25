package start.module.myvim.temp;

import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import org.netbeans.editor.EditorUI;
import org.netbeans.editor.StatusBar;
import org.netbeans.editor.Utilities;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;
import start.module.myvim.utilities.EVIConstant;
import static start.module.myvim.utilities.ComponentUtils.*;

public class TestEditorUI {
    
    private static Logger logger = Logger.getLogger("TestEditorUI");
    
    public  static void test(KeyEvent e) {
        int code = e.getKeyCode();
        JTextComponent c = (JTextComponent)e.getSource();
        if (c != null) {
            EditorUI editor = Utilities.getEditorUI(c);
            if (editor != null) {
                if (code == KeyEvent.VK_Y) {
                    editor.setLineNumberEnabled(true);
                } else if (code == KeyEvent.VK_N) {
                    editor.setLineNumberEnabled(false);
                } else if (code == KeyEvent.VK_T) {
                    JToolBar tb = editor.getToolBarComponent();
                    tb.add(new JTextField("evi"));
                    tb.updateUI();
                } else if (code == KeyEvent.VK_S) {
                    processStatus(e);
                }
            }
        }
    }
    
    
    public static void processStatus(final KeyEvent e) {
        JTextComponent c = (JTextComponent)e.getSource();
        if (c != null) {
            StatusBar bar = Utilities.getEditorUI(c).getStatusBar();
            System.err.println(bar.getCellCount());
            System.err.println(bar.getCellByName(StatusBar.CELL_POSITION).getText());
            JLabel commandTip = new JLabel("myCustomer...");
            commandTip.setSize(20, bar.getPanel().getHeight());
            //commandTip.setBackground(Color.RED);
            commandTip.setBackground(c.getForeground());
            int cellCount = bar.getCellCount();
            final JLabel mainLabel = bar.getCellByName("EVI");
            if (SwingUtilities.isEventDispatchThread()) {
                mainLabel.setText("evi changed");
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        mainLabel.setText("runnable");
                    }
                });
            }
        }
    }
    
    public static void getOpendPanes() {
        TopComponent.Registry r = TopComponent.getRegistry();
        Set comps = TopComponent.getRegistry().getOpened(); //get all window components in netbeans
        for (Iterator ite = comps.iterator(); ite.hasNext();) {
            System.out.println("=====================");
            TopComponent comp = (TopComponent)ite.next();
            System.out.println("Component Name:" + comp.getName() + "     Counts:" + comp.getComponentCount());
            int counts = comp.getComponentCount();
            for (int i = 0; i < counts; i++) {
                System.out.println("Name:" + comp.getComponent(i).toString());
            }
            
            Node[] arr = comp.getActivatedNodes();
            //Node[] arr = comp.getRegistry().getCurrentNodes();
            
            if (arr != null) {
                System.out.println("Current Nodes:" + arr.length);
                for (int j = 0; j < arr.length; j++) {
                    EditorCookie ec = (EditorCookie) arr[j].getCookie(EditorCookie.class);
                    if (ec != null) {
                        JEditorPane[] panes = ec.getOpenedPanes();
                        if (panes != null) {
                            System.out.println("Pane Counts:" + panes.length);
                            for (int i = 0; i < panes.length; i++) {
                                System.out.println("Pane Name:" + panes[i]);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static void getAllOpendPanes() {
        System.out.println("------------------------------Get All Opnened Panes----------------------------");
        Set comps = TopComponent.getRegistry().getOpened();
        for (Iterator ite = comps.iterator(); ite.hasNext();) {
            TopComponent comp = (TopComponent)ite.next();
            System.out.println("Name:" + comp.getName());
            Node[] arr = comp.getActivatedNodes();
            if (arr != null) {
                for (int j = 0; j < arr.length; j++) {
                    EditorCookie ec = (EditorCookie) arr[j].getCookie(EditorCookie.class);
                    if (ec != null) {
                        JEditorPane[] panes = ec.getOpenedPanes();
                        if (panes != null) {
                            TopComponent activetc = TopComponent.getRegistry().getActivated();
                            for (int i = 0; i < panes.length; i++) {
                                if (activetc.isAncestorOf(panes[i])) {
                                    System.out.println("=====find component======");
                                    //panes[i].addKeyListener(new EVIKeyListener());
                                    if (!panes[i].getKeymap().getName().equals(EVIConstant.EVI_KEYMAP))
                                        addEVIKeymap(panes[i]);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    public static void detectOpnedPanes() {
        System.out.println("------------------------------Detect Opened Panes Keymap----------------------------");
        Set comps = TopComponent.getRegistry().getOpened();
        for (Iterator ite = comps.iterator(); ite.hasNext();) {
            TopComponent comp = (TopComponent)ite.next();
            System.out.println("Name:" + comp.getName());
            Node[] arr = comp.getActivatedNodes();
            if (arr != null) {
                for (int j = 0; j < arr.length; j++) {
                    EditorCookie ec = (EditorCookie) arr[j].getCookie(EditorCookie.class);
                    if (ec != null) {
                        JEditorPane[] panes = ec.getOpenedPanes();
                        if (panes != null) {
                            TopComponent activetc = TopComponent.getRegistry().getActivated();
                            for (int i = 0; i < panes.length; i++) {
                                if (activetc.isAncestorOf(panes[i])) {
                                    System.out.println("=====find component======");
                                    System.out.println("keymap:" + panes[i].getKeymap());
                                    System.out.println("Parent Keymap:" + panes[i].getKeymap().getResolveParent());
                                    break;
                                    
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static void removePanesKeymap() {
        System.out.println("------------------------------Remove Opened Panes Keymap----------------------------");
        Set comps = TopComponent.getRegistry().getOpened();
        for (Iterator ite = comps.iterator(); ite.hasNext();) {
            TopComponent comp = (TopComponent)ite.next();
            System.out.println("Name:" + comp.getName());
            Node[] arr = comp.getActivatedNodes();
            if (arr != null) {
                for (int j = 0; j < arr.length; j++) {
                    EditorCookie ec = (EditorCookie) arr[j].getCookie(EditorCookie.class);
                    if (ec != null) {
                        JEditorPane[] panes = ec.getOpenedPanes();
                        if (panes != null) {
                            TopComponent activetc = TopComponent.getRegistry().getActivated();
                            for (int i = 0; i < panes.length; i++) {
                                if (activetc.isAncestorOf(panes[i])) {
                                    System.out.println("=====find component======");
                                    //panes[i].addKeyListener(new EVIKeyListener());
                                    //ComponentUtil.addEVIKeymap(panes[i]);
                                    if (panes[i].getKeymap().getName().equals(EVIConstant.EVI_KEYMAP)) {
                                        Keymap parent = panes[i].getKeymap().getResolveParent();
                                        //panes[i].removeKeymap(EVIConstant.EVI_KEYMAP);
                                        panes[i].setKeymap(parent);
                                        System.out.println("Panes keymap retain:" + panes[i].getKeymap().getName());
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    public static void testDefaultAction(KeyEvent e) {
        
    }
    
    public static void testEditorCookie() {
        //            logger.info("action is executed");
        DataObject.Registry registries = DataObject.getRegistry();
        registries.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                //System.out.println("ChangedListener: o = " + e.getSource().getClass());
                // System.out.println("ChangedListener: o.source = " + e.getSource());
            }
        });
        DataObject[] objects = registries.getModified();
        for (int i = 0; i < objects.length; i++) {
            DataObject dataObj = objects[i];
            System.out.println("data object name = " + dataObj.getName());
            System.out.println("data object pimary file name = " + dataObj.getPrimaryFile().getName());
            Set fss = dataObj.files();
            Iterator iter = fss.iterator();
            while (iter.hasNext()) {
                FileObject fo = (FileObject) iter.next();
                System.out.println("\tset file object: " + fo.getName());
            }
        }
        Node[] arr = TopComponent.getRegistry().getCurrentNodes();
        for (int i = 0; i < arr.length; i++) {
            EditorCookie ec = (EditorCookie) arr[i].getCookie(EditorCookie.class);
            if (ec != null) {
                logger.info(" " + ec);
                JEditorPane[] panes = ec.getOpenedPanes();
                if (panes != null) {
                    // USE panes
                }
            }
        }
        Node[] n = TopComponent.getRegistry().getActivatedNodes();
        if (n.length == 1) {
            EditorCookie ec = (EditorCookie) n[0].getCookie(EditorCookie.class);
            if (ec != null) {
                JEditorPane[] panes = ec.getOpenedPanes();
                if (panes.length > 0) {
                    int cursor = panes[0].getCaret().getDot();
                    String selection = panes[0].getSelectedText();
                    // USE selection
                    logger.info("Caret Position:" + cursor + "Selected Text:" + selection);
                }
            }
        }
    }
    
    public static void testOverriteMode(JTextComponent target) {
        EditorUI ui = Utilities.getEditorUI(target);
        Boolean o = (Boolean)ui.getProperty(EditorUI.OVERWRITE_MODE_PROPERTY);
        logger.info("Overwrite Mode:" + o);
    }
    
    public static void testActiveNode(JTextComponent target) {
        Node ns[] = TopComponent.getRegistry().getActivatedNodes();
        for (Node n : ns) {
            logger.info("Node:" + n);
        }
    }
}