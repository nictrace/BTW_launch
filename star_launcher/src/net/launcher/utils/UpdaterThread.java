package net.launcher.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
//import java.util.List;
import net.launcher.components.Game;
import net.launcher.utils.BaseUtils;
import net.launcher.utils.GuardUtils;
import net.launcher.utils.PostUtils;
import net.launcher.utils.ZipUtils;

public class UpdaterThread extends Thread {

   public int procents = 0;
   public long totalsize = 0L;
   public long currentsize = 0L;
   public String currentfile = "...";
   public int downloadspeed = 0;
   public ArrayList<String> files;
   public String state = "...";
   public boolean error = false;
   public boolean zipupdate = false;
   public boolean asupdate = false;
   public String answer;
   String boundary = PostUtils.randomString() + PostUtils.randomString() + PostUtils.randomString();

   public UpdaterThread(ArrayList<String> files, boolean zipupdate, boolean asupdate, String answer) {
      this.files = files;
      this.zipupdate = zipupdate;
      this.asupdate = asupdate;
      this.answer = answer;
   }

   public void run() {
      try {
         String e = BaseUtils.getAssetsDir().getAbsolutePath();
         String urlTo = BaseUtils.buildUrl("clients");
         File dir = new File(e);
         if(!dir.exists()) {
            dir.mkdirs();
         }

         this.totalsize = GuardUtils.filesize;
         this.state = "Закачка файлов...";
         byte[] buffer = new byte[65536];

         String file;
         for(int path = 0; path < this.files.size(); ++path) {
            this.currentfile = (String)this.files.get(path);
            file = this.currentfile.replace(" ", "%20");
            BaseUtils.send("Downloading file: " + this.currentfile);

            try {
               dir = new File(e + "/" + this.currentfile.substring(0, this.currentfile.lastIndexOf("/")));
            } catch (Exception var18) {
               ;
            }

            if(!dir.exists()) {
               dir.mkdirs();
            }

            HttpURLConnection ct = null;
            URL url = new URL(urlTo + file);
            ct = (HttpURLConnection)url.openConnection();
            ct.setRequestMethod("GET");
            ct.setRequestProperty("User-Agent", "Launcher/64.0");
            ct.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
            ct.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            ct.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + this.boundary);
            BufferedInputStream is = new BufferedInputStream(ct.getInputStream());
            FileOutputStream fos = new FileOutputStream(e + "/" + this.currentfile);
            long downloadStartTime = System.currentTimeMillis();
            int downloadedAmount = 0;
//            boolean bs = false;
            MessageDigest m = MessageDigest.getInstance("MD5");

            int var21;
            while((var21 = is.read(buffer, 0, buffer.length)) != -1) {
               fos.write(buffer, 0, var21);
               m.update(buffer, 0, var21);
               this.currentsize += (long)var21;
               this.procents = (int)(this.currentsize * 100L / this.totalsize);
               downloadedAmount += var21;
               long timeLapse = System.currentTimeMillis() - downloadStartTime;
               if(timeLapse >= 1000L) {
                  this.downloadspeed = (int)((float)((int)((float)downloadedAmount / (float)timeLapse * 100.0F)) / 100.0F);
                  downloadedAmount = 0;
                  downloadStartTime += 1000L;
               }
            }

            is.close();
            fos.close();
            BaseUtils.send("File downloaded: " + this.currentfile);
         }

         this.state = "Закачка завершена";
         String var20;
         if(this.zipupdate) {
            var20 = BaseUtils.getMcDir().getAbsolutePath() + File.separator;
            file = var20 + "config.zip";
            BaseUtils.setProperty(BaseUtils.getClientName() + "_zipmd5", GuardUtils.hash((new File(file)).toURI().toURL()));
            ZipUtils.unzip(var20, file);
         }

         if(this.asupdate) {
            var20 = BaseUtils.getAssetsDir().getAbsolutePath() + File.separator;
            file = var20 + "assets.zip";
            BaseUtils.setProperty("assets_aspmd5", GuardUtils.hash((new File(file)).toURI().toURL()));
            ZipUtils.unzip(var20, file);
         }

         new Game(this.answer);
      } catch (Exception var19) {
         var19.printStackTrace();
         this.state = var19.toString();
         this.error = true;
      }
   }
}
