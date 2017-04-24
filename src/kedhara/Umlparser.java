//package kedhara;

public class Umlparser {

    public static void main(String[] args) throws Exception {
        if (args[0].equals("class")) {
        	System.out.println("Generate class");
            StartToParse sp = new StartToParse(args[1], args[2]);
            sp.start();
        }
 //       else if (args[0].equals(("sequence"))){
 ///           System.out.println("Entered Sequence");
  //      	ParseSeqCode  seq = new ParseSeqCode(args[1],args[2],args[3],args[4]);
  //          seq.start();
  //      }
            
        else
        {
            System.out.println("Arguement is invalid " + args[0]);
        }

    }

}