package graph;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class FileParserTest {
    FileParser fp;
    ClassNode C1;
    ClassNode C2;
    @Before
    public void setup() throws IOException {
        fp = new FileParser(new File("testfile"));
        C1 = new ClassNode("A", "test");
        C2 = new ClassNode("B", "test");
        C1.setParentName("B");
    }
    @Test
    public void getClassNodes() {
        ArrayList<ClassNode> nodes = fp.getClassNodes();
        boolean found_a = false;
        for(ClassNode testNode: nodes){
            if(C1.getClassName().equals(testNode.getClassName()) && C1.getParentName().equals(testNode.getParentName())){
                found_a = true;
            }
            assertTrue("found_a should be true", found_a);

        }
    }
}