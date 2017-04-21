package net.launcher.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.Image;
//import java.awt.LayoutManager;
//import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
//import java.io.Console;
import java.io.File;
import java.io.IOException;
//import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.ProcessBuilder;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
//import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import net.launcher.run.Settings;
import net.launcher.utils.BaseUtils;
import net.launcher.utils.EncodingUtils;
import net.launcher.utils.GuardUtils;
import net.launcher.utils.java.eURLClassLoader;
import net.minecraft.Launcher;

public class Game extends JFrame {
    private static final long serialVersionUID = 1;
    public static Launcher mcapplet;
    private static eURLClassLoader cl;
    static String Cl;
    Timer timer = null;
    int i = 0;
    static List<String> params;
    public static Thread start;

    public Game(final String answer) {
        GuardUtils.getLogs(new File(BaseUtils.getAssetsDir().getAbsolutePath() + File.separator + BaseUtils.getClientName()));
        GuardUtils.getLogs(new File(BaseUtils.getAssetsDir().getAbsolutePath() + File.separator + "logs"));
        String bin = BaseUtils.getMcDir().toString() + File.separator;
        
        System.out.println("eURLClassLoader(...)");
        cl = new eURLClassLoader((URL[])GuardUtils.url.toArray(new URL[GuardUtils.url.size()]));
        
        boolean old = false;
        try {
            cl.loadClass("net.minecraft.client.MinecraftApplet");
            old = true;
        }
        catch (Exception e) {
            // empty catch block
        }

        String user = answer.split("<br>")[1].split("<:>")[0];
        String session = EncodingUtils.xorencode(EncodingUtils.inttostr(answer.split("<br>")[1].split("<:>")[1]), Settings.protectionKey);
        Thread ch = new Thread(new Runnable(){

            @Override
            public void run() {
                do {
                    GuardUtils.check();
                    try {
                        Thread.sleep(30000);
                        continue;
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                        continue;
                    }
//                    break;
                } while (true);
            }
        });
        ch.start();
        if (old) {
            Thread check = new Thread(new Runnable(){

                @Override
                public void run() {
                    for (int z = 0; z < Settings.useModCheckerint; ++z) {
                        GuardUtils.checkMods(answer, false);
                        try {
                            Thread.sleep(30000);
                            continue;
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            check.start();
            try {
                this.addWindowListener(new WindowListener(){

                    @Override
                    public void windowOpened(WindowEvent e) {
                    }

                    @Override
                    public void windowIconified(WindowEvent e) {
                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {
                    }

                    @Override
                    public void windowDeactivated(WindowEvent e) {
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                    }

                    @Override
                    public void windowActivated(WindowEvent e) {
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        Game.mcapplet.stop();
                        Game.mcapplet.destroy();
                        System.exit(0);
                    }
                });
                this.setForeground(Color.BLACK);
                this.setBackground(Color.BLACK);
                mcapplet = new Launcher(bin, GuardUtils.url.toArray(new URL[GuardUtils.url.size()]));
                Game.mcapplet.customParameters.put("username", user);
                Game.mcapplet.customParameters.put("sessionid", session);
                Game.mcapplet.customParameters.put("stand-alone", "true");
                if (Settings.useAutoenter) {
                    Game.mcapplet.customParameters.put("server", Settings.servers[Frame.main.servers.getSelectedIndex()].split(", ")[1]);
                    Game.mcapplet.customParameters.put("port", Settings.servers[Frame.main.servers.getSelectedIndex()].split(", ")[2]);
                }
                this.setTitle(Settings.titleInGame); //"SpaceTechnology");
                if (Frame.main != null) {
                    Frame.main.setVisible(false);
                    this.setBounds(Frame.main.getBounds());
                    this.setExtendedState(Frame.main.getExtendedState());
                    this.setMinimumSize(Frame.main.getMinimumSize());
                }
                this.setSize(Settings.width, Settings.height + 28);
                this.setMinimumSize(new Dimension(Settings.width, Settings.height + 28));
                this.setLocationRelativeTo(null);
                mcapplet.setForeground(Color.BLACK);
                mcapplet.setBackground(Color.BLACK);
                this.setLayout(new BorderLayout());
                this.add((Component)mcapplet, "Center");
                this.validate();
                if (BaseUtils.getPropertyBoolean("fullscreen")) {
                    this.setExtendedState(6);
                }
                this.setIconImage(BaseUtils.getLocalImage("favicon"));
                this.setVisible(true);
                if (Settings.useConsoleHider) {
                    System.setErr(new PrintStream(new NulledStream()));
                    System.setOut(new PrintStream(new NulledStream()));
                }
                mcapplet.init();
                mcapplet.start();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Thread check = new Thread(new Runnable(){

                @Override
                public void run() {
                    for (int z = 0; z < Settings.useModCheckerint; ++z) {
                        GuardUtils.checkMods(answer, false);
                        try {
                            Thread.sleep(30000);
                            continue;
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            check.start();
            try {
                System.out.println("Running Minecraft");
                String jarpath = BaseUtils.getMcDir().toString() + File.separator;
                String minpath = BaseUtils.getMcDir().toString();
                String assets = BaseUtils.getAssetsDir().toString() + File.separator;
                System.setProperty("fml.ignoreInvalidMinecraftCertificates", "true");
                System.setProperty("fml.ignorePatchDiscrepancies", "true");
                System.setProperty("org.lwjgl.librarypath", jarpath + "natives");
                System.setProperty("net.java.games.input.librarypath", jarpath + "natives");
                System.setProperty("java.library.path", jarpath + "natives");
                if (BaseUtils.getPropertyBoolean("fullscreen")) {
                    params.add("--fullscreen");
                    params.add("true");
                } else {
                    params.add("--width");
                    params.add(String.valueOf(Settings.width));
                    params.add("--height");
                    params.add(String.valueOf(Settings.height));
                }
                if (Settings.useAutoenter) {
                    params.add("--server");
                    params.add(Settings.servers[Frame.main.servers.getSelectedIndex()].split(", ")[1]);
                    params.add("--port");
                    params.add(Settings.servers[Frame.main.servers.getSelectedIndex()].split(", ")[2]);
                }
                try {
                    cl.loadClass("com.mojang.authlib.Agent");
                    params.add("--accessToken");
                    params.add(session);
                    params.add("--uuid");
                    params.add(EncodingUtils.xorencode(EncodingUtils.inttostr(answer.split("<br>")[0].split("<:>")[1]), Settings.protectionKey));
                    params.add("--userProperties");
                    params.add("{}");
                    params.add("--assetIndex");
                    params.add(Settings.servers[Frame.main.servers.getSelectedIndex()].split(", ")[3]);
                }
                catch (ClassNotFoundException e2) {
                    params.add("--session");
                    params.add(session);
                }
                params.add("--username");
                params.add(user);
                params.add("--version");
                params.add(Settings.servers[Frame.main.servers.getSelectedIndex()].split(", ")[3]);
                params.add("--gameDir");
                params.add(minpath);
                params.add("--assetsDir");
                if (Integer.parseInt(Settings.servers[Frame.main.servers.getSelectedIndex()].split(", ")[3].replace(".", "")) < 173) {
                    params.add(assets + "assets/virtual/legacy");
                } else {
                    params.add(assets + "assets");
                }
                boolean tweakClass = false;
                try {
                    cl.loadClass("com.mumfrey.liteloader.launch.LiteLoaderTweaker");
                    params.add("--tweakClass");
                    params.add("com.mumfrey.liteloader.launch.LiteLoaderTweaker");
                    tweakClass = true;
                }
                catch (ClassNotFoundException e) {
                    // empty catch block
                }
                try {
                    cl.loadClass("cpw.mods.fml.common.launcher.FMLTweaker");
                    params.add("--tweakClass");
                    params.add("cpw.mods.fml.common.launcher.FMLTweaker");
                    tweakClass = true;
                }
                catch (ClassNotFoundException e) {
                    // empty catch block
                }
                try {
                    cl.loadClass("net.minecraftforge.fml.common.launcher.FMLTweaker");
                    params.add("--tweakClass");
                    params.add("net.minecraftforge.fml.common.launcher.FMLTweaker");
                    tweakClass = true;
                }
                catch (ClassNotFoundException e) {
                    // empty catch block
                }
                
/*	============ Запуск модуля защиты =============== */
                if(BaseUtils.getPlatform() == 2){
                	try{
                		BaseUtils.send("Statring guard...");
                		String bits = System.getProperty("sun.arch.data.model");
                		if(bits == "32") bits="";
                		String pf = BaseUtils.getMcDir().getPath();
                		pf += "\\config\\guard\\mGuard" + bits + ".exe";
                		BaseUtils.send("Launching: " + pf);
                		File f = new File(pf);
                	    if(!f.exists()) {
            	            JOptionPane.showMessageDialog(null, "Модуль защиты не обнаружен!", "Ошибка запуска", 0, (Icon)null);
            	            Runtime.getRuntime().halt(1);
                	    }
                		ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", pf);
                    	pb.start(); 	// команда исполнится в любом случае, т.к cmd.exe есть везде

                    	// теперь надо завести таймер на минуту, и вырубить всё, если нет соединения...
                    	timer = new Timer(60000, new ActionListener() {

							public void actionPerformed(ActionEvent evt) {
								Thread.currentThread().setName("NetGuard-tmr");
                    	        if (Settings.GuardState.get() != 1) {
                    	            timer.stop();
                    	            
                    	            BaseUtils.send("Guard module not loaded yet, stoppnig game...");
                    	            //Class<?> af;
									try {
										cl.close();		// closing classloadres
										/*
										af = Class.forName("java.lang.Shutdown");
	                                    Method m = af.getDeclaredMethod("halt0", Integer.TYPE);
	                                    m.setAccessible(true);
	                                    m.invoke(null, new Object[] { Integer.valueOf(1) });
	                                    
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									} catch (NoSuchMethodException e) {
										e.printStackTrace();
									} catch (SecurityException e) {
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									} catch (IllegalArgumentException e) {
										e.printStackTrace();
									} catch (InvocationTargetException e) {
										e.printStackTrace();*/
									} catch (IOException e) {
										e.printStackTrace();
									}
//                    	            BaseUtils.sendErr("[NetGuard]: halt won't work, trying hard methods...");
//                    	            Runtime.getRuntime().halt(1);
                    	        }
                    	    }    
                    	});
                    	timer.start();	// если эту строчку закомментить - защиты не будет 
                    	
                	} catch (IOException e){
                		BaseUtils.send("process launch error!");
                		JOptionPane.showMessageDialog(Frame.main, "Модуль защиты не запускается!", "Ошибка запуска", 0, (Icon)null);
                		Runtime.getRuntime().halt(1);
                	}
                }
/*	================================================= */
                
                Cl = tweakClass ? "net.minecraft.launchwrapper.Launch" : "net.minecraft.client.main.Main";
                Frame.main.setVisible(false);
                GuardUtils.delete(new File(assets + "assets/skins"));
                start.start();
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    static {
        Cl = null;
        params = new ArrayList<String>();
        start = new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    Class<?> start = cl.loadClass(Game.Cl);
                    Method main = start.getMethod("main", String[].class);
                    main.invoke(null, new Object[] { Game.params.toArray(new String[0]) });
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(Frame.main, e, "Ошибка запуска", 0, null);
                    try {
                        Class<?> af = Class.forName("java.lang.Shutdown");
                        Method m = af.getDeclaredMethod("halt0", Integer.TYPE);
                        m.setAccessible(true);
                        m.invoke(null, new Object[] { Integer.valueOf(1) });
                    }
                    catch (Exception x) {
                        // empty catch block
                    }
                }
            }
        });
    }

}

