package graph;

import static org.junit.Assert.*;

public class ClassNodeTest {

    private ClassNode node;
    @org.junit.Before
    public void setup(){
        node = new ClassNode("TestNode", "TestPackage");
    }

    @org.junit.Test
    public void getClassName() {
        assertEquals("name should be TestNode","TestNode", node.getClassName());
    }

    @org.junit.Test
    public void setClassName() {
        node.setClassName("ASD");
        assertEquals("name should be ASD",  "ASD", node.getClassName());

    }

    @org.junit.Test
    public void setParentName() {
        node.setParentName("Parent");
        assertEquals("parent name should be Parent", "Parent", node.getParentName());
    }

    @org.junit.Test
    public void getParentName() {
        node.setParentName("XYZ");
        assertEquals("Parent name should ne XYZ", "XYZ", node.getParentName());

    }

    @org.junit.Test
    public void getPackageName() {
        assertEquals("Node package name should  be TestPackage","TestPackage", node.getPackageName());
    }

    @org.junit.Test
    public void setPackageName() {
        node.setPackageName("MasikNev");
        assertEquals("Node package name should  be MasikNev", "MasikNev",  node.getPackageName());
    }



    @org.junit.Test
    public void getParentPackage() {
        node.setParentPackage("ParentPackageUj");
        assertEquals("The correct parent package would be ParentPackageUj", "ParentPackageUj", node.getParentPackage() );
    }

    @org.junit.Test
    public void setParentPackage() {
        node.setParentPackage("ParentPackageSetterTeszt");
        assertEquals("The correct parent package would be ParentPackageSetterTeszt", "ParentPackageSetterTeszt", node.getParentPackage() );
    }


}