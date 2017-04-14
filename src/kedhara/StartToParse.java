package kedhara;

import java.io.*;
import java.util.*;
import java.lang.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;


public class StartToParse {
	final String srcFdr;
	final String outFdr;
	HashMap<String, Boolean> map;
	HashMap<String, String> classconmap;
	String srcyuml;
	ArrayList<CompilationUnit> compUnit;
	//@To get srcyuml//
	//constructor
	
	StartToParse(String srcFdr,String outPath){
	this.srcFdr = srcFdr;
	//@change@ @Important@ change the outfolder path accordingly
	this.outFdr = srcFdr + "\\" + outPath + ".png";
	map = new HashMap<String, Boolean>();
	classconmap = new HashMap<String, String>();
	srcyuml="";
	}
	
	private String srcyumlSep(String code) {
        String[] srcCode = code.split(",");
	}
	
	public void start() throws Exception {
        compUnit = getcompUnit(srcFdr);
        buildMap(compUnit);
        for (CompilationUnit cu : compUnit)
            srcyuml += uparse(cu);
        srcyuml += uparseAdd();
        srcyuml = srcyumlUniquer(srcyuml);
        System.out.println("Unique Code: " + srcyuml);
        GenerateDiagram.generatePNG(srcyuml, outPath);
    }
	
	//If map.put //for interface its true
	//for class its false
    private void buildMap(ArrayList<CompilationUnit> compUnit) {
        for (CompilationUnit cu : compUnit) {
            List<TypeDeclaration> clist = cu.getTypes();
            for (Node nd : clist) {
                ClassOrInterfaceDeclaration ci = (ClassOrInterfaceDeclaration) nd;
                map.put(ci.getName(), ci.isInterface()); 
                                                           
            }
        }
    }

	private String uparseAdd(){}
	private String uparse(CompilationUnit compUnit){
		String appends = ",";
		String cName = "";
		String out = "";
		String Variables = "";
		String cshName = "";
		ArrayList<String> makeFieldPublic = new ArrayList<String>();
        List<TypeDeclaration> ltd = compUnit.getTypes();
        Node node = ltd.get(0);
		// Gets cName
        ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) node;
        if (cid.isInterface()) {
            cName = "[" + "<<interface>>;";
        } else {
            cName = "[";
        }
        cName += cid.getName();
        cshName = cid.getName();
        
     // Check extends, implements
        if (cid.getExtends() != null) {
            appends += "[" + cshName + "] " + "-^ " + cid.getExtends();
            appends += ",";
        }
        if (cid.getImplements() != null) {
            List<ClassOrInterfaceType> interfaceList = (List<ClassOrInterfaceType>) cid
                    .getImplements();
            for (ClassOrInterfaceType iface : interfaceList) {
                appends += "[" + cshName + "] " + "-.-^ " + "["
                        + "<<interface>>;" + iface + "]";
                appends += ",";
            }
        }
        
     // Combine className, methods and Variables
        out += cName;
        if (!Variables.isEmpty()) {
            out += "|" + changeBrackets(Variables);
        }
        if (!methods.isEmpty()) {
            out += "|" + changeBrackets(methods);
        }
        out += "]";
        out += appends;
        return out;
    }
	}	
	//}
	//Coverts AccessModifiers to symbols
	 private String amToSym(String stringModifier) {
		 { if(stringModifier.equals("private"))
				return "-";
			else if(stringModifier.equals("public"))
				return "+";
			else
				return "";
	        }
	    }
	private String srcyumlUniquer(String code) {
        String[] codeLines = code.split(",");
        String[] uniqueCodeLines = new LinkedHashSet<String>(
                Arrays.asList(codeLines)).toArray(new String[0]);
        String out = String.join(",", uniqueCodeLines);
        return out;
    }
	//@Method Map Parse - To get keys and to add the connections
	private String mapParse(){
		String output = "";
		//@to get the keys
		Set<String> keys = classconmap.keySet();
		for(String k : keys) {
			String[] cnames = k.split("-");
			if(map.get(cnames[0]))
				output += "[<<interface>>;" + cnames[0] + "]";
			else
				output +="[" + cnames[0] + "]";
			//@to add the connection
			output += classconmap.get(k);
			if(map.get(cnames[1]))
				output += "[<<interface>>;" + cnames[1] + "]";
			else
				output +="[" + cnames[1] + "]";
			output += ",";
			
		}
		return output;
	}
	// To-ParseMthds
    boolean nextParam = false;
    for (BodyDeclaration bd : ((TypeDeclaration) node).getMembers()) {}
	public void start() throws Exception {
		compUnit = getCompUnit(srcFdr);
//change needs		buildMap(compUnit);
		for (CompilationUnit comp : compUnit)
//change needs				srcyuml += parser(comp);
		srcyuml += mapParse();
//change needs			srcyuml = srcyumlUniquer(srcyuml);
        System.out.println("Unique Code: " + srcyuml);
// change needs	       GenerateDiagram.generatePNG(srcyuml, outFdr);
	}
	
	//@Method to get .java source files from input folder//
	private ArrayList<CompilationUnit> getCompUnit(String srcFdr) 
			throws Exception{
	    
		//@Getting filenames of all files in a folder
	 File folder = new File(srcFdr);
	 ArrayList<CompilationUnit> compUnit = new ArrayList<CompilationUnit>();
		 
	 //@@For loop for Arrays- Variable file holds the current value from the folder array
	 for (final File jf : folder.listFiles()) {
		 if (jf.isFile() && jf.getName().endsWith(".java")) {
		    FileInputStream filein = new FileInputStream(jf);
		    CompilationUnit comp;
		    try{
		    	comp = JavaParser.parse(filein);
		    	compUnit.add(comp);
		    } finally {
		    	filein.close();
		    }
		    
		 }
	 }
		 return compUnit;
	 }
	
	
	}


	

		
	
	
	


