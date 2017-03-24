package kedhara;

import java.io.*;
import java.net.*;

public class UmlGenerator {
	public static Boolean generatePNG(String grmr, String oPath) {

        try {
            String webLink = "https://yuml.me/diagram/plain/class/" + grmr
                    + ".png";
            URL url = new URL(webLink);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            if (con.getResponseCode() != 200) {
                throw new RuntimeException(
                        "Connection Failed : Error code is : " + con.getResponseCode());
            }
            OutputStream oStream = new FileOutputStream(new File(oPath));
            int r = 0;
            byte[] bytes = new byte[1024];

            while ((r = con.getInputStream().read(bytes)) != -1) {
                oStream.write(bytes, 0, r);
            }
            oStream.close();
            con.disconnect();
        } catch (MalformedURLException uexp) {
            uexp.printStackTrace();
        } catch (IOException uexp) {
            uexp.printStackTrace();
        }
        return null;
    }
}
