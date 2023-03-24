import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * A már mentett Klasszok ujbuli visszatolteset segito JMENUITEM, mentesenkent egy darab van*/
public class DeserializerMenuItem extends JMenuItem implements ActionListener {
    private File fileToLoad;
    private ClassHierarchyVisualizer parentFrame;

    public DeserializerMenuItem(ClassHierarchyVisualizer parentFrame, File fileToLoad){
        super(fileToLoad.getName());
        this.parentFrame = parentFrame;
        this.fileToLoad = new File(parentFrame.createPathToSavedProject(fileToLoad));
        this.addActionListener(this);
    }
    /**A betoltest vegzo metodus megvalositasa*/
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            parentFrame.loadGraphFromDesirialization(fileToLoad);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}
