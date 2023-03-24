import graph.ClassNode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**A gráf egy bizonyos ágát kivalaszto, es lemento JmenuItem*/
public class SaveBranchMenuItem extends JMenuItem implements ActionListener {
    private ClassHierarchyVisualizer mainFrame;
    private ClassNode node;
    public SaveBranchMenuItem(ClassHierarchyVisualizer mainFrame, ClassNode node){
        super(node.getClassName());
        this.addActionListener(this);
        this.mainFrame = mainFrame;
        this.node = node;
    }
    /**A mentest vegzo metodus megvalositasa*/
    @Override
    public void actionPerformed(ActionEvent e) {

        File newFile = new File(mainFrame.createPathToSavedProject(new File(node.getClassName())));
        if(!newFile.exists()) {
            FileOutputStream f = null;
            try {
                f = new FileOutputStream(newFile);
                try {
                    ObjectOutputStream out = new ObjectOutputStream(f);
                    node.recursiveSerialization(out);
                    mainFrame.addNewLoadMenuItem(newFile);
                    out.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

    }
}
