package start.module.myvim.highlight;

import java.awt.Point;

/**
 *
 * @author jker
 */
public class HighlightLine implements Comparable<HighlightLine> {
    
    //saved start and end position caret in selected line.
    private int start;
    private int end;
    //saved content of seleted line
    private String text;
    
    public HighlightLine() {
        
    }

    public HighlightLine(int start, int end, String text) {
        this.start = start;
        this.end = end;
        this.text = text;
    }

    public void setHighlightLine(int start, int end, String text) {
        this.start = start;
        this.text = text;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return ("HighlightLine Start:" + start + "  End:" + end + " Text:" + text);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof HighlightLine) {
            HighlightLine other = (HighlightLine)obj;
            return ((start == other.getStart()) && (end == other.getEnd()) && (text.equals(other.getText())));
        }
        return false;
    }    

    public int compareTo(HighlightLine o) {
        return start - ((HighlightLine)o).getStart();
    }

}
