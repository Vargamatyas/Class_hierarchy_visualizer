import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**Projektek folderekbol valo betolteset segito JMENUITEM*/
public class LoadFromDictionaryMenuItem extends JMenuItem implements ActionListener {
    private ClassHierarchyVisualizer mainWindow;
    private JFileChooser fc;
    public LoadFromDictionaryMenuItem(ClassHierarchyVisualizer main, JFileChooser fc){
        super("Load Project From Directory");
        this.addActionListener(this);
        mainWindow = main;
        this.fc = fc;
    }
    /**A betoltest vegzo metodus megvalositasa*/
    @Override
    public void actionPerformed(ActionEvent e) {


        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = fc.showOpenDialog(mainWindow);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                this.mainWindow.loadDirectory(file);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
