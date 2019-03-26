package start.module.myvim.handler.action;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.document.LineDocumentUtils;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import org.openide.text.NbDocument;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.ExClipboard;
import start.module.myvim.EVIEditor;
import start.module.myvim.highlight.HighlightLine;
import start.module.myvim.highlight.HighlightLineQueue;
import start.module.myvim.state.Mode;
import start.module.myvim.utilities.VIEXPairTable;
import start.module.myvim.utilities.VIEXPairTable.CATEGORY;
import static start.module.myvim.state.CommandModeState.*;
import static start.module.myvim.utilities.ComponentUtils.*;
import static start.module.myvim.state.VIEXRepository.*;
import static start.module.myvim.handler.action.EVIActionFactory.*;

/**
 *
 * @author jker
 */
class EVIActionFactory {
    private static final Logger logger = Logger.getLogger(EVIActionFactory.class.getName());

    static BaseDocument getDocument(JTextComponent target) {
        Document doc = target.getDocument();
        if (doc instanceof BaseDocument) {
            return (BaseDocument)doc;
        }
        return null;
    }

//\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD
    static void beep() {
        Toolkit.getDefaultToolkit().beep();
    }

    static void deleteSelectedText(JTextComponent target) {
            if (isTargetEditable(target)) {
                Document doc = target.getDocument();
                if (isVisualBlockMode(target)) {
                    //logger.info("delete selection in visual block mode");
                    removeHighlights(target);
                    if (doc instanceof BaseDocument) {
                        BaseDocument bdoc = (BaseDocument)doc;
                        HighlightLineQueue lineQueue = getRespository().getEditorState(target).getHighlightLineQueue();
                        if (lineQueue.isValid() || lineQueue.isDeleted())
                            return ;
                        bdoc.runAtomic(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //List<HighlightLine> queue = HighlightLineQueue.getInstance().getQueue();
                                    List<HighlightLine> queue = lineQueue.getQueue();
                                    List<HighlightLine> lines = new ArrayList<HighlightLine>(queue);
                                    Collections.sort(lines);
                                    int rcounts = 0;
                                    for (HighlightLine line : lines) {
                                        bdoc.remove(line.getStart() - rcounts, line.getEnd() - line.getStart());//是不是要减去被删除的呢？要不接下来的删除会不会出错?
                                        rcounts += (line.getEnd() - line.getStart());
                                    }
                                    lineQueue.setDeleted(true);
                                    lineQueue.setValid(true);
                                } catch (BadLocationException ex) {
                                    //ex.printStackTrace();
                                }                               
                            }
                        });

                    }
                } else {
                    String rtext = target.getSelectedText();
                    copyContentToClipboard(rtext);
                    target.replaceSelection(""); //Waring:.......bugs occured. doc instead of target
                }
                switchMode(Mode.COMMAND_MODE, target);
            }
    }

    static void delChasPair(final JTextComponent target) {
        if (isTargetEditable(target)) {
            //Document doc = target.getDocument();
            final BaseDocument doc = getDocument(target);
            doc.runAtomic(new Runnable() {
                public void run() {
                    String selText = target.getSelectedText();
                    try {
                        Caret caret = target.getCaret();
                        int p = caret.getDot();
                        int min = 0, len = 0;
                        if (selText == null) {
                            min = p;
                            len = 1;
                            selText = doc.getText(p, 1);
                        } else {
                            len = selText.length();
                            int p2 = caret.getMark();
                            min = Math.min(p, p2);
                        }
                        String text = VIEXPairTable.getInstance().getPairValue(CATEGORY.CHAR_PAIR, selText);
                        if (text == null)
                            return ;
                        // Remove a text pair
                        int le = LineDocumentUtils.getLineEnd((BaseDocument)doc, min);
                        String rs = doc.getText(min + 1, le - min);
                        int i = rs.indexOf(text);
                        if (i != -1) {
                            doc.remove(min, len);
                            doc.remove(min + i - len + 1, text.length());
                        }
                    } catch (BadLocationException ex) {
                        logger.warning(ex.getMessage());
                    }
                }
            });

        }
    }


    static void copyContentToClipboard(String text) {
        if (text != null) {
            Clipboard clipboard = getExClipboard();
            clipboard.setContents(new StringSelection(text), null);
        }
    }

    static String getContentFromClipboard() {
        Clipboard clipboard = getExClipboard();
        try {
            return (String)clipboard.getData(DataFlavor.stringFlavor);
        } catch (IOException ex) {
            //ex.printStackTrace();
        } catch (UnsupportedFlavorException ex) {
            //ex.printStackTrace();
        }
        return null;
    }

    static Clipboard getExClipboard() {
        // Lookup and cache the Platfrom's clipboard
        Clipboard clipboard = (ExClipboard) Lookup.getDefault().lookup(ExClipboard.class);
        if (clipboard == null) {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
        return clipboard;
    }


    static void insertTextPair(String s, JTextComponent target) {
        if (isTargetEditable(target)) {
            Caret caret = target.getCaret();
            int p = caret.getDot();
            int ws = 0;
            int we = 0;
            try {
                String text = target.getSelectedText();
                final BaseDocument doc = getDocument(target);
                if (text == null) {
                    ws = LineDocumentUtils.getWordStart(doc, p);
                    we = LineDocumentUtils.getWordStart(doc, p);
                    if (ws == we)
                        return ;
                } else {
                    ws = Math.min(caret.getDot(), caret.getMark());
                    we = Math.max(caret.getDot(), caret.getMark());
                }
                String pair = VIEXPairTable.getInstance().getPairValue(CATEGORY.CHAR_PAIR, s);
                if (pair != null) {
                    doc.insertString(ws, s, null);
                    doc.insertString(we + s.length(), pair, null);
                }
            } catch (BadLocationException ex) {
                logger.warning(ex.getMessage());
            }
        }
    }

    static boolean isTargetEditable(JTextComponent target) {
        FileObject fileObject = NbEditorUtilities.getFileObject(target.getDocument());
        if (!fileObject.canWrite()) {
            eviStatusBarInfo(target, NbBundle.getMessage(EVIEditor.class, "EDITOR_READ_ONLY"));
            return false;
        }
        return ((target != null) && target.isEnabled() && target.isEditable());
    }

    static boolean isTargetEnabled(JTextComponent target) {
        return ((target != null) && target.isEnabled());
    }


    //save caret magic position
    static void setCaretMagicPosition(JTextComponent textComp, int position) {
        try {
            Rectangle r = textComp.modelToView(position);
            if (r != null) {
                Point p = new Point(r.x, r.y);
                textComp.getCaret().setMagicCaretPosition(p);
            } else {

            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

}
