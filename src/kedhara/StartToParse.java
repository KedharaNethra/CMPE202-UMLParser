package kedhara;

import java.io.*;
import java.util.*;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.*;

public class StartToParse {
	final String srcFdr;
	final String outFdr;
	HashMap<String, Boolean> map;
	HashMap<String, String> classconmap;
	String srcyuml;
	ArrayList<CompilationUnit> compUnit;
	//@To get yumlcode//
	//constructor
	
	StartToParse(String srcFdr,String outPath){
	this.srcFdr = srcFdr;
	//@change@ @Important@ change the outfolder path accordingly
	this.outFdr = srcFdr + "\\" + outPath + ".png";
	map = new HashMap<String, Boolean>();
	classconmap = new HashMap<String, String>();
	srcyuml="";
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
	
	public void start() throws Exception {
		compUnit = getCompUnit(srcFdr);
//change needs		buildMap(compUnit);
		for (CompilationUnit comp : compUnit)
//change needs				srcyuml += parser(comp);
		srcyuml += mapParse();
//change needs			srcyuml = yumlCodeUniquer(srcyuml);
        System.out.println("Unique Code: " + srcyuml);
// change needs	       GenerateDiagram.generatePNG(srcyuml, outFdr);
	}
	
	//@Method to get .java source files from input folder//
	private ArrayList<CompilationUnit> getCompUnit(String srcFdr) 
			throws Exception{
	    
		//@Getting filenames of all files in a folder
	 File folder = new File(srcFdr);
	 ArrayList<CompilationUnit> compUnit = new ArrayList<CompilationUnit>();
	 //File[] listOfFiles = folder.listFiles();

	  //  for (int i = 0; i < listOfFiles.length; i++) {
	   //   if (listOfFiles[i].isFile()) {
	    //    System.out.println("File " + listOfFiles[i].getName());
	    //  } else if (listOfFiles[i].isDirectory()) {
	    //    System.out.println("Directory " + listOfFiles[i].getName());@@//
	 
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


	

		
	
	
	


