package start.module.myvim.handler.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JList;
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
import start.module.myvim.quicklist.FileObjectQuickListItem;
import start.module.myvim.quicklist.VIEXPopup;
import start.module.myvim.quicklist.VIEXQuickListItem;
import start.module.myvim.quicklist.VIEXQuickListScrollPane;
import start.module.myvim.utilities.VIEXBundle;
import static start.module.myvim.utilities.ComponentUtils.*;

/**
 *
 * @author jker
 * @Deprecated No longer used. Instead use the {@link QuickNavagatorWorker}.
 */
public class FileSearch {
    
    private VIEXPopup popup = VIEXPopup.getPopup();

    private static FileSearch fileSearch;
    
    private List results = new LinkedList();

    // flag indicate start where from...
    private int start = 0;
    // key for prior search
    private String key = "";
    
    private FileSearch() {}
    
    public static FileSearch getInstance() {
        if (null == fileSearch) {
            fileSearch = new FileSearch();
        }
        return fileSearch;
    }
    
    protected List getResults() {
        return this.results;
    }
    
    protected void execute(final JTextComponent target, String content) {
        //logger.info("open project files list action executed");
        if (null == target)
            return ;
        TopComponent topComp = TopComponent.getRegistry().getActivated();
        if (topComp == null)
            return ;
        final Node[] nodes = topComp.getActivatedNodes();
        if (nodes == null)
            return ;
        new Thread(new Runnable() {
            public void run() {
                Project project = findProject(nodes);
                if (project != null) {
                    results.clear();
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
                    Collections.sort(results, new Comparator() {
                        public int compare(Object o1, Object o2) {
                            VIEXQuickListItem item1 = (VIEXQuickListItem)o1;
                            VIEXQuickListItem item2 = (VIEXQuickListItem)o2;
                            return item1.getItemValue().compareToIgnoreCase(item2.getItemValue());
                        }
                        
                        public boolean equals(Object obj) {
                            boolean retValue;
                            retValue = super.equals(obj);
                            return retValue;
                        }
                        
                    });
                    VIEXQuickListScrollPane spane = new VIEXQuickListScrollPane();
                    spane.setData(results, VIEXBundle.getMessage("TAB_LIST_TITLE"), 0);
                    popup.setEditorComponent(target);
                    popup.setContentComponent(spane);
                    popup.show();
                }
            }
        }).start();
    }
    
    protected void prefixMatch(JTextComponent component, String content) {
        int i = 0;
        if (key.length() != 0) {
            if (content.startsWith(key)) {
                i = start;
            } else {
                start = 0;
                key = "";
            }
        }
        for (int counts = results.size(); i < counts; i++) {
            // compare, insensitive
            if (((FileObjectQuickListItem)results.get(i)).value().getNameExt().toLowerCase().startsWith(content.toLowerCase())) {
                //System.out.println(((FileObjectQuickListItem)results.get(i)).value().getNameExt());
                if (popup.isVisible()) {
                    JList view = popup.getContentComponent().getView();
                    view.setSelectedIndex(i);
                    view.ensureIndexIsVisible(i);
                    key = content;
                    start = i;
                    return ;
                }
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
                //file.getNameExt().matches("^(CVS|SCCS|vssver\.scc|#.*#|%.*%|\.(cvsignore|svn|DS_Store))$|^\.[#_]|~$");
                if (file.canWrite()) {
                    if (!isMatch(file.getNameExt()))
                        result.add(new FileObjectQuickListItem(file));
                }
            }
        }
    }
    
    private boolean isMatch(String s) {
        String PATTERN = "^(CVS|SCCS|vssver\\.scc|#.*#|%.*%|\\.(cvsignore|svn|DS_Store))$|^\\.[#_]|~$|^\\..*|\\.tmp$|";
        Pattern pa = Pattern.compile(PATTERN);
        Matcher ma = pa.matcher(s);
        if (ma.matches()) {
            return true;
        }
        return false;
    }
    
}
