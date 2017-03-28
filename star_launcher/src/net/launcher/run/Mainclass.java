package net.launcher.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.launcher.MusPlay;
import net.launcher.components.Frame;
import net.launcher.utils.BaseUtils;

public class Mainclass {

   public static void main(String[] args) throws Exception {
      boolean test = (args.length != 0) ? args[0].equals("true") : false;
      if(test) {
         File dir = new File(BaseUtils.getAssetsDir().toString());
         if(!dir.exists()) {
            dir.mkdirs();
         }

         InputStream stream = Starter.class.getResourceAsStream("/net/launcher/theme/favicon.png");
         if(stream != null) {
        	 FileOutputStream resStreamOut = null;
        	 byte[] buffer = new byte[4096];

        	 try {
        		 resStreamOut = new FileOutputStream(new File(BaseUtils.getAssetsDir().toString() + "/favicon.png"));

        		 int readBytes;
        		 while((readBytes = stream.read(buffer)) > 0) {
        			 resStreamOut.write(buffer, 0, readBytes);
        		 }
        	 } catch (IOException var11) {
        		 var11.printStackTrace();
        	 } finally {
        		 stream.close();
        		 resStreamOut.close();
        	 }
         }
         
         Frame.start();
         if(BaseUtils.getPropertyBoolean("Music", true)) {
        	 new MusPlay(Settings.iMusicname);
         }
      } else {
         Starter.main((String[])null);
      }
   }
}
