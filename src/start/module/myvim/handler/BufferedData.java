package start.module.myvim.handler;

/**
 *
 *  @author jker
 */
public class BufferedData {
    
    private StringBuffer command = new StringBuffer();
    
    private static final BufferedData bc = new BufferedData();
    
    public BufferedData() {}
    
    public static BufferedData getInstance() {
        return bc;
    }
    
    public String assembleTypedChar(char key) {
       return command.append(key).toString();       
    }
    
    public void addString(String s) {
        command.append(s);
    }
    
    public void delete(int start, int end) {
        command.delete(start, end);
    }
    
    public String removeTailChar() {
        if (command.length() == 0)
            return null;
       return command.deleteCharAt(command.length() - 1).toString();
    }
    
    public String getFullContent() {
        return command.toString();
    }
    
    public void clean() {
        command.delete(0, command.length());
    }
}