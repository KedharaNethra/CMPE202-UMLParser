package kedhara;

import java.io.*;
import java.util.*;
import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;

import net.sourceforge.plantuml.SourceStringReader;

public class ParseSeqCode {
    String pumlCode;
    final String inPath;
    final String outPath;
    final String inFuncName;
    final String inClassName;

    HashMap<String, String> mapMethodClass;
    ArrayList<CompilationUnit> cuArray;
    HashMap<String, ArrayList<MethodCallExpr>> mapMethodCalls;

    ParseSeqCode(String inPath, String inClassName, String inFuncName,
            String outFile) {
        this.inPath = inPath;
        this.outPath = inPath + "\\" + outFile + ".png";
        this.inClassName = inClassName;
        this.inFuncName = inFuncName;
        mapMethodClass = new HashMap<String, String>();
        mapMethodCalls = new HashMap<String, ArrayList<MethodCallExpr>>();
        pumlCode = "@startuml\n";
    }

    public void start() throws Exception {
        cuArray = getCuArray(inPath);
        buildMaps();
        pumlCode += "actor user #black\n";
        pumlCode += "user" + " -> " + inClassName + " : " + inFuncName + "\n";
        pumlCode += "activate " + mapMethodClass.get(inFuncName) + "\n";
        parse(inFuncName);
        pumlCode += "@enduml";
        generateDiagram(pumlCode);
        System.out.println("pUML Code:\n" + pumlCode);
    }

    private void parse(String callingFunc) {
        String className = "";
        String ShrtName = "";


        for (MethodCallExpr mce : mapMethodCalls.get(callingFunc)) {
            String callingClass = mapMethodClass.get(callingFunc);
            String calledFunc = mce.getName();
            String calledClass = mapMethodClass.get(calledFunc);
            if (mapMethodClass.containsKey(calledFunc)) {
                pumlCode += callingClass + " -> " + calledClass + " : "
                        + mce.toStringWithoutComments() + "\n";
                pumlCode += "activate " + calledClass + "\n";
                parse(calledFunc);
                pumlCode += calledClass + " -->> " + callingClass + "\n";
                pumlCode += "deactivate " + calledClass + "\n";
            }
        }
    }
   

    private void buildMaps() {
        for (CompilationUnit cu : cuArray) {
            String className = "";
            List<TypeDeclaration> td = cu.getTypes();
            for (Node n : td) {
            	//to get the classname
            	ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) n;
                if (coi.isInterface()) {
                    className = "[" + "<<interface>>;";
                } else {
                    className = "[";
                }
                className += coi.getName();
                ShrtName = coi.getName();

                ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) n;
                className = coi.getName();
                for (BodyDeclaration bd : ((TypeDeclaration) coi)
                        .getMembers()) {
                    if (bd instanceof MethodDeclaration) {
                        MethodDeclaration md = (MethodDeclaration) bd;
                        ArrayList<MethodCallExpr> mcea = new ArrayList<MethodCallExpr>();
                        for (Object bs : md.getChildrenNodes()) {
                            if (bs instanceof BlockStmt) {
                            	for (Object fs : md.getChildrenNodes()) {
                            		if (fs instance of BlockStmt){
                            	}
                                for (Object es : ((Node) bs)
                                        .getChildrenNodes()) {
                                                                            }
                                    }
                                }
                            }
                        }
                        mapMethodCalls.put(md.getName(), mcea);
                        mapMethodClass.put(md.getName(), className);
                    }
                }
            }
        }
       
    }

    private ArrayList<CompilationUnit> getCuArray(String inPath)
            throws Exception {
        File folder = new File(inPath);
        ArrayList<CompilationUnit> cuArray = new ArrayList<CompilationUnit>();
        for (final File f : folder.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".java")) {
                FileInputStream in = new FileInputStream(f);
                CompilationUnit cu;
                try {
                    cu = JavaParser.parse(in);
                    cuArray.add(cu);
                } finally {
                    in.close();
                }
            }
        }
        return cuArray;
    }
    @SuppressWarnings
    private void printMaps() {
        System.out.println("Method calls to Map");
        //To get all Keys
        Set<String> k = mapMethodCalls.keySet(); 
            System.out.println(i + "->" + mapMethodCalls.get(i));
        }
        System.out.println("method calls passed");
        k = null;
        }
    private String generateDiagram(String source) throws IOException {

        OutputStream png = new FileOutputStream(outPath);
        SourceStringReader reader = new SourceStringReader(source);
        String desc = reader.generateImage(png);
        return desc;

    }

   
}
