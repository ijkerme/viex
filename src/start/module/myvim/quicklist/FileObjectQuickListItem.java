package start.module.myvim.quicklist;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import start.module.myvim.exception.EVIException;
import start.module.myvim.state.Mode;
import start.module.myvim.handler.CommandMode;
import start.module.myvim.utilities.VIEXBundle;
import static start.module.myvim.state.VIEXRepository.*;

/**
 *
 * @author jker
 */
public class FileObjectQuickListItem implements VIEXQuickListItem<FileObject> {

    private FileObject fo;
    private boolean immediate = false;
    private JPanel panel = new JPanel();
    private JLabel label = new JLabel();

    public FileObjectQuickListItem(FileObject fo) {
        this.fo = fo;
        if (fo != null) {
            label.setText(fo.getPath());
        }
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.add(label);
    }

    public FileObject value() {
        return fo;
    }

    public String getItemValue() {
        return fo.getNameExt();
    }

    public void defaultAction(JComponent component) {
        try {
            if (fo.isValid()) {
                DataObject dobject = DataObject.find(fo);
                EditCookie edit = (EditCookie) dobject.getCookie(EditCookie.class);
                if (edit != null) {
                    edit.edit();
                } else {
                    OpenCookie open = (OpenCookie) dobject.getCookie(OpenCookie.class);
                    if (open != null) {
                        open.open();
                        //System.out.println("Open File Object...");
                    }
                }
                if (component == null) {
                    return;
                }
                if (component instanceof JTextComponent) {
                    JTextComponent target = (JTextComponent) component;
                    CommandMode mode = getRespository().getEditorState(target).getCommandMode();
                    if (mode == null) {
                        throw new EVIException(VIEXBundle.getMessage("MODE_ERROR"));
                    }
                    mode.switchMode(Mode.COMMAND_MODE, target);
                    if (target.getCaret().isSelectionVisible()) {
                        //cancel caret selected
                        target.setCaretPosition(target.getCaretPosition());
                    }
                }
            }
        } catch (DataObjectNotFoundException ex) {
            //ex.printStackTrace();
        }
    }

    public void showDescription() {
        VIEXPopup vpopup = VIEXPopup.getPopup();
        if (!vpopup.isVisible()) {
            return;
        }
        VIEXDescriptionPopup popup = VIEXDescriptionPopup.getPopup();
        popup.setEditorComponent(vpopup.getEditorComponent());
        popup.setContentComponent(panel);
        popup.show();
    }

    public boolean isImmediate() {
        return this.immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public Component getListCellComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        return new JPanel() {

            protected void paintComponent(Graphics g) {
                int i = VIEXJList.DARKER_COLOR_COMPONENT;
                Font font = new Font("Serif", Font.PLAIN, 10);
                String text = font.getFamily();
                FontMetrics fm = g.getFontMetrics(font);
                Color bgColor;
                Color fgColor;
                if (isSelected) {
                    bgColor = list.getSelectionBackground();
                    fgColor = list.getSelectionForeground();
                } else {
                    // not selected
                    bgColor = list.getBackground();
                    if ((index % 2) == 0) {
                        // every second item slightly different
                        bgColor = new Color(Math.abs(bgColor.getRed() - i), Math.abs(bgColor.getGreen() - i), Math.abs(bgColor.getBlue() - i));
                    }
                    fgColor = list.getForeground();
                }
                g.setColor(bgColor);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(fgColor);
                //g.setFont(font);
                g.drawString(index + "    " + getItemValue(), 0, fm.getAscent());
            }
        };
    }
}