package start.module.myvim.options;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.spi.options.OptionsCategory;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import start.module.myvim.EVIEditor;

public final class VIEXOptionsCategory extends OptionsCategory {
    
    public Icon getIcon() {
        return new ImageIcon(Utilities.loadImage("start/module/myvim/resources/vi32.png"));
    }
    
    public String getCategoryName() {
        return NbBundle.getMessage(EVIEditor.class, "OptionsCategory_Name");
    }
    
    public String getTitle() {
        return NbBundle.getMessage(EVIEditor.class, "OptionsCategory_Title");
    }
    
    public OptionsPanelController create() {
        return new VIEXOptionsPanelController();
    }
    
}
