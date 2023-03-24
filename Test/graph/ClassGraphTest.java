package graph;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClassGraphTest {
    ClassGraph testGraph;
    @Before
    public void setup(){
        testGraph = new ClassGraph();
    }
    @Test
    public void setProjectName() {
        testGraph.setProjectName("alma");
        assertEquals("Projectname should be alma", "alma", testGraph.getProjectName());
    }
}