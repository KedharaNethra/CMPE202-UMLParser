//package kedhara;

public class Umlparser {

    public static void main(String[] args) throws Exception {
        if (args[0].equals("class")) {
        	System.out.println("Choose for the diagram type");
        	System.out.println("Requested for" + args[0] + "Diagram");
            StartToParse sp = new StartToParse(args[1], args[2]);
            sp.dobuildComp();
            System.out.println("Generated Successfully");
        }
 //       else if (args[0].equals(("sequence"))){
 ///           System.out.println("Entered Sequence");
  //           
  //      }
            
        else
        {
            System.out.println("Arguement is invalid " + args[0]);
        }

    }

}