package start.module.myvim.handler.action;

import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import org.netbeans.editor.AnnotationDesc;
import org.netbeans.editor.Annotations;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.BaseKit;
import org.netbeans.editor.EditorUI;
//import org.netbeans.lib.editor.bookmarks.api.Bookmark;
//import org.netbeans.lib.editor.bookmarks.api.BookmarkList;
//import org.netbeans.swing.tabcontrol.TabData;
//import org.netbeans.swing.tabcontrol.TabbedContainer;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;
import java.awt.event.ActionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import start.module.myvim.exception.EVIException;
import start.module.myvim.handler.CommandMode;
import start.module.myvim.handler.action.quicklist.QuickNavagatorFactory;
import start.module.myvim.handler.action.quicklist.QuickNavagatorWorker;
import start.module.myvim.handler.cmdhandler.CommandHandlerFactory;
import start.module.myvim.highlight.HighlightLine;
import start.module.myvim.highlight.HighlightLineQueue;
import start.module.myvim.quicklist.VIEXPopup;
import start.module.myvim.quicklist.VIEXQuickListItem;
import start.module.myvim.quicklist.VIEXQuickListScrollPane;
import start.module.myvim.state.Mode;
import start.module.myvim.state.QuickListType;
import start.module.myvim.utilities.CommandUtils;
import static javax.swing.text.Utilities.*;
import static start.module.myvim.state.CommandModeState.*;
import static start.module.myvim.utilities.ComponentUtils.*;
import static start.module.myvim.state.VIEXRepository.*;
import static start.module.myvim.utilities.CommandUtils.*;
import static start.module.myvim.utilities.FileUtils.*;
import start.module.myvim.highlight.EVIHighlightPainter;
import start.module.myvim.utilities.VIEXBundle;
import static start.module.myvim.utilities.VIEXUtil.*;
import static start.module.myvim.utilities.TextUtil.*;
import static start.module.myvim.handler.action.EVIActionFactory.*;

/**
 * Command Action implementation
 *
 * @author jker
 */
public class EVIActionFacade {
    
    private static final Logger logger = Logger.getLogger(EVIActionFacade.class.getName());
    
    
    protected static class FirstCharLineAction implements CommandAction {
        public void execute(ActionEvent e, String content) {
            
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int caret = target.getCaretPosition();
                try {
                    //int start = getRowStart(target, caret);
                    int start = org.netbeans.editor.Utilities.getRowFirstNonWhite((BaseDocument)target.getDocument(), caret);
                    if (start == -1 || caret == start) {
                        beep();
                        return ;
                    } else {
                        if (isVisualMode(target)) {
                            target.moveCaretPosition(start);
                        } else {
                            target.setCaretPosition(start);
                        }
                        setCaretMagicPosition(target, start);
                    }
                } catch (BadLocationException ex) {
                    beep();
                }
            }
        }
    }
    
    protected static class LastCharLineAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int caret = target.getCaretPosition();
                try {
                    //int end = getRowEnd(target, caret);
                    int end = org.netbeans.editor.Utilities.getRowLastNonWhite((BaseDocument)target.getDocument(), caret);
                    //if (caret != end) {
                    if (caret == end || end == -1) {
                        beep();
                        return ;
                    } else {
                        if (isVisualMode(target)) {
                            target.moveCaretPosition(end);
                        } else {
                            target.setCaretPosition(end);
                        }
                        setCaretMagicPosition(target, end);
                    }
                } catch (BadLocationException ex) {
                    beep();
                }
            }
        }
        
    }
    
    protected static class BeginLineAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int p = target.getCaretPosition();
                try {
                    int sp = getRowStart(target, p);
                    target.setCaretPosition(sp);
                    setCaretMagicPosition(target, sp);
                } catch (BadLocationException ex) {
                    //ex.printStackTrace();
                }
            }
        }
        
    }
    
    protected static class MiddleLineAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            
        }
        
    }
    
    protected static class LeftCommandAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                moveLeft(target, content);
            }
        }
        
        private void moveLeft(JTextComponent textComp, String content) {
            int position = textComp.getCaretPosition();
            if (null != content && 0 < content.length()) {
                position -= Integer.parseInt(content);
            } else {
                position -= 1;
            }
            if (0 <= position) {
                if (isVisualMode(textComp)) {
                    textComp.moveCaretPosition(position);
                } else {
                    textComp.setCaretPosition(position);
                    if (isVisualBlockMode(textComp))
                        rectangleAction(textComp);
                }
                setCaretMagicPosition(textComp, position);
            } else {
                int length = textComp.getDocument().getLength();
                if (isVisualMode(textComp)) {
                    textComp.moveCaretPosition(length);
                } else {
                    textComp.setCaretPosition(length);
                    if (isVisualBlockMode(textComp))
                        rectangleAction(textComp);
                }
                setCaretMagicPosition(textComp, length);
            }
        }
    }
    
    protected static class RightCommandAction implements CommandAction {
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                moveRight(target, content);
            }
        }
        
        private void moveRight(JTextComponent textComp, String content) {
            int position = textComp.getCaretPosition();
            if (null != content && 0 < content.length()) {
                position += Integer.parseInt(content);
            } else {
                position += 1;
            }
            int length = textComp.getDocument().getLength();
            if (length >= position) {
                if (isVisualMode(textComp)) {
                    textComp.moveCaretPosition(position);
                } else {
                    textComp.setCaretPosition(position);
                    if (isVisualBlockMode(textComp))
                        rectangleAction(textComp);
                }
                setCaretMagicPosition(textComp, position);
            } else {
                if (isVisualMode(textComp)) {
                    textComp.moveCaretPosition(0);
                } else {
                    textComp.setCaretPosition(0);
                    if (isVisualBlockMode(textComp))
                        rectangleAction(textComp);
                }
                setCaretMagicPosition(textComp, 0);
            }
        }
    }
    
    protected static class UpCommandAction implements CommandAction {
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                /*if (isMiscMode(target) && VIEXPopup.getPopup().isVisible()) {
                    VIEXPopup.getPopup().getContentComponent().up();
                    return ;
                }*/
                moveUp(target, content);
            }
        }
        
        private void moveUp(JTextComponent textComp, String content) {
            int position = 0;
            Caret caret = textComp.getCaret();
/*            try {
                int x = textComp.modelToView(c.getDot()).x;
                if (null != content && 0 < content.length()) {
                    Document doc = textComp.getDocument();
                    if (doc instanceof BaseDocument) {
                        BaseDocument baseDoc = (BaseDocument)doc;
                        int lines = org.netbeans.editor.Utilities.getLineOffset(baseDoc, c.getDot()) + 1;
                        int rc = org.netbeans.editor.Utilities.getRowCount(baseDoc);
                        int offLine = Integer.parseInt(content);
                        if ((lines - offLine) <= 0) {
                            position = org.netbeans.editor.Utilities.getRowStartFromLineOffset(baseDoc, 0);
                        } else {
                            position = org.netbeans.editor.Utilities.getRowStartFromLineOffset(baseDoc, lines - offLine - 1);
                        }
                    }
                } else {
                    //position = getPositionAbove(textComp,c.getDot(), x);
                    position = org.netbeans.editor.Utilities.getPositionAbove(textComp, c.getDot(), x);
                }*/
            int dot = caret.getDot();
            try {
                if (null != content && 0 <content.length()) {
                    Document doc = textComp.getDocument();
                    if (doc instanceof BaseDocument) {
                        BaseDocument baseDoc = (BaseDocument)doc;
                        int lines = org.netbeans.editor.Utilities.getLineOffset(baseDoc, caret.getDot()) + 1;
                        int rc = org.netbeans.editor.Utilities.getRowCount(baseDoc);
                        int offLine = Integer.parseInt(content);
                        if (( lines - offLine) <= 0) {
                            dot = org.netbeans.editor.Utilities.getRowStartFromLineOffset(baseDoc, 0);
                        } else {
                            dot = org.netbeans.editor.Utilities.getRowStartFromLineOffset(baseDoc, lines - offLine - 1);
                        }
                        Point p = caret.getMagicCaretPosition();
                        if (p == null) {
                            Rectangle r = textComp.modelToView(dot);
                            if (r != null) {
                                p = new Point(r.x, r.y);
                                caret.setMagicCaretPosition(p);
                            } else {
                                return ;
                            }
                        }
                        Rectangle r = textComp.modelToView(dot);
                        if (r != null) {
                            r.x = p.x;
                            Point p1 = new Point(r.x, r.y);
                            dot = textComp.viewToModel(p1);
                        }
                    }
                } else {
                    Point p = caret.getMagicCaretPosition();
                    if (p == null) {
                        Rectangle r = textComp.modelToView(dot);
                        if (r!=null){
                            p = new Point(r.x, r.y);
                            caret.setMagicCaretPosition(p);
                        }else{
                            return; // model to view failed
                        }
                    }
                    dot = org.netbeans.editor.Utilities.getPositionAbove(textComp, dot, p.x);
                }
            } catch (BadLocationException ex) {
                //ex.printStackTrace();
                beep();
            }
            if (dot >= 0) {
                if (isVisualMode(textComp)) {
                    caret.moveDot(dot);
                } else {
                    caret.setDot(dot);
                    if (isVisualBlockMode(textComp))
                        rectangleAction(textComp);
                }
            } else {
                beep();
            }
        }
    }
    
    protected static class DownCommandAction implements CommandAction {
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                /*if (isMiscMode(target) && VIEXPopup.getPopup().isVisible()) {
                    VIEXPopup.getPopup().getContentComponent().down();
                    return ;
                } */
                moveDown(target, content);
            }
        }
        
        private void moveDown(JTextComponent textComp, String content) {
            Caret caret = textComp.getCaret();
            int dot = caret.getDot();
            try {
/*                int x = textComp.modelToView(c.getDot()).x;
                if (null != content && 0 < content.length()) {
                    Document doc = textComp.getDocument();
                    if (doc instanceof BaseDocument) {
                        BaseDocument baseDoc = (BaseDocument)doc;
                        int lines = org.netbeans.editor.Utilities.getLineOffset(baseDoc, c.getDot()) + 1;
                        int rc = org.netbeans.editor.Utilities.getRowCount(baseDoc);
                        int offLine = Integer.parseInt(content);
                        if ((lines + offLine) > (rc + 1)) {
                            position = org.netbeans.editor.Utilities.getRowStartFromLineOffset(baseDoc, rc - 1);
                        } else {
                            position = org.netbeans.editor.Utilities.getRowStartFromLineOffset(baseDoc, lines + offLine - 1);
                        }
                    }
                } else {
                    //position = getPositionBelow(textComp,c.getDot(), x);
                    position = org.netbeans.editor.Utilities.getPositionBelow(textComp, c.getDot(), x);
                }*/
                if (null != content && 0 < content.length()) {
                    Document doc = textComp.getDocument();
                    if (doc instanceof BaseDocument) {
                        BaseDocument baseDoc = (BaseDocument)doc;
                        int lines = org.netbeans.editor.Utilities.getLineOffset(baseDoc, caret.getDot()) + 1;
                        int rc = org.netbeans.editor.Utilities.getRowCount(baseDoc);
                        int offLine = Integer.parseInt(content);
                        if ((lines + offLine) > (rc + 1)) {
                            dot = org.netbeans.editor.Utilities.getRowStartFromLineOffset(baseDoc, rc - 1);
                        } else {
                            dot = org.netbeans.editor.Utilities.getRowStartFromLineOffset(baseDoc, lines + offLine - 1);
                        }
                        Point p = caret.getMagicCaretPosition();
                        if (p == null) {
                            Rectangle r = textComp.modelToView(dot);
                            if (r != null) {
                                p = new Point(r.x, r.y);
                                caret.setMagicCaretPosition(p);
                            } else {
                                return ;
                            }
                        }
                        Rectangle r = textComp.modelToView(dot);
                        if (r != null) {
                            r.x = p.x;
                            Point p1 = new Point(r.x, r.y);
                            dot = textComp.viewToModel(p1);
                        }
                    }
                } else {
                    Point p = caret.getMagicCaretPosition();
                    if (p == null) {
                        Rectangle r = textComp.modelToView(dot);
                        if (r!=null){
                            p = new Point(r.x, r.y);
                            caret.setMagicCaretPosition(p);
                        }else{
                            return; // model to view failed
                        }
                    }
                    dot = org.netbeans.editor.Utilities.getPositionBelow(textComp, dot, p.x);
                }
            } catch (BadLocationException ex) {
                //ex.printStackTrace();
                beep();
            }
            if (dot >= 0) {
                if (isVisualMode(textComp)) {
                    caret.moveDot(dot);
                } else {
                    caret.setDot(dot);
                    if (isVisualBlockMode(textComp))
                        rectangleAction(textComp);
                }
            } else {
                beep();
            }
        }
    }
    
    protected static class FirstLineAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                Position position = target.getDocument().getStartPosition();
                if (isVisualMode(target)) {
                    target.moveCaretPosition(position.getOffset());
                } else {
                    target.setCaretPosition(position.getOffset());
                }
                setCaretMagicPosition(target, position.getOffset());
            }
        }
        
    }
    
    protected static class LastLineAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int caretEnd = target.getDocument().getLength() - 1;
                try {
                    if (caretEnd > 0) {
                        int begin = getRowStart(target, caretEnd);
                        if (isVisualMode(target)) {
                            target.moveCaretPosition(begin);
                        } else {
                            target.setCaretPosition(begin);
                        }
                        setCaretMagicPosition(target, begin);
                    } else {
                        beep();
                    }
                } catch (BadLocationException ex) {
                    beep();
                }
            }
        }
        
    }
    
    protected static class NextWordAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int caret = target.getCaretPosition();
                try {
                    int position = org.netbeans.editor.Utilities.getNextWord(target, caret);
                    if (isVisualMode(target)) {
                        target.moveCaretPosition(position);
                    } else {
                        target.setCaretPosition(position);
                    }
                } catch (BadLocationException ex) {
                    beep();
                }
                
            }
        }
        
    }
    
    protected static class PrevWordAction  implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int caret = target.getCaretPosition();
                try {
                    int position = org.netbeans.editor.Utilities.getPreviousWord(target, caret);
                    if (isVisualMode(target)) {
                        target.moveCaretPosition(position);
                    } else {
                        target.setCaretPosition(position);
                    }
                    setCaretMagicPosition(target, position);
                } catch (BadLocationException ex) {
                    //ex.printStackTrace();
                }
                
            }
        }
        
    }

    protected static class NextWordSpaceAction implements CommandAction {
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int p = target.getCaretPosition();
                Document doc = target.getDocument();
                int len = doc.getLength();
                try {
                    while (++p < len) {
                        String t = doc.getText(p, 1);
                        if (Character.isSpaceChar(t.charAt(0))) {
                            target.setCaretPosition(p + 1);
                            break ;
                        }
                    }
                    if (len == p) {
                        target.setCaretPosition(len);
                    }
                } catch (BadLocationException ex) {
                    //ex.printStackTrace();
                }
            }
        }
    }

    protected static class PrevWordSpaceAction implements CommandAction {
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int p = target.getCaretPosition();
                Document doc = target.getDocument();
                try {
                    --p; // important
                    while (--p > 0) {
                        String t = doc.getText(p, 1);
                        if (Character.isSpaceChar(t.charAt(0))) {
                            target.setCaretPosition(p + 1);
                            break ;
                        }
                    }
                    if (0 == p) {
                        target.setCaretPosition(0);
                    }
                } catch (BadLocationException ex) {
                    //ex.printStackTrace();
                }
            }
        }
    }
    
    //Integrated LineTools, Sandip V. Chitale
    protected static class MoveLineUpAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                Document doc = target.getDocument();
                if (doc instanceof BaseDocument) {
                    ((BaseDocument)doc).atomicLock();
                }
                try {
                    Element rootElement = doc.getDefaultRootElement();
                    
                    Caret caret = target.getCaret();
                    boolean selection = false;
                    boolean backwardSelection = false;
                    int start = target.getCaretPosition();
                    int end = start;
                    
                    // check if there is a selection
                    if (caret.isSelectionVisible()) {
                        int selStart = caret.getDot();
                        int selEnd = caret.getMark();
                        start = Math.min(selStart, selEnd);
                        end =   Math.max(selStart, selEnd) - 1;
                        selection = true;
                        backwardSelection = (selStart >= selEnd);
                    }
                    
                    int zeroBaseStartLineNumber = rootElement.getElementIndex(start);
                    int zeroBaseEndLineNumber = rootElement.getElementIndex(end);
                    
                    if (zeroBaseStartLineNumber == -1) {
                        // could not get line number
                        beep();
                        return;
                    } else if (zeroBaseStartLineNumber == 0) {
                        // already first line
                        return;
                    } else {
                        try {
                            //see if it be start with digital
                            /*int lc = 1;
                            if (null != content && 0 < content.length()) {
                                lc = Integer.parseInt(content);
                            }*/
                            // get line text
                            Element startLineElement = rootElement.getElement(zeroBaseStartLineNumber);
                            int startLineStartOffset = startLineElement.getStartOffset();
                            
                            Element endLineElement = rootElement.getElement(zeroBaseEndLineNumber);
                            int endLineEndOffset = endLineElement.getEndOffset();
                            
                            String linesText = doc.getText(startLineStartOffset, (endLineEndOffset - startLineStartOffset));
                            
                            //if command start with digital then move up lc lines
                            Element previousLineElement = rootElement.getElement(zeroBaseStartLineNumber - 1);
                            int previousLineStartOffset = previousLineElement.getStartOffset();
                            
                            int column = start - startLineStartOffset;
                            
                            // remove the line
                            doc.remove(startLineStartOffset, Math.min(doc.getLength(),endLineEndOffset) - startLineStartOffset);
                            
                            // insert the text before the previous line
                            doc.insertString(previousLineStartOffset, linesText, null);
                            
                            if (selection) {
                                // select moved lines
                                if (backwardSelection) {
                                    caret.setDot(previousLineStartOffset + column);
                                    caret.moveDot(previousLineStartOffset + (endLineEndOffset - startLineStartOffset) - (endLineEndOffset - end - 1));
                                } else {
                                    caret.setDot(previousLineStartOffset + (endLineEndOffset - startLineStartOffset) - (endLineEndOffset - end - 1));
                                    caret.moveDot(previousLineStartOffset + column);
                                }
                            } else {
                                // set caret position
                                target.setCaretPosition(previousLineStartOffset + column);
                            }
                        } catch (BadLocationException ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }
                } finally {
                    if (doc instanceof BaseDocument) {
                        ((BaseDocument)doc).atomicUnlock();
                    }
                }
            } else {
                beep();
            }
        }
    }
    
    protected static class MoveLineDownAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                Document doc = target.getDocument();
                if (doc instanceof BaseDocument) {
                    ((BaseDocument)doc).atomicLock();
                }
                try {
                    Element rootElement = doc.getDefaultRootElement();
                    
                    Caret caret = target.getCaret();
                    boolean selection = false;
                    boolean backwardSelection = false;
                    int start = target.getCaretPosition();
                    int end = start;
                    
                    // check if there is a selection
                    if (caret.isSelectionVisible()) {
                        int selStart = caret.getDot();
                        int selEnd = caret.getMark();
                        start = Math.min(selStart, selEnd);
                        end =   Math.max(selStart, selEnd) - 1;
                        selection = true;
                        backwardSelection = (selStart >= selEnd);
                    }
                    
                    int zeroBaseStartLineNumber = rootElement.getElementIndex(start);
                    int zeroBaseEndLineNumber = rootElement.getElementIndex(end);
                    
                    if (zeroBaseEndLineNumber == -1) {
                        // could not get line number
                        beep();
                        return;
                    } else if (zeroBaseEndLineNumber >= (rootElement.getElementCount() - 2)) {
                        // already last or penultimate line (due to a getLength() bug)
                        return;
                    } else {
                        try {
                            // get line text
                            Element startLineElement = rootElement.getElement(zeroBaseStartLineNumber);
                            int startLineStartOffset = startLineElement.getStartOffset();
                            
                            Element endLineElement = rootElement.getElement(zeroBaseEndLineNumber);
                            int endLineEndOffset = endLineElement.getEndOffset();
                            
                            String linesText = doc.getText(startLineStartOffset, (endLineEndOffset - startLineStartOffset));
                            
                            Element nextLineElement = rootElement.getElement(zeroBaseEndLineNumber + 1);
                            int nextLineStartOffset = nextLineElement.getStartOffset();
                            int nextLineEndOffset = nextLineElement.getEndOffset();
                            
                            int column = start - startLineStartOffset;
                            
                            // insert it after next line
                            doc.insertString(nextLineEndOffset, linesText, null);
                            
                            // remove original line
                            doc.remove(startLineStartOffset, (endLineEndOffset - startLineStartOffset));
                            
                            if (selection) {
                                // select moved lines
                                if (backwardSelection) {
                                    caret.setDot(nextLineEndOffset  - (endLineEndOffset - startLineStartOffset) + column);
                                    caret.moveDot(nextLineEndOffset - (endLineEndOffset - end - 1));
                                } else {
                                    caret.setDot(nextLineEndOffset - (endLineEndOffset - end - 1));
                                    caret.moveDot(nextLineEndOffset  - (endLineEndOffset - startLineStartOffset) + column);
                                }
                            } else {
                                // set caret position
                                target.setCaretPosition(Math.min(doc.getLength() - 1, nextLineEndOffset + column - (endLineEndOffset - startLineStartOffset)));
                            }
                        } catch (BadLocationException ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }
                } finally {
                    if (doc instanceof BaseDocument) {
                        ((BaseDocument)doc).atomicUnlock();
                    }
                }
            } else {
                beep();
            }
        }
    }
    
    protected static class CopyLineUpAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                Document doc = target.getDocument();
                if (doc instanceof BaseDocument) {
                    ((BaseDocument)doc).atomicLock();
                }
                try {
                    Element rootElement = doc.getDefaultRootElement();
                    
                    Caret caret = target.getCaret();
                    boolean selection = false;
                    boolean backwardSelection = false;
                    int start = target.getCaretPosition();
                    int end = start;
                    
                    // check if there is a selection
                    if (caret.isSelectionVisible()) {
                        int selStart = caret.getDot();
                        int selEnd = caret.getMark();
                        start = Math.min(selStart, selEnd);
                        end =   Math.max(selStart, selEnd) - 1;
                        selection = true;
                        backwardSelection = (selStart >= selEnd);
                    }
                    
                    int zeroBaseStartLineNumber = rootElement.getElementIndex(start);
                    int zeroBaseEndLineNumber = rootElement.getElementIndex(end);
                    
                    if (zeroBaseStartLineNumber == -1) {
                        // could not get line number
                        beep();
                        return;
                    } else {
                        try {
                            // get line text
                            Element startLineElement = rootElement.getElement(zeroBaseStartLineNumber);
                            int startLineStartOffset = startLineElement.getStartOffset();
                            
                            Element endLineElement = rootElement.getElement(zeroBaseEndLineNumber);
                            int endLineEndOffset = endLineElement.getEndOffset();
                            
                            String linesText = doc.getText(startLineStartOffset, (endLineEndOffset - startLineStartOffset));
                            
                            int column = start - startLineStartOffset;
                            
                            // insert it
                            doc.insertString(startLineStartOffset, linesText, null);
                            
                            if (selection) {
                                // select moved lines
                                if (backwardSelection) {
                                    caret.setDot(startLineStartOffset + column);
                                    caret.moveDot(startLineStartOffset + (endLineEndOffset - startLineStartOffset) - (endLineEndOffset - end - 1));
                                } else {
                                    caret.setDot(startLineStartOffset + (endLineEndOffset - startLineStartOffset) - (endLineEndOffset - end - 1));
                                    caret.moveDot(startLineStartOffset + column);
                                }
                            } else {
                                // set caret position
                                target.setCaretPosition(startLineStartOffset + column);
                            }
                        } catch (BadLocationException ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }
                } finally {
                    if (doc instanceof BaseDocument) {
                        ((BaseDocument)doc).atomicUnlock();
                    }
                }
            } else {
                beep();
            }
        }
    }
    
    protected static class CopyLineDownAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                Document doc = target.getDocument();
                if (doc instanceof BaseDocument) {
                    ((BaseDocument)doc).atomicLock();
                }
                try {
                    Element rootElement = doc.getDefaultRootElement();
                    
                    
                    Caret caret = target.getCaret();
                    boolean selection = false;
                    boolean backwardSelection = false;
                    int start = target.getCaretPosition();
                    int end = start;
                    
                    // check if there is a selection
                    if (caret.isSelectionVisible()) {
                        int selStart = caret.getDot();
                        int selEnd = caret.getMark();
                        start = Math.min(selStart, selEnd);
                        end =   Math.max(selStart, selEnd) - 1;
                        selection = true;
                        backwardSelection = (selStart >= selEnd);
                    }
                    
                    int zeroBaseStartLineNumber = rootElement.getElementIndex(start);
                    int zeroBaseEndLineNumber = rootElement.getElementIndex(end);
                    
                    if (zeroBaseEndLineNumber == -1) {
                        // could not get line number
                        beep();
                        return;
                    } else {
                        try {
                            // get line text
                            Element startLineElement = rootElement.getElement(zeroBaseStartLineNumber);
                            int startLineStartOffset = startLineElement.getStartOffset();
                            
                            Element endLineElement = rootElement.getElement(zeroBaseEndLineNumber);
                            int endLineEndOffset = endLineElement.getEndOffset();
                            
                            String linesText = doc.getText(startLineStartOffset, (endLineEndOffset - startLineStartOffset));
                            
                            int column = start - startLineStartOffset;
                            
                            // insert it after next line
                            doc.insertString(endLineEndOffset, linesText, null);
                            
                            if (selection) {
                                // select moved lines
                                if (backwardSelection) {
                                    caret.setDot(endLineEndOffset + column);
                                    caret.moveDot(endLineEndOffset + (endLineEndOffset - startLineStartOffset) - (endLineEndOffset - end - 1));
                                } else {
                                    caret.setDot(endLineEndOffset + (endLineEndOffset - startLineStartOffset) - (endLineEndOffset - end - 1));
                                    caret.moveDot(endLineEndOffset + column);
                                }
                            } else {
                                // set caret position
                                target.setCaretPosition(Math.min(doc.getLength() - 1, endLineEndOffset + column));
                            }
                        } catch (BadLocationException ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }
                } finally {
                    if (doc instanceof BaseDocument) {
                        ((BaseDocument)doc).atomicUnlock();
                    }
                }
            } else {
                beep();
            }
        }
        
    }
    //end
    
    protected static class CopyLineToClipboard implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int caret = target.getCaretPosition();
                try {
                    int begin = getRowStart(target, caret);
                    int end = getRowEnd(target, caret);
                    Document doc = target.getDocument();
                    copyContentToClipboard("\n" + doc.getText(begin, end - begin));
                } catch (BadLocationException ex) {
                    //
                }
            }
        }
        
    }
    
    protected static class MoveNextAnnotationAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                try {
                    int lastLine = org.netbeans.editor.Utilities.getLineOffset(org.netbeans.editor.Utilities.getDocument(target), target.getCaret().getDot()) + 1;
                    BaseDocument doc = org.netbeans.editor.Utilities.getDocument(target);
                    Annotations  ann = doc.getAnnotations();
                    int lines = org.netbeans.editor.Utilities.getRowCount(doc);
                    //repeat find annotation
                    int rline = lines;
                    for (int i = lastLine; i < rline; i++) {
                        AnnotationDesc desc = ann.getActiveAnnotation(i);
                        if (desc != null) {
                            //logger.info("Line:" + desc.getOffset() + desc.getShortDescription());
                            target.getCaret().setDot(desc.getOffset());
                            eviStatusBarInfo(target, desc.getShortDescription());
                            return ;
                        }
                        if ((i + 1) == lines) {
                            i = 0;
                            rline = lastLine;
                        }
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
    }
    
    protected static class ShiftLineLeftAction implements CommandAction {
        
        private Action a;
        
        public ShiftLineLeftAction() {
            a = new NBActionFacade.ShiftLineAction(NBActionFacade.shiftLineLeftAction, false);
        }
        
        public void execute(ActionEvent e, String content) {
            a.actionPerformed(e);
        }
        
    }
    
    protected static class ShiftLineRightAction implements CommandAction {
        
        private Action a;
        
        public ShiftLineRightAction() {
            a = new NBActionFacade.ShiftLineAction(NBActionFacade.shiftLineRightAction, true);
        }
        
        public void execute(ActionEvent e, String content) {
            a.actionPerformed(e);
        }
        
    }
    
    protected static class InsertAtCaretAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if ((target != null) && target.isEnabled()) {
                target.setCaretPosition(target.getCaretPosition());
                removeEVIKeymap(target);
            }
        }
        
    }

    protected static class InsertBeginLineAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int p = target.getCaretPosition();
                try {
                    int start = Utilities.getRowStart(target, p);
                    Caret caret = target.getCaret();
                    caret.setDot(start);
                } catch (BadLocationException ex) {
                    // ex.printStackTrace();
                }
                removeEVIKeymap(target);
            }
        }
        
    }
    
    protected static class InsertAfterCaretAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)){
                int caret = target.getCaretPosition();
                try {
                    int start = getRowStart(target, caret);
                    int end = getRowEnd(target, caret);
                    if (caret < target.getDocument().getLength()) {
                        if (caret < end) {
                            target.setCaretPosition(caret + 1);
                        }
                    }
                    removeEVIKeymap(target);
                } catch (BadLocationException ee) {
                    
                }
            }
        }
        
    }
    
    protected static class InsertBelowLineAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int caret = target.getCaretPosition();
                try {
                    int position = getRowEnd(target, caret);
                    target.setCaretPosition(position);
                    target.getDocument().insertString(position, "\n", null);
                    removeEVIKeymap(target);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
    }
    
    protected static class InsertEndLineAction implements CommandAction {
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int p = target.getCaretPosition();
                try {
                    int end = getRowEnd(target, p);
                    Caret caret = target.getCaret();
                    if (caret.isSelectionVisible()) {
                        caret.setDot(caret.getDot());
                    } else {
                        caret.setDot(end);
                    }
                } catch (BadLocationException ex) {
                    // ex.printStackTrace();
                }
                removeEVIKeymap(target);
            }
        }
    }

    protected static class InsertCharsPairAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            insertTextPair(content, target);
        }

    }

    protected static class ChangeCurrentLineAction implements CommandAction {
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int p = target.getCaretPosition();
                try {
                    int begin = getRowStart(target, p);
                    int end = getRowEnd(target, p);
                    Document doc = target.getDocument();
                    if (doc instanceof BaseDocument) {
                        BaseDocument doc2 = (BaseDocument)doc;
                        try {
                            doc2.atomicLock();
                            doc2.remove(begin, end - begin);
                        } finally {
                            doc2.atomicUnlock();
                        }
                    }
                } catch (BadLocationException ex) {
                    //ex.printStackTrace();
                }
                removeEVIKeymap(target);
            }
        }
    }
    
    protected static class ChangeToEndLineAction implements CommandAction {
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int p = target.getCaretPosition();
                try {
                    int end = getRowEnd(target, p);
                    Document doc = target.getDocument();
                    if (doc instanceof BaseDocument) {
                        BaseDocument doc2 = (BaseDocument)doc;
                        try {
                            doc2.atomicLock();
                            doc2.remove(p, end - p);
                        } finally {
                            doc2.atomicUnlock();
                        }
                    }
                } catch (BadLocationException ex) {
                    //ex.printStackTrace();
                }
                removeEVIKeymap(target);
            }
        }
    }
    
    protected static class SwitchCaseAction implements CommandAction {
        
        private Action a;
        
        public SwitchCaseAction() {
            a = new NBActionFacade.ChangeCaseAction(NBActionFacade.switchCaseAction, NBActionFacade.CASE_SWITCH, false);
        }
        
        public void execute(ActionEvent e, String content) {
            a.actionPerformed(e);
        }
    }
    
    protected static class SwitchUpperAction implements CommandAction {
        
        private Action a;
        
        public SwitchUpperAction() {
            a = new NBActionFacade.ChangeCaseAction(NBActionFacade.toUpperCaseAction, NBActionFacade.CASE_UPPER, false);
        }
        
        public void execute(ActionEvent e, String content) {
            a.actionPerformed(e);
        }
        
    }
    
    protected static class SwitchLowerAction implements CommandAction {
        
        private Action a;
        
        public SwitchLowerAction() {
            a = new NBActionFacade.ChangeCaseAction(NBActionFacade.toLowerCaseAction, NBActionFacade.CASE_LOWER, false);
        }
        
        public void execute(ActionEvent e, String content) {
            a.actionPerformed(e);
        }
        
    }
    
    protected static class SwitchUpperLineAction implements CommandAction {
        
        private Action a;
        
        public SwitchUpperLineAction() {
            a = new NBActionFacade.ChangeCaseAction(NBActionFacade.toUpperCaseAction, NBActionFacade.CASE_UPPER, true);
        }
        
        public void execute(ActionEvent e, String content) {
            a.actionPerformed(e);
        }
    }
    
    protected static class SwitchLowerLineAction implements CommandAction {
        
        private Action a;
        
        public SwitchLowerLineAction() {
            a = new NBActionFacade.ChangeCaseAction(NBActionFacade.toLowerCaseAction, NBActionFacade.CASE_LOWER, true);
        }
        
        public void execute(ActionEvent e, String content) {
            a.actionPerformed(e);
        }
        
    }
    
    protected static class SwitchCaseLineAction implements CommandAction {
        
        private Action a;
        
        public SwitchCaseLineAction() {
            a = new NBActionFacade.ChangeCaseAction(NBActionFacade.switchCaseAction, NBActionFacade.CASE_SWITCH, true);
        }
        
        public void execute(ActionEvent e, String content) {
            a.actionPerformed(e);
        }
        
    }

    protected static class SurroundWithSingleMarkAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            insertTextPair("'", target);
        }

    }

    protected static class SurroundWithDoubleMarkAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            insertTextPair("\"", target);
        }

    }
    
    protected static class DeleteNextCharAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                Caret caret = target.getCaret();
                int dot = caret.getDot();
                int mark = caret.getMark();
                try {
                    Document doc = target.getDocument();
                    if (dot == mark) {
                        if (doc.getLength() > dot) {
                            doc.remove(dot, 1);
                        } else if (0 < dot) {
                            doc.remove(dot -1, 1);
                        } else {
                            //
                        }
                    } else {
                        int min = Math.min(dot, mark);
                        int max = Math.max(dot, mark);
                        doc.remove(min, max - min);
                    }
                } catch (BadLocationException ex) {
                    
                }
            }
        }
        
    }
    
    protected static class DeletePrevCharAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                Caret caret = target.getCaret();
                int dot = caret.getDot();
                int mark = caret.getMark();
                Document doc = target.getDocument();
                try {
                    if ((dot == mark)) {
                        if (0 < dot) {
                            doc.remove(dot -1, 1);
                        } else if (doc.getLength() > dot) {
                            doc.remove(dot, 1);
                        } else {
                            //
                        }
                    } else {
                        int min = Math.min(dot, mark);
                        int max = Math.max(dot, mark);
                        doc.remove(min, max - min);
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
    }

    protected static class DelCharsPairAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            delChasPair(target);
        }

    }
    
    protected static class DeleteLineAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int caret = target.getCaretPosition();
                try {
                    int begin = getRowStart(target, caret);
                    int end = getRowEnd(target, caret);
                    int length = target.getDocument().getLength();
                    if (begin > 0) {
                        if (end >= length) {
                            begin -= 1;
                        } else if (end < length) {
                            end += 1;
                        }
                    } else {
                        if (end < length){
                            end += 1;
                        }
                    }
                    Document doc = target.getDocument();
                    String rtext = doc.getText(begin, end - begin);
                    if (rtext.indexOf('\n') != -1) {
                        String rtext2 = "\n" + rtext.substring(0, rtext.indexOf('\n'));//\uFFFD\uFFFD'\n'\u05B8\u02BE\uFFFD\uFFFD\uFFFD\uFFFDdd
                        copyContentToClipboard(rtext2);
                    }
                    doc.remove(begin, end - begin);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
    }
    
    protected static class DeleteToEndAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int caret = target.getCaretPosition();
                try {
                    int end = getRowEnd(target, caret);
                    if (caret != end) {
                        String rtext = target.getDocument().getText(caret, end - caret);
                        copyContentToClipboard(rtext);
                        target.getDocument().remove(caret, end - caret);
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                
            }
        }
        
    }
    
    protected static class DeleteToBeginAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int caret = target.getCaretPosition();
                try {
                    int begin = getRowStart(target, caret);
                    if (begin != caret) {
                        String rtext = target.getDocument().getText(begin, caret - begin);
                        copyContentToClipboard(rtext);
                        target.getDocument().remove(begin, caret - begin);
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
    }
    
    protected static class DeleteNextWordAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int caret = target.getCaretPosition();
                try {
                    Document doc = target.getDocument();
                    if (doc instanceof BaseDocument) {
                        BaseDocument doc2 = (BaseDocument)doc;
                        int p = 0;
                        try {
                            doc2.atomicLock();
                            String s = doc2.getText(caret, 1);
                            // fixed can't delete space when execute "dw" command.
                            if (' ' == s.charAt(0))
                                p = Utilities.getWordEnd(target, caret);
                            else
                                p = org.netbeans.editor.Utilities.getWordEnd(target, caret);
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        } finally {
                            doc2.atomicUnlock();
                        }
                        String rtext = target.getDocument().getText(caret, p - caret);
                        copyContentToClipboard(rtext);
                        target.getDocument().remove(caret, p - caret);
                    }
                    //int position = org.netbeans.editor.Utilities.getWordEnd(target, caret);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                
            }
        }
        
    }
    
    protected static class DeletePrevWordAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int caret= target.getCaretPosition();
                try {
                    int position = org.netbeans.editor.Utilities.getWordStart(target, caret);
                    if ((position == caret) && (caret > 0)) {
                        position = getPreviousWord(target, caret);
                    }
                    String rtext = target.getDocument().getText(position, caret - position);
                    copyContentToClipboard(rtext);
                    target.getDocument().remove(position, caret - position);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                
            }
        }
        
    }
    
    protected static class DeleteCaretWordAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int caret = target.getCaretPosition();
                try {
                    int ws = Utilities.getWordStart(target, caret);
                    int we = Utilities.getWordEnd(target, caret);
                    Document doc = target.getDocument();
                    String rtext = doc.getText(ws, we - ws);
                    copyContentToClipboard(rtext);
                    doc.remove(ws, we - ws);
                } catch (BadLocationException ex) {
                    beep();
                }
            }
        }
        
    }
    
    protected static class DeleteIncspaceCaretWordAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int caret = target.getCaretPosition();
                try {
                    int ws = Utilities.getWordStart(target, caret);
                    int we = Utilities.getWordEnd(target, caret);
                    int ls = Utilities.getRowStart(target, caret);
                    int le = Utilities.getRowEnd(target, caret);
                    
                    Document doc = target.getDocument();
                    for (int i = 1; true; i++) {
                        int s = ws - i;
                        if (ls <= s) {
                            String c = doc.getText(s, 1);
                            if (!c.equals(" ")) {
                                ws = s + 1;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    //\uFFFD\u04B3\uFFFD\u04F9\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\u06B5\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\u04BB\uFFFD\uFFFD\uFFFD\u01FF\u0578\uFFFD\uFFFD\uFFFD\u05B7\uFFFD\uFFFD\u03BB\uFFFD\uFFFD
                    for (int i = 1; true; i++) {
                        int s = we + i;
                        if (le >= s) {
                            String c = doc.getText(s, 1);
                            if (!c.equals(" ")) {
                                we = s;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    
                    int r = we - ws;
                    if (r > 0) {
                        String rtext = doc.getText(ws, r);
                        copyContentToClipboard(rtext);
                        doc.remove(ws, r);
                    }
                } catch (BadLocationException ex) {
                    beep();
                }
            }
        }
        
    }
    
    protected static class DeleteNewlineAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int caret = target.getCaretPosition();
                try {
                    int le = Utilities.getRowEnd(target, caret);
                    Document doc = target.getDocument();
                    if (doc.getText(le, 1).charAt(0) == '\n') {
                        doc.remove(le, 1);
                        int i = le;
                        while (Character.isWhitespace(doc.getText(i, 1).charAt(0))) {
                            i++;
                        }
                        doc.remove(le, i - le);
                        target.setCaretPosition(le);
                    }
                } catch (BadLocationException ex) {
                    //ex.printStackTrace();
                    beep();
                }
            }
        }
        
    }
    
    protected static class DeleteAndInsertAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                if (target.getSelectedText() != null) {
                    target.replaceSelection("");
                } else {
                    try {
                        Document doc = target.getDocument();
                        int position = target.getCaretPosition();
                        if ('\n' != doc.getText(position, 1).charAt(0))
                            target.getDocument().remove(target.getCaretPosition(), 1);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
                removeEVIKeymap(target);
            }
        }
        
    }

    protected static class ChangeWordAction implements CommandAction {
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int caret = target.getCaretPosition();
                try {
                    Document doc = target.getDocument();
                    if (doc instanceof BaseDocument) {
                        BaseDocument doc2 = (BaseDocument)doc;
                        int p = 0;
                        try {
                            doc2.atomicLock();
                            String s = doc2.getText(caret, 1);
                            // fixed can't delete space when execute "dw" command.
                            if (' ' == s.charAt(0))
                                p = Utilities.getWordEnd(target, caret);
                            else
                                p = org.netbeans.editor.Utilities.getWordEnd(target, caret);
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        } finally {
                            doc2.atomicUnlock();
                        }
                        String rtext = target.getDocument().getText(caret, p - caret);
                        copyContentToClipboard(rtext);
                        target.getDocument().remove(caret, p - caret);
                    }
                    //int position = org.netbeans.editor.Utilities.getWordEnd(target, caret);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                removeEVIKeymap(target);
            }
        }
        
    }
    
    protected static class UndoAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                BaseKit kit = org.netbeans.editor.Utilities.getKit(target);
                if (kit != null) {
                    Action a = kit.getActionByName(BaseKit.undoAction);
                    if (a != null) {
                        a.actionPerformed(e);
                    }
                }
            }
        }
        
    }

    protected static class GotoFileAction implements CommandAction {

/*        public static final String[] disallow = new String[] {
            "~", "!", "@", "#", "$", "%", "^", "&", "*",
            "(", ")", "+", "=", "|", "}", "{", "[", "]",
            "<", ">", "?", ",", "'", "\"",  "\n" , "\t", "`",
        };*/

        public static String disallow = "~ !@#$%^&*()+=|}{[]<>?,'\"\n\t`\r";
       
        public void execute(ActionEvent e, String content) {
            logger.fine("execute goto file action...");
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                String filePath = guessFilePath(target);
                //logger.info("File Path:" + filePath);
                FileObject fo = guessFileObject(filePath, getCurrentFile(target), false);
                if (null == fo || !fo.isValid()) {
                    return ;
                }
                //logger.info("Open file:" + fo.getName());
                try {
                    DataObject dobject = DataObject.find(fo);
                    EditorCookie cookie = (EditorCookie) dobject.getCookie(EditorCookie.class);
                    if (cookie != null)
                        cookie.open();
                } catch (DataObjectNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private String guessFilePath(JTextComponent target) {
            int p = target.getCaretPosition();
                //int sp = Utilities.getRowStart(target, p);
                //int ep = Utilities.getRowEnd(target, p);
            Document doc = target.getDocument();
            int start = 0;
            int end = 0;
            int i = 0;
            
            disallow += getDisallowCharsInSystem();
            //logger.info("Disallow chars:" + disallow);

            String text = null;
            try {
                int temp = p + i;
                int docLen = doc.getLength();
                while(temp < docLen) {  // froward look
                    //logger.info("forward look...");
                    String s = doc.getText(temp, 1);
                    if (disallow.indexOf(s) != -1) { 
                        end = temp - 1;      // removed disallow char
                        break ;
                    } else if (temp + 1 >= docLen) { // arrived end of document
                        end = temp;
                        break ;
                    }
                    i++;
                    temp = p + i;
                }
                i = 0;
                temp = p - i;
                while(temp >= 0) {  // back look
                    //logger.info("back look...");
                    String s= doc.getText(temp, 1);
                    if (disallow.indexOf(s) != -1) {
                        start = temp + 1;    // removed disallow char
                        break ;
                    } else if (temp - 1 < 0) { // arrived start of document
                        start = temp;        
                        break ;
                    }
                    i++;
                    temp = p - i;
                }      
                text = doc.getText(start, end + 1 - start);
            } catch (BadLocationException ex) {
                    //ex.printStackTrace();
            }
            return text;
        }
        
    }

    protected static class HexValueAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int p = target.getCaretPosition();
                try {
                    String s = target.getDocument().getText(p, 1);
                    String hexs = "";
                    hexs = toHexValue(s);
                    org.netbeans.editor.Utilities.setStatusText(target, "--Hex Value--" + hexs);
                } catch (BadLocationException ex) {
                    //ex.printStackTrace();
                }
            }
        }

    }

    protected static class AsciiValueAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int p = target.getCaretPosition();
                String asciis = " <";
                try {
                    String s = target.getDocument().getText(p, 1);
                    asciis += s;
                    asciis += "> ";
                    asciis += Character.codePointAt(s, 0);
                    asciis += ", Hex Value:";
                    asciis += toHexValue(s); 
                } catch (BadLocationException ex) {
                    //ex.printStackTrace();
                }
                org.netbeans.editor.Utilities.setStatusText(target, asciis);
            }
        }
        
    }


    protected static class MiscModeCommandAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (target == null)
                return ;
            CommandMode mode = getRespository().getEditorState(target).getCommandMode();
            if (mode == null)
                throw new EVIException(VIEXBundle.getMessage("MODE_ERROR"));
            mode.switchMode(Mode.MISC_MODE, target);
            if (target.getCaret().isSelectionVisible()) //cancel caret selected
                target.setCaretPosition(target.getCaretPosition());
        }
        
    }
    
    protected static class ClosePopupAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            VIEXPopup popup = VIEXPopup.getPopup();
            if (popup.isVisible())
                popup.hide();
            JTextComponent target = getTextComponent(e);
            if (target == null)
                return ;
            CommandMode mode = getRespository().getEditorState(target).getCommandMode();
            if (mode == null)
                throw new EVIException(VIEXBundle.getMessage("MODE_ERROR"));
            mode.switchMode(Mode.COMMAND_MODE, target);
        }
        
    }

    protected static class Native2AsciiAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                Caret caret = target.getCaret();
                int p = Math.min(caret.getDot(), caret.getMark());
                String s = native2Ascii(target.getSelectedText());
                deleteSelectedText(target);
                try {
                    target.getDocument().insertString(p, s, null);
                } catch (BadLocationException ex) {
                    logger.severe(ex.getMessage());
                }
            }
        }

    }

    protected static class Ascii2NativeAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                Caret caret = target.getCaret();
                int p = Math.min(caret.getDot(), caret.getMark());
                String s = ascii2Native(target.getSelectedText());
                deleteSelectedText(target);
                try {
                    target.getDocument().insertString(p, s, null);
                } catch (BadLocationException ex) {
                    logger.severe(ex.getMessage());
                }
            }
        }

    }

    protected static class GenerateSUIDAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            logger.info("Executing Generate SUID");
        }

    }
    
    //NON-API Module
    protected static class OpenBookmarkListAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
/*            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                Document doc = target.getDocument();
                BookmarkList bookmarks = BookmarkList.get(doc);
                int counts = bookmarks.getBookmarkCount();
                List<VIEXQuickListItem> itemList = new ArrayList();
                for (int i = 0; i < counts; i++) {
                    Bookmark bk = bookmarks.getBookmark(i);
                    itemList.add(new BookmarkQuickListItem(bk));
                }
                if (0 == itemList.size())
                    return ;
                VIEXPopup popup = VIEXPopup.getPopup();
                popup.setEditorComponent(target);
                quickListPane.setData(itemList, VIEXBundle.getMessage("TAB_LIST_TITLE"), 0);
                popup.setContentComponent(quickListPane);
                popup.QUICK_LIST_TYPE = QuickListType.BOOKMARK;
                popup.show();
            }*/
        }
        
    }
    
    //NON-API Module
    protected static class OpenEditorListAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            final JTextComponent target = getTextComponent(e);
            if (!isTargetEnabled(target))
                return ;
            QuickNavagatorWorker worker = QuickNavagatorFactory.getNavagatorFactory().getNavagatorWorker(QuickListType.TAB_EIDTOR);
            worker.setComponent(target);
            worker.openQuickList();
        }
        
        /*private TabbedContainer getTabbedContainer(Container component) {
            if ((component instanceof TabbedContainer) || (null == component))
                return (TabbedContainer)component;
            return getTabbedContainer(component.getParent());
        }*/
    }
    
    protected static class OpenProjectFilesListAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            //logger.info("open project files list action executed");
            final JTextComponent target = getTextComponent(e);
            if (!isTargetEnabled(target))
                return ;
            //FileSearch.getInstance().execute(target, content);
            QuickNavagatorWorker worker = QuickNavagatorFactory.getNavagatorFactory().getNavagatorWorker(QuickListType.FILE_OBJECT);
            worker.setComponent(target);
            worker.openQuickList();
        }
        
    }
    
    protected static class UpMiscCommandAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            QuickNavagatorWorker worker = QuickNavagatorFactory.getNavagatorFactory().getNavagatorWorker();
            if (worker != null)
                worker.up();
        }
        
    }
    
    protected static class DownMiscCommandAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            QuickNavagatorWorker worker = QuickNavagatorFactory.getNavagatorFactory().getNavagatorWorker();
            if (worker != null)
                worker.down();
        }
        
    }
    
    protected static class ListPageUpAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            QuickNavagatorWorker worker = QuickNavagatorFactory.getNavagatorFactory().getNavagatorWorker();
            if (worker != null)
                worker.pageUp();
        }
        
    }
    
    protected static class ListPageDownAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            QuickNavagatorWorker worker = QuickNavagatorFactory.getNavagatorFactory().getNavagatorWorker();
            if (worker != null)
                worker.pageDown();
        }
        
    }
    
    protected static class MiscDigitalAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            VIEXPopup popup = VIEXPopup.getPopup();
            if (popup.isVisible()) {
                JTextComponent target = getMostActiveComponent();
                if (target != null) {
                    if (isValid(content, target)) {
                        try {
                            int i = NumberFormat.getInstance().parse(content).intValue();
                            JList view = popup.getContentComponent().getView();
                            int mcounts = view.getModel().getSize();
                            if (isMatch(i, mcounts)) {// Match prefix of list index, reset when if not found.
                                view.setSelectedIndex(i);
                                view.ensureIndexIsVisible(i);
                                if (i * 10 > mcounts)
                                    CommandHandlerFactory.getCommandHandler(null, target).reset();
                            } else {
                                CommandHandlerFactory.getCommandHandler(null, target).reset();
                            }
                        } catch (ParseException ex) {
                            //   ex.printStackTrace(); never occured
                        }
                    }
                    VIEXQuickListItem item = (VIEXQuickListItem)popup.getContentComponent().getView().getSelectedValue();
                    if (null == item)
                        return ;
                    if (item.isImmediate())
                        item.defaultAction(target);
                    else if (isEndWithEnter(content)) {
                        item.defaultAction(target);
                    }
                }
            }
        }
        
        private boolean isValid(String content, JTextComponent target) {
            try {
                int i = NumberFormat.getInstance().parse(content).intValue();
                if (i < VIEXPopup.getPopup().getContentComponent().getView().getModel().getSize())
                    return true;
                else {
                    CommandHandlerFactory.getCommandHandler(null, target).reset();
                }
                return false;
            } catch (Exception ex) {
                return false;
            }
        }
        
        private boolean isMatch(int content, int size) {
            if (content <= size)
                return true;
            return false;
        }
        
    }
    
    protected static class ReverseListAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            //logger.info("reverse list action executed");
            QuickNavagatorFactory.getNavagatorFactory().getNavagatorWorker().reverseList();
        }
        
    }
    
    protected static class SearchMatchItemAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            //logger.info("Search match item Action");
            JTextComponent target = getTextComponent(e);
            if (!isTargetEnabled(target))
                return ;
            String key = null;
            VIEXPopup popup = VIEXPopup.getPopup();
            if (CommandUtils.isEndWithEnter(content)) {
                popup.getContentComponent().setTitle(" ");
            } else {
                key = content;
                popup.getContentComponent().setTitle("/" + key);
                //FileSearch.getInstance().prefixMatch(target, key);
                QuickNavagatorFactory.getNavagatorFactory().getNavagatorWorker().prefixMatch(key);
            }
        }
        
    }
    
    
    
    protected static class ShowLineNumberAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                EditorUI editor = org.netbeans.editor.Utilities.getEditorUI(target);
                if (editor != null) {
                    editor.setLineNumberEnabled(!editor.isLineNumberEnabled());
                    //AllOptionsFolder.getDefault().setLineNumberVisible(!AllOptionsFolder.getDefault().getLineNumberVisible());
                }
            }
        }
        
    }
    
    protected static class SearchNextByHistoryKeyAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                //HistorySearch hs = HistorySearch.getInstance();
                HistorySearch hs = getRespository().getEditorState(target).getHistorySearch();
                String key = hs.getHistoryCommand();
                if (key.length() != 0) {
                    SearchEngine engine = SearchEngine.getEngine();
                    engine.setSource(target);
                    //engine.setDirection(SearchEngine.NEXT);
                    engine.tracking(key, hs.getOffset());
                    if (engine.isFound()) {
                        hs.setOffset(engine.getEnd()); //\uFFFD\uFFFD\u04BB\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\u0470\uFFFD\uFFFD\uFFFD\uFFFD\u05B7\uFFFD\u03BB\uFFFDú\uFFFD\uFFFD\u6FEA\u02BC
                    }
                }
            }
        }
        
    }
    
    protected static class SearchPrevByHistoryKeyAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                //HistorySearch hs = HistorySearch.getInstance();
                HistorySearch hs = getRespository().getEditorState(target).getHistorySearch();
                String key = hs.getHistoryCommand();
                if (key.length() != 0) {
                    SearchEngine engine = SearchEngine.getEngine();
                    engine.setSource(target);
                    engine.setDirection(SearchEngine.PREV);
                    engine.tracking(key, hs.getOffset());
                    if (engine.isFound()) {
                        hs.setOffset(engine.getStart());
                    }
                }
            }
        }
        
    }
    
    
//NB
    protected static class SaveAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            Node[] n = TopComponent.getRegistry().getActivatedNodes();
            if (n.length == 1) {
                EditorCookie ec = (EditorCookie) n[0].getCookie(EditorCookie.class);
                if (ec != null) {
                    try {
                        ec.saveDocument();
                    } catch (IOException ex) {
                        //ex.printStackTrace();
                    }
                }
            }
        }
        
    }
//NB
    protected static class SaveAndExitAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            Node[] n = TopComponent.getRegistry().getActivatedNodes();
            if (n.length == 1) {
                EditorCookie ec = (EditorCookie) n[0].getCookie(EditorCookie.class);
                if (ec != null) {
                    try {
                        ec.saveDocument();
                        ec.close();
                    } catch (IOException ex) {
                        //ex.printStackTrace();
                    }
                }
            }
        }
        
    }
    
    protected static class ExitForceAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            Node[] n = TopComponent.getRegistry().getActivatedNodes();
            if (n.length == 1) {
                EditorCookie ec = (EditorCookie) n[0].getCookie(EditorCookie.class);
                if (ec != null) {
                    //ec.getDocument().
                    //logger.info("" + ec);
                    DataObject dobject = (DataObject)n[0].getLookup().lookup(DataObject.class);
                    if (dobject.isModified())
                        dobject.setModified(false);
                    ec.close();
                    try {
                        dobject.setValid(false);
                    } catch (PropertyVetoException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        
    }
    
    protected static class ExitAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            Node[] n = TopComponent.getRegistry().getActivatedNodes();
            if (n.length == 1) {
                EditorCookie ec = (EditorCookie)n[0].getCookie(EditorCookie.class);
                if (ec != null) {
                    ec.close();
                }
            }
        }
        
    }
    
    protected static class NewAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            //logger.info("New file:" + content);
            JTextComponent target = getTextComponent(e);
            if (null == target)
                return ;
            try {
                FileObject currentFile = getCurrentFile(target);
                FileObject fo = new NewFileTemplate().createFileFromTemplate(currentFile, content);
                if (null == fo || !fo.isValid()) {
                    return ;
                }
                DataObject dobject = DataObject.find(fo);
                EditorCookie cookie = (EditorCookie) dobject.getCookie(EditorCookie.class);
                if (cookie != null) {
                    cookie.open();
                }
            } catch (IOException ex) {
                //ex.printStackTrace();
            }
        }
    }
    
    protected static class ReplaceSingleCharAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                int caret = target.getCaretPosition();
                Document doc = target.getDocument();
                if (caret < doc.getLength()) {
                    if (target.getSelectedText() != null) {
                        target.replaceSelection(content);
                    } else {
                        try {
                            doc.remove(caret, 1);
                            if (content != null) {
                                doc.insertString(caret, content, null);
                            }
                        } catch (BadLocationException ex) {
                            beep();
                        }
                    }
                }
            }
        }
        
    }
    
    protected static class FindWithinLineAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target) && (content != null)){
                Caret caret = target.getCaret();
                int dot = caret.getDot() + 1;
                try {
                    Document doc = target.getDocument();
                    if (dot < doc.getLength()) {
                        int end = Utilities.getRowEnd(target, dot);
                        if (dot < end) {
                            String text = doc.getText(dot, end - dot);
                            int offset = 0;
                            if ((offset = text.indexOf(content)) != -1) {
                                caret.setDot(dot + offset);
                            }
                        }
                    }else {
                        beep();
                    }
                } catch (BadLocationException ex) {
                    beep();
                    ex.printStackTrace();
                }
            }
        }
        
    }
    
    protected static class FindReverseWithinLineAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target) && (content != null)) {
                Caret caret = target.getCaret();
                int dot = caret.getDot();
                try {
                    if (0 < dot) {
                        int begin = Utilities.getRowStart(target, dot);
                        if (dot > begin) {
                            Document doc = target.getDocument();
                            String text = doc.getText(begin, dot - begin);
                            int offset = 0;
                            if ((offset = text.lastIndexOf(content)) != -1) {
                                caret.setDot(begin + offset);
                            }
                        }
                    } else {
                        beep();
                    }
                } catch (BadLocationException ex) {
                    beep();
                    ex.printStackTrace();
                }
            }
        }
        
    }
    
    
    protected static class GotoLineAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target) && (content != null)) {
                BaseDocument doc = org.netbeans.editor.Utilities.getDocument(target);
                int numeric = org.netbeans.editor.Utilities.getRowCount(doc);
                try {
                    int goLine = Integer.parseInt(content);
                    if (goLine > numeric) {
                        goLine = numeric;
                    }
                    int start = org.netbeans.editor.Utilities.getRowStartFromLineOffset(doc, goLine - 1);
                    if (start != -1) {
                        target.setCaretPosition(start);
                    } else {
                        target.getToolkit().beep();
                    }
                } catch (NumberFormatException ee) {
                    target.getToolkit().beep();
                }
            }
        }
        
    }
    
//\uFFFD\u0F2D\uFFFD\uFFFD\uFFFD\u06BF\uFFFD\uFFFD\u04FB\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD
    protected static class DeleteSelectionCharAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            deleteSelectedText(target);
        }
        
    }
    
//"/"\uFFFD\uFFFD\uFFFD\uFFFD
    protected static class SearchStringAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            final JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                final String data = content;
                if (SwingUtilities.isEventDispatchThread()) {
                    search(target, data);
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            search(target, data);
                        }
                    });
                }
            }
        }
        
        private void search(final JTextComponent target, final String data) {
            Document doc = target.getDocument();
            String key = null;
            boolean enter = false;
            if (CommandUtils.isEndWithEnter(data)) {
                key = data.substring(0, data.length() - 1);
                enter = true;
            } else {
                key = data;
            }
            if (doc != null) {
                int docLength = doc.getLength();
                //try {
                Caret caret = target.getCaret();
                int dot = caret.getDot();
                int mark = caret.getMark();
                int min = Math.min(dot, mark);
                int max = Math.max(dot, mark);
                
                SearchEngine engine = SearchEngine.getEngine();
                engine.setSource(target);
                engine.tracking(key, min);
                if (engine.isFound()) {
                    target.moveCaretPosition(engine.getEnd());
                } else {
                    cancel(target);
                }
                if (enter) {
                    cancel(target);
                    HighlightSearch light = HighlightSearch.getDefault();
                    //light.setHighlight(true);
                    light.setRegExp(key);
                    light.displayHighlight(target);
                    HistorySearch hs = getRespository().getEditorState(target).getHistorySearch();
                    if (light.isMatch())
                        hs.setHistoryCommand(key, engine.getEnd());
                    else
                        hs.cleanHistoryCommand();
                }
            }
        }
        
        private void cancel(JTextComponent target) {
            if (target.getSelectedText() != null) {
                Caret caret = target.getCaret();
                int min = Math.min(caret.getDot(), caret.getMark());
                caret.setDot(min);
            }
        }
        
    }
    
    protected static class SearchWordAtCaretAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int caret = target.getCaretPosition();
                try {
                    int ws = org.netbeans.editor.Utilities.getWordStart(target, caret);
                    int we = org.netbeans.editor.Utilities.getWordEnd(target, caret);
                    String word = target.getText(ws, we - ws);
                    if (word.length() == 0)
                        return ;
                    SearchEngine engine = SearchEngine.getEngine();
                    engine.setSource(target);
                    engine.tracking(word, we);
                    if (engine.isFound()) {
                        //HistorySearch.getInstance().setHistoryCommand(word, engine.getEnd());
                        getRespository().getEditorState(target).getHistorySearch().setHistoryCommand(word, engine.getEnd());
                        HighlightSearch highlight = HighlightSearch.getDefault();
                        highlight.setRegExp(word);
                        highlight.displayHighlight(target);
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                
            }
        }
        
    }
    
    protected static class CopyToClipboardAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                if (isVisualBlockMode(target)) {
                    removeHighlights(target);
                    HighlightLineQueue lineQueue = getRespository().getEditorState(target).getHighlightLineQueue();
                    if (lineQueue.getQueue().isEmpty())
                        return ;
                    lineQueue.setValid(true);
                } else {
                    String rtext = target.getSelectedText();
                    copyContentToClipboard(rtext);
                }
                target.setCaretPosition(target.getCaretPosition());
                switchMode(Mode.COMMAND_MODE, target);
            }
        }
        
    }
    
    protected static class PasteFromClipboardAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                String rtext = getContentFromClipboard();
                if (rtext != null) {
                    int caret = target.getCaretPosition();
                    Document doc = target.getDocument();
                    try {
                        int endp = getRowEnd(target, caret);
                        int newp = 0;
                        if ('\n' == rtext.charAt(0)) {
                            doc.insertString(endp, rtext, null);
                            //target.setCaretPosition(endp + 1);
                            newp = endp + 1;
                        } else {
                            if (caret == endp) {
                                newp = caret + rtext.length() - 1;
                                doc.insertString(caret, rtext, null);
                            } else {
                                doc.insertString(caret + 1, rtext, null);
                                newp = caret + rtext.length();
                            }
                        }
                        while (Character.isWhitespace(doc.getText(newp, 1).charAt(0))) {
                            newp++;
                        }
                        target.setCaretPosition(newp);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        
    }
    
    protected static class PasteUpFromClipboardAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                String rtext = getContentFromClipboard();
                int p = target.getCaretPosition();
                if ('\n' == rtext.charAt(0)) {
                    rtext = rtext.substring(1);
                }
                Document doc = target.getDocument();
                if (doc instanceof BaseDocument) {
                    BaseDocument doc2 = (BaseDocument)doc;
                    try {
                        doc2.atomicLock();
                        int begin = getRowStart(target, p);
                        int newp = 0;
                        doc2.insertString(begin, rtext, null);
                        newp = begin + rtext.length();
                        if (rtext.charAt(rtext.length() - 1) != '\n')
                            doc2.insertString(newp, "\n", null);
                    } catch (BadLocationException ex) {
                        //
                    } finally {
                        doc2.atomicUnlock();
                    }
                }
            }
        }
        
    }
    
    protected static class PasteSelectedRectangleTextAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                //List<HighlightLine> queue = HighlightLineQueue.getInstance().getQueue();
                HighlightLineQueue lineQueue = getRespository().getEditorState(target).getHighlightLineQueue();
                if (!lineQueue.isValid())
                    return ;
                List<HighlightLine> queue = lineQueue.getQueue();
                if (queue.isEmpty())
                    return ;
                if (target.getDocument() instanceof BaseDocument) {
                    BaseDocument doc = (BaseDocument) target.getDocument();
                    try {
                        doc.atomicLock();
                        int qsize = queue.size();
                        int cp = target.getCaretPosition();
                        int ls = Utilities.getRowStart(target, cp); //for used to padding backspaces
                        int length = cp - ls; //the lengths that it's from line start to current caret position.
                        int tempLength = 0;
                        Rectangle r = target.modelToView(cp);
                        Rectangle temp = null;
                        int icaret0 = cp;
                        int icaret1 = 0;
                        boolean isLast = false;
                        List<HighlightLine> lines = new ArrayList<HighlightLine>(queue);
                        Collections.sort(lines);
                        int tempc = 0;
                        boolean once = false;
                        for (HighlightLine line : lines) {
                            //
                            if (temp == null)
                                doc.insertString(icaret0, line.getText(), null);
                            else if ((temp.y == r.y) || isLast) {
                                isLast = true;
                                int docLength = doc.getLength();
                                if (once) {
                                    doc.insertString(docLength, "\n", null);
                                    docLength += 1;
                                    once = false;
                                }
                                doc.insertString(docLength, paddingBackspaces(length) + line.getText() + "\n", null);
                            } else if (temp.x != r.x) {//maybe be two,one temp and r at different column, the other one is chars size is different in different lines.
                                tempLength = length - getLengths(target, icaret0);
                                if (tempLength > 0) {
                                    icaret1 = Utilities.getRowEnd(target, icaret0);
                                    doc.insertString(icaret1, paddingBackspaces(tempLength) + line.getText(), null);
                                } else
                                    doc.insertString(icaret0, line.getText(), null);
                            } else {
                                doc.insertString(icaret0, line.getText(), null);
                            }
                            //
                            if (!isLast) {
                                tempc = icaret0;
                                icaret0 = Utilities.getPositionBelow(target, icaret0, target.modelToView(cp).x); //always similar for x-coodinator
                                temp = target.modelToView(icaret0);
                                if (tempc == icaret0) {
                                    isLast = true;
                                    once = true;
                                }
                            }
                        }
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    } finally {
                        doc.atomicUnlock();
                    }
                }
            }
        }
        
        private String paddingBackspaces(int counts) {
            StringBuilder sb = new StringBuilder();
            while (counts-- > 0)
                sb.append(" ");// has bugs for chars size is differents
            return sb.toString();
        }
        
        private int getLengths(JTextComponent target, int tempCaret) {
            try {
                return (Utilities.getRowEnd(target, tempCaret) - Utilities.getRowStart(target, tempCaret));
            } catch (BadLocationException ex) {
                //ex.printStackTrace();
                return -1;
            }
        }
    }
    
    protected static class VisualBlockAction implements CommandAction {
        
        public void execute(ActionEvent e, String content) {
            //logger.info("visual block action executed");
            JTextComponent target = getTextComponent(e);
            if (target == null)
                return ;
            //CommandMode mode = CommandModeOperator.getOperator().get(target);
            CommandMode mode = getRespository().getEditorState(target).getCommandMode();
            if (mode == null)
                throw new EVIException(VIEXBundle.getMessage("MODE_ERROR"));
            mode.switchMode(Mode.VISUAL_BLOCK_MODE, target);
            if (target.getCaret().isSelectionVisible()) //cancel caret selected
                target.setCaretPosition(target.getCaretPosition());
            rposition = target.getCaretPosition();
        }
        
    }
    
    private static EVIHighlightPainter hpainter = new EVIHighlightPainter(null);//Highlight color is c.getSelectionColor.
    //private static VIEXHighlightPainter viexPainter = new VIEXHighlightPainter(Color.RED);
    private static int rposition = 0; //used to saved caret position at switch to visual block mode.
    private static void rectangleAction(JTextComponent target) {
        //HighlightLineQueue queue = HighlightLineQueue.getInstance();
        HighlightLineQueue queue = getRespository().getEditorState(target).getHighlightLineQueue();
        if (!queue.getQueue().isEmpty())
            queue.getQueue().clear();
        int cposition = target.getCaretPosition(); //used to saved caret position that is current mode.
        
        Document doc = target.getDocument();
        try {
            Rectangle r0 = target.modelToView(rposition);
            Rectangle r1 = target.modelToView(cposition);
            Rectangle temp = new Rectangle();
            int lfirst = 0;
            int llast = 0;
            if (r0.x > r1.x) {
                temp.x = r1.x;
                lfirst = r1.x;
                llast = r0.x;
            } else {
                temp.x = r1.x;
                lfirst = r0.x;
                llast = r1.x;
            }
            temp.y = r0.y;
            
            Point p0 = new Point(temp.x, temp.y);
            Point p1 = new Point(r0.x, r0.y);
            
            int m0 = target.viewToModel(p0);
            int m1 = target.viewToModel(p1);
            int max = Math.max(m0, m1);
            int min = Math.min(m0, m1);
            String text = doc.getText(min, max - min);
            //System.out.println("===============Start================="); //debug
            //Position hp0 = NbDocument.createPosition(doc, min, Position.Bias.Forward);
            //Position hp1 = NbDocument.createPosition(doc, max, Position.Bias.Forward);
            
            removeHighlights(target);
            Highlighter h = target.getHighlighter();
            if (text.length() != 0)
                //if (b)
                h.addHighlight(min, max - 1, hpainter);
            //else
            //    h.addHighlight(min, max, hpainter);
            queue.addHighlighterLine(new HighlightLine(min, max, text));
            //end got first line texts.
            
            //if twice carets position is in different lines, start gets more lines.
            if (r0.y != r1.y)  {
                int ncaret = 0;
                m0 = rposition;
                p0.x = lfirst;
                p1.x = llast;
                while (true) {
                    if (rposition > cposition) {
                        ncaret = Utilities.getPositionAbove(target, m0, target.modelToView(m0).x);
                    } else {
                        ncaret = Utilities.getPositionBelow(target, m0, target.modelToView(m0).x);
                    }
                    if (-1 == ncaret)
                        break ;
                    p1.y = p0.y = target.modelToView(ncaret).y;
                    m0 = target.viewToModel(p0);
                    m1 = target.viewToModel(p1);
                    max = Math.max(m0, m1);
                    min = Math.min(m0, m1);
                    text = doc.getText(min, max - min);
                    //System.out.println(text); //debug
                    queue.addHighlighterLine(new HighlightLine(min, max, text));
                    
                    int nc = target.modelToView(ncaret).x;
                    
                    //if got ncaret's view position not be in between lfirst and llast, then not added highlight.
                    //if (nc < (llast + 1) && nc > (lfirst -1))
                    //System.out.println("-------------Text length------" + text.length()); //debug
                    if (text.length() != 0) //if no selected's texts then as no added highlight.
                        //if (b)
                        h.addHighlight(min, max - 1, hpainter);
                    //else
                    //    h.addHighlight(min, max, hpainter);
                    if (target.modelToView(ncaret).y == target.modelToView(cposition).y)
                        break ;
                }
                queue.setDeleted(false);
                queue.setValid(false);
                //System.out.println(queue.toString());
                //System.out.println("====================End===================");
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
//\uFFFD\uFFFD
    public static class ToogleTypingModeAction extends AbstractAction {
        
        public void actionPerformed(ActionEvent e) {
            //logger.info("ToogleTypingModeAction is executed");
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                EditorUI editorUI = org.netbeans.editor.Utilities.getEditorUI(target);
                Boolean overwriteMode = (Boolean)editorUI.getProperty(EditorUI.OVERWRITE_MODE_PROPERTY);
                // Now toggle
                overwriteMode = (overwriteMode == null || !overwriteMode.booleanValue()) ? Boolean.TRUE : Boolean.FALSE;
                editorUI.putProperty(EditorUI.OVERWRITE_MODE_PROPERTY, overwriteMode);
            }
        }
        
    }

    private static VIEXQuickListScrollPane quickListPane = new VIEXQuickListScrollPane();
    
}