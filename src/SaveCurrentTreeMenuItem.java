import graph.ClassGraph;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**Az aktualis projekt grafjanak az egeszet lemento Jmenuitem*/
public class SaveCurrentTreeMenuItem extends JMenuItem implements ActionListener {
    private ClassHierarchyVisualizer mainWindow;
    public SaveCurrentTreeMenuItem(ClassHierarchyVisualizer mainWindow){
        super("Save current project");
        this.addActionListener(this);
        this.mainWindow = mainWindow;
    }
    /**A mentest vegzo metodus megvalositasa*/
    @Override
    public void actionPerformed(ActionEvent e) {
        ClassGraph graphToBeSaved = mainWindow.getClassHierarchyGraph();
        File testExistence = new File(mainWindow.createPathToSavedProject(mainWindow.getCurrentProject()));
        if(!testExistence.exists()) {
            try {
                graphToBeSaved.setProjectName(this.mainWindow.getCurrentProject().getName());

                graphToBeSaved.saveGraph(mainWindow.getWorkingDirectory());
                mainWindow.addNewLoadMenuItem(this.mainWindow.getCurrentProject());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
