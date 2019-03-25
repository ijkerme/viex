package start.module.myvim;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeSupport;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;

/**
 * Listener
 *@ActionID(id = "start.module.myvim.EVIEditor", category = "View")
 *@ActionRegistration(displayName = "viex", iconBase = "start/module/myvim/resources/vi.png", 
 *       enabledOn = @ActionState(type = EVIEditor.ActionModel.class, property = "bool2Prop", checkedValue="true"),
 *       checkedOn = @ActionState(type = EVIEditor.ActionModel.class, property = "boolProp", checkedValue="true"))
 *@ActionReferences(value = {
 *   @ActionReference(path = "Menu/View"),
 *   @ActionReference(path = "Toolbars/View")})
 * @author jker
 */
@ActionID(id = "start.module.myvim.EVIEditor", category = "View")
@ActionRegistration(displayName = "viex", iconBase = "start/module/myvim/resources/vi.png")
@ActionReferences(value = {
    @ActionReference(path = "Menu/View"),
    @ActionReference(path = "Toolbars/View")})
public final class EVIEditor extends AbstractAction {

    private VIEXInitialSetting listener;
    private boolean isOpened = false;

    /**
     *
     */
    public EVIEditor() {
        listener = new VIEXInitialSetting();
    }

    private void viStateChange(boolean b) {
        System.out.println(b);
        if (b) {
            listener.attach();
        } else {
            listener.detach();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        isOpened = ! isOpened;
        viStateChange(isOpened);
    }

    public static class DefaultActionModel {

        boolean boolProp;
        boolean bool2Prop;

        PropertyChangeSupport supp = new PropertyChangeSupport(this);

        public boolean isEnabled() {
            return boolProp;
        }

        public void setEnabled(boolean e) {
            this.boolProp = e;
            supp.firePropertyChange("enabled", null, null);

        }

        public boolean getSwingSelectedKey() {
            return bool2Prop;
        }

        public void setSwinSelectedKey(boolean s) {
            this.bool2Prop = s;
            supp.firePropertyChange(Action.SELECTED_KEY, null, null);
        }
        
        public String toString() {
            return "Enabled:" + boolProp + ", " + bool2Prop;
        }
    }

    public static class ActionModel {

        boolean boolProp = true;
        boolean bool2Prop = true;

        String prop;
        Object anyProp;
        boolean noneBoolProp;
        int intProp;

        PropertyChangeSupport supp = new PropertyChangeSupport(this);

        public boolean getBool2Prop() {
            return true;
        }

        public boolean isBoolProp() {
            return true;
        }

        public String getProp() {
            return prop;
        }

        public String getAnyProp() {
            return null;
        }

        int getIntProp() {
            return 0;
        }
    }
}
