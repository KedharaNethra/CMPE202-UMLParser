package kedhara;

public class Umlparser {

    public static void main(String[] args) throws Exception {
        if (args[0].equals("class")) {
            StartToParse stp = new StartToParse(args[1], args[2]);
            stp.start();
               } else {
            System.out.println("Arguement is invalid " + args[0]);
         //Add code to call Sequence   
        }

    }

}