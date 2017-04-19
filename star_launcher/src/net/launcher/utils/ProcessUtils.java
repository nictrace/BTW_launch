package net.launcher.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
//import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import net.launcher.utils.BaseUtils;

public class ProcessUtils {

   private Process process = null;


   public ProcessUtils(Process process) {
      this.process = process;
   }

   public void print() {
      Thread errorThread = new Thread() {
         public void run() {
            ProcessUtils.this.print(true);
         }
      };
      errorThread.start();
      this.print(false);
   }

   private void print(boolean isErrorStream) {
	   String fname;
	   
      try {
         InputStream e;
         if(isErrorStream) {
            e = this.process.getErrorStream();
         } else {
            e = this.process.getInputStream();
         }

         InputStreamReader reader = new InputStreamReader(e, System.getProperty("file.encoding"));
         BufferedReader buf = new BufferedReader(reader);
         String line = null;
         
         if(isErrorStream){ fname = "error.log"; }
         else { fname = "main.log"; }
         
         File b = new File(BaseUtils.getAssetsDir(), "logs" + File.separator + fname);
         if(b.exists()||b.length() > 0){
        	 b.renameTo(new File(BaseUtils.getAssetsDir(), "logs" + File.separator + fname + ".1"));
        	 b = new File(BaseUtils.getAssetsDir(), "logs" + File.separator + fname);
        	 b.createNewFile();
         }

         try (FileOutputStream fos = new FileOutputStream(b)) {
        	 Writer w = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
        	 String crlf = System.getProperty("line.separator");
         while(this.isRunning()) {
            try {
               while((line = buf.readLine()) != null) {
                  if(isErrorStream) {
                     BaseUtils.sendErrp(line);
//                	  w.write(line + crlf);
                  } else {
//                     BaseUtils.sendp(line);
                	  w.write(line + crlf);
                  }
               }
            } catch (IOException var16) {
               ;
            } finally {
               try {
            	   w.flush();
            	   w.close();
            	   buf.close();
            	   fos.close();                  
               } catch (IOException var15) {
                  ;
               }

            }
         }
        	 
     }
     catch (IOException ef){}

      } catch (IOException var18) {
         BaseUtils.sendErr("Не удалось установить кодировку при выводе сообщений об отладке");
         var18.printStackTrace();
      }

   }

   public boolean isRunning() {
      try {
         this.process.exitValue();
      } catch (IllegalThreadStateException var2) {
         return true;
      }

      System.exit(0);
      return false;
   }
}
