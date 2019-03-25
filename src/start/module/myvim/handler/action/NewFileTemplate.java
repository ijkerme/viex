package start.module.myvim.handler.action;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import start.module.myvim.utilities.FileUtils;
import start.module.myvim.utilities.VIEXPairTable;
import start.module.myvim.utilities.VIEXPairTable.CATEGORY;
import static start.module.myvim.utilities.FileUtils.*;

/**
 * New a file from existed a file template
 * 
 * @author jker
 */
public class NewFileTemplate {

    /**
     *  Map for the template name and the suffix
     */
    private Map params = new HashMap();
    private String fileName;
    private String templateName;
    private String content;

    public FileObject createFileFromTemplate(FileObject currFile, String content) {
        this.content = content;
        if (handleContent())
            return create(currFile, true);
        else
            return null;
    }

    /**
     *
     *
     */
    private FileObject create(FileObject parent, boolean create) {
        String ms = FileUtils.getMatchChars(FileUtil.toFile(parent), content);
        File file = FileUtils.normalizeFile(FileUtil.toFile(parent), content);
        FileObject fo = null;
        File f = new File(file.getPath() + separator + ms);
        try {
            if (!f.exists()) {
                if (create) {
                    f = new File(file.getPath());
                    f.mkdirs();
                    String fn = f.getCanonicalPath();
                    FileObject tempfo = FileUtil.toFileObject(new File(fn));
                    DataFolder df = DataFolder.findFolder(tempfo);
                    if (fileName == null || fileName.length() == 0)
                        return null;
                    else
                        fo = createFromTemplateFile(templateName, df, fileName, params);
                }
            } else {
                fo = FileUtil.toFileObject(f.getCanonicalFile());
            }
        } catch (IOException ex) {
            //ex.printStackTrace();
        }
        return fo;
    }

    private FileObject createFromTemplateFile(String templateName, DataFolder folder,
                                           String name, Map<String, Object> parameters) {
        FileSystem root = Repository.getDefault().getDefaultFileSystem();
        FileObject template = root.findResource(templateName);
        try {
            DataObject dTemplate = DataObject.find(template);
            DataObject dobject = dTemplate.createFromTemplate(folder, name, parameters);
            FileObject retFile = dobject.getPrimaryFile();
            return retFile;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    private boolean handleContent() {
        content = FileUtils.trimLeft(content);
        int i = content.lastIndexOf(".");
        int i2 = content.lastIndexOf(separator);
        if (i != -1) {
            if (i2 != -1)
                fileName = content.substring(i2 + 1, i);
            else
                fileName = content.substring(0, i);
            String s = content.substring(i + 1, content.length());
            templateName = getTemplateName(s);
        }
        return true;
    }

    private String getTemplateName(String s) {
        initParams();
        return VIEXPairTable.getInstance().getPairValue(CATEGORY.TEMPLATE_PAIR, s);
    }

    private void initParams() {
        params.put("name", fileName);
    }
}
