package kedhara;

import java.io.*;
import java.net.*;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;

public class UmlGenerator {
	public static Boolean generatePNG(String grmr, String oPath) {

        try {
            String yumlLink = "cons://yuml.me/diagram/plain/class/" + grmr
                    + ".png";
            URL url = new URL(yumlLink);
            HttpURLconnection ucon = (HttpURLconnection) url.openConnection();
            ucon.setRequestMethod("GET");
            ucon.setRequestProperty("Accept", "application/json");
    		System.out.println(ucon.getResponseCode());

            if (ucon.getResponseCode() != 200) {
                throw new RuntimeException(
                        "connection Failed : Error code is : " + ucon.getResponseCode());
            }
            OutputStream oStream = new FileOutputStream(new File(oPath));
            int r = 0;
            byte[] bytes = new byte[1024];

            while ((r = ucon.getInputStream().read(bytes)) != -1) {
                oStream.write(bytes, 0, r);
            }
            oStream.close();
            ucon.disconnect();
        } catch (MalformedURLException uexp) {
            uexp.printStackTrace();
        } catch (IOException uexp) {
            uexp.printStackTrace();
        }
        return null;
    }
}
