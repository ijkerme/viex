package start.module.myvim.utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;

/**
 *
 * @author jker
 */
public class FileUtils {

    public static String separator;
    public static char separatorChar;
    private static Logger LOG = Logger.getLogger("FileUtils");
    static {
        separator = File.separator;
        separatorChar = File.separatorChar;
    }
    
    /**
     *  分析一个字符串，返回此字符串所指定的文件， 所指定的文件如果是相对路径，则相对工作目
     * 录为 <code>dir</code> 所在的目录, 它并不会分析<code>ss</code>字符串是否
     * 符合正确的路径名称
     *
     * @param dir 相对路径的参照目录
     * @param ss  要分析的路径字符串
     * @return    返回<code>ss</code>指定的文件所在的目录, 指定的文件是相对路径则指定的文件以<code>dir</code>作为当前目录
     */
    public static File normalizeFile(File dir, String ss) {
        ss = FileUtils.trimLeft(ss); // trim left chars
        if (null == ss || null == dir)
            throw new IllegalArgumentException("all args can't null");
        
        int i = ss.lastIndexOf(separator);
        String prefix = "";
        if (i != -1)
            prefix = ss.substring(0, i + 1);
        File file = null;
        boolean relatively = isRelatively(ss);
        if (relatively) {
            file = new File(dir, prefix);
        } else if (!relatively) {
            file = new File(prefix);
        }
        if (file != null)
            return file;
        
        File file2 = new File(separatorChar + dir.getPath() + separatorChar + prefix);
        if (file2.exists())
            return file2;
        return dir;
    }
    
    public static String getMatchChars(File parent, String ss) {
        ss = FileUtils.trimLeft(ss); // trim left chars
        int i = ss.lastIndexOf(separator);
        if (i != -1) {
            return ss.substring(i + 1, ss.length());
        }
        return ss;
    }
    
    private static boolean isRelatively(String ss) {
        if (ss.startsWith(separator))
            return false;
        return true;
    }
    
    public static List<FileObject> processFile(File file, String match) {
        //LOG.info("Match string:" + match + "|");
        List<FileObject> fileNames = new ArrayList();
        File[] files = file.listFiles();     // use File Filter instead of???
        if (files != null) {
            for (int i = 0, count = files.length; i < count; i++) {
                File f = files[i];
                //LOG.info("Process file:" + f);
                if (f.getName().startsWith(match) && !f.isHidden()) {  // can't add hidden files
                    try {
                        fileNames.add(FileUtil.toFileObject(f.getCanonicalFile()));
                    } catch (IOException ex) {
                        //ex.printStackTrace();
                    }
                    //LOG.info("Match file:" + f.getName());
                }
            }
            //LOG.info("Ok childs size:" + fileNames.size());
        }
        return fileNames;
    }
    
    public static String trimLeft(String s) {
        if (null == s)
            return "";
        int i = 0;
        for (int len = s.length(); i < len && ' ' == s.charAt(i); i++)
            ;
        //LOG.info(s.substring(i));
        return s.substring(i);
    }
    
    
    /**
     *   获取当前工作的文档
     *
     */
    public static FileObject getCurrentFile(JTextComponent target) {
        FileObject fileObject = NbEditorUtilities.getFileObject(target.getDocument());
        FileObject currentFile = fileObject.getParent();
        return currentFile;
    }
    
    /**
     *   
     *  
     */
    public static FileObject guessFileObject(String content, FileObject parent, boolean create) {
        String ss = FileUtils.trimLeft(content); // trim left chars
        String ms = FileUtils.getMatchChars(FileUtil.toFile(parent), ss);
        File file = FileUtils.normalizeFile(FileUtil.toFile(parent), ss);
        //String fileName = file.getPath() + "/" + ms;
        FileObject fo = null;
        File f = new File(file.getPath() + separator + ms);
        //logger.info("New File:" + f.getPath());
        int i = ss.lastIndexOf(separator);
        try {
            if (!f.exists()) {
                if (create) {
                    f = new File(file.getPath());
                    f.mkdirs();
                    f = new File(file.getPath() + separator + ms);
                    if (f.createNewFile())                 // throw IOException in linux, not in windows
                        fo = FileUtil.toFileObject(f.getCanonicalFile());
                }
            } else {
                fo = FileUtil.toFileObject(f.getCanonicalFile());
            }
        } catch (IOException ex) {
            //ex.printStackTrace();
        }
        return fo;
    }

    public static String getDisallowCharsInSystem() {
        if (Utilities.isUnix()) {
            return "\\:"; 
        } else if (Utilities.isWindows()) {
            return "";
        }
        return "";
    }
    
    // copied from netbeans 6.0
    
    public static FileObject createData(final File data) throws IOException {
        FileObject retval = null;
        File root = getRoot(data);
        if (!root.exists()) {
            throw new IOException(data.getAbsolutePath());
        }
        FileObject rootFo = FileUtil.toFileObject(root);
        assert rootFo != null : root.getAbsolutePath();
        final String relativePath = getRelativePath(root, data);
        try {
            retval = FileUtil.createData(rootFo,relativePath);
        } catch (IOException ex) {
            //thus retval = null;
        }
        //if refresh needed because of external changes
        if (retval == null || !retval.isValid()) {
            rootFo.getFileSystem().refresh(false);
            retval = FileUtil.createData(rootFo,relativePath);
        }
        assert retval != null;
        return retval;
    }
    
    public static File getRoot(final File dir) {
        File retval = dir;
        for (; retval.getParentFile() != null; retval = retval.getParentFile());
        assert retval != null;
        return retval;
    }
    
    public static String getRelativePath(final File dir, final File file) {
        Stack<String> stack = new Stack<String>();
        File tempFile = file;
        while(tempFile != null && !tempFile.equals(dir)) {
            stack.push(tempFile.getName());
            tempFile = tempFile.getParentFile();
        }
        assert tempFile != null : file.getAbsolutePath() + "not found in " + dir.getAbsolutePath();//NOI18N
        StringBuilder retval = new StringBuilder();
        while (!stack.isEmpty()) {
            retval.append(stack.pop());
            if (!stack.isEmpty()) {
                retval.append('/');//NOI18N
            }
        }
        return retval.toString();
    }
    
}
