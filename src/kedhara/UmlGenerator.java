//package kedhara;

import java.io.*;
import java.net.*;
import java.io.IOException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;

public class UmlGenerator {
	public static Boolean generatePNG(String grmr, String oPath) {

        try {
            String yumlLink = "https://yuml.me/diagram/scruffy/class/" + grmr + ".png";
  
            URL url = new URL(yumlLink);
            HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
            ucon.setRequestMethod("GET");
            ucon.setRequestProperty("Accept", "application/json");
    		System.out.println(ucon.getResponseCode());

            if (ucon.getResponseCode() != 200) {
                throw new RuntimeException(
                        "yuml connection failed : Error code is : " + ucon.getResponseCode());
            }
            FileOutputStream os = new FileOutputStream(new File(oPath));
            int rd = 0;
            byte[] bytes = new byte[1024];

            while ((rd = ucon.getInputStream().read(bytes)) != -1) {
                os.write(bytes, 0, rd);
            }
            os.close();
            ucon.disconnect();
        } catch (MalformedURLException uexp) {
            uexp.printStackTrace();
        } catch (IOException uexp) {
            uexp.printStackTrace();
        }
        return null;
    }
}
