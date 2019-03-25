package start.module.myvim.options;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import start.module.myvim.EVIEditor;
import start.module.myvim.handler.action.quicklist.impl.FileObjectQuickNavagatorImpl;
import start.module.myvim.utilities.VIEXBundle;
import start.module.myvim.utilities.VIEXOptions;
import static start.module.myvim.utilities.VIEXOptions.*;

public final class VIEXPanel extends javax.swing.JPanel {
    
    private final VIEXOptionsPanelController controller;
    
    VIEXPanel(VIEXOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        // TODO listen to changes in form fields and call controller.changed()
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        setBackground(Color.GRAY);
        // Search Panel
        searchPanel = new javax.swing.JPanel();
        cbIgnoreCase = new javax.swing.JCheckBox();
        searchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(VIEXBundle.getMessage(VIEXPanel.class, "SLASH_SEARCH")));
        org.openide.awt.Mnemonics.setLocalizedText(cbIgnoreCase, VIEXBundle.getMessage(VIEXPanel.class, "IGNORE_CASE"));
        cbIgnoreCase.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbIgnoreCase.setMargin(new java.awt.Insets(0, 0, 0, 0));
        searchPanel.add(cbIgnoreCase, BorderLayout.WEST);

        // Quick list regular panel
        regexPanel = new JPanel();
        regexPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(VIEXBundle.getMessage(VIEXPanel.class, "QUICK_LIST_REGULAR")));
        cbIgnoreFiles = new JCheckBox(VIEXBundle.getMessage(VIEXPanel.class, "QUICK_LIST_IGNORE_FILES"));
        bReset = new JButton(VIEXBundle.getMessage(VIEXPanel.class, "QUICK_LIST_RESET"));
        bReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tfRegularExp.setText(FileObjectQuickNavagatorImpl.DEFAULT_PATTERN);
            }
        });
        regexPanel.setLayout(new FlowLayout());
        regexPanel.add(cbIgnoreFiles);
        regexPanel.add(tfRegularExp);
        regexPanel.add(bReset);

        JPanel generalPane = new JPanel(new GridLayout(5, 1));
        generalPane.add(searchPanel);
        generalPane.add(regexPanel);

        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("General", generalPane);
        tabPane.addTab("Key Table", optionTable);
        setBackground(java.awt.Color.white);
        setLayout(new BorderLayout());
        add(tabPane, BorderLayout.CENTER);
    }

    void load() {
        // TODO read settings and initialize GUI
        // Example:
        // someCheckBox.setSelected(Preferences.userNodeForPackage(VIEXPanel.class).getBoolean("someFlag", false));
        // or:
        // someTextField.setText(SomeSystemOption.getDefault().getSomeStringProperty());
        cbIgnoreCase.setSelected(getBoolean(GENERAL, VIEXBundle.getMessage(VIEXPanel.class, "OptionsIgnoreCase")));
        cbIgnoreFiles.setSelected(getBoolean(GENERAL, VIEXBundle.getMessage(VIEXPanel.class, "OptionsIgnoreFiles")));
        tfRegularExp.setText(getVIEXOption(GENERAL, VIEXBundle.getMessage(VIEXPanel.class, "OptionsQuickListRegularExp"), FileObjectQuickNavagatorImpl.DEFAULT_PATTERN));
        optionTable.load();
    }
    
    void store() {
        // TODO store modified settings
        // Example:
        // Preferences.userNodeForPackage(VIEXPanel.class).putBoolean("someFlag", someCheckBox.isSelected());
        // or:
        // SomeSystemOption.getDefault().setSomeStringProperty(someTextField.getText());
        putBoolean(GENERAL, VIEXBundle.getMessage(VIEXPanel.class, "OptionsIgnoreCase"), cbIgnoreCase.isSelected());
        putBoolean(GENERAL, VIEXBundle.getMessage(VIEXPanel.class, "OptionsIgnoreFiles"), cbIgnoreFiles.isSelected());
        putVIEXOption(GENERAL, VIEXBundle.getMessage(VIEXPanel.class, "OptionsQuickListRegularExp"), tfRegularExp.getText().trim());
        optionTable.store();
    }
    
    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }
    
    private javax.swing.JCheckBox cbIgnoreCase;
    private javax.swing.JPanel searchPanel;

    private KeyPairPanel optionTable = new KeyPairPanel();
    private JPanel regexPanel;
    private JCheckBox cbIgnoreFiles;
    private final JTextField tfRegularExp = new JTextField(50);
    private JButton bReset;
}
