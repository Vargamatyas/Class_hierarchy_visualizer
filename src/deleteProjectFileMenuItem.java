import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**Torli az aktualis graf menteset*/
public class deleteProjectFileMenuItem extends JMenuItem implements ActionListener {
    private ClassHierarchyVisualizer mainFrame;
    public deleteProjectFileMenuItem(ClassHierarchyVisualizer mainFrame){
        super("Delete Current Projects Save File");
        this.mainFrame = mainFrame;
        this.addActionListener(this);

    }
    /**A torlest vegzo metodus megvalositasa*/
    @Override
    public void actionPerformed(ActionEvent e) {
        mainFrame.deleteSaveFileForCurrentProject();
    }
}
