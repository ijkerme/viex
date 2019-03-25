package start.module.myvim.handler.action.quicklist;

import start.module.myvim.handler.action.quicklist.impl.FileObjectQuickNavagatorImpl;
import start.module.myvim.handler.action.quicklist.impl.TopComponentQuickNavagatorImpl;
import start.module.myvim.state.QuickListType;
import static start.module.myvim.state.QuickListType.*;

/**
 *
 * @author jker
 */
public class QuickNavagatorFactory {
    
    private static QuickNavagatorFactory navagator;
    private static QuickListType listType;
    
    private QuickNavagatorFactory() {
    }
    
    public static QuickNavagatorFactory getNavagatorFactory() {
        if (null == navagator) {
            navagator = new QuickNavagatorFactory();
        }
        return navagator;
    }
    
    public QuickNavagatorWorker getNavagatorWorker() {
        if (null == listType)
            return null;
        switch(listType) {
            case TAB_EIDTOR:
                return TopComponentQuickNavagatorImpl.getInstance();
            case FILE_OBJECT:
                return FileObjectQuickNavagatorImpl.getInstance();
            default:
                return null;
        }
    }
    
    public QuickNavagatorWorker getNavagatorWorker(QuickListType type) {
        listType = type;
        return getNavagatorWorker();
    }
    
    public void setQuickListType(QuickListType type) {
        listType = type;
    }
    
    public QuickListType getQuickListType() {
        return listType;
    }
    
}
