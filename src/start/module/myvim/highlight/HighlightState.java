package start.module.myvim.highlight;

import java.awt.Color;
import javax.swing.text.Position;

/**
 *
 * @author jker
 */
public class HighlightState {
    
    private Color color;
    private Position start;
    private Position end;
    
    /** Creates a new instance of DefaultHighlight */
    public HighlightState(Color color, Position start, Position end) {
        this.color = color;
        this.start = start;
        this.end = end;
    }
    
    public int getStart() {
        return start.getOffset();
    }
    
    public int getEnd() {
        return end.getOffset();
    }
    
    public Color getColor() {
        return color;
    }
}