//package kedhara;

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
    HashMap<String, String> classconmap;
    ArrayList<CompilationUnit> compUnit;
    Map<String,List> classInterfaceDictionary = new HashMap<String,List>();
    List classList = new ArrayList<String>();
    List interfaceList = new ArrayList<String>();
    StartToParse(String srcFdr, String oPath) {
        this.srcFdr = srcFdr;
      //@change@ @Important@ change the outfolder path accordingly
     //   String oPath = "C:\\Users\\tneth\\Desktop\\Check\\New"+oPath+".png";
       this.outFdr = srcFdr + "\\" + oPath + ".png";
        map = new HashMap<String, Boolean>();
        classconmap = new HashMap<String, String>();
        srcyuml = "";
    }

    public void start() throws Exception {
        compUnit = readsrcFdr(srcFdr);
        buildMap(compUnit);
        System.out.println(classInterfaceDictionary);
        
        for (CompilationUnit cu : compUnit)
            srcyuml += parser(cu);
        srcyuml += parseAdditions();
        srcyuml = srcSep(srcyuml);
        System.out.println("Print srcyuml unique code: " + srcyuml);
        UmlGenerator.generatePNG(srcyuml, outFdr); //UmlGenerator to generate the diagram
    }

    private String srcSep(String src) {
        String[] spSrc = src.split(",");
        String[] spUnq = new LinkedHashSet<String>(
                Arrays.asList(spSrc)).toArray(new String[0]);
        String appends = String.join(",", spUnq);
        return appends;
    }
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
    private String parseAdditions() {
        String appends = "";
        Set<String> keys = classconmap.keySet(); // get all keys
        for (String i : keys) {
            String[] classes = i.split("-");
            if(classInterfaceDictionary.get("interface").contains(classes[0]))
                appends += "[<<interface>>;" + classes[0] + "]";
            else
                appends += "[" + classes[0] + "]";
            appends += classconmap.get(i); // Add connection
            if(classInterfaceDictionary.get("interface").contains(classes[1]))
                appends += "[<<interface>>;" + classes[1] + "]";
            else
                appends += "[" + classes[1] + "]";
            appends += ",";
        }
        return appends;
    }

    private String parser(CompilationUnit cu) {
        String cName = "";
        String cshName = "";
        String cMethods = "";
        String cVariables = "";
        String additions = ",";
        


        ArrayList<String> makeFieldPublic = new ArrayList<String>();
        List<TypeDeclaration> ltd = cu.getTypes();
        Node node = ltd.get(0); // assuming no nested classes

        // Get cName
        ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) node;
        if (cid.isInterface()) {
            cName = "[" + "<<interface>>;";
        } else {
            cName = "[";
        }
        cName += cid.getName();
        cshName = cid.getName();

        // Parsing cMethods
        boolean nextParam = false;
        for (BodyDeclaration bod : ((TypeDeclaration) node).getMembers()) {
            // Get cMethods
            if (bod instanceof ConstructorDeclaration) {
                ConstructorDeclaration cd = ((ConstructorDeclaration) bod);
                if (cd.getDeclarationAsString().startsWith("public")
                        && !cid.isInterface()) {
                    if (nextParam)
                        cMethods += ";";
                    cMethods += "+ " + cd.getName() + "(";
                    for (Object gcn : cd.getChildrenNodes()) {
                        if (gcn instanceof Parameter) {
                            Parameter paramCast = (Parameter) gcn;
                            String paramClass = paramCast.getType().toString();
                            String paramName = paramCast.getChildrenNodes()
                                    .get(0).toString();
                            cMethods += paramName + " : " + paramClass;
                         if((classInterfaceDictionary.get("class").contains(paramClass) ||
                            		classInterfaceDictionary.get("interface").contains(paramClass))
                            	&& classInterfaceDictionary.get("class").contains(cshName)) {
                                additions += "[" + cshName
                                        + "] uses -.->";
                                     if(classInterfaceDictionary.get("interface").contains(paramClass))
                                    additions += "[<<interface>>;" + paramClass
                                            + "]";
                                else
                                    additions += "[" + paramClass + "]";
                            }
                            additions += ",";
                        }
                    }
                    cMethods += ")";
                    nextParam = true;
                }
            }
        }
        for (BodyDeclaration bod : ((TypeDeclaration) node).getMembers()) {
            if (bod instanceof MethodDeclaration) {
                MethodDeclaration md = ((MethodDeclaration) bod);
                // To get only public cMethods
                if (md.getDeclarationAsString().startsWith("public")
                        && !cid.isInterface()) {
                    // 	Find Setters and Getters
                    if (md.getName().startsWith("set")
                            || md.getName().startsWith("get")) {
                        String varName = md.getName().substring(3);
                        makeFieldPublic.add(varName.toLowerCase());
                    } else {
                        if (nextParam)
                            cMethods += ";";
                        cMethods += "+ " + md.getName() + "(";
                        for (Object gcn : md.getChildrenNodes()) {
                            if (gcn instanceof Parameter) {
                                Parameter paramCast = (Parameter) gcn;
                                String paramClass = paramCast.getType()
                                        .toString();
                                String paramName = paramCast.getChildrenNodes()
                                        .get(0).toString();
                                cMethods += paramName + " : " + paramClass;
                            if(classInterfaceDictionary.get("interface").contains(paramClass) &&
                                		classInterfaceDictionary.get("class").contains(cshName)){
                                    additions += "[" + cshName
                                            + "] uses -.->";
                            if(classInterfaceDictionary.get("interface").contains(paramClass))
                                        additions += "[<<interface>>;"
                                                + paramClass + "]";
                                    else
                                        additions += "[" + paramClass + "]";
                                }
                                additions += ",";
                            } else {
                                String methodBody[] = gcn.toString().split(" ");
                                for (String brckts : methodBody) {
                                	if ((classInterfaceDictionary.get("class").contains(brckts) ||
                                			classInterfaceDictionary.get("interface").contains(brckts))
                                            && classInterfaceDictionary.get("class").contains(cshName)) {
                                        additions += "[" + cshName
                                                + "] uses -.->";
                                      	if(classInterfaceDictionary.get("interface").contains(brckts))
                                            additions += "[<<interface>>;" + brckts
                                                    + "]";
                                        else
                                            additions += "[" + brckts + "]";
                                        additions += ",";
                                    }
                                }
                            }
                        }
                        cMethods += ") : " + md.getType();
                        nextParam = true;
                    }
                }
            }
        }
        // Parsing cVariables
        boolean nextField = false;
        for (BodyDeclaration bod : ((TypeDeclaration) node).getMembers()) {
            if (bod instanceof FieldDeclaration) {
                FieldDeclaration fd = ((FieldDeclaration) bod);
                String cVariablescope = symbolModifier(
                        bod.toStringWithoutComments().substring(0,
                                bod.toStringWithoutComments().indexOf(" ")));
                String fieldClass = changeBrackets(fd.getType().toString());
                String fieldName = fd.getChildrenNodes().get(1).toString();
                if (fieldName.contains("="))
                    fieldName = fd.getChildrenNodes().get(1).toString()
                            .substring(0, fd.getChildrenNodes().get(1)
                                    .toString().indexOf("=") - 1);
                // Change scope of getter, setters
                if (cVariablescope.equals("-")
                        && makeFieldPublic.contains(fieldName.toLowerCase())) {
                    cVariablescope = "+";
                }
                String getDepen = "";
                boolean getDepenMultiple = false;
                if (fieldClass.contains("(")) {
                    getDepen = fieldClass.substring(fieldClass.indexOf("(") + 1,
                            fieldClass.indexOf(")"));
                    getDepenMultiple = true;
                }
                else if(classInterfaceDictionary.get("interface").contains(fieldClass)){
                    getDepen = fieldClass;
                }
                
                if (getDepen.length() > 0 && classInterfaceDictionary.get("interface").contains(getDepen)) {
                    String connection = "-";
                    if (classconmap
                            .containsKey(getDepen + "-" + cshName)) {
                        connection = classconmap
                                .get(getDepen + "-" + cshName);
                        if (getDepenMultiple)
                            connection = "*" + connection;
                        classconmap.put(getDepen + "-" + cshName,
                                connection);
                    } else {
                        if (getDepenMultiple)
                            connection += "*";
                        classconmap.put(cshName + "-" + getDepen,
                                connection);
                    }
                }
                if (cVariablescope == "+" || cVariablescope == "-") {
                    if (nextField)
                        cVariables += "; ";
                    cVariables += cVariablescope + " " + fieldName + " : " + fieldClass;
                    nextField = true;
                }
            }

        }
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
    
    // This method changes the brackets
    private String changeBrackets(String brckts) {
        brckts = brckts.replace("[", "(");
        brckts = brckts.replace("]", ")");
        brckts = brckts.replace("<", "(");
        brckts = brckts.replace(">", ")");
        return brckts;
    }

    //This method gets variable's AccessModifier and changes them to symbol
    private String symbolModifier(String stringModifier) {
        if(stringModifier.contains("public"))
        	return "+";
        else if(stringModifier.contains("private"))
              return "-";
        else
        	  return "";
    }
    

    private void buildMap(ArrayList<CompilationUnit> compUnit) {
        for (CompilationUnit cu : compUnit) {
            List<TypeDeclaration> cl = cu.getTypes();
            for (Node n : cl) {
                ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) n;
                // false is class,
               // true is interface
                if(cid.isInterface())
                {
                	interfaceList.add(cid.getName());
                }
                else{
                	classList.add(cid.getName());
                }
                
                classInterfaceDictionary.put("class",classList);
                classInterfaceDictionary.put("interface", interfaceList);
            }
        }
    }

}