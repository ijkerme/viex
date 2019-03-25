package start.module.myvim.highlight;

import java.util.Comparator;

/**
 *
 * @author jker
 */
public class HighlightLineComparator implements Comparator<HighlightLine>{

    public int compare(HighlightLine o1, HighlightLine o2) {
        return o1.compareTo(o2) > 0 ? -1 : (o1.compareTo(o2) == 0 ? 0 : 1);
    }
    
}
