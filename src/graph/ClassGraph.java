package graph;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**KlassNodeokat tároló osztály, gyakorlatilag a Klass diagram megtestesítője*/
public class ClassGraph{

    /**A reprezentált projekt neve*/

    private String projectName;
    /**A projekt Klasszjai*/
    private ArrayList<ClassNode> Nodes;
    /**Azon Klasszok akiknek nincs parent Klasszja*/
    private ArrayList<ClassNode> rootNodes;
    /**Azon Klasszok, melyekből leszármaznak a többiek*/
    private ArrayList<ClassNode> notLeafNodes;
    /**Paraméter nélküli konstruktor*/
    public ClassGraph(){
        projectName = null;
        Nodes = new ArrayList<ClassNode>();
        rootNodes = new ArrayList<>();
        notLeafNodes = new ArrayList<>();
    }
    /**@param projectName a projekt neve
     * beallitja a projekt nevét*/
    public void setProjectName(String projectName){
        this.projectName = projectName;
    }

    /**visszaadja a projekt nevét*/
    public String getProjectName(){return this.projectName;}
    /**@param node-parentjét megkeresi
     * @param nodeList-ben, ha megtalalja visszater vele, ha nem akkor nullal*/
    public ClassNode findParent(ClassNode node, ArrayList<ClassNode> nodeList){
        if(node.getParentName()!=null){
            for(ClassNode potentialsParent: nodeList){
                String parentPackage = potentialsParent.getPackageName();
                String parentClassName = potentialsParent.getClassName();
                boolean parentPackageMatch = false;
                if(node.getParentPackage()==null){
                    if(parentPackage==null){
                        parentPackageMatch = true;
                    }
                }else {
                    if(parentPackage!=null){
                        if(parentPackage.equals(node.getParentPackage())) {
                            parentPackageMatch = true;
                        }
                    }
                }
                if(parentClassName.equals(node.getParentName()) && parentPackageMatch){
                    return potentialsParent;
                }
            }
        }
        return null;
    }
    /**a parentclasszokhoz childokat rendel, ha nincs parent classz, de valaki orokol attol a nem letezo
     * parenttol, akkor legenerálja azt*/
    public void addChildsToParents(){
        //generates non existing parentNodes and adds childs to existing parent classes
        ArrayList<ClassNode> newNodes = new ArrayList<>();
        for(ClassNode node: Nodes){
            if(node.getParentName()!=null) {
                ClassNode potentialParentInNodes = findParent(node, Nodes);
                if (potentialParentInNodes != null) potentialParentInNodes.addChild(node);
                else {
                    ClassNode potentialParentInNewNodes = findParent(node, newNodes);
                    if (potentialParentInNewNodes != null) potentialParentInNewNodes.addChild(node);
                    else {
                        ClassNode newParent = new ClassNode(node.getParentName(), node.getParentPackage());
                        newParent.setCreatedInChildParentMatching();
                        newNodes.add(newParent);
                        newParent.addChild(node);
                    }
                }
            }
            }
        Nodes.addAll(newNodes);
    }
    /**kigyujti a rootnodeokat a rootNodes attr-ba*/
    private void collectRootNodes(){
        for(ClassNode node: Nodes){
            if(node.getParentName()==null){
                rootNodes.add(node);
            }
        }
    }
    /**A rootnodeokra meghívja  acreateTreeNode fv-t ami rekurzivan megjeleniti a gráfot*/
    public void showGraph(DefaultMutableTreeNode root){
        for(ClassNode rootNode: rootNodes){
            rootNode.createTreeNode(root);
        }
    }

    /**Segedfugveny az elozohoz, elindítja azt*/
    public void wrapperLoadGraphFromDirectory(File f) throws IOException{
        loadGraphFromDirectory(f);
        addChildsToParents();
        collectRootNodes();
    }
    /**Legenerál egy classzgráfot egy kiválasztott projektbol*/
    public void loadGraphFromDirectory(File f) throws IOException {
        if(f.isDirectory()) {
            File[] files = f.listFiles();
            if(files != null) {
                for (File file : files) loadGraphFromDirectory(file);
            }
        }
        else{
            FileParser fp = new FileParser(f);
            Nodes.addAll(fp.getClassNodes());
        }
    }

    /**Szerializalja az összes nodeot*/
    public void saveGraph(File workingDirectory) throws IOException {
        if(projectName!=null) {
            File newFile = new File(workingDirectory.getAbsolutePath().concat(System.getProperty("file.separator")).concat(projectName));
            FileOutputStream f = new FileOutputStream(newFile);
            ObjectOutputStream out = new ObjectOutputStream(f);
            for (ClassNode node : Nodes) {
                out.writeObject(node);
            }
            f.close();
        }
    }

    /**deszerializalja a fájlba mentett nodeokat*/
    public void deserializeGraph(File graphFile) throws IOException, ClassNotFoundException {
        Nodes = new ArrayList<>();
        FileInputStream f = new FileInputStream(graphFile.getAbsolutePath());
        ObjectInputStream in = new ObjectInputStream(f);
        boolean run = true;
        while(run){
            try {
                Object obj = in.readObject();
                if (obj != null) {
                    Nodes.add((ClassNode) obj );
                }
                else {
                    run=false;
                }
            }catch (EOFException e){run=false;}
        }
        addChildsToParents();
        collectRootNodes();
    }
    /**Kiválaszt minden olyan nodeot aminek van legalább egy örököse*/
    public void selectNotLeafNodes() {
        for(ClassNode node: Nodes){
            if(node.getChilds()!=null && 0 < node.getChilds().size()) notLeafNodes.add(node);
        }
    }
    /**Visszatér a nem levél nodeokkal*/
    public ArrayList<ClassNode> getNotLeafNodes(){
        return notLeafNodes;
    }

}
