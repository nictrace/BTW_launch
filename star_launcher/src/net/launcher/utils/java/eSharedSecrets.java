package net.launcher.utils.java;

import net.launcher.utils.java.eJavaNetAccess;
import sun.misc.SharedSecrets;

public class eSharedSecrets extends SharedSecrets {

   private static eJavaNetAccess javaNetAccess;


   public static void seteJavaNetAccess(eJavaNetAccess jna) {
      javaNetAccess = jna;
   }

   public static eJavaNetAccess geteJavaNetAccess() {
      return javaNetAccess;
   }
}
