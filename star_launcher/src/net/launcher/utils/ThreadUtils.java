package net.launcher.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

//import java.io.PrintStream;
//import java.net.URI;
//import java.net.URL;
//import java.security.CodeSource;
//import java.security.ProtectionDomain;
//import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import net.y;
//import javax.swing.JTextPane;
//import net.launcher.components.Button;
//import net.launcher.components.Checkbox;
//import net.launcher.components.Combobox;
import net.launcher.components.Frame;
//import net.launcher.components.Panel;
//import net.launcher.components.Passfield;
import net.launcher.components.PersonalContainer;
//import net.launcher.components.Serverbar;
//import net.launcher.components.Textfield;
import net.launcher.run.Settings;
import net.launcher.theme.Message;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ThreadUtils
{
  public static UpdaterThread updaterThread;
  public static Thread serverPollThread;
  static String d = y.e() + "1234";
  
  public ThreadUtils() {}
  
  public static void updateNewsPage(final String url)
  {
    BaseUtils.send("Updating news page...");
    if (!BaseUtils.getPropertyBoolean("loadnews", true))
    {
      Frame.main.browser.setText("<center><font color=\"#F0F0F0\" style=\"font-family:Tahoma\">Загрузка новостей не включена</font></center>");
      return;
    }
    Frame.main.browser.setText("<center><font color=\"#F0F0F0\" style=\"font-family:Tahoma\">Обновление страницы...</font></center>");
    Thread t = new Thread()
    {
      public void run()
      {
//    	  String _url = url;
        try
        {
          Frame.main.browser.setPage(url);
          BaseUtils.send("Updating news page sucessful!");
        }
        catch (Exception e)
        {
          Frame.main.browser.setText("<center><font color=\"#FF0000\" style=\"font-family:Tahoma\"><b>Ошибка загрузки новостей:<br>" + e.toString() + "</b></font></center>");
          BaseUtils.send("Updating news page fail! (" + e.toString() + ")");
        }
        interrupt();
      }
    };
    t.setName("Update news thread");
    t.start();
  }
  
  static String token = new String(Frame.password.getPassword());
  
  public static void auth(final boolean personal)
  {
    BaseUtils.send("Logging in, login: " + Frame.login.getText());
    Thread t = new Thread()
    {
      public void run()
      {
        try
        {
          boolean error = false;
          ThreadUtils.token = new String(Frame.password.getPassword());
          if (Frame.token.equals("token")) {
            try
            {
              ThreadUtils.token = ThreadUtils.decrypt(BaseUtils.getPropertyString("password"), ThreadUtils.d);
            }
            catch (Exception e)
            {
              Frame.main.panel.tmpString = Message.Null;
              error = true;
            }
          }
          BaseUtils.sendErr("auth:" + BaseUtils.getClientName() + ":" + Frame.login.getText() + ":" + ThreadUtils.token + ":" + GuardUtils.hash(ThreadUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().toURL()) + ":" + Frame.token);
          String answer2 = BaseUtils.execute(BaseUtils.buildUrl("launcher.php"), new Object[] { "action", 

          encrypt("auth:" + BaseUtils.getClientName() + ":" + Frame.login.getText() + ":" + ThreadUtils.token + ":" + GuardUtils.hash(ThreadUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().toURL()) + ":" + Frame.token, Settings.key1) });
          
//          BaseUtils.send(answer2);
          String answer = null;
          System.err.println();
          try
          {
            answer = ThreadUtils.decrypt(answer2, Settings.key1);
          }
          catch (Exception e)
          {
            if (Settings.release)
            {
              Frame.main.setUpdateComp("???");
              return;
            }
          }
          if (answer == null)
          {
            Frame.main.panel.tmpString = Message.Null;
            error = true;
          }
          else if (answer.length() == 0)
          {
            Frame.main.panel.tmpString = Message.Null;
            BaseUtils.sendErr("Ошибочка в launcher.php или вебсервере");
            BaseUtils.sendErr("Включите вывод ошибок, раскомментируйте строки в connect.php");
            BaseUtils.sendErr("Error_Reporting(E_ALL | E_STRICT);");
            BaseUtils.sendErr("Ini_Set('display_errors', true);");
            error = true;
          }
          else
          {
            if (answer.contains("badlauncher<$>"))
            {
              Frame.main.setUpdateComp(answer.replace("badlauncher<$>_", ""));
              return;
            }
            if (answer.contains("errorLogin<$>"))
            {
              if (Frame.token.equals("token"))
              {
                BaseUtils.setProperty("password", "-");
                Frame.password.setVisible(true);
                Frame.toGame.setVisible(false);
                Frame.toPersonal.setVisible(false);
                Frame.toAuth.setVisible(true);
                Frame.toLogout.setVisible(false);
                Frame.toRegister.setVisible(Settings.useRegister);
                Frame.token = "null";
                Frame.login.setEditable(true);
                Frame.main.panel.repaint();
                Frame.main.panel.tmpString = Message.errorTocen;
              }
              else
              {
                Frame.main.panel.tmpString = Message.errorLogin;
              }
              error = true;
            }
            else if (answer.contains("errorsql<$>"))
            {
              Frame.main.panel.tmpString = Message.errorsql;
              error = true;
            }
            else if (answer.contains("client<$>"))
            {
              Frame.main.panel.tmpString = Message.client.replace("%%", answer.replace("client<$>", "клиент"));
              error = true;
            }
            else if (answer.contains("temp<$>"))
            {
              Frame.main.panel.tmpString = Message.temp;
              error = true;
            }
            else if (answer.contains("badhash<$>"))
            {
              Frame.main.panel.tmpString = Message.badhash;
              error = true;
            }
            else if (answer.split("<br>").length != 4)
            {
              Frame.main.panel.tmpString = answer;
              error = true;
            }
          }
          if (error)
          {
            Frame.main.panel.tmpColor = Color.red;
            try
            {
              sleep(2000L);
            }
            catch (InterruptedException e) {}
            Frame.main.setAuthComp();
          }
          else
          {
            String version = answer.split("<br>")[0].split("<:>")[0];
            if (!version.equals(Settings.masterVersion))
            {
              Frame.main.setUpdateComp(version);
              return;
            }
            BaseUtils.send("Logging in successful");
            if (personal)
            {
              Frame.main.panel.tmpString = Message.tmpDownload;
              String personal = BaseUtils.execute(BaseUtils.buildUrl("launcher.php"), new Object[] { "action", 
              
                ThreadUtils.encrypt("getpersonal:0:" + Frame.login.getText() + ":" + ThreadUtils.token, Settings.key1) });
              if (personal.contains("==")) {
                personal = ThreadUtils.decrypt(personal, Settings.key1);
              }
              if (personal == null)
              {
                Frame.main.panel.tmpString = Message.Null;
                error = true;
              }
              else if (personal.contains("errorLogin"))
              {
                Frame.main.panel.tmpString = Message.errorLogin;
                error = true;
              }
              else if (personal.contains("errorsql"))
              {
                Frame.main.panel.tmpString = Message.errorsql;
                error = true;
              }
              else if (personal.contains("temp"))
              {
                Frame.main.panel.tmpString = Message.temp;
                error = true;
              }
              else if (personal.contains("noactive"))
              {
                Frame.main.panel.tmpString = Message.noactive;
                error = true;
              }
              else if (personal.contains("badhash"))
              {
                Frame.main.panel.tmpString = Message.badhash;
                error = true;
              }
              else if ((personal.split("<:>").length != 13) || (personal.split("<:>")[0].length() != 7))
              {
                Frame.main.panel.tmpString = personal;
                error = true;
              }
              if (error)
              {
                Frame.main.panel.tmpColor = Color.red;
                try
                {
                  sleep(2000L);
                }
                catch (InterruptedException e) {}
                Frame.main.setAuthComp();
                return;
              }
              try
              {
                Frame.main.panel.tmpString = Message.skin;
                BufferedImage skinImage = BaseUtils.getSkinImage(answer.split("<br>")[1].split("<:>")[0]);
                Frame.main.panel.tmpString = Message.cloak;
                BufferedImage cloakImage = BaseUtils.getCloakImage(answer.split("<br>")[1].split("<:>")[0]);
                Frame.main.panel.tmpString = Message.skinImage;
                skinImage = ImageUtils.parseSkin(skinImage);
                Frame.main.panel.tmpString = Message.cloakImage;
                cloakImage = ImageUtils.parseCloak(cloakImage);
                Frame.main.panel.tmpString = "";
                PersonalContainer pc = new PersonalContainer(personal.split("<:>"), skinImage, cloakImage);
                Frame.main.setPersonal(pc);
                
                return;
              }
              catch (Exception e)
              {
                BaseUtils.throwException(e, Frame.main);return;
              }
            }
            BaseUtils.setProperty("password", ThreadUtils.encrypt(answer.split("<br>")[2].split("<br>")[0], ThreadUtils.d).replaceAll("\n|\r\n", ""));
            if (Frame.token.equals("null"))
            {
              Frame.password.setVisible(false);
              Frame.toGame.setVisible(true);
              Frame.toPersonal.setVisible(Settings.usePersonal);
              Frame.toAuth.setVisible(false);
              Frame.toLogout.setVisible(true);
              Frame.toRegister.setVisible(false);
              Frame.token = "token";
              Frame.login.setEditable(false);
              Frame.main.setAuthComp();
              return;
            }
            ThreadUtils.runUpdater(answer);
          }
          interrupt();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    };
    t.setName("Auth thread");
    t.start();
  }
  
  public static void runUpdater(String answer)
  {
    boolean zipupdate = false;
    boolean asupdate = false;
    ArrayList<String> files = GuardUtils.updateMods(answer);
    
    String folder = BaseUtils.getMcDir().getAbsolutePath() + File.separator;
    String asfolder = BaseUtils.getAssetsDir().getAbsolutePath() + File.separator;
    if ((!answer.split("<br>")[0].split("<:>")[2].split("<>")[0].equals(BaseUtils.getPropertyString(BaseUtils.getClientName() + "_zipmd5"))) || 
      (!new File(folder + "config").exists()) || 
      (Frame.main.updatepr.isSelected()))
    {
      GuardUtils.filesize += Integer.parseInt(answer.split("<br>")[0].split("<:>")[2].split("<>")[1]);
      files.add("/" + BaseUtils.getClientName() + "/config.zip");
      zipupdate = true;
    }
    if (!Settings.assetsfolder) {
      if ((!answer.split("<br>")[0].split("<:>")[3].split("<>")[0].equals(BaseUtils.getPropertyString("assets_aspmd5"))) || 
        (!new File(asfolder + "assets").exists()) || 
        (Frame.main.updatepr.isSelected()))
      {
        GuardUtils.filesize += Integer.parseInt(answer.split("<br>")[0].split("<:>")[3].split("<>")[1]);
        files.add("/assets.zip");
        asupdate = true;
      }
    }
    BaseUtils.send("---- Filelist start ----");
    for (Object s : files.toArray()) {
      BaseUtils.send("- " + (String)s);
    }
    BaseUtils.send("---- Filelist end ----");
    BaseUtils.send("Running updater...");
    updaterThread = new UpdaterThread(files, zipupdate, asupdate, answer);
    updaterThread.setName("Updater thread");
    Frame.main.setUpdateState();
    updaterThread.run();
  }
  
  public static void pollSelectedServer()
  {
	  if(serverPollThread != null){
		  try {
			  serverPollThread.interrupt();
			  serverPollThread = null;
		  }
		  catch (Exception e) {
		        BaseUtils.send(e.getMessage());
		  }
	}
    BaseUtils.send("Refreshing server state... (" + Frame.main.servers.getSelected() + ")");
    serverPollThread = new Thread()
    {
      public void run()
      {
        Frame.main.serverbar.updateBar("Обновление...", BaseUtils.genServerIcon(new String[] { null, "0", null }));
        int sindex = Frame.main.servers.getSelectedIndex();
        String ip = Settings.servers[sindex].split(", ")[1];
        int port = BaseUtils.parseInt(Settings.servers[sindex].split(", ")[2], 25565);
        String[] status = BaseUtils.pollServer(ip, port);
        String text = BaseUtils.genServerStatus(status);
        BufferedImage img = BaseUtils.genServerIcon(status);
        Frame.main.serverbar.updateBar(text, img);
        
        ThreadUtils.serverPollThread.interrupt();
        ThreadUtils.serverPollThread = null;
        BaseUtils.send("Refreshing server done!");
      }
    };
    serverPollThread.setName("Server poll thread");
    serverPollThread.start();
  }
  
  public static byte[] getBytesFromFile(File file)
    throws Exception
  {
    InputStream is = new FileInputStream(file);
    long length = file.length();
    byte[] bytes = new byte[(int)length];
    int offset = 0;
    int numRead = 0;
    while ((offset < bytes.length) && 
      ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {
      offset += numRead;
    }
    is.close();
    
    return bytes;
  }
  
  public static void upload(final File file, final int type)
  {
    new Thread()
    {
      public void run()
      {
        String get = type > 0 ? "uploadcloak" : "uploadskin";
        
        String bytes64 = null;
        try
        {
          byte[] bytes = ThreadUtils.getBytesFromFile(file);
          bytes64 = new String(new BASE64Encoder().encode(bytes));
        }
        catch (Exception e1)
        {
          e1.printStackTrace();
        }
        String answer = BaseUtils.execute(BaseUtils.buildUrl("launcher.php"), new Object[] { "action", 
        
          ThreadUtils.encrypt(get + ":0:" + Frame.login.getText() + ":" + ThreadUtils.token, Settings.key1), "ufile", bytes64 });
        
//        BaseUtils.send(answer);
        boolean error = false;
        if (answer == null)
        {
          Frame.main.panel.tmpString = Message.Null;
          error = true;
        }
        else if (answer.contains("nofile"))
        {
          Frame.main.panel.tmpString = Message.nofile;
          error = true;
        }
        else if (answer.contains("skinerr"))
        {
          Frame.main.panel.tmpString = Message.skinerr;
          error = true;
        }
        else if (answer.contains("cloakerr"))
        {
          Frame.main.panel.tmpString = Message.cloakerr;
          error = true;
        }
        else if (answer.contains("fileerr"))
        {
          Frame.main.panel.tmpString = Message.fileerr;
          error = true;
        }
        else if (answer.contains("errorLogin"))
        {
          Frame.main.panel.tmpString = Message.errorLogin;
          error = true;
        }
        else if (answer.contains("errorsql"))
        {
          Frame.main.panel.tmpString = Message.errorsql;
          error = true;
        }
        else if (answer.contains("temp"))
        {
          Frame.main.panel.tmpString = Message.temp;
          error = true;
        }
        else if (answer.contains("noactive"))
        {
          Frame.main.panel.tmpString = Message.noactive;
          error = true;
        }
        else if (answer.contains("badhash"))
        {
          Frame.main.panel.tmpString = Message.badhash;
          error = true;
        }
        else if (!answer.contains("success"))
        {
          Frame.main.panel.tmpString = answer;
          error = true;
        }
        if (error)
        {
          Frame.main.panel.tmpColor = Color.red;
          try
          {
            sleep(2000L);
          }
          catch (InterruptedException e) {}
          Frame.main.setPersonal(Frame.main.panel.pc);
          return;
        }
        if (type > 0)
        {
          Frame.main.panel.pc.realmoney = Integer.parseInt(answer.replaceAll("success:", ""));
          Frame.main.panel.pc.cloak = ImageUtils.parseCloak(BaseUtils.getCloakImage(Frame.login.getText()));
        }
        else
        {
          Frame.main.panel.pc.skin = ImageUtils.parseSkin(BaseUtils.getSkinImage(Frame.login.getText()));
        }
        Frame.main.setPersonal(Frame.main.panel.pc);
      }
    }.start();
  }
  
  public static void vaucher(final String vaucher)
  {
    new Thread()
    {
      public void run()
      {
        String answer = BaseUtils.execute(BaseUtils.buildUrl("launcher.php"), new Object[] { "action", 
        
          ThreadUtils.encrypt("activatekey:0:" + Frame.login.getText() + ":" + ThreadUtils.token, Settings.key1), "key", vaucher });
        
        boolean error = false;
        if (answer == null)
        {
          Frame.main.panel.tmpString = Message.Null;
          error = true;
        }
        else if (answer.contains("keyerr"))
        {
          Frame.main.panel.tmpString = Message.keyerr;
          error = true;
        }
        else if (answer.contains("errorLogin"))
        {
          Frame.main.panel.tmpString = Message.errorLogin;
          error = true;
        }
        else if (answer.contains("errorsql"))
        {
          Frame.main.panel.tmpString = Message.errorsql;
          error = true;
        }
        else if (answer.contains("temp"))
        {
          Frame.main.panel.tmpString = Message.temp;
          error = true;
        }
        else if (answer.contains("noactive"))
        {
          Frame.main.panel.tmpString = Message.noactive;
          error = true;
        }
        else if (answer.contains("badhash"))
        {
          Frame.main.panel.tmpString = Message.badhash;
          error = true;
        }
        else if (!answer.contains("success"))
        {
          Frame.main.panel.tmpString = answer;
          error = true;
        }
        if (error)
        {
          Frame.main.panel.tmpColor = Color.red;
          try
          {
            sleep(2000L);
          }
          catch (InterruptedException e) {}
          Frame.main.setPersonal(Frame.main.panel.pc);
          return;
        }
        Frame.main.panel.pc.realmoney = Integer.parseInt(answer.replaceAll("success:", ""));
        Frame.main.setPersonal(Frame.main.panel.pc);
      }
    }.start();
  }
  
  public static void exchange(final String text)
  {
    new Thread()
    {
      public void run()
      {
        String answer = BaseUtils.execute(BaseUtils.buildUrl("launcher.php"), new Object[] { "action", 
        
          ThreadUtils.encrypt("exchange:0:" + Frame.login.getText() + ":" + ThreadUtils.token, Settings.key1), "buy", text });
        
        boolean error = false;
        if (answer == null)
        {
          Frame.main.panel.tmpString = Message.Null;
          error = true;
        }
        else if (answer.contains("econo"))
        {
          Frame.main.panel.tmpString = Message.econo;
          error = true;
        }
        else if (answer.contains("ecoerr"))
        {
          Frame.main.panel.tmpString = Message.ecoerr;
          error = true;
        }
        else if (answer.contains("moneyno"))
        {
          Frame.main.panel.tmpString = Message.moneyno;
          error = true;
        }
        else if (answer.contains("errorLogin"))
        {
          Frame.main.panel.tmpString = Message.errorLogin;
          error = true;
        }
        else if (answer.contains("errorsql"))
        {
          Frame.main.panel.tmpString = Message.errorsql;
          error = true;
        }
        else if (answer.contains("temp"))
        {
          Frame.main.panel.tmpString = Message.temp;
          error = true;
        }
        else if (answer.contains("noactive"))
        {
          Frame.main.panel.tmpString = Message.noactive;
          error = true;
        }
        else if (answer.contains("badhash"))
        {
          Frame.main.panel.tmpString = Message.badhash;
          error = true;
        }
        else if (!answer.contains("success"))
        {
          Frame.main.panel.tmpString = answer;
          error = true;
        }
        if (error)
        {
          Frame.main.panel.tmpColor = Color.red;
          try
          {
            sleep(2000L);
          }
          catch (InterruptedException e) {}
          Frame.main.setPersonal(Frame.main.panel.pc);
          return;
        }
        String[] moneys = answer.replaceAll("success:", "").split(":");
        Frame.main.panel.pc.realmoney = Integer.parseInt(moneys[0]);
        Frame.main.panel.pc.iconmoney = Double.parseDouble(moneys[1]);
        Frame.main.vaucher.setText("");
        Frame.main.setPersonal(Frame.main.panel.pc);
      }
    }.start();
  }
  
  public static void buyVip(final int i)
  {
    new Thread()
    {
      public void run()
      {
        String z = i > 0 ? "buypremium" : "buyvip";
        String answer = BaseUtils.execute(BaseUtils.buildUrl("launcher.php"), new Object[] { "action", 
        
          ThreadUtils.encrypt(z + ":0:" + Frame.login.getText() + ":" + ThreadUtils.token, Settings.key1) });
        
        boolean error = false;
        if (answer == null)
        {
          Frame.main.panel.tmpString = Message.Null;
          error = true;
        }
        else if (answer.contains("moneyno"))
        {
          Frame.main.panel.tmpString = Message.moneyno;
          error = true;
        }
        else if (answer.contains("errorLogin"))
        {
          Frame.main.panel.tmpString = Message.errorLogin;
          error = true;
        }
        else if (answer.contains("errorsql"))
        {
          Frame.main.panel.tmpString = Message.errorsql;
          error = true;
        }
        else if (answer.contains("temp"))
        {
          Frame.main.panel.tmpString = Message.temp;
          error = true;
        }
        else if (answer.contains("noactive"))
        {
          Frame.main.panel.tmpString = Message.noactive;
          error = true;
        }
        else if (answer.contains("badhash"))
        {
          Frame.main.panel.tmpString = Message.badhash;
          error = true;
        }
        else if (!answer.contains("success"))
        {
          Frame.main.panel.tmpString = answer;
          error = true;
        }
        if (error)
        {
          Frame.main.panel.tmpColor = Color.red;
          try
          {
            sleep(2000L);
          }
          catch (InterruptedException e) {}
          Frame.main.setPersonal(Frame.main.panel.pc);
          return;
        }
        String[] data = answer.replaceAll("success:", "").split(":");
        Frame.main.panel.pc.realmoney = Integer.parseInt(data[0]);
        Frame.main.panel.pc.dateofexpire = BaseUtils.unix2hrd(Long.parseLong(data[1]));
        Frame.main.panel.pc.ugroup = (i > 0 ? "Premium" : "VIP");
        Frame.main.setPersonal(Frame.main.panel.pc);
      }
    }.start();
  }
  
  public static void register(final String name, final String pass, final String pass2, final String mail)
  {
    new Thread()
    {
      public void run()
      {
        String answer1 = BaseUtils.execute(BaseUtils.buildUrl("reg.php"), new Object[] { "action", "register", "user", name, "password", pass, "password2", pass2, "email", mail });
        
        boolean error = false;
        if (answer1 == null)
        {
          Frame.main.panel.tmpString = Message.Null;
          error = true;
        }
        else if (answer1.contains("done"))
        {
          Frame.main.panel.tmpString = Message.done;
          error = false;
        }
        else if (answer1.contains("errorField"))
        {
          Frame.main.panel.tmpString = Message.errorField;
          error = true;
        }
        else if (answer1.contains("errorMail"))
        {
          Frame.main.panel.tmpString = Message.errorMail;
          error = true;
        }
        else if (answer1.contains("errorMail2"))
        {
          Frame.main.panel.tmpString = Message.errorMail2;
          error = true;
        }
        else if (answer1.contains("errorLoginSymbol"))
        {
          Frame.main.panel.tmpString = Message.errorLoginSymbol;
          error = true;
        }
        else if (answer1.contains("passErrorSymbol"))
        {
          Frame.main.panel.tmpString = Message.passErrorSymbol;
          error = true;
        }
        else if (answer1.contains("errorPassToPass"))
        {
          Frame.main.panel.tmpString = Message.errorPassToPass;
          error = true;
        }
        else if (answer1.contains("errorSmallLogin"))
        {
          Frame.main.panel.tmpString = Message.errorSmallLogin;
          error = true;
        }
        else if (answer1.contains("errorPassSmall"))
        {
          Frame.main.panel.tmpString = Message.errorPassSmall;
          error = true;
        }
        else if (answer1.contains("emailErrorPovtor"))
        {
          Frame.main.panel.tmpString = Message.emailErrorPovtor;
          error = true;
        }
        else if (answer1.contains("Errorip"))
        {
          Frame.main.panel.tmpString = Message.Errorip;
          error = true;
        }
        else if (answer1.contains("loginErrorPovtor"))
        {
          Frame.main.panel.tmpString = Message.loginErrorPovtor;
          error = true;
        }
        else if (answer1.contains("errorsql"))
        {
          Frame.main.panel.tmpString = Message.errorsql;
          error = true;
        }
        else if (answer1.contains("registeroff"))
        {
          Frame.main.panel.tmpString = Message.registeroff;
          error = true;
        }
        else
        {
          Frame.main.panel.tmpString = Message.unknown.replace("%%", answer1);
          error = true;
        }
        if (error)
        {
          Frame.main.panel.tmpColor = Color.red;
          try
          {
            sleep(2000L);
          }
          catch (InterruptedException e) {}
          Frame.main.setRegister();
          return;
        }
        Frame.main.panel.tmpColor = Color.GREEN;
        try
        {
          sleep(2000L);
        }
        catch (InterruptedException e) {}
        Frame.main.setAuthComp();
      }
    }.start();
  }
  
  public static void unban()
  {
    new Thread()
    {
      public void run()
      {
    	  String answer = BaseUtils.execute(BaseUtils.buildUrl("launcher.php"), new Object[] { "action", 
          ThreadUtils.encrypt("buyunban:0:" + Frame.login.getText() + ":" + ThreadUtils.token, Settings.key1) });
        
    	  boolean error = false;
    	  if (answer == null)
    	  {
    		  Frame.main.panel.tmpString = Message.Null;
    		  error = true;
    	  }
    	  else if (answer.contains("moneyno"))
    	  {
    		  Frame.main.panel.tmpString = Message.moneyno;
    		  error = true;
    	  }
    	  else if (answer.contains("banno"))
    	  {
    		  Frame.main.panel.tmpString = Message.banno;
    		  error = true;
    	  }
    	  else if (answer.contains("moneyno"))
    	  {
    		  Frame.main.panel.tmpString = Message.moneyno;
    		  error = true;
    	  }
    	  else if (answer.contains("errorLogin"))
    	  {
    		  Frame.main.panel.tmpString = Message.errorLogin;
    		  error = true;
    	  }
    	  else if (answer.contains("errorsql"))
    	  {
    		  Frame.main.panel.tmpString = Message.errorsql;
    		  error = true;
    	  }
    	  else if (answer.contains("temp"))
    	  {
    		  Frame.main.panel.tmpString = Message.temp;
    		  error = true;
    	  }
    	  else if (answer.contains("noactive"))
    	  {
    		  Frame.main.panel.tmpString = Message.noactive;
    		  error = true;
    	  }
    	  else if (answer.contains("badhash"))
    	  {
    		  Frame.main.panel.tmpString = Message.badhash;
    		  error = true;
    	  }
    	  else if (!answer.contains("success"))
    	  {
    		  Frame.main.panel.tmpString = answer;
    		  error = true;
    	  }
    	  if (error)
    	  {
    		  Frame.main.panel.tmpColor = Color.red;
    		  try
    		  {
    			  sleep(2000L);
    		  }
    		  catch (InterruptedException e) {}
    		  Frame.main.setPersonal(Frame.main.panel.pc);
    		  return;
    	  }
    	  String[] s = answer.split(":");
    	  Frame.main.panel.pc.ugroup = s[2];
    	  Frame.main.buyUnban.setEnabled(false);
    	  Frame.main.panel.pc.realmoney = Integer.parseInt(s[1]);
    	  Frame.main.setPersonal(Frame.main.panel.pc);
      }
    }.start();
  }
  
  static String encrypt(String input, String key)
  {
    byte[] crypted = null;
    try
    {
      SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(1, skey);
      crypted = cipher.doFinal(input.getBytes());
    }
    catch (Exception e)
    {
      BaseUtils.sendErr("Ключ должен быть 16 символов");
    }
    return new String(new BASE64Encoder().encode(crypted));
  }
  
  static String decrypt(String input, String key)
  {
    byte[] output = null;
    try
    {
      SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(2, skey);
      output = cipher.doFinal(new BASE64Decoder().decodeBuffer(input));
    }
    catch (Exception e)
    {
      BaseUtils.sendErr("Ключ шифрование не совпадает или больше 16 символов, или получена ошибка от launcher.php");
      BaseUtils.sendErr("Проверьте настройку  в Settings.java или connect.php");
    }
    return new String(output);
  }
}
