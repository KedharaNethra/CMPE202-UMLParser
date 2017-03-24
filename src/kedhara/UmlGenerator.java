package kedhara;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;

public class UmlGenerator {
	public static Boolean generatePNG(String grmr, String oPath) {

        try {
            String webLink = "https://yuml.me/diagram/plain/class/" + grmr
                    + ".png";
            URL url = new URL(webLink);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException(
                        "Connection Failed : Error code is : " + conn.getResponseCode());
            }
            OutputStream outputStream = new FileOutputStream(new File(oPath));
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = conn.getInputStream().read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
