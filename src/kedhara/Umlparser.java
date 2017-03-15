package kedhara;

public class Umlparser {

    public static void main(String[] args) throws Exception {
        if (args[0].equals("class")) {
            JavaParserEngine jpe = new JavaParserEnginee(args[1], args[2]);
            jpe.start();
               } else {
            System.out.println("Arguement is invalid " + args[0]);
        }

    }

}