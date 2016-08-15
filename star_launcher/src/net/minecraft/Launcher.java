package net.minecraft;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.launcher.run.Settings;
import net.launcher.utils.BaseUtils;
import net.launcher.utils.java.eURLClassLoader;

public class Launcher extends Applet implements AppletStub {

   private static final long serialVersionUID = 1L;
   private Applet mcApplet = null;
   public Map<String, Object> customParameters = new HashMap<String, Object>();
   private int context = 0;
   private boolean active = false;
   private URL[] urls;
   private String bin;


   public Launcher(String bin, URL[] urls) {
      this.bin = bin;
      this.urls = urls;
   }

   public void init()
   {
      if(this.mcApplet != null)
      {
         this.mcApplet.init();
         return;
      }

      
      new Runnable()
      {
            public void run()
            {
               Settings.onStartMinecraft();
            }
      }.run();

      eURLClassLoader cl = new eURLClassLoader(this.urls);
      System.setProperty("org.lwjgl.librarypath", this.bin + "natives");
      System.setProperty("net.java.games.input.librarypath", this.bin + "natives");
      try 
      {
            BaseUtils.patchDir(cl);
            Class<?> e = cl.loadClass("net.minecraft.client.MinecraftApplet");
            System.setProperty("minecraft.applet.WrapperClass", Launcher.class.getName());
            Applet applet = (Applet)e.newInstance();
            this.mcApplet = applet;
            applet.setStub(this);
            applet.setSize(this.getWidth(), this.getHeight());
            this.setLayout(new BorderLayout());
            this.add(applet, "Center");
            applet.init();
            this.active = true;
            this.validate();
	  } catch (Exception e)
      {
            e.printStackTrace();
      }
   }
   
   public String getParameter(String name) 
   {
      String custom = (String)this.customParameters.get(name);
      if(custom != null) return custom;
      try
      {
    	  return super.getParameter(name);
      } catch (Exception e)
      {
    	  this.customParameters.put(name, (Object)null);
      }
      return null;
   }

   public void start() {
      if(this.mcApplet != null) 
      {
         BaseUtils.send("Redirecting to Minecraft...");
         BaseUtils.send("--------------------------------");
         this.mcApplet.start();
         return;
      }
   }

   public boolean isActive() {
      if(this.context == 0) 
      {
         this.context = -1;
         try 
         {
            if(this.getAppletContext() != null) 
               this.context = 1;
         } catch (Exception var2) { }
      }
      return this.context == -1 ? this.active : super.isActive();
   }

   public URL getDocumentBase() {
      try {
         return new URL("http://www.minecraft.net/game/");
      } catch (MalformedURLException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public void stop() {
      if(this.mcApplet != null) {
         this.active = false;
         this.mcApplet.stop();
      }
   }

   public void destroy() {
      if(this.mcApplet != null) {
         this.mcApplet.destroy();
      }
   }

   public void appletResize(int w, int h) {}

   public void replace(Applet applet) {
      this.mcApplet = applet;
      applet.setStub(this);
      applet.setSize(this.getWidth(), this.getHeight());
      this.setLayout(new BorderLayout());
      this.add(applet, "Center");
      applet.init();
      this.active = true;
      applet.start();
      this.validate();
   }
}
