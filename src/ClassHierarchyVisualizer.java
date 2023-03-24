import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JFileChooser;
import javax.swing.tree.DefaultTreeModel;

import graph.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**Az összes többi classzt ez használja, itt valósul meg maga az alkalmazás egésze*/
public class ClassHierarchyVisualizer extends JFrame{ //inheriting JFrame
    /**A grafot reprezentalo Jtree*/
    private JTree tree;
    /**A közös hidden root a gráfnak*/
    private DefaultMutableTreeNode root;
    /**Az aktuálisan vizualizált gráf*/
    private ClassGraph classHierarchyGraph;
    /**A az alkalmazas altal hasznalt file chooser, a projektek betöltéséhez*/
    private JFileChooser fc;
    /**A menübár*/
    private JMenuBar bar;
    /**Az aktuális projektfájl*/
    private File currentProject;
    /**A JscrollPane ami a JTreet öleli körbe, hogy ne legyen probléma a túl nagy meretu gráfokkal*/
    private JScrollPane scrollPane;
    /**Minden betoltessel kapcsolatos JMenuItemnek a közös tartója*/
    private JMenu Load;
    /**Minden mentessel kapcsolatos JmenuItem ide van hozzaadva*/
    private JMenu Save;
    /**Azon fileok, melyekhez már tartozik mentes*/
    private ArrayList<File> savedProjects;
    /**Azon menuelemek amelyeket a mentett projektekhez generalunk, hogy konnyebb legyen a betoltes*/
    private ArrayList<JMenuItem> loaderItems;
    /**Az a folder ahol fut a program*/
    private File workingDirectory;
    /**Visszater a workingdirectoryvel*/
    public File getWorkingDirectory(){
        return workingDirectory;
    }
    /**Megjeleniti a grafot*/
    private void showGraph(){
        classHierarchyGraph.showGraph(root);
    }
    /**Vissza adja az aktualisan kivalasztott projektnek a fileját*/
    public File getCurrentProject(){
        return currentProject;
    }

    /**Visszater az app KlassGraphjával*/
    ClassGraph getClassHierarchyGraph(){
        return this.classHierarchyGraph;
    }
    /**A Jtreenek a modellje, fontos hogy meglegyen, hisszen ennek a segitsegevel frissitjuk a grafot*/
    private DefaultTreeModel treeModel;
    /**Ez a konstruktora, minden az app elinditasahoz szukseges objektum itt inicializalodik*/
    ClassHierarchyVisualizer() throws IOException {
        tryToCreateSaveDirectory();
        loaderItems = new ArrayList<>();
        savedProjects = new ArrayList<>();
        workingDirectory = new File(System.getProperty("user.dir").concat("/saveDirectory"));
        bar = new JMenuBar();
        this.setJMenuBar(bar);
        Load = new JMenu("Load");
        Save = new JMenu("Save");
        bar.add(Load);
        bar.add(Save);
        fc = new JFileChooser(System.getProperty("user.dir"));
        Load.add(new LoadFromDictionaryMenuItem(this, fc));
        Load.add(new deleteProjectFileMenuItem(this));
        Save.add(new SaveCurrentTreeMenuItem(this));
        generateLoaderMenuItems();

        root = new DefaultMutableTreeNode("Root");

        tree = new JTree(root);
        scrollPane = new JScrollPane(tree);
        add(scrollPane);
        tree.setRootVisible(false);
        treeModel = (DefaultTreeModel)tree.getModel();
        root = (DefaultMutableTreeNode)treeModel.getRoot();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        setVisible(true);
        this.setSize(400,500);

    }
    /**Megprobalja letrehozni a menteshez hasznalt foldert*/
    private void tryToCreateSaveDirectory(){
        new File(String.valueOf(System.getProperty("user.dir").concat("/saveDirectory"))).mkdirs();
    }

    /**Mivel a treenodeok alap esetben zarva vannak, igy vegig kell iteralni rajtuk, ezt vegzi ez a fv*/
    private void expandAllNodes(JTree tree) {
        int j = tree.getRowCount();
        int i = 0;
        while(i < j) {
            tree.expandRow(i);
            i += 1;
            j = tree.getRowCount();
        }
    }

    /**A filechooser segitsegeve betolti a grafot, majd ujra rajzolja azt*/
    public void loadDirectory(File directoryToShow) throws IOException {
        currentProject = directoryToShow;
        root.removeAllChildren();
        this.classHierarchyGraph = new ClassGraph();
        this.classHierarchyGraph.wrapperLoadGraphFromDirectory(directoryToShow);
        this.classHierarchyGraph.setProjectName(currentProject.getAbsolutePath());

        showGraph();

        tree.setRootVisible(false);
        setVisible(true);
        treeModel.reload(root);
        expandAllNodes(tree);
        updateSaveMenu();
    }

    /**Ha lementunk egy projektet, akkor annak a gyors eleresehez hozza adunk egy deszerializalo menuitemet*/
    public void addNewLoadMenuItem(File projectFile){
        if(projectFile!=null){
            DeserializerMenuItem newMenuItem = new DeserializerMenuItem(this, projectFile);
            Load.add(newMenuItem);
            loaderItems.add(newMenuItem);
        }
        bar.updateUI();
    }

    /**Torli az aktualis projekthez tartozo mentett filet (ha van ilyen)*/
    public void deleteSaveFileForCurrentProject(){
        File toDelete = new File(createPathToSavedProject(this.currentProject));
        if(toDelete.exists()){
            toDelete.delete();
            for(int index=0;index<loaderItems.size();++index){
                if(loaderItems.get(index).getText().equals(this.currentProject.getName())){
                    JMenuItem toBeDeleted = loaderItems.remove(index);
                    Load.remove(toBeDeleted);
                    root.removeAllChildren();
                    treeModel.reload(root);
                    bar.updateUI();
                }
            }
        }
    }
    /**Szerializalt filebol tolti be a gráfot*/
    public void loadGraphFromDesirialization(File fileToLoad) throws IOException, ClassNotFoundException {
        currentProject = fileToLoad;
        root.removeAllChildren();
        classHierarchyGraph = new ClassGraph();
        classHierarchyGraph.deserializeGraph(fileToLoad);
        showGraph();
        classHierarchyGraph.setProjectName(fileToLoad.getName());

        tree.setRootVisible(false);
        tree.setVisible(true);
        treeModel.reload(root);
        expandAllNodes(tree);
        updateSaveMenu();
    }

    /**kigyujti azokat a projekteket amikhez van szerializalt mentes*/
    private void collectSavedProjects(){
        File[] savedFilesArray = workingDirectory.listFiles();
        if(savedFilesArray!=null) Collections.addAll(savedProjects, savedFilesArray);
    }

    /**Updateli a save menut*/
    private void updateSaveMenu(){
        Save.removeAll();
        Save.add(new SaveCurrentTreeMenuItem(this));
        generateSaveBranchMenuItems();
        bar.updateUI();
    }

    /**A mar mentett projektekhez generál egy menuitemet, amivel be lehet oket tolteni*/
    private void generateLoaderMenuItems(){
        collectSavedProjects();
        for(File projectFile: savedProjects){
            if(projectFile!=null) {
                DeserializerMenuItem newItem = new DeserializerMenuItem(this, projectFile);
                Load.add(newItem);
                loaderItems.add(newItem);
            }
        }
    }
    /**A nem leafnodeokhoz general ilyen, a mentest konnyito Menuitemeket*/
    private void generateSaveBranchMenuItems(){
        classHierarchyGraph.selectNotLeafNodes();
        for(ClassNode node: classHierarchyGraph.getNotLeafNodes()){
            Save.add(new SaveBranchMenuItem(this, node));
        }
        bar.updateUI();

    }
    /**Hasznos segedfugveny, a menteshez hasznalt mappabol es egy filebol kepez pathot*/
    public String createPathToSavedProject(File fileToCreatePathTo){
        return workingDirectory.getAbsolutePath().concat(System.getProperty("file.separator")).concat(fileToCreatePathTo.getName());
    }
    /**A main fv*/
    public static void main(String[] args) throws IOException {
        new ClassHierarchyVisualizer();
    }}