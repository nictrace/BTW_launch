package net.launcher.utils.java;

import java.net.URL;
import java.security.AccessControlContext;
import net.launcher.utils.java.eURLClassLoader;

final class FactoryURLClassLoader extends eURLClassLoader {

   FactoryURLClassLoader(URL[] urls, ClassLoader parent, AccessControlContext acc) {
      super(urls, parent, acc);
   }

   FactoryURLClassLoader(URL[] urls, AccessControlContext acc) {
      super(urls, acc);
   }

   @Override
   public final Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      SecurityManager sm = System.getSecurityManager();
      if(sm != null) {
         int i = name.lastIndexOf(46);
         if(i != -1) {
            sm.checkPackageAccess(name.substring(0, i));
         }
      }

      return super.loadClass(name, resolve);
   }

   static {
      ClassLoader.registerAsParallelCapable();
   }
}
