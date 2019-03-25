package start.module.myvim.handler.cmdhandler;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import start.module.myvim.handler.BufferedData;
import start.module.myvim.handler.action.ActionFactory;
import start.module.myvim.handler.action.CommandAction;

import static start.module.myvim.utilities.CommandUtils.*;
import static start.module.myvim.utilities.FileUtils.*;
import static start.module.myvim.state.CommandType.*;
import static start.module.myvim.state.ActionType.*;
import start.module.myvim.utilities.ComponentUtils;

/**
 *
 * @author jker
 */
public class ColonCommandHandler extends ContentCommandHandler {
    
    public static final String CURRENT = "current";
    public static final String HIGHER  = "higher";
    public static final String ROOT    = "root";
    
    private static final Logger LOG = Logger.getLogger("ColonCommandHandler");
    
    private List<FileObject> fileNames = new ArrayList<FileObject>();
    private int index;
    private String matchs = "";   // 保存匹配字符
    
    public ColonCommandHandler(){
        super("ColonCommandParser");
    }
    
    public void parse(BufferedData cmd, ActionEvent e) {
        if (cmd != null) {
            // remove end char if Backspace key is pressed.
            if (isEndWithBackspace(cmd.getFullContent())) {
                cmd.removeTailChar();          //在这里要按照情况清空 fileNames
                cmd.removeTailChar();
                updateStatusBarInfo(cmd.getFullContent());
                clear();
            } else if (isEndWithTab(cmd.getFullContent())) {
                cmd.removeTailChar();          // first remove char '\t'
                process(e, cmd);
            } else {
                setBufferedData(cmd);
                if (isEndWithEnter(cmd.getFullContent())) {
                    cmd.removeTailChar();       // remove '\n' 字符
                    String cmdString = cmd.getFullContent();
                    clear();         // reset tab data
                    ActionFactory af = ActionFactory.getInstance();
                    String[] commands = af.getCommands(COLON_COMMAND);
                    String cmdPrefix = "";
                    int cl = 0;
                    for (int i = 0, length = commands.length; i < length; i++) {
                        String single = commands[i];
                        if (cmdString.startsWith(single)) {
                            if (cl < single.length()) {//handle ":q",":q!", find most command of length
                                cl = single.length();
                                cmdPrefix = single;
                                setContent(cmdString.substring(cmdPrefix.length()));
                            }
                        }
                    }
                    if (cmdPrefix.length() > 0) {
                        setComplete(true);
                        CommandAction cmdAction = af.getAction(COLON_ACTION, cmdPrefix);
                        if (cmdAction != null) {
                            setAction(cmdAction);
                        } else
                            reset();
                    } else {
                        reset();
                        StatusDisplayer.getDefault().setStatusText("Not found action");
                    }
                } else
                    clear();
            }
        }
    }
    
    private void process(ActionEvent e, BufferedData cmd) {
        int size = fileNames.size();
        if (size > 0) {
            next(cmd, size);
        } else {
            JTextComponent target = ComponentUtils.getTextComponent(e);
            if ((target != null) && target.isEnabled()) {
                String[] cs = splitCommanAndFileName(cmd.getFullContent());
                //LOG.info("Tab-->get command string:" + cs[0]);
                //LOG.info("Tab-->get command value:" + cs[1]);
                if (cs[0] != null && cs[0].equals(":new")) {
                    
                    FileObject fileObject = NbEditorUtilities.getFileObject(target.getDocument());
                    FileObject currentFile = fileObject.getParent();
                    
                    //String ss = cs[1].trim();  // 应该只trim掉前导空格
                    String ss = trimLeft(cs[1]); // trim left chars
                    
                    File ff = normalizeFile(FileUtil.toFile(currentFile), ss);
                    matchs = getMatchChars(FileUtil.toFile(currentFile), ss);
                    fileNames = processFile(ff, matchs);
                }
                // add first value of matched
                if (fileNames.size() > 0) {
                    FileObject fn = fileNames.get(index);
                    int ml = matchs.length();
                    if (ml > 0) {
                        int l2 = cmd.getFullContent().length();
                        int start = l2 - ml;
                        cmd.delete(start, l2); // delete string of match
                        matchs = "";           // reset, just only once added
                    }
                    changeData(fileNames.get(index), cmd);
                    index++;
                }
                
            }
        }
        updateStatusBarInfo(cmd.getFullContent());
    }
    
    private void next(final BufferedData cmd, final int size) {
        FileObject tempf = null;
        if (index == size) {
            tempf = fileNames.get(size - 1);
            index = 0;
        } else {
            tempf = fileNames.get(index - 1);
        }
        int l = tempf.getNameExt().length();
        int l2 = cmd.getFullContent().length();
        int start = l2 - l;
        cmd.delete(start, l2);                 // delete previous chars
        changeData(fileNames.get(index), cmd);
        index++;
    }
    
    private String[] splitCommanAndFileName(String s) {
        String[] ss = new String[2];
        int i = s.indexOf(' ');
        if (i != -1) {
            ss[0] = s.substring(0, i);
            ss[1] = s.substring(i);
        }
        return ss;
    }
    
    private void changeData(FileObject e, BufferedData cmd) {
        cmd.addString(e.getNameExt());
    }
    
    private void clear() {
        fileNames.clear();
        index = 0;
    }
    
}
