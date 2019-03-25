package start.module.myvim.highlight;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author jker
 */
public class HighlightLineQueue {
    
    private List<HighlightLine> queue = new LinkedList<HighlightLine>();
    //if HighlightLines's line position is from the low high.
    private boolean isLow = true; //default from the low high
    //if queue contents already deleted, prevered more than one delete action
    private boolean deleted = false;

    //if queue contents can pasted.
    private boolean valid = false;
    
    private static HighlightLineQueue lQueue;
    
    public HighlightLineQueue(){
        
    }

    public static synchronized HighlightLineQueue getInstance() {
        if (lQueue == null) {
            lQueue = new HighlightLineQueue();
        }
        return lQueue;
    }

    public boolean addHighlighterLine(HighlightLine line) {
        return queue.add(line);
    }

    public boolean removeHighlighterLine(HighlightLine line) {
        return queue.remove(line);
    }

    public List<HighlightLine> getQueue() {
        return queue;
    }
    
    public void clean() {
        queue.clear();
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }   
    
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (HighlightLine line : queue) {
            sb.append(line.toString() + "\n");
        }
        return sb.toString();
    }

}
