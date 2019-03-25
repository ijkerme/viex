package start.module.myvim.handler.action;


/**
 *
 * @author jker
 */
public class HistorySearch {
    
    private static HistorySearch historySearch;
    
    //private LinkedList commands = new LinkedList();
    private String command = "";
    
    private int offset;
    
    public HistorySearch() {};
    
    public static HistorySearch getInstance() {
        if (historySearch == null) {
            historySearch = new HistorySearch();
        }
        return historySearch;
    }

    public void setHistoryCommand(String commandm, int offsetm) {
        this.command = commandm;
        this.offset = offsetm;
    }
    
    public String getHistoryCommand() {
        return this.command;
    }
    
    public void cleanHistoryCommand() {
        command = "";
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public void setOffset(int offsetm) {
        this.offset = offsetm;
    }
}
