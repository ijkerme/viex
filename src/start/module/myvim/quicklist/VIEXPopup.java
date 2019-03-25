package start.module.myvim.quicklist;

import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import start.module.myvim.state.QuickListType;

/**
 *
 * @author jker
 */
public class VIEXPopup {
    
    public static QuickListType QUICK_LIST_TYPE;
    
    private Popup popup;
    
    private WeakReference<JTextComponent> editorReferenceRef;
    
    private VIEXQuickListScrollPane contentComponent;
    
    private Rectangle screenBounds;
    
    private static final int POPUP_VERTICAL_GAP = 2;
    private static final int POPUP_WIDTH = 250;
    private static final int HEADER_HEIGHT = 20;
    private static final int SCROLLBAR_WIDTH = 20;
    
    private static VIEXPopup viexPopup;
    
    private VIEXPopup() {
    }
    
    public static VIEXPopup getPopup() {
        if (viexPopup != null)
            return viexPopup;
        viexPopup = new VIEXPopup();
        return viexPopup;
    }
    
    public void setEditorComponent(JTextComponent component) {
        if (null == component)
            throw new NullPointerException("component can't be null");
        this.editorReferenceRef = new WeakReference<JTextComponent>(component);
    }
    
    public JTextComponent getEditorComponent() {
        return editorReferenceRef.get();
    }
    
    public void setContentComponent(VIEXQuickListScrollPane component) {
        this.contentComponent = component;
    }
    
    public VIEXQuickListScrollPane getContentComponent() {
        return this.contentComponent;
    }
    
    private Rectangle getScreenBounds() {
        if (screenBounds == null) {
            JTextComponent editorComponent = getEditorComponent();
            GraphicsConfiguration configuration = editorComponent != null
                    ? editorComponent.getGraphicsConfiguration() : null;
            screenBounds = configuration != null
                    ? configuration.getBounds() : new Rectangle();
        }
        return screenBounds;
    }
    
    private Rectangle getPositionAtCaretBelow() {
        Rectangle caret = null;
        try {
            caret = getEditorComponent().modelToView(getEditorComponent().getCaretPosition());
        } catch (BadLocationException ex) {
            caret = new Rectangle();
        }
        return caret;
    }
    
    private boolean isCaretVisible() {
        JTextComponent component = editorReferenceRef.get();
        Rectangle r = component.getVisibleRect();
        Rectangle p = getPositionAtCaretBelow();
        return r.contains(p.getLocation());
    }
    
    private Point getOffset() {
        JTextComponent component = editorReferenceRef.get();
        if (component == null)
            return new Point();
        Rectangle contentBounds = getContentComponentBounds();
        Rectangle screenBounds = getScreenBounds();
        Rectangle caretPosition = getPositionAtCaretBelow();
        
        Point temp = new Point();
        temp.x = caretPosition.x + caretPosition.width;
        temp.y = caretPosition.y + caretPosition.height;

        Point p = new Point();
        if (isCaretVisible()) {
            SwingUtilities.convertPointToScreen(temp, component);
            if ((screenBounds.height - temp.y) >= contentBounds.height) {
                p.y = temp.y;
            } else {
                p.y = screenBounds.height - contentBounds.height;
            }
            if ((screenBounds.width - temp.x) >= contentBounds.width) {
                p.x = temp.x;
            } else {
                p.x = screenBounds.width - contentBounds.width - SCROLLBAR_WIDTH;
            }
        } else {
            Point cornerRight = component.getBounds().getLocation();
            SwingUtilities.convertPointToScreen(cornerRight, component);
            if ((screenBounds.height - cornerRight.y) >= contentBounds.height) {
                p.y = cornerRight.y;
            } else {
                p.y = screenBounds.height - contentBounds.height;
            }
            if ((screenBounds.width - cornerRight.x) >= contentBounds.width) {
                p.x = cornerRight.x;
            } else {
                p.x = screenBounds.width - contentBounds.width -SCROLLBAR_WIDTH;
            }
        }
        return p;
    }
    
    private Rectangle getContentComponentBounds() {
        JList view = contentComponent.getView();
        int size = view.getVisibleRowCount();
        int height = view.getFixedCellHeight() * size + HEADER_HEIGHT;
        int width = view.getFixedCellWidth();
        return new Rectangle(width, height);
    }
    
    public void show() {
        // Hide the original popup if exists
        if (isVisible()) {
            popup.hide();
            popup = null;
        }
        //if (!isVisible()) {
        JComponent contComp = getContentComponent();
        if (null == contComp) {
            return ;
        }
        Point p = getOffset();
        PopupFactory factory = PopupFactory.getSharedInstance();
        popup = factory.getPopup(getEditorComponent(), contComp, p.x, p.y);
        popup.show();
        //}
    }
    
    public void hide() {
        if (popup != null) {
            popup.hide();
            popup = null;
            screenBounds = null;
            contentComponent.emptyListData();
            contentComponent = null;
            VIEXDescriptionPopup dpopup = VIEXDescriptionPopup.getPopup();
            if (dpopup.isVisible())
                dpopup.hide();
        }
    }
    
    public boolean isVisible() {
        return popup != null;
    }
}
