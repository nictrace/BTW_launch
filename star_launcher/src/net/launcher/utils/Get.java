package net.launcher.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import net.launcher.utils.GuardUtils;

public class Get {

   static List<String> getLibs(File libsfolder) {
      ArrayList<String> libs = new ArrayList<String>();
      if(!libsfolder.exists()) {
         libsfolder.mkdirs();
      }

      File[] var2 = libsfolder.listFiles();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         File file = var2[var4];
         if(file.isDirectory()) {
            libs.addAll(getLibs(file));
         } else {
            try {
               libs.add(file.getAbsolutePath() + ":>" + GuardUtils.hash(file.toURI().toURL()));
            } catch (MalformedURLException var7) {
               var7.printStackTrace();
            }
         }
      }

      return libs;
   }
}
