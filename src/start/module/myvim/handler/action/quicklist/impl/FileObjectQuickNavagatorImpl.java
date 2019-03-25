package start.module.myvim.handler.action.quicklist.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;
import start.module.myvim.exception.EVIException;
import start.module.myvim.handler.action.quicklist.AbstractQuickNavagatorWorker;
import start.module.myvim.quicklist.FileObjectQuickListItem;
import start.module.myvim.quicklist.VIEXPopup;
import start.module.myvim.quicklist.VIEXQuickListItem;
import start.module.myvim.quicklist.VIEXQuickListScrollPane;
import start.module.myvim.utilities.VIEXBundle;
import static start.module.myvim.utilities.VIEXOptions.*;
import start.module.myvim.options.VIEXPanel;

/**
 *  open project file navagator
 *
 * @author jker
 */
public class FileObjectQuickNavagatorImpl extends AbstractQuickNavagatorWorker {

    public static final String DEFAULT_PATTERN = "^(CVS|SCCS|vssver\\.scc|#.*#|%.*%|\\.(cvsignore|svn|DS_Store))$|^\\.[#_]|~$|^\\..*|.*\\.tmp|";

    private String PATTERN; 
    private boolean ignore;
    
    private Thread workerThread;
    
    private static FileObjectQuickNavagatorImpl foqn = new FileObjectQuickNavagatorImpl();
    
    private FileObjectQuickNavagatorImpl() {
        
    }
    
    public static FileObjectQuickNavagatorImpl getInstance() {
        return foqn;
    }
    
    public void openQuickList() {
        if (isRunning())
            return ;
        //System.out.println("open project files list navagator");
        setRunning(true);
        new Thread(this).start();
    }
    
    public void run() {
        //System.out.println("start thread...");
        if (null == target)
            throw new EVIException("component cann't be null which used show popup");
        TopComponent topComp = TopComponent.getRegistry().getActivated();
        if (topComp == null)
            return ;
        final Node[] nodes = topComp.getActivatedNodes();
        if (nodes == null)
            return ;
        runTask(nodes);
        setRunning(false);
    }
    
    private void runTask(Node[] nodes) {
        Project project = findProject(nodes);
        if (project != null) {
            results.clear();
            PATTERN = getRegularExpression();
            ignore = isIgnore();
            SourceGroup[] sg = ProjectUtils.getSources(project).getSourceGroups(Sources.TYPE_GENERIC);
            for (int i = 0, counts = sg.length; i < counts; i++) {
                FileObject fobject = sg[i].getRootFolder();
                if (fobject.isFolder()) {
                    iteratorDir(fobject.getChildren(), results);
                }
            }
            //System.out.println("size:" + results.size());
            if (0 == results.size())
                return ;
            sortResults(results);
            if (isRunning()) {
                //System.out.println("navagator is running");
                VIEXQuickListScrollPane spane = new VIEXQuickListScrollPane();
                spane.setData(results, VIEXBundle.getMessage("PROJECT_FILES_LIST"), 0);
                popup.setEditorComponent(target);
                popup.setContentComponent(spane);
                popup.show();
            } else {
                results.clear();
                popup.hide();
            }
        }
        
    }
    
    private Project findProject(Node[] activatedNodes) {
        for (int i = 0; i < activatedNodes.length; i++) {
            Project p = (Project)activatedNodes[i].getLookup().lookup(Project.class);
            if (p != null) {
                return p;
            }
            DataObject dataObject = (DataObject)activatedNodes[i].getLookup().lookup(DataObject.class);
            if (dataObject == null) {
                continue;
            }
            p = FileOwnerQuery.getOwner(dataObject.getPrimaryFile());
            if (p != null) {
                return p;
            }
        }
        return null;
    }
    
    private void iteratorDir(FileObject[] files, List result) {
        for (FileObject file : files) {
            if (file.isFolder()) {
                if (!file.getName().equals("build"))
                    iteratorDir(file.getChildren(), result);
            } else {
                if (file.canWrite()) {
                    if (ignore) {
                        //System.out.println("ignore");
                        if (!isMatch(file.getNameExt()))
                            result.add(new FileObjectQuickListItem(file));
                    } else {
                        //System.out.println("match");
                        if (isMatch(file.getNameExt()))
                            result.add(new FileObjectQuickListItem(file));
                    }
                }
            }
        }
    }
    
    private boolean isMatch(String s) {
        Pattern pa = Pattern.compile(PATTERN);
        Matcher ma = pa.matcher(s);
        if (ma.matches())
            return true;
        return false;
    }
    
    private String getRegularExpression() {
        return getVIEXOption(GENERAL, VIEXBundle.getMessage(VIEXPanel.class, "OptionsQuickListRegularExp"), DEFAULT_PATTERN);
    }
    
    private boolean isIgnore() {
        return getBoolean(GENERAL, VIEXBundle.getMessage(VIEXPanel.class, "OptionsIgnoreFiles"));
    }
    
}
