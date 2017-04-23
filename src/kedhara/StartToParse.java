package kedhara;

import java.io.*;
import java.util.*;


import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class StartToParse {
	String srcyuml;
	String appends = "";
	final String srcFdr;
    final String outFdr;
    HashMap<String, Boolean> map;
    HashMap<String, String> mapCon;
    ArrayList<CompilationUnit> compUnit;

    StartToParse(String srcFdr, String oPath) {
        this.srcFdr = srcFdr;
      //@change@ @Important@ change the outfolder path accordingly
        this.outFdr = srcFdr + "\\" + oPath + ".png";
        map = new HashMap<String, Boolean>();
        mapCon = new HashMap<String, String>();
        srcyuml = "";
    }

    

    private String srcSep(String src) {
        String[] spSrc = src.split(",");
        String[] spUnq = new LinkedHashSet<String>(
                Arrays.asList(spSrc)).toArray(new String[0]);
        String appends = String.join(",", spUnq);
        return appends;
    }
    
    public void start() throws Exception {
        compUnit = readsrcFdr(srcFdr);
        buildMap(compUnit);
        for (CompilationUnit cu : compUnit)
            srcyuml += uparse(cu);
        srcyuml += uparseAdd();
        srcyuml = srcSep(srcyuml);
        System.out.println("Print srcyuml unique code: " + srcyuml);
        UmlGenerator.generatePNG(srcyuml, outFdr); //UmlGenerator to generate the diagram
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

	
	private String uparseAdd() {
        String appends = "";
        Set<String> keys = mapCon.keySet(); // get all keys
        for (String k : keys) {
            String[] cls = k.split("-");
            if (map.get(cls[0]))
                appends += "[<<interface>>;" + cls[0] + "]";
            else
                appends += "[" + cls[0] + "]";
            appends += mapCon.get(k); // Add connection
            if (map.get(cls[1]))
                appends += "[<<interface>>;" + cls[1] + "]";
            else
                appends += "[" + cls[1] + "]";
            appends += ",";
        }
        return appends;
    }
	private String uparse(CompilationUnit compUnit){
		    String cName = "";
	        String cshName = "";
	        String cMethods = "";
	        String cVariables = "";
	        String additions = ",";
	        
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
            additions += "[" + cshName + "] " + "-^ " + cid.getExtends();
            additions += ",";
        }
        if (cid.getImplements() != null) {
            List<ClassOrInterfaceType> interfaceList = (List<ClassOrInterfaceType>) cid
                    .getImplements();
            for (ClassOrInterfaceType intface : interfaceList) {
                additions += "[" + cshName + "] " + "-.-^ " + "["
                        + "<<interface>>;" + intface + "]";
                additions += ",";
            }
        }
        // Combine cName, cMethods and cVariables
        appends += cName;
        if (!cVariables.isEmpty()) {
            appends += "|" + changeBrackets(cVariables);
        }
        if (!cMethods.isEmpty()) {
            appends += "|" + changeBrackets(cMethods);
        }
        appends += "]";
        appends += additions;
        return appends;
    }

//This method changes the brackets
    private String changeBrackets(String brckts) {
    brckts = brckts.replace("[", "(");
    brckts = brckts.replace("]", ")");
    brckts = brckts.replace("<", "(");
    brckts = brckts.replace(">", ")");
    return brckts;
    }
    //This method changes access modifiers to symbols
    //gets variables access modifiers
    private String symbolModifier(String stringModifier) {
        if(stringModifier.contains("public"))
        	return "+";
        else if(stringModifier.contains("private"))
              return "-";
        else
        	  return "";
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
    // Get the  Methods
    if (bd instanceof ConstructorDeclaration) {
        ConstructorDeclaration cd = ((ConstructorDeclaration) bd);
        if (cd.getDeclarationAsString().startsWith("public")
                && !coi.isInterface()) {
            if (nextParam)
                methods += ";";
            methods += "+ " + cd.getName() + "(";
            for (Object gcn : cd.getChildrenNodes()) {
                if (gcn instanceof Parameter) {
                    Parameter paramCast = (Parameter) gcn;
                    String paramClass = paramCast.getType().toString();
                    String paramName = paramCast.getChildrenNodes()
                            .get(0).toString();
                    methods += paramName + " : " + paramClass;
                    if (map.containsKey(paramClass)
                            && !map.get(classShortName)) {
                        additions += "[" + classShortName
                                + "] uses -.->";
                        if (map.get(paramClass))
                            additions += "[<<interface>>;" + paramClass
                                    + "]";
                        else
                            additions += "[" + paramClass + "]";
                    }
                    additions += ",";
                }
            }
            methods += ")";
            nextParam = true;
        }
    }
}
	
	// Change scope of getter, setters
    if (fieldScope.equals("-")
            && makeFieldPublic.contains(fieldName.toLowerCase())) {
        fieldScope = "+";
    }
    String getDepen = "";
    boolean getDepenMultiple = false;
	
	//@Method to get .java source files from input folder//
    private ArrayList<CompilationUnit> readsrcFdr(String srcFdr)
            throws Exception {
        File folder = new File(srcFdr);
        ArrayList<CompilationUnit> compUnit = new ArrayList<CompilationUnit>();
        for (final File f : folder.listFiles()) {
            if (f.isFile() && f.getName().endsWith(".java")) {
                FileInputStream in = new FileInputStream(f);
                CompilationUnit cu;
                try {
                    cu = JavaParser.parse(in);
                    compUnit.add(cu);
                } finally {
                    in.close();
                }
            }
        }
        return compUnit;
    }
	    
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


	

		
	
	
	


