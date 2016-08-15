package net.launcher.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

         while(this.isRunning()) {
            try {
               while((line = buf.readLine()) != null) {
                  if(isErrorStream) {
                     BaseUtils.sendErrp(line);
                  } else {
                     BaseUtils.sendp(line);
                  }
               }
            } catch (IOException var16) {
               ;
            } finally {
               try {
                  buf.close();
               } catch (IOException var15) {
                  ;
               }

            }
         }
      } catch (UnsupportedEncodingException var18) {
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
