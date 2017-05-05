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
 //@@Define Necessary maps and List's needed@@//
    TreeMap<String, String> doConnection;
    ArrayList<CompilationUnit> compUnit;
    Map<String,List> doCIDictionary = new HashMap<String,List>();
    List classList = new ArrayList<String>();
    List interfaceList = new ArrayList<String>();
    StartToParse(String srcFdr, String oPath) {
        this.srcFdr = srcFdr;
      //@change@ @Important@ change the outfolder path accordingly
        this.outFdr = srcFdr + "/" + oPath + ".png";
        System.out.println("check path" + outFdr);
//        doConnection = new HashMap<String, String>();
        doConnection = new TreeMap<String, String>();
        srcyuml = "";
    }

    public void dobuildComp() throws Exception {
        compUnit = readsrcFdr(srcFdr);
        addToList(compUnit);
        System.out.println(doCIDictionary);
        
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
        Set<String> keys = doConnection.keySet(); // get all keys
        for (String i : keys) {
            String[] classes = i.split("-");
            //if (doMap.get(classes[0]))
            if(doCIDictionary.get("interface").contains(classes[0]))
                appends += "[<<interface>>;" + classes[0] + "]";
            else
                appends += "[" + classes[0] + "]";
            appends += doConnection.get(i); // Add connection
            //if (doMap.get(classes[1]))
            if(doCIDictionary.get("interface").contains(classes[1]))
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
        //lets check as there is no nested type classes.
        Node n1 = ltd.get(0); 

        // Get cName
        ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) n1;
        if (cid.isInterface()) {
            cName = "[" + "<<interface>>;";
        } else {
            cName = "[";
        }
        cName += cid.getName();
        cshName = cid.getName();

        // Parsing cMethods
        boolean nextParam = false;
        for (BodyDeclaration bod : ((TypeDeclaration) n1).getMembers()) {
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
                            //if (doMap.containsKey(paramClass)
                            if((doCIDictionary.get("class").contains(paramClass) ||
                            		doCIDictionary.get("interface").contains(paramClass))
                            	&& doCIDictionary.get("class").contains(cshName)) {
                                additions += "[" + cshName
                                        + "] uses -.->";
                                //if (doMap.get(paramClass))
                                if(doCIDictionary.get("interface").contains(paramClass))
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
        for (BodyDeclaration bod : ((TypeDeclaration) n1).getMembers()) {
            if (bod instanceof MethodDeclaration) {
                MethodDeclaration md = ((MethodDeclaration) bod);
                // Get only public cMethods
                if (md.getDeclarationAsString().startsWith("public")
                        && !cid.isInterface()) {
                    // Identify Setters and Getters
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
                               // if (doMap.containsKey(paramClass)
                                 //       && !doMap.get(cshName)) {
                                if(doCIDictionary.get("interface").contains(paramClass) &&
                                		doCIDictionary.get("class").contains(cshName)){
                                    additions += "[" + cshName
                                            + "] uses -.->";
                                    //if (doMap.get(paramClass))
                                    if(doCIDictionary.get("interface").contains(paramClass))
                                        additions += "[<<interface>>;"
                                                + paramClass + "]";
                                    else
                                        additions += "[" + paramClass + "]";
                                }
                                additions += ",";
                            } else {
                                String methodBody[] = gcn.toString().split(" ");
                                for (String brckts : methodBody) {
                                    //if (doMap.containsKey(brckts)
                                	if ((doCIDictionary.get("class").contains(brckts) ||
                                			doCIDictionary.get("interface").contains(brckts))
                                            && doCIDictionary.get("class").contains(cshName)) {
                                        additions += "[" + cshName
                                                + "] uses -.->";
                                        //if (doMap.get(brckts))
                                        	if(doCIDictionary.get("interface").contains(brckts))
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
        for (BodyDeclaration bod : ((TypeDeclaration) n1).getMembers()) {
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
                //else if (doMap.containsKey(fieldClass)) {
                else if(doCIDictionary.get("interface").contains(fieldClass) || doCIDictionary.get("class").contains(fieldClass)){
                    getDepen = fieldClass;
                }
                
                if (getDepen.length() > 0 && (doCIDictionary.get("interface").contains(getDepen) || doCIDictionary.get("class").contains(getDepen))) {
                //if (getDepen.length() > 0 && doMap.containsKey(getDepen)) {
                    String connection = "-";

                    if (doConnection
                            .containsKey(getDepen + "-" + cshName)) {
                        connection = doConnection
                                .get(getDepen + "-" + cshName);
                        if (getDepenMultiple)
                            connection = "*" + connection;
                        doConnection.put(getDepen + "-" + cshName,
                                connection);
                    } else {
                        if (getDepenMultiple)
                            connection += "*";
                        doConnection.put(cshName + "-" + getDepen,
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
            List<ClassOrInterfaceType> interfaceList = (List<ClassOrInterfaceType>) cid.getImplements();
            for (ClassOrInterfaceType cit : interfaceList) {
                additions += "[" + cshName + "] " + "-.-^ " + "["
                        + "<<interface>>;" + cit + "]";
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
    

    private void addToList(ArrayList<CompilationUnit> compUnit) {
        for (CompilationUnit cu : compUnit) {
            List<TypeDeclaration> cl = cu.getTypes();
            for (Node e : cl) {
                ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) e;
                                // false is class,
               // true is interface
                if(cid.isInterface())
                {
                	interfaceList.add(cid.getName());
                }
                else{
                	classList.add(cid.getName());
                }
                
                doCIDictionary.put("class",classList);
                doCIDictionary.put("interface", interfaceList);
            }
        }
    }

}


