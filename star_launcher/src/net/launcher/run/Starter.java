package net.launcher.run;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import net.launcher.components.Frame;
import net.launcher.run.Mainclass;
import net.launcher.run.Settings;
import net.launcher.utils.BaseUtils;
import net.launcher.utils.ProcessUtils;

public class Starter {

   public static void main(String[] args) throws Exception {
      try {
         String e = Starter.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
         int x1 = BaseUtils.getPropertyInt("memory", Settings.defaultmemory);
         ArrayList<String> m1 = new ArrayList<String>();
         m1.add(System.getProperty("java.home") + "/bin/java");
         if(System.getProperty("sun.arch.data.model").equals("32") && x1 > 1024) {
            x1 = 1024;
         }

         m1.add("-Xmx" + x1 + "m");
         //m1.add("-XX:MaxPermSize=128m"); - для java8 не актуально
         m1.add("-XX:+DisableAttachMechanism");
         if(System.getProperty("os.name").toLowerCase().startsWith("mac")) {
            m1.add("-Xdock:name=Minecraft");
            m1.add("-Xdock:icon=" + BaseUtils.getAssetsDir().toString() + "/favicon.png");
         }

         m1.add("-classpath");
         m1.add(e);
         m1.add(Mainclass.class.getCanonicalName());
         m1.add("true");
         ProcessBuilder pb = new ProcessBuilder(m1);
         pb.directory(new File(BaseUtils.getAssetsDir().toString()));
         Process process = pb.start();
         if(process == null) {
            throw new Exception("Launcher can\'t be started!");
         }

         (new ProcessUtils(process)).print();
      } catch (Exception var7) {
         JOptionPane.showMessageDialog(Frame.main, var7, "Ошибка запуска", 0, (Icon)null);

         try {
            Class<?> x = Class.forName("java.lang.Shutdown");
            Method m = x.getDeclaredMethod("halt0", new Class[]{Integer.TYPE});
            m.setAccessible(true);
            m.invoke((Object)null, new Object[]{Integer.valueOf(1)});
            
         } catch (Exception var6) {
            ;
         }
      }

   }
}
