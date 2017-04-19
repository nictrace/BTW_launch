package net.launcher.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import net.launcher.components.Frame;
import net.launcher.components.Game;
import net.launcher.run.Settings;

public class GuardUtils {

   public static boolean ret = false;
   public static List<URL> url = new ArrayList<URL>();
   static long filesize = 0L;


   public static ArrayList<String> updateMods(String answer) {
      ret = false;
      ArrayList<String> files = new ArrayList<String>();
      String dir = BaseUtils.getAssetsDir().getAbsolutePath().replace("\\", "/");
      String[] modsArray = answer.split("<br>")[3].split("<::>")[0].split("<:>");
      ArrayList<String> site = new ArrayList<String>();
      ArrayList<String> sit = new ArrayList<String>();
      ArrayList<String> cl = new ArrayList<String>();
      ArrayList<String> client = new ArrayList<String>();
      String[] scn_dirs = answer.split("<::>")[1].split("<:b:>");

      for(int i = 0; i < scn_dirs.length; ++i) {
         cl.addAll(Get.getLibs(new File(dir + File.separator + scn_dirs[i])));
      }

      Iterator<String> var16 = cl.iterator();

      String check;
      while(var16.hasNext()) {
         check = var16.next();
         client.add(check.replace("\\", "/"));
      }

      String[] var17 = modsArray;
      int var18 = modsArray.length;

      for(int file = 0; file < var18; ++file) {
         String add = var17[file];
         site.add(dir + "/" + add);
         sit.add(dir + "/" + add.split(":>")[0] + ":>" + add.split(":>")[1]);
         if(add.contains(BaseUtils.getClientName() + "/" + "bin")) {
            File file1 = new File(dir + File.separator + add.split(":>")[0]);

            try {
               url.add(file1.toURI().toURL());
            } catch (MalformedURLException var15) {
               ;
            }
         }
      }

      var16 = client.iterator();

      while(var16.hasNext()) {
         check = var16.next();
         if(!sit.contains(check) && !check.contains(dir + "/assets/skins/")) {
            File var19 = new File(check.split(":>")[0]);
            System.err.println("Delete -> " + var19);
            delete(var19);
            ret = true;
         }
      }

      var16 = site.iterator();

      while(var16.hasNext()) {
         check = var16.next();
         if(!client.contains(check.split(":>")[0] + ":>" + check.split(":>")[1])) {
            files.add(check.replace(dir, "").split(":>")[0]);
            filesize += Integer.parseInt(check.replace(dir, "").split(":>")[2]);
         }
      }

      return files;
   }

   @SuppressWarnings("deprecation")
public static void checkMods(String answer, boolean action) {
      BaseUtils.sendp("ANTICHEAT: Rechecking jars....");
      updateMods(answer);
      if(ret && action) {
         Frame.main.setError("Ошибка вторичной проверки кеша.");
      } else if(ret && !action) {
         BaseUtils.sendp("ANTICHEAT: Strange mods detected");

         try {
            Class<?> e = Class.forName("java.lang.Shutdown");
            Method m = e.getDeclaredMethod("halt0", new Class[]{Integer.TYPE});
            m.setAccessible(true);
            m.invoke((Object)null, new Object[]{Integer.valueOf(1)});
         } catch (Exception var4) {
            ;
         }

         Game.start.stop();
      } else {
         BaseUtils.sendp("ANTICHEAT: Mod checking done");
      }
   }

   public static void check() {
      if(checkProcesses(Settings.p)) {
         try {
            Class<?> e = Class.forName("java.lang.Shutdown");
            Method m = e.getDeclaredMethod("halt0", new Class[]{Integer.TYPE});
            m.setAccessible(true);
            m.invoke((Object)null, new Object[]{Integer.valueOf(1)});
         } catch (Exception var2) {
            ;
         }
      }

   }

   public static void delete(File file) {
      try {
         if(!file.exists()) {
            return;
         }

         if(file.isDirectory()) {
            File[] e = file.listFiles();
            int var2 = e.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               File f = e[var3];
               delete(f);
            }

            file.delete();
         } else {
            file.delete();
         }
      } catch (Exception var5) {
         ;
      }

   }

   // запретил чистить лог!
   public static void getLogs(File Logs) {
      if(!Logs.exists()) {
         Logs.mkdirs();
      }

      File[] var1 = Logs.listFiles();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         File file = var1[var3];
         if(!file.isDirectory() && file.getName().contains(".log")) {
//            delete(file);
         }
      }

   }

   /**
    * file hashing function
    * @param url file address in URL notation
    * @return 'h' or 'd'
    */
   public static String hash(URL url) {
      if(url == null) {
         return "h";
      } else if(urltofile(url).isDirectory()) {
         return "d";
      } else {
         InputStream IS = null;
         DigestInputStream DI = null;
         BufferedInputStream BS = null;
         Formatter F = null;

         try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            IS = url.openStream();
            BS = new BufferedInputStream(IS);
            DI = new DigestInputStream(BS, e);

            while(DI.read() != -1) {
               ;
            }

            byte[] Md = e.digest();
            F = new Formatter();
            byte[] Mi = Md;
            int I = Md.length;

            for(int str = 0; str < I; ++str) {
               byte Bi = Mi[str];
               F.format("%02x", new Object[]{Byte.valueOf(Bi)});
            }

            String var34 = F.toString();
            String var35 = var34;
            return var35;
         } catch (Exception var32) {
            ;
         } finally {
            try {
               IS.close();
               IS = null;
            } catch (Exception var31) {
               ;
            }

            try {
               DI.close();
               DI = null;
            } catch (Exception var30) {
               ;
            }

            try {
               BS.close();
               BS = null;
            } catch (Exception var29) {
               ;
            }

            try {
               F.close();
               F = null;
            } catch (Exception var28) {
               ;
            }

         }
         return "h";
      }
   }

   public static File urltofile(URL url) {
      try {
         return new File(url.toURI());
      } catch (URISyntaxException var2) {
         return new File(url.getPath().replace("file:/", "").replace("file:", ""));
      }
   }

   public static boolean checkProcesses(String[] onlineData) {
      if(onlineData == null) {
         return false;
      } else {
         try {
            int e = BaseUtils.getPlatform();
            ArrayList<String> processes = new ArrayList<String>();
            Process p;
            if(e == 2) {
               p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe /v /fo list");
            } else {
               p = Runtime.getRuntime().exec("ps -e");
            }

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            Throwable process = null;

            try {
               String line;
               try {
                  while((line = input.readLine()) != null) {
                     processes.add(line.toLowerCase());
                  }
               } catch (Throwable var17) {
                  process = var17;
                  throw var17;
               }
            } finally {
               if(input != null) {
                  if(process != null) {
                     try {
                        input.close();
                     } catch (Throwable var16) {
                        process.addSuppressed(var16);
                     }
                  } else {
                     input.close();
                  }
               }

            }

            Iterator<String> var20 = processes.iterator();

            while(var20.hasNext()) {
               String var21 = var20.next();
               String[] var7 = onlineData;
               int var8 = onlineData.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  String Data = var7[var9];
                  if(var21.contains(Data.toLowerCase())) {
                     return true;
                  }
               }
            }
         } catch (Exception var19) {
            ;
         }

         return false;
      }
   }

}
