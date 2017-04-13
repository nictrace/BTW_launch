package net.launcher.utils.java;

import java.nio.ByteBuffer;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.util.HashMap;
import sun.security.util.Debug;

public class eSecureClassLoader extends ClassLoader {

   private final boolean initialized;
   private final HashMap<CodeSource, ProtectionDomain> pdcache = new HashMap<CodeSource, ProtectionDomain>(11);
   private static final Debug debug = Debug.getInstance("scl");


   protected eSecureClassLoader(ClassLoader parent) {
      super(parent);
      SecurityManager security = System.getSecurityManager();
      if(security != null) {
         security.checkCreateClassLoader();
      }

      this.initialized = true;
   }

   protected eSecureClassLoader() {
      SecurityManager security = System.getSecurityManager();
      if(security != null) {
         security.checkCreateClassLoader();
      }

      this.initialized = true;
   }

   protected final Class<?> defineClass(String name, byte[] b, int off, int len, CodeSource cs) {
      return this.defineClass(name, b, off, len, this.getProtectionDomain(cs));
   }

   protected final Class<?> defineClass(String name, ByteBuffer b, CodeSource cs) {
      return this.defineClass(name, b, this.getProtectionDomain(cs));
   }

   protected PermissionCollection getPermissions(CodeSource codesource) {
      this.check();
      return new Permissions();
   }

   private ProtectionDomain getProtectionDomain(CodeSource cs) {
      if(cs == null) {
         return null;
      } else {
         ProtectionDomain pd = null;
//         HashMap<CodeSource, ProtectionDomain> var3 = this.pdcache;
         synchronized(this.pdcache) {
            pd = (ProtectionDomain)this.pdcache.get(cs);
            if(pd == null) {
               PermissionCollection perms = this.getPermissions(cs);
               pd = new ProtectionDomain(cs, perms, this, (Principal[])null);
               this.pdcache.put(cs, pd);
               if(debug != null) {
                  debug.println(" getPermissions " + pd);
                  debug.println("");
               }
            }

            return pd;
         }
      }
   }

   private void check() {
      if(!this.initialized) {
         throw new SecurityException("ClassLoader object not initialized");
      }
   }

   static {
      ClassLoader.registerAsParallelCapable();
   }
}
