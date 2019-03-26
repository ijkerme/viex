package start.module.myvim.handler.action;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.document.LineDocumentUtils;
import org.netbeans.api.editor.fold.Fold;
import org.netbeans.api.editor.fold.FoldHierarchy;
import org.netbeans.api.editor.fold.FoldUtilities;
import org.netbeans.editor.*;
import org.netbeans.lib.editor.util.CharSequenceUtilities;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import start.module.myvim.state.EditorState;
import start.module.myvim.utilities.ComponentUtils;
import static start.module.myvim.state.VIEXRepository.*;
import static start.module.myvim.utilities.ComponentUtils.*;
import static start.module.myvim.handler.action.EVIActionFactory.*;

/**
 *
 * @author jker
 */
public class NBActionFacade {
    
    private static final Logger logger = Logger.getLogger("EVIKeyAction");
    
    private Action pageUp = new HalfPageAction("HALF_PAGE_UP", true);
    private Action pageDown = new HalfPageAction("HALF_PAGE_DOWN", false);
    
    public static final String toUpperCaseAction = "to-upper-case";
    public static final String toLowerCaseAction = "to-lower-case";
    public static final String switchCaseAction = "switch-case";
    public static final String shiftLineLeftAction = "shift-line-left";
    public static final String shiftLineRightAction = "shift-line-right";
    
    public static final int CASE_UPPER = 0;
    public static final int CASE_LOWER = 1;
    public static final int CASE_SWITCH = 2;
    
    public static Action getActionByName(String name) {
        return null;
    }
    
    //
    public static class NopAction extends AbstractAction {
        
        public void actionPerformed(ActionEvent e) {
            //logger.info("Nop"); //Nop operation
        }
        
    }
    
    //Backspace keybinding
    public static class BackspaceAction extends AbstractAction {
        
        public void actionPerformed(ActionEvent e) {
            EditorState estate = getRespository().getEditorState((JTextComponent)e.getSource());
            if (estate == null)
                return ;
            String s = estate.getBufferedData().removeTailChar();
            if (s != null) {
                ComponentUtils.updateStatusBarInfo(s);
            }
        }
        
    }
    
    public static class HalfPageAction extends AbstractAction {
        
        // ture up, false down
        private boolean direct;
        
        public HalfPageAction(String name, boolean direct) {
            super(name);
            this.direct = direct;
        }
        
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if ((target != null) && target.isEnabled()) {
                BaseKit kit = Utilities.getKit(target);
                if (null == kit)
                    return ;
                if (direct) {
                    Action a = kit.getActionByName(BaseKit.pageUpAction);
                    if (null == a)
                        return ;
                    a.actionPerformed(e);
                } else {
                    Action a = kit.getActionByName(BaseKit.pageDownAction);
                    if (null == a)
                        return ;
                    a.actionPerformed(e);
                }
            }
        }
        
    }
    
    public static class PageUpAction extends AbstractAction {
        
        public PageUpAction(String name) {
            super(name);
        }
        
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                try {
                    Caret caret = target.getCaret();
                    BaseDocument doc = (BaseDocument)target.getDocument();
                    int caretOffset = caret.getDot();
                    Rectangle caretBounds = ((BaseTextUI)target.getUI()).modelToView(target, caretOffset);
                    if (caretBounds == null) {
                        return; // Cannot continue reasonably
                    }
                    
                    // Retrieve caret magic position and attempt to retain
                    // the x-coordinate information and use it
                    // for setting of the new caret position
                    Point magicCaretPosition = caret.getMagicCaretPosition();
                    if (magicCaretPosition == null) {
                        magicCaretPosition = new Point(caretBounds.x, caretBounds.y);
                    }
                    
                    Rectangle visibleBounds = target.getVisibleRect();
                    int newCaretOffset;
                    Rectangle newCaretBounds;
                    
                    // Check whether caret was contained in the original visible window
                    if (visibleBounds.contains(caretBounds)) {
                        // Clone present view bounds
                        Rectangle newVisibleBounds = new Rectangle(visibleBounds);
                        // Do viewToModel() and modelToView() with the left top corner
                        // of the currently visible view. If that line is not fully visible
                        // then it should be the bottom line of the previous page
                        // (if it's fully visible then the line above it).
                        int topLeftOffset = target.viewToModel(new Point(
                                visibleBounds.x, visibleBounds.y));
                        Rectangle topLeftLineBounds = target.modelToView(topLeftOffset);
                        
                        // newVisibleBounds.y will hold bottom of new view
                        if (topLeftLineBounds.y != visibleBounds.y) {
                            newVisibleBounds.y = topLeftLineBounds.y + topLeftLineBounds.height;
                        } // Component view starts right at the line boundary
                        // Go back by the view height
                        newVisibleBounds.y -= visibleBounds.height;
                        
                        // Find the new caret bounds by using relative y position
                        // on the original caret bounds. If the caret's new relative bounds
                        // would be visually above the old bounds
                        // the view should be shifted so that the relative bounds
                        // are the same (user's eyes do not need to move).
                        int caretRelY = caretBounds.y - visibleBounds.y;
                        int caretNewY = newVisibleBounds.y + caretRelY;
                        newCaretOffset = target.viewToModel(new Point(magicCaretPosition.x, caretNewY));
                        newCaretBounds = target.modelToView(newCaretOffset);
                        if (newCaretBounds.y < caretNewY) {
                            // Need to go one line down to retain the top line
                            // of the present newVisibleBounds to be fully visible.
                            // Attempt to go forward by height of caret
                            newCaretOffset = target.viewToModel(new Point(magicCaretPosition.x,
                                    newCaretBounds.y + newCaretBounds.height));
                            newCaretBounds = target.modelToView(newCaretOffset);
                        }
                        
                        // Shift the new visible bounds so that the caret
                        // does not visually move
                        newVisibleBounds.y = newCaretBounds.y - caretRelY;
                        
                        // Scroll the window to the requested rectangle
                        target.scrollRectToVisible(newVisibleBounds);
                        
                    } else { // Caret outside of originally visible window
                        // Shift the dot by the visible bounds height
                        Point newCaretPoint = new Point(magicCaretPosition.x,
                                caretBounds.y - visibleBounds.height);
                        newCaretOffset = target.viewToModel(newCaretPoint);
                        newCaretBounds = target.modelToView(newCaretOffset);
                    }
                    
                    //if (select) {
                    //  caret.moveDot(newCaretOffset);
                    //} else {
                    caret.setDot(newCaretOffset);
                    //}
                    
                    // Update magic caret position
                    magicCaretPosition.y = newCaretBounds.y;
                    caret.setMagicCaretPosition(magicCaretPosition);
                    
                } catch (BadLocationException ex) {
                    target.getToolkit().beep();
                }
            }
        }
        
    }
    
    public static class PageDownAction extends AbstractAction {
        
        public PageDownAction(String name) {
            super(name);
        }
        
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                try {
                    Caret caret = target.getCaret();
                    BaseDocument doc = (BaseDocument)target.getDocument();
                    int caretOffset = caret.getDot();
                    Rectangle caretBounds = ((BaseTextUI)target.getUI()).modelToView(target, caretOffset);
                    if (caretBounds == null) {
                        return; // Cannot continue reasonably
                    }
                    
                    // Retrieve caret magic position and attempt to retain
                    // the x-coordinate information and use it
                    // for setting of the new caret position
                    Point magicCaretPosition = caret.getMagicCaretPosition();
                    if (magicCaretPosition == null) {
                        magicCaretPosition = new Point(caretBounds.x, caretBounds.y);
                    }
                    
                    Rectangle visibleBounds = target.getVisibleRect();
                    int newCaretOffset;
                    Rectangle newCaretBounds;
                    
                    // Check whether caret was contained in the original visible window
                    if (visibleBounds.contains(caretBounds)) {
                        // Clone present view bounds
                        Rectangle newVisibleBounds = new Rectangle(visibleBounds);
                        // Do viewToModel() and modelToView() with the left bottom corner
                        // of the currently visible view.
                        // That line should be the top line of the next page.
                        int bottomLeftOffset = target.viewToModel(new Point(
                                visibleBounds.x, visibleBounds.y + visibleBounds.height));
                        Rectangle bottomLeftLineBounds = target.modelToView(bottomLeftOffset);
                        
                        // newVisibleBounds.y will hold bottom of new view
                        newVisibleBounds.y = bottomLeftLineBounds.y;
                        
                        // Find the new caret bounds by using relative y position
                        // on the original caret bounds. If the caret's new relative bounds
                        // would be visually below the old bounds
                        // the view should be shifted so that the relative bounds
                        // are the same (user's eyes do not need to move).
                        int caretRelY = caretBounds.y - visibleBounds.y;
                        int caretNewY = newVisibleBounds.y + caretRelY;
                        newCaretOffset = target.viewToModel(new Point(magicCaretPosition.x, caretNewY));
                        newCaretBounds = target.modelToView(newCaretOffset);
                        if (newCaretBounds.y > caretNewY) {
                            // Need to go one line above to retain the top line
                            // of the present newVisibleBounds to be fully visible.
                            // Attempt to go up by height of caret.
                            newCaretOffset = target.viewToModel(new Point(magicCaretPosition.x,
                                    newCaretBounds.y - newCaretBounds.height));
                            newCaretBounds = target.modelToView(newCaretOffset);
                        }
                        
                        // Shift the new visible bounds so that the caret
                        // does not visually move
                        newVisibleBounds.y = newCaretBounds.y - caretRelY;
                        
                        // Scroll the window to the requested rectangle
                        target.scrollRectToVisible(newVisibleBounds);
                        
                    } else { // Caret outside of originally visible window
                        // Shift the dot by the visible bounds height
                        Point newCaretPoint = new Point(magicCaretPosition.x,
                                caretBounds.y + visibleBounds.height);
                        newCaretOffset = target.viewToModel(newCaretPoint);
                        newCaretBounds = target.modelToView(newCaretOffset);
                    }
                    
                    //if (select) {
                    //  caret.moveDot(newCaretOffset);
                    //} else {
                    caret.setDot(newCaretOffset);
                    //}
                    
                    // Update magic caret position
                    magicCaretPosition.y = newCaretBounds.y;
                    caret.setMagicCaretPosition(magicCaretPosition);
                    
                } catch (BadLocationException ex) {
                    target.getToolkit().beep();
                }
            }
            
        }
        
    }
    
    public static class ChangeCaseAction extends AbstractAction {
        
        private int changeCaseMode;
        private boolean isLine;
        
        public ChangeCaseAction(String name, int changeCaseMode, boolean isLine) {
            this.isLine = isLine;
            this.changeCaseMode = changeCaseMode;
        }
        
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                Caret caret = target.getCaret();
                if (isLine) {
                    int p = caret.getDot();
                    try {
                        int sp = javax.swing.text.Utilities.getRowStart(target, p);
                        int ep = javax.swing.text.Utilities.getRowEnd(target, p);
                        caret.setDot(sp);
                        caret.moveDot(ep);
                    } catch (BadLocationException ex) {
                    }
                    
                }
                try {
                    BaseDocument doc = (BaseDocument)target.getDocument();
                    if (Utilities.isSelectionShowing(caret)) {
                        int startPos = target.getSelectionStart();
                        int endPos = target.getSelectionEnd();
                        Utilities.changeCase(doc, startPos, endPos - startPos, changeCaseMode);
                        caret.setDot(endPos);
                    } else {
                        int dotPos = caret.getDot();
                        Utilities.changeCase(doc, dotPos, 1, changeCaseMode);
                        caret.setDot(dotPos + 1);
                    }
                } catch (BadLocationException ex) {
                    target.getToolkit().beep();
                }
            }
        }
        
    }

    public static class ShiftLineAction extends AbstractAction {
        
        //private Action a;
        private boolean right;
        
        public ShiftLineAction(String name, boolean right) {
            this.right = right;
            if (right) {
                putValue(Action.NAME, BaseKit.shiftLineRightAction);
            } else {
                putValue(Action.NAME, BaseKit.shiftLineLeftAction);
            }
            //a = new org.netbeans.editor.ActionFactory.ShiftLineAction(name, right);
        }

        public void actionPerformed(ActionEvent e) {
            final JTextComponent target = getTextComponent(e);
            if (isTargetEditable(target)) {
                final Caret caret = target.getCaret();
                final BaseDocument doc = Utilities.getDocument(target);
                doc.runAtomicAsUser (new Runnable () {
                    public void run () {
                        DocumentUtilities.setTypingModification(doc, true);
                        try {
                            boolean right = BaseKit.shiftLineRightAction.equals(getValue(Action.NAME));
                            if (Utilities.isSelectionShowing(caret)) {
                                BaseKitBridge.shiftBlock(
                                    doc,
                                    target.getSelectionStart(), target.getSelectionEnd(),
                                    right);
                            } else {
                                BaseKitBridge.shiftLine(doc, caret.getDot(), right);
                            }
                        } catch (GuardedException e) {
                            target.getToolkit().beep();
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        } finally {
                            DocumentUtilities.setTypingModification(doc, false);
                        }
                    }
                });
            }

        }
        
    }

    public static class MatchBraceAction implements CommandAction {

        private Action a;
        
        public MatchBraceAction() {
             // implemented for netbeans 6.0, use Editor Braces Matching instead of,
        }

        public void execute(ActionEvent e, String content) {
            // a.actionPerformed(e);
        }
        
    }

    public static class ScrollUpAction extends AbstractAction {

        public ScrollUpAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                EditorUI editorUI = Utilities.getEditorUI(target);
                Rectangle bounds = editorUI.getExtentBounds();
                bounds.y -= editorUI.getLineHeight();
                bounds.x += editorUI.getTextMargin().left;
                //editorUI.scrollRectToVisible(bounds, EditorUI.SCROLL_SMALLEST);   
                target.scrollRectToVisible(bounds);
                //Rectangle rec = target.getVisibleRect();
                scrollCaret(target, true);
            }
        }
        
    }

    public static class ScrollDownAction extends AbstractAction {

        public ScrollDownAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                EditorUI editorUI = Utilities.getEditorUI(target);
                Rectangle bounds = editorUI.getExtentBounds();
                bounds.y += editorUI.getLineHeight();
                bounds.x += editorUI.getTextMargin().left;
                //editorUI.scrollRectToVisible(bounds, EditorUI.SCROLL_SMALLEST);         
                target.scrollRectToVisible(bounds);
                scrollCaret(target, false);
            }
        }

    }

    public static class OpenAllFoldAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                openAllFold(getFoldHierarchy(target));
            }
        }
        
    }

    public static class CloseAllFoldAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                closeAllFold(getFoldHierarchy(target));
            }
        }

    }

    public static class OpenFoldAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int p = target.getCaretPosition();
                openFold(getFoldHierarchy(target), p);
            }
        }

    }

    public static class CloseFoldAction implements CommandAction {

        public void execute(ActionEvent e, String content) {
            JTextComponent target = getTextComponent(e);
            if (isTargetEnabled(target)) {
                int p = target.getCaretPosition();
                closeFold(getFoldHierarchy(target), p);
            }
        }

    }

    private static FoldHierarchy getFoldHierarchy(JTextComponent target) {
        FoldHierarchy hierarchy = FoldHierarchy.get(target);
        return hierarchy;
    }

    private static void openAllFold(FoldHierarchy hierarchy) {
        FoldUtilities.expandAll(hierarchy);
    }

    private static void closeAllFold(FoldHierarchy hierarchy) {
        FoldUtilities.collapseAll(hierarchy);
    }

    private static void openFold(FoldHierarchy hierarchy, int p) {
        hierarchy.lock();
        try {
            Fold f = FoldUtilities.findNearestFold(hierarchy, p);
            hierarchy.expand(f);
        } finally {
            hierarchy.unlock();
        }
    }

    private static void closeFold(FoldHierarchy hierarchy, int p) {
        hierarchy.lock();
        try {
            Fold f = FoldUtilities.findNearestFold(hierarchy, p);
            hierarchy.collapse(f);
        } finally {
            hierarchy.unlock();
        }
    }

    private static void scrollCaret(JTextComponent target, boolean up) {
        try {
            Rectangle rec = target.getVisibleRect();
            int p = target.getCaretPosition();
            Rectangle prec = target.modelToView(p);

            if (!rec.contains(prec)) {
                if (up) {
                    p = target.viewToModel(rec.getLocation());
                } else {
                    p = target.viewToModel(new Point(0, rec.y + rec.height));
                }
                target.setCaretPosition(p);
            }
        } catch (BadLocationException ex) {
        }
    }
    
    // Bridge to Netbeans.BaseKit
    private static class BaseKitBridge {
    /** Change the indent of the given row. Document is atomically locked
     * during this operation.
     */
    static void changeRowIndent (final BaseDocument doc, final int pos, final int newIndent) throws BadLocationException {
        final BadLocationException[] badLocationExceptions = new BadLocationException [1];
        doc.runAtomic (new Runnable () {
            public void run () {
                try {
                    int indent = newIndent < 0 ? 0 : newIndent;
                    int firstNW = LineDocumentUtils.getLineFirstNonWhitespace(doc, pos);
                    if (firstNW == -1) { // valid first non-blank
                        firstNW = LineDocumentUtils.getLineEnd(doc, pos);
                    }
                    int replacePos = LineDocumentUtils.getLineStart(doc, pos);
                    int removeLen = firstNW - replacePos;
                    CharSequence removeText = DocumentUtilities.getText(doc, replacePos, removeLen);
                    String newIndentText = IndentUtils.createIndentString(doc, indent);
                    int newIndentTextLength = newIndentText.length();
                    if (indent >= removeLen) {
                        if (CharSequenceUtilities.startsWith(newIndentText, removeText)) {
                            // Skip removeLen chars at start
                            newIndentText = newIndentText.substring(removeLen);
                            replacePos += removeLen;
                            removeLen = 0;
                        } else if (CharSequenceUtilities.endsWith(newIndentText, removeText)) {
                            // Skip removeLen chars at the end
                            newIndentText = newIndentText.substring(0, newIndentText.length() - removeLen);
                            removeLen = 0;
                        }
                    } else {
                        if (CharSequenceUtilities.startsWith(removeText, newIndentText)) {
                            // Skip newIndentText chars at start
                            replacePos += newIndentTextLength;
                            removeLen -= newIndentTextLength;
                            newIndentText = null;
                        } else if (CharSequenceUtilities.endsWith(removeText, newIndentText)) {
                            // Skip  newIndentText chars at the end
                            removeLen -= newIndentTextLength;
                            newIndentText = null;
                        }
                    }

                    if (removeLen != 0) {
                        doc.remove(replacePos, removeLen);
                    }

                    if (newIndentText != null) {
                        doc.insertString(replacePos, newIndentText, null);
                    }
                } catch (BadLocationException ex) {
                    badLocationExceptions [0] = ex;
                }
            }
        });
        if (badLocationExceptions[0] != null)
            throw badLocationExceptions [0];
    }

    /** Increase/decrease indentation of the block of the code. Document
    * is atomically locked during the operation.
    * <br/>
    * If indent is in between multiplies of shiftwidth it jumps to multiplies of shiftwidth.
    * 
    * @param doc document to operate on
    * @param startPos starting line position
    * @param endPos ending line position
    * @param shiftCnt positive/negative count of shiftwidths by which indentation
    *   should be shifted right/left
    */
    static void changeBlockIndent (final BaseDocument doc, final int startPos, final int endPos,
                                  final int shiftCnt) throws BadLocationException {
        GuardedDocument gdoc = (doc instanceof GuardedDocument)
                               ? (GuardedDocument)doc : null;
        if (gdoc != null){
            for (int i = startPos; i<endPos; i++){
                if (gdoc.isPosGuarded(i)){
                    java.awt.Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
        }

        final BadLocationException[] badLocationExceptions = new BadLocationException [1];
        doc.runAtomic (new Runnable () {
            public void run () {
                try {
                    int shiftWidth = doc.getShiftWidth();
                    if (shiftWidth <= 0) {
                        return;
                    }
                    int indentDelta = shiftCnt * shiftWidth;
                    int end = (endPos > 0 && LineDocumentUtils.getLineStart(doc, endPos) == endPos) ?
                        endPos - 1 : endPos;

                    int lineStartOffset = LineDocumentUtils.getLineStart(doc, startPos );
                    int lineCount = LineDocumentUtils.getLineCount(doc, startPos, end);
                    for (int i = lineCount - 1; i >= 0; i--) {
                        int indent = Utilities.getRowIndent(doc, lineStartOffset);
                        int newIndent = (indent == -1) ? 0 : // Zero indent if row is white
                                ((indent + indentDelta +
                                    ((shiftCnt < 0) ? shiftWidth - 1 : 0))
                                    / shiftWidth * shiftWidth);
                                
                        changeRowIndent(doc, lineStartOffset, Math.max(newIndent, 0));
                        lineStartOffset = LineDocumentUtils.getLineStartFromIndex(doc, lineStartOffset);
                    }
                } catch (BadLocationException ex) {
                    badLocationExceptions [0] = ex;
                }
            }
        });
        if (badLocationExceptions[0] != null)
            throw badLocationExceptions [0];
    }

    /** Shift line either left or right */
    static void shiftLine(BaseDocument doc, int dotPos, boolean right) throws BadLocationException {
        int ind = doc.getShiftWidth();
        if (!right) {
            ind = -ind;
        }

        if (LineDocumentUtils.isLineWhitespace(doc, dotPos)) {
            ind += Utilities.getVisualColumn(doc, dotPos);
        } else {
            ind += Utilities.getRowIndent(doc, dotPos);
        }
        ind = Math.max(ind, 0);
        changeRowIndent(doc, dotPos, ind);
    }
    
    /** Shift block either left or right */
    static void shiftBlock (final BaseDocument doc, final int startPos, final int endPos,
                                  final boolean right) throws BadLocationException {
        GuardedDocument gdoc = (doc instanceof GuardedDocument)
                               ? (GuardedDocument)doc : null;
        if (gdoc != null){
            for (int i = startPos; i<endPos; i++){
                if (gdoc.isPosGuarded(i)){
                    java.awt.Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
        }

        final BadLocationException[] badLocationExceptions = new BadLocationException [1];
        doc.runAtomic (new Runnable () {
            public void run () {
                try {
                    int shiftWidth = doc.getShiftWidth();
                    if (shiftWidth <= 0) {
                        return;
                    }
                    int indentDelta = right ? shiftWidth : -shiftWidth;
                    int end = (endPos > 0 && LineDocumentUtils.getLineStart(doc, endPos) == endPos) ?
                        endPos - 1 : endPos;

                    int lineStartOffset = LineDocumentUtils.getLineStart(doc, startPos );
                    int lineCount = LineDocumentUtils.getLineCount(doc, startPos, end);
                    for (int i = lineCount - 1; i >= 0; i--) {
                        int indent = Utilities.getRowIndent(doc, lineStartOffset);
                        int newIndent = (indent == -1) ? 0 : // Zero indent if row is white
                                indent + indentDelta;
                                
                        changeRowIndent(doc, lineStartOffset, Math.max(newIndent, 0));
                        lineStartOffset = LineDocumentUtils.getLineStartFromIndex(doc, lineStartOffset);
                    }
                } catch (BadLocationException ex) {
                    badLocationExceptions [0] = ex;
                }
            }
        });
        if (badLocationExceptions[0] != null)
            throw badLocationExceptions [0];
    }
    }
}
