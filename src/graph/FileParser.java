package graph;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**Segít kinyerni fájlokból a class hierarchia reprezentálásához fontos adatokat*/

public class FileParser{

    /**A feldolgozott fájlban declarált package neve ha van*/
    private String currentPackageName;
    /**Az import sorok tárolása*/
    private ArrayList<ArrayList<String>> importLines;
    /**Az import sorokbol lenyeges adat*/
    private ArrayList<String> parsedImportLine;
    /**A fájlban deklarált classzok*/
    private ArrayList<ClassNode> nodes;
    /**A feldolgozott fájl*/
    private File fileToProcess;
    /**A fájl tartalma ' '-mentén tördelve*/
    private ArrayList<String> fileContent;

    /**Konstruktor
     * @param fileToProcess a feldolgozni kivant fájl*/
    public FileParser(File fileToProcess) throws IOException {
        currentPackageName = null;
        this.fileToProcess = fileToProcess;
        this.importLines = new ArrayList<>();
        nodes = new ArrayList<>();
        fileContent = new ArrayList<>();
        this.processFile();
    }

    /**import lineok feldarabolása itt tortenik, kinyeri a fontos adatokat melyek kesobb a packagek meghatarozasahoz lesznek
     * fontosak*/
    public void splitImportLines(){
        this.parsedImportLine = new ArrayList<>();
        for(ArrayList<String> importLine: importLines){
            for(String word: importLine){
                if(word.contains(".")){
                    String finalWord = word;
                    if(word.contains(";")){
                        finalWord = finalWord.replace(";","");
                    }
                    parsedImportLine.add(finalWord);
                }
            }
        }
    }

    /**visszater a megtalalt classzokkal*/
    public ArrayList<ClassNode> getClassNodes(){
        return nodes;
    }
    /**Betolti a fajl tartalmat*/
    private void readContent() throws IOException {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(fileToProcess));
        String line;
        while ((line = reader.readLine()) != null){
            String[] lineContent = line.replaceAll("\\s+", " ").split(" ");
            Collections.addAll(fileContent, lineContent);
        }
    }
    /**A feldolgozott import sorok alapjan megprobalja a parent classzok packaget megallapitani*/
    private void decideParentPackages(){
        // decides the parentpackage
        for(ClassNode node: nodes){
            for(String packageName: parsedImportLine){
                ArrayList<String> lineContent = new ArrayList<>();
                Collections.addAll(lineContent, packageName.split("\\."));
                if(!lineContent.isEmpty()) {
                    String importedObjectName = lineContent.remove(lineContent.size()-1);
                    if(node.getParentName()!=null) {
                        if (node.getParentName().equals(importedObjectName)) {
                            node.setParentPackage(String.join(".", lineContent));
                            break;
                        }
                    }
                }
            }
        }
    }
    /**Egy string arraylistbol állīt elo classzt*/
    private void createNode(ArrayList<String> data){
        ClassNode newNode = new ClassNode();
        for(int index=0; index<data.size(); ++index){
            if(data.get(index).equals("class")){
                String name = data.get(index+1);
                if(name.contains("{")) {
                    name = name.replace("{", "");
                }
                newNode.setClassName(name);
            }
            if(data.get(index).equals("extends")){
                String parentName = data.get(index+1);
                if(parentName.contains("{")) {
                    parentName = parentName.replace("{", "");
                }
                newNode.setParentName(parentName);
                newNode.setParentPackage(null);
            }
        }
        this.nodes.add(newNode);
    }
    /**A fájl tartalmabol kigyujti azokat a reszeket amik a classz deklaraciot tartalmazzák*/
    private void selectClassDeclarations(){
        //collect data, pass it to create Node, when classdefcound true it will collect everything then when { found closes it
        boolean classDecFound = false;
        ArrayList<String> nodeData = new ArrayList<>();
        for(String word: fileContent){
            if(word.equals("class")){
                classDecFound = true;
                nodeData.add(word);
            }
            else {
                if(classDecFound){
                    if(word.contains("{")){
                        nodeData.add(word);
                        classDecFound = false;
                        createNode(nodeData);
                        nodeData = new ArrayList<>();
                    }
                    else nodeData.add(word);
                }
            }

        }
    }
    /**
     * eldonti a fileban levo packaget*/
    private void decidePackage(){
        for(int index = 0; index<fileContent.size();++index){
            if(fileContent.get(index).equals("package")){
                String rawPackageName = fileContent.get(index+1);
                if(rawPackageName.contains(";")) this.currentPackageName = rawPackageName.replace(";", "");
                else {this.currentPackageName = rawPackageName;}
                break;
            }
        }
    }

    /**A fajlban deklaralt classzoknak a fajhoz tartozo packaget allitja be*/
    private void applyPackage(){
        for(ClassNode node: nodes){
            if(this.currentPackageName != null) node.setPackageName(this.currentPackageName);
            else node.setPackageName(null);
        }
    }
    /**Kivalasztja az import sorokat*/
    private void selectImportLines(){
        boolean importFound = false;
        ArrayList<String> importLine = new ArrayList<>();
        for(String word: fileContent){
            if(word.equals("import")){
                importFound = true;
                importLine = new ArrayList<>();
                importLine.add(word);
            }
            else {
                if(importFound){
                    if(word.contains(";")){
                        importLine.add(word);
                        importFound = false;
                        importLines.add(importLine);
                        importLine = null;
                    }
                    else importLine.add(word);
                }
            }
        }
    }

    /**Elvegzi a file feldolgozasat*/
    private void processFile() throws IOException {
        readContent();
        selectImportLines();
        splitImportLines();
        decidePackage();
        selectClassDeclarations();
        applyPackage();
        decideParentPackages();
    }

}
