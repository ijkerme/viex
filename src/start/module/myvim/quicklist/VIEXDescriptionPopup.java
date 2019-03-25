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
public class VIEXDescriptionPopup {
    
    private Popup popup;
    
    private static VIEXDescriptionPopup viexPopup;
    
    private JComponent contentComponent;
    
    private WeakReference<JTextComponent> editorReferenceRef;

    private Rectangle screenBounds;

    private static final int POPUP_GAP = 3;
    
    private VIEXDescriptionPopup() {
    }
    
    public static VIEXDescriptionPopup getPopup() {
        if (viexPopup != null)
            return viexPopup;
        viexPopup = new VIEXDescriptionPopup();
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
    
    public void setContentComponent(JComponent component) {
        this.contentComponent = component;
    }
    
    public JComponent getContentComponent() {
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
    
    private Rectangle getVIEXPopupBounds() {
        JComponent component = VIEXPopup.getPopup().getContentComponent();
        return component.getBounds();
    }
    
    private Point getVIEXPopupLocation() {
        JComponent component = VIEXPopup.getPopup().getContentComponent();
        return component.getLocationOnScreen();
    }
    
    private int getX() {
        Rectangle sr = getScreenBounds();
        Rectangle r = getVIEXPopupBounds();
        Point pl = getVIEXPopupLocation();
        int offset = pl.x + r.width + POPUP_GAP;
        int ow = sr.width - offset;
        int cw = getContentComponent().getPreferredSize().width;
        if (ow <= cw) {
            int p2 = pl.x - cw - POPUP_GAP;
            if (p2 < 0)
                return 0;
            else
                return pl.x - cw - POPUP_GAP;
        } else
            return offset;
    }
    
    private int getY() {
        Point p = getVIEXPopupLocation();
        return p.y;
    }
    
    private Point getSelectedItemIndexBounds() {
        JList view = VIEXPopup.getPopup().getContentComponent().getView();
        return view.indexToLocation(view.getSelectedIndex());
    }
    
    public void show() {
        // Hide the original popup if exists
        if (isVisible()) {
            popup.hide();
            popup = null;
        }
        JComponent contComp = getContentComponent();
        if (null == contComp) {
            return ;
        }
        Rectangle r = getVIEXPopupBounds();
        Point pl = getVIEXPopupLocation();
        PopupFactory factory = PopupFactory.getSharedInstance();
        popup = factory.getPopup(getEditorComponent(), contComp, getX(), getY());
        popup.show();
    }
    
    public void hide() {
        if (popup != null) {
            popup.hide();
            popup = null;
        }
    }
    
    public boolean isVisible() {
        return popup != null;
    }
    
}
