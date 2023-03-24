package graph;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
// import javax.swing.
import java.util.ArrayList;
/**
* A KlassNode osztály reprezentálja és tárolja a fájlokból beolvasott egy-egy osztályt
* */
public class ClassNode implements Serializable{
    /**
    * A Klass nevét tároló mező
    * */
    private String className;

    /**
    * A Klasszot tartalmazó package neve
    * */
    private String packageName;
    /**
    * A Klass parent classja (ha létezik)
    * */
    private String parentName;
    /**
    * A parent Klass package
    * */
    private String parentPackage;

    /**
    * megmondja, hogy a Klasszt a fielokbó kiolvasva deklaráltan találtuk, vagy később generàltuk*/
    boolean createdInChildParentMatching;

    /**
    * Paraméter nélküli konstruktor*/
    public ClassNode(){
        this.parentPackage = null;
        childs = new ArrayList<>();
        this.parentName = null;
        createdInChildParentMatching = false;
    }

    /**
     * Paraméteres  konstruktor
     * @param className: a Klass neve
     * @param packageName: a Klass packagének a neve*/
    public ClassNode(String className, String packageName){
        this.className = className;
        this.packageName = packageName;
        this.parentPackage = null;
        childs = new ArrayList<>();
        this.parentName = null;
        createdInChildParentMatching = false;
    }

    /**
    * igazra állítja createdInChildParentMatching értékét*/
    public void setCreatedInChildParentMatching(){
        createdInChildParentMatching = true;
    }
    /**
     * tárolja azon classzokat, melyek ennek a classnak a leszármazottjai*/
    transient private ArrayList<ClassNode> childs;

    /**
     * visszatér a Klass nevével*/
    public String getClassName(){
        return this.className;
    }
    /**
     * @param newName: a Klass uj neve*/
    public void setClassName(String newName){
        this.className = newName;
    }
    /**
     * @param newName: a Klass parent classjának új  neve*/
    public  void setParentName(String newName){
        this.parentName = newName;
    }
    /**
     * @param packageName: a package erteke erre fog változni*/
    public void setPackageName(String packageName){
        this.packageName = packageName;
    }
    /**
     * @return visszatér a parent nevével*/
    public String getParentName(){
        return this.parentName;
    }
    /**
     * @return visszatér a parent package nevével*/

    public String getParentPackage(){
        return this.parentPackage;
    }
    /**
     * @param parentPackage: beállítja a parent package erteket erre*/

    public void setParentPackage(String parentPackage){this.parentPackage = parentPackage;}
    /**@return visszatér a Klass packageének a nevével*/

    public String getPackageName(){
        return this.packageName;
    }
    /**Uj leszarmazott classzot ad a classzhoz
     * @param node az uj leszármazott*/

    public void addChild(ClassNode node){
        if(childs==null) childs = new ArrayList<>();
        childs.add(node);
    }

    /**@return a leszármazottak*/

    public ArrayList<ClassNode> getChilds(){
        return childs;
    }
    /**@param os az az object ouptu stream ahova szerializálni fogjuk az objektumot
     * a rekurzív fügvényt meghívja leszármazottain is*/

    public void recursiveSerialization(ObjectOutputStream os) throws IOException {
        os.writeObject(this);
        if(childs!=null&&0<childs.size()){
            for(ClassNode node: childs) node.recursiveSerialization(os);
        }
    }
    /**Az applikációban való reprezentációját hozza létre, egy TreeNode formájában*/

    public void createTreeNode(DefaultMutableTreeNode root){
        String editedPackageName = this.packageName;
        if(editedPackageName==null) editedPackageName = "";
        DefaultMutableTreeNode nodesTreeNode = new DefaultMutableTreeNode(editedPackageName.concat(" ").concat(this.className));
        root.add(nodesTreeNode);
        if(childs!=null) {
            for (ClassNode child : childs) {
                child.createTreeNode(nodesTreeNode);
            }
        }
    }

}
