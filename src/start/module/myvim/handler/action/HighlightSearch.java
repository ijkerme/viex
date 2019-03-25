package start.module.myvim.handler.action;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import start.module.myvim.highlight.HighlightState;
import start.module.myvim.highlight.VIEXHighlightPainter;
import static start.module.myvim.utilities.ComponentUtils.*;
import start.module.myvim.utilities.VIEXBundle;
import start.module.myvim.utilities.VIEXOptions;

/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */



/**
 * The Regular Expression Highlighter.
 *
 * Sandip V. Chitale (Sandip.Chitale@Sun.Com) RegHighlighter based
 */
public final class HighlightSearch {
    private static HighlightSearch INSTANCE = new HighlightSearch();
    
    public static HighlightSearch getDefault() {
        return INSTANCE;
    }
    
    private Color[] colors = new Color[] {
        new Color(202, 255, 112),
        new Color(193, 255, 193),
        new Color(224, 255, 255),
        new Color(255, 193, 193),
        new Color(255, 187, 255),
        new Color(255, 222, 255),
        new Color(255, 239, 213),
    };
    
    /**
     * Holds value of property regExp.
     */
    private String regExp = "";
    
    private boolean match;
    /**
     * Holds value of property matchCase.
     */
    //private boolean matchCase = false;
    
    /**
     * Holds value of property highlight.
     */
    private boolean highlight = true;
    
    /**
     * Holds value of property highlightGroups.
     */
    private boolean highlightGroups;
    
    private Map<JTextComponent, FileObject> comp2FO;
    private Map<FileObject, Collection<JTextComponent>> fo2Comp;
    private Map/*<JTextComponent, HighlightLayer>*/ comp2Highlights;
    
    public HighlightSearch() {
        comp2FO = new WeakHashMap<JTextComponent, FileObject>();
        fo2Comp = new WeakHashMap<FileObject, Collection<JTextComponent>>();
        comp2Highlights = new WeakHashMap/*<JTextComponent, HighlightLayer>*/();
    }
    
    /**
     * Getter for property regExp.
     * @return Value of property regExp.
     */
    public String getRegExp() {
        return this.regExp;
    }
    
    /**
     * Setter for property regExp.
     */
    public void setRegExp(String regExp) {
        this.regExp = regExp;
    }
    
    /**
     * Getter for property matchCase.
     * @return Value of property matchCase.
     */
    public boolean isIgnoreCase() {
        return VIEXOptions.getBoolean(VIEXOptions.GENERAL, VIEXBundle.getMessage("OptionsIgnoreCase"));
    }

    public boolean isMatch() {
        return match;
    }
    
    /**
     * Setter for property matchCase.
     * @param matchCase New value of property matchCase.
     */
    //public void setMatchCase(boolean matchCase) {
      //  this.matchCase = matchCase;
       // updateHighlightRegExp();
    //}
    
    /**
     * Getter for property highlight.
     * @return Value of property highlight.
     */
    public boolean isHighlight() {
        return this.highlight;
    }
    
    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }
    
    /**
     * Getter for property highlightGroups.
     * @return Value of property highlightGroups.
     */
    public boolean isHighlightGroups() {
        return this.highlightGroups;
    }
    
    /**
     * Setter for property highlightGroups.
     * @param highlightGroups New value of property highlightGroups.
     */
    public void setHighlightGroups(boolean highlightGroups) {
        this.highlightGroups = highlightGroups;
        if (highlight) {
            updateHighlightRegExp();
        }
    }
    
    public void displayHighlight(JTextComponent target) {
        assureRegistered(target);
        if ((regExp == null) || (regExp.length() == 0)) {
            clearAllHighlights();
        } else {
            updateHighlightRegExp();
        }
    }
    
    Pattern compileRegExp(String regExp) throws PatternSyntaxException {
        if (isIgnoreCase()) {
            return Pattern.compile(regExp, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        } else {
            return Pattern.compile(regExp, Pattern.MULTILINE);
        }
    }
    
    private synchronized void assureRegistered(JTextComponent c) {
        if (c == null || comp2Highlights.get(c) != null)
            return ;
        
        comp2FO.put(c, null);
        //-c.addPropertyChangeListener(this);
        updateFileObjectMapping(c);
        comp2Highlights.put(c, new ArrayList());
    }
    
    private synchronized void updateFileObjectMapping(JTextComponent c) {
        Document doc = c.getDocument();
        Object   stream = doc.getProperty(Document.StreamDescriptionProperty);
        
        FileObject old = (FileObject) comp2FO.put(c, null);
        
        if (old != null) {
            Collection/*<JTextComponent>*/ components = (Collection) fo2Comp.get(old);
            
            if (components != null) {
                components.remove(old);
            }
        }
        
        if (stream != null && stream instanceof DataObject) {
            FileObject fo = ((DataObject) stream).getPrimaryFile();
            
            comp2FO.put(c, fo);
            getComponents(fo).add(c);
        }
    }
    
    private Collection/*<JTextComponent>*/ getComponents(FileObject fo) {
        Collection/*<JTextComponent>*/ components = (Collection) fo2Comp.get(fo);
        
        if (components == null) {
            fo2Comp.put(fo, components = new ArrayList/*<JTextComponent>*/());
        }
        
        return components;
    }
    
    private void clearAllHighlights() {
        for (Iterator it = fo2Comp.keySet().iterator(); it.hasNext();) {
            FileObject fileObject = (FileObject) it.next();
            clearHighlights(fileObject);
        }
    }
    
    private void clearHighlights(FileObject fo) {
        clearHighlights(fo, true);
    }
    
    private void updateHighlightRegExp() {
        JTextComponent textComponent = getMostActiveComponent();
        if (textComponent == null) {
            return;
        }
        
        if (!(textComponent.getDocument() instanceof BaseDocument)) {
            return;
        }
        
        if (textComponent.getDocument().getLength() == 0) {
            return;
        }
        
        FileObject fileObject = NbEditorUtilities.getFileObject(textComponent.getDocument());
        if (fileObject == null) {
            return;
        }
        
        String regExp = getRegExp();
        if (regExp == null || regExp.length() == 0) {
            clearHighlights(fileObject);
        } else {
            if (isHighlight()) {
                try {
                    // is it a valid regexp?
                    Pattern compiledRegExp = compileRegExp(regExp);
                    
                    Document document = textComponent.getDocument();
                    int length = document.getLength();
                    String text = textComponent.getDocument().getText(0, length);
                    Matcher matcher = compiledRegExp.matcher(text);
                    
                    clearHighlights(fileObject, false);
                    List<HighlightState> regExpMatches = new ArrayList<HighlightState>();
                    while (matcher.find()) {
                        for (int i = 0 ; i < Math.min(colors.length, matcher.groupCount() + 1); i++) {
                            int start = matcher.start(i);
                            int end = matcher.end(i);
                            if (start >= end) {
                                continue;
                            }
                            Position startPosition = NbDocument.createPosition(document, start,  Position.Bias.Forward);
                            Position endPosition = NbDocument.createPosition(document,   end,    Position.Bias.Forward);
                            regExpMatches.add(new HighlightState(colors[i], startPosition, endPosition));
                            if (!highlightGroups) {
                                // bail out after first iteration
                                break;
                            }
                        }
                    }
                    if (regExpMatches.isEmpty())
                        match = false;
                    else {
                        setHighlights(fileObject, regExpMatches);
                        match = true;
                    }
                } catch (PatternSyntaxException pse) {
                    clearHighlights(fileObject);
                    return;
                } catch (BadLocationException ble) {
                    clearHighlights(fileObject);
                    return;
                }
            } else {
                clearHighlights(fileObject);
            }
        }
    }
    
    private void setHighlights(FileObject fo, Collection/*<Highlight>*/ highlights) {
        for (Iterator i = getComponents(fo).iterator(); i.hasNext(); ) {
            JTextComponent c = (JTextComponent) i.next();
            
            //
            Highlighter highlighter = c.getHighlighter();
            
            // Clear existing highlights
            clearHighlights(fo, false);
            
            // Add new highlights
            List compHighlights = (List) comp2Highlights.get(c);
            for (Iterator it = highlights.iterator(); it.hasNext();) {
                HighlightState regExpHighlight = (HighlightState) it.next();
                try {
                    Object tag = highlighter.addHighlight(regExpHighlight.getStart(),
                            regExpHighlight.getEnd() - 1, new VIEXHighlightPainter(regExpHighlight.getColor()));
                    compHighlights.add(tag);
                } catch (BadLocationException ble) {
                    
                }
            }
            c.repaint();
        }
    }
    
    private void clearHighlights(FileObject fo, boolean repaint) {
        for (Iterator i = getComponents(fo).iterator(); i.hasNext(); ) {
            JTextComponent c = (JTextComponent) i.next();
            Highlighter highlighter = c.getHighlighter();
            
            // Clear existing highlights
            List compHighlights = (List) comp2Highlights.get(c);
            for (Iterator it = compHighlights.iterator(); it.hasNext();) {
                Object tag = (Object) it.next();
                highlighter.removeHighlight(tag);
            }
            compHighlights.clear();
            c.repaint();
        }
    }


}

