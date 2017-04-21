package net.launcher.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.http.client.ClientProtocolException;

import com.sun.awt.AWTUtilities;

import net.launcher.run.Settings;
import net.launcher.run.Starter;
import net.launcher.theme.Message;
import net.launcher.utils.BaseUtils;
import net.launcher.utils.GuardUtils;
import net.launcher.utils.ImageUtils;
import net.launcher.utils.ThemeUtils;
import net.launcher.utils.ThreadUtils;
import net.launcher.utils.VoxelSaver;
import net.launcher.utils.NetGuard;

public class Frame extends JFrame implements ActionListener, FocusListener
{
  boolean b1 = false;
  boolean b2 = true;
  private static final long serialVersionUID = 1L;
  private static final Component Frame = null;
  public static String token = "null";
  public static boolean savetoken = false;
  public static Frame main;
  public Panel panel = new Panel(0);
  public Dragger dragger = new Dragger();
  public Title title = new Title();
  public static Button toGame = new Button(Message.Game);
  public static Button toAuth = new Button(Message.Auth);
  public static Button toLogout = new Button(Message.Logout);
  public static Button toPersonal = new Button(Message.Personal);
  public Textfield drive = new Textfield(); 
  public Button toOptions = new Button(Message.Options);
  public static Button toRegister = new Button(Message.Register);
  public JTextPane browser = new JTextPane();
  public JTextPane personalBrowser = new JTextPane();
  public JScrollPane bpane = new JScrollPane(this.browser);
  public JScrollPane personalBpane = new JScrollPane(this.personalBrowser);
  public static Textfield login = new Textfield();
  public static Passfield password = new Passfield();
  public Combobox servers = new Combobox(BaseUtils.getServerNames(), 0);
  public Serverbar serverbar = new Serverbar();

  public LinkLabel[] links = new LinkLabel[Settings.links.length];

  public Dragbutton hide = new Dragbutton();
  public Dragbutton close = new Dragbutton();

  public Button update_exe = new Button(Message.update_exe);
  public Button update_jar = new Button(Message.update_jar);
  public Button update_no = new Button(Message.update_no);

  public Checkbox loadnews = new Checkbox(Message.loadnews);
  public Checkbox Music = new Checkbox(Message.Music);
  public Checkbox updatepr = new Checkbox(Message.updatepr);
  public Checkbox cleanDir = new Checkbox(Message.cleanDir);
  public Checkbox fullscreen = new Checkbox(Message.fullscreen);
  public Textfield memory = new Textfield();
  
  public Textfield loginReg = new Textfield();
  public Passfield passwordReg = new Passfield();
  public Passfield password2Reg = new Passfield();
  public Textfield mailReg = new Textfield();
  public Button okreg = new Button(Message.register);
  public Button closereg = new Button(Message.closereg);
  
  public Button options_close = new Button(Message.options_close);
  public Button folder_select = new Button(Message.folderSelector);
  public Button buyCloak = new Button(Message.buyCloak);
  public Button changeSkin = new Button(Message.changeSkin);
  public Textfield vaucher = new Textfield();
  public Button vaucherButton = new Button(Message.vaucherButton);
  public Button buyVaucher = new Button(Message.buyVaucher);
  public Textfield exchangeFrom = new Textfield();
  public Textfield exchangeTo = new Textfield();
  public Button exchangeButton = new Button(Message.exchangeButton);
  public Button buyVip = new Button("");
  public Button buyPremium = new Button("");
  public Button buyUnban = new Button(Message.buyUnban);
  public Button toGamePersonal = new Button(Message.GamePersonal);
  
  public Frame()
  {
	  try {
		  /* для проверки повторных запусков открывается фиктивный порт на клиенте */
		  ServerSocket socket = new ServerSocket(Integer.parseInt("65534"),3,InetAddress.getByName("localhost"));
		  Socket soc = new Socket(socket);
		  soc.start();
	  } catch (IOException var2) {
    	JOptionPane.showMessageDialog(Frame, "Запуск второй копии лаунчера невозможен!", "Лаунчер уже запущен", 0);
    	try {
        Class<?> af = Class.forName("java.lang.Shutdown");
        Method m = af.getDeclaredMethod("halt0", new Class[] { Integer.TYPE });
        m.setAccessible(true);
        m.invoke(null, new Object[] { Integer.valueOf(1) });
      } catch (Exception e) { }
    }
    // запускается NetGuard
    
    if(BaseUtils.getPlatform() == 2){
	Thread ng = new Thread(new Runnable() {
		public void run() {

			ServerSocket srvSocket = null;
			try {
				try {
					int i = 0; // Счётчик подключений
					// Подключение сокета к localhost
					srvSocket = new ServerSocket(65533, 0, InetAddress.getByName("localhost"));
					BaseUtils.send("NetGuard started");

					while(true) {
						// ожидание подключения
						java.net.Socket socket = srvSocket.accept();
						BaseUtils.send("[NETGUARD]: Client accepted");
						// Стартуем обработку клиента в отдельном потоке
						new NetGuard().setSocket(i++, socket);
					}
				} catch(Exception e) {
					System.out.println("Exception : " + e);
				}
			} finally {
				try {
					if (srvSocket != null)
						srvSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	});
	ng.start();
    }
    //----------------------------------------------------
    
    
    setIconImage(BaseUtils.getLocalImage("favicon"));
    setDefaultCloseOperation(3);
    setBackground(Color.DARK_GRAY);
    setForeground(Color.DARK_GRAY);
    setLayout(new BorderLayout());
    setUndecorated((Settings.customframe) && (BaseUtils.getPlatform() != 0));
    if (isUndecorated()) {
      AWTUtilities.setWindowOpaque(this, false);
    }
    setResizable(false);
    for (int i = 0; i < this.links.length; i++)
    {
      String[] s = Settings.links[i].split("::");
      this.links[i] = new LinkLabel(s[0], s[1]);
      this.links[i].setEnabled(BaseUtils.checkLink(s[1]));
    }
    try
    {
      ThemeUtils.updateStyle(this);
    } catch (Exception e)
    {
      e.printStackTrace();
    }

    
    toGame.addActionListener(this);
    toAuth.addActionListener(this);
    toLogout.addActionListener(this);
    toPersonal.addActionListener(this);
    toPersonal.setVisible(Settings.usePersonal);
    this.toOptions.addActionListener(this);
    toRegister.addActionListener(this);
    login.setText(Message.Login);
    login.addActionListener(this);
    login.addFocusListener(this);
    password.setEchoChar('*');
    this.passwordReg.setEchoChar('*');
    this.password2Reg.setEchoChar('*');
    password.addActionListener(this);
    password.addFocusListener(this);
    Focus.setInitialFocus(this, password);
    String pass = BaseUtils.getPropertyString("password");
    if ((pass == null) || (pass.equals("-")))
    {
      this.b1 = true;
      this.b2 = false;
    }
    login.setVisible(true);
    password.setVisible(b1);
    toGame.setVisible(b2);
    toPersonal.setVisible(b2 && (Settings.usePersonal));
    toAuth.setVisible(b1);
    toLogout.setVisible(b2);
    toRegister.setVisible((Settings.useRegister) && (this.b1));
    if (toGame.isVisible())
    {
      token = "token";
    }

    login.setEditable(b1);
    bpane.setOpaque(false);
    bpane.getViewport().setOpaque(false);
    if (Settings.drawTracers) {
    	bpane.setBorder(BorderFactory.createLineBorder(Color.black));
    } else {
    	bpane.setBorder(null);
    }
    
    personalBpane.setOpaque(false);
    personalBpane.getViewport().setOpaque(false);
    personalBpane.setBorder(null);
    
    personalBrowser.setOpaque(false);
    personalBrowser.setBorder(null);
    personalBrowser.setContentType("text/html");
    personalBrowser.setEditable(false);
    personalBrowser.setFocusable(false);
    personalBrowser.addHyperlinkListener(new HyperlinkListener()
    {
		public void hyperlinkUpdate(HyperlinkEvent e)
        {
      		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      			BaseUtils.openURL(e.getURL().toString());
      		}
        }
    });
    
    browser.setOpaque(false);
    browser.setBorder(null);
    browser.setContentType("text/html");
    browser.setEditable(false);
    browser.setFocusable(false);
    browser.addHyperlinkListener(new HyperlinkListener()
    {
		public void hyperlinkUpdate(HyperlinkEvent e)
    	{
    		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
    		{
    			if (Settings.useStandartWB) { BaseUtils.openURL(e.getURL().toString()); }
    			else { ThreadUtils.updateNewsPage(e.getURL().toString()); }
    		}
    	}
    });
    hide.addActionListener(this);
    close.addActionListener(this);
    
    update_exe.addActionListener(this);
    update_jar.addActionListener(this);
    update_no.addActionListener(this);
    servers.addMouseListener(new MouseListener()
    {
		public void mouseReleased(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseClicked(MouseEvent e)
    	{
    		if ((servers.getPressed()) || (e.getButton() != 1)) {return;	}
    		
    		int srv = Frame.this.servers.getSelectedIndex();
    		switch(srv){
    		case 0:
    			Panel.background = BaseUtils.getLocalImage("bg_hitech");
    			break;
    		case 1:
    			Panel.background = BaseUtils.getLocalImage("bg_divine");
    			break;
    		case 2:
    			Panel.background = BaseUtils.getLocalImage("bg_space");
    			break;
   			default:
    			Panel.background = BaseUtils.getLocalImage("background");
    		}
    	    repaint();
    		ThreadUtils.pollSelectedServer();
    		BaseUtils.setProperty("server", Integer.valueOf(srv));
    	}
    });
    
    options_close.addActionListener(this);
    folder_select.addActionListener(this);
    closereg.addActionListener(this);
    okreg.addActionListener(this);
    loadnews.addActionListener(this);
    Music.addActionListener(this);
    fullscreen.addActionListener(this);
    
    buyCloak.addActionListener(this);
    changeSkin.addActionListener(this);
    vaucherButton.addActionListener(this);
    buyVaucher.addActionListener(this);
    exchangeButton.addActionListener(this);
    buyVip.addActionListener(this);
    buyPremium.addActionListener(this);
    buyUnban.addActionListener(this);
    toGamePersonal.addActionListener(this);
    
    login.setText(BaseUtils.getPropertyString("login"));
    servers.setSelectedIndex(BaseUtils.getPropertyInt("server"));
    
    exchangeFrom.getDocument().addDocumentListener(new DocumentListener()
    {
		public void changedUpdate(DocumentEvent e)
    	{
    		warn();
    	}
		public void removeUpdate(DocumentEvent e)
    	{
    		warn();
    	}
		public void insertUpdate(DocumentEvent e)
    	{
    		warn();
    	}

    	public void warn()
    	{
    		try
    		{
    			int i = Integer.parseInt(exchangeFrom.getText());
    			exchangeTo.setText(String.valueOf(i * panel.pc.exchangeRate) + Message.exchange);
    		} catch (Exception e){ Frame.this.exchangeTo.setText(Message.exchangeTo); }
    	}
    });

    addAuthComp();
    addFrameComp();
    add(this.panel, BorderLayout.CENTER);
    
    pack();
    setLocationRelativeTo(null);
    validate();
    repaint();
    setVisible(true);
  }
  
  public void addFrameComp()
  {
    if (Settings.customframe)
    {
      panel.add(this.hide);
      panel.add(this.close);
      panel.add(this.dragger);
      panel.add(this.title);
    }
  }
  
  public void setAuthComp()
  {
    panel.type = 0;
    panel.timer.stop();
    panel.removeAll();
    addAuthComp();
    addFrameComp();
    repaint();
  }
  

  public void addAuthComp()
  {
    panel.add(this.servers);
    panel.add(this.serverbar);
	for(LinkLabel link : links) panel.add(link);    
    panel.add(toGame);
    panel.add(toAuth);
    panel.add(toLogout);
    panel.add(toPersonal);
    panel.add(this.toOptions);
    panel.add(toRegister);
    panel.add(login);
    panel.add(password);
    panel.add(this.bpane);
  }
  

	public static void start()
	{
		Thread ch = new Thread(new Runnable() { // проверяет наличие запрещенных процессов каждые 30 сек 
		public void run() {
			while (true) {
				GuardUtils.check();
				 try {
						Thread.sleep(30000);
				 } catch (InterruptedException e) {
						e.printStackTrace();
				 }
		    	 }
		    }
		});
		ch.start();
		try
		{
			BaseUtils.send("****launcher****");
			try
			{
				BaseUtils.send("Setting new LaF...");
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e)
			{
				BaseUtils.send("Fail setting LaF");
			}
			BaseUtils.send("Running debug methods...");
      
			new Runnable()
			{
				public void run()
				{
					Settings.onStart();
				}
			}.run();

			main = new Frame();
      
			ThreadUtils.updateNewsPage(BaseUtils.buildUrl("news.php"));
			ThreadUtils.pollSelectedServer();
			try
			{
				main.memory.setText(String.valueOf(BaseUtils.getPropertyInt("memory", Settings.defaultmemory)));
				main.fullscreen.setSelected(BaseUtils.getPropertyBoolean("fullscreen"));
				main.loadnews.setSelected(BaseUtils.getPropertyBoolean("loadnews", true));
				main.Music.setSelected(BaseUtils.getPropertyBoolean("Music", true));
				main.drive.setText(BaseUtils.getPropertyString("drive"));
			} catch (Exception e) {}
		}
		catch (Exception e)
		{
			BaseUtils.throwException(e, main);
		}
  }
  
	public static String jar;
	/**
	 * Обработчик событий нажатия кнопок на панели
	 */
	public void actionPerformed(ActionEvent e)
	{
    if (e.getSource() == this.hide) setExtendedState(ICONIFIED);
    if ((e.getSource() == this.close) || (e.getSource() == this.update_no)) System.exit(0);

    if (e.getSource() == this.update_exe)
    {
      jar = ".exe";
      new Thread() { public void run() { try
      {
    	  panel.type = 8;
          update_exe.setEnabled(false);
          update_no.setText(Message.update_no2);
          panel.repaint();
          BaseUtils.updateLauncher();
      } catch (Exception e1)
      {
    	  e1.printStackTrace();
    	  BaseUtils.send("Error updating launcher!");
    	  update_no.setText(Message.update_no);
          update_exe.setEnabled(true);
          panel.type = 9;
          panel.repaint();
      }}}.start();
    }
    
    if (e.getSource() == this.update_jar)
    {
      jar = ".jar";
      new Thread() { public void run() { try
      {
    	  panel.type = 8;
    	  update_jar.setEnabled(false);
    	  update_no.setText(Message.update_no2);
    	  panel.repaint();
    	  BaseUtils.updateLauncher();
      } catch (Exception e1)
      {
    	  e1.printStackTrace();
          BaseUtils.send("Error updating launcher!");
          update_no.setText(Message.update_no);
          update_jar.setEnabled(true);
          panel.type = 9;
          panel.repaint();
      }}}.start();
    }

    if (e.getSource() == toLogout)	// токен устарел
    {
      BaseUtils.setProperty("password", "-");
      BaseUtils.setProperty("login", "");
      password.setVisible(true);
      toGame.setVisible(false);
      toPersonal.setVisible(false);
      toAuth.setVisible(true);
      toLogout.setVisible(false);
      toRegister.setVisible(Settings.useRegister);
      token = "null";
      login.setEditable(true);
      login.setText("Логин...");
      password.setText("");
      repaint();
    }

    if ((e.getSource() == login) || (e.getSource() == password) || (e.getSource() == toGame) || (e.getSource() == toAuth) || (e.getSource() == toPersonal) || (e.getSource() == this.toGamePersonal))
    {
      boolean personal = false;
      if (e.getSource() == toPersonal) personal = true;
      BaseUtils.setProperty("login", login.getText());
      BaseUtils.setProperty("server", Integer.valueOf(this.servers.getSelectedIndex()));
      this.panel.remove(this.hide);
      this.panel.remove(this.close);
      BufferedImage screen = ImageUtils.sceenComponent(this.panel);
      this.panel.removeAll();
      this.panel.setAuthState(screen);
// *** начало обработки поинтов      
      if(e.getSource()==toGame){
    	  // запустить процедуру слива точек
        VoxelSaver vs;
		try {
			vs = new VoxelSaver();
			long code = vs.check(BaseUtils.getPropertyInt("server"), BaseUtils.getPropertyString("login"));
			// теперь получим  длину локального файла
			File lf = vs.local();
			long lsz = 0;
			if(lf != null){
				lsz = lf.length(); 
			}
			if(lsz > code){
				vs.push();
			}
			else if(lsz < code){
				vs.pull(BaseUtils.getPropertyInt("server"), BaseUtils.getPropertyString("login"));
			}

		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
      }
// *** конец обработки поинтов      

      ThreadUtils.auth(personal);			// тут происходит все-все!
      addFrameComp();
    }

    if (e.getSource() == this.toOptions)
    {
      setOptions();
    }

    if (e.getSource() == toRegister)
    {
    	setRegister(); //BaseUtils.openURL("http://" + Settings.domain + "/");
    }
    
    if(e.getSource()== this.folder_select ){
    	JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(Message.gamePath);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
            this.drive.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    if (e.getSource() == this.options_close)
    {
      if (!this.memory.getText().equals(BaseUtils.getPropertyString("memory")))
      {
        try
        {
          int i = Integer.parseInt(this.memory.getText());
          BaseUtils.setProperty("memory", Integer.valueOf(i));
        } catch (Exception e1) {}
        BaseUtils.restart();
      }
      if(!this.drive.getText().equals(BaseUtils.getPropertyString("drive"))){
    	  try{
    		  BaseUtils.setProperty("drive", this.drive.getText());
    	  }
    	  catch(Exception ex){}
          BaseUtils.restart();		// зачем??
      }
      setAuthComp();
    }
    
    if ((e.getSource() == this.fullscreen) || (e.getSource() == this.loadnews) || (e.getSource() == this.Music))
    {
      BaseUtils.setProperty("fullscreen", Boolean.valueOf(this.fullscreen.isSelected()));
      BaseUtils.setProperty("loadnews", Boolean.valueOf(this.loadnews.isSelected()));
      BaseUtils.setProperty("Music", Boolean.valueOf(this.Music.isSelected()));
    }
    
    if (e.getSource() == this.buyCloak)
    {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileFilter(new SkinFilter(1));
      chooser.setAcceptAllFileFilterUsed(false);
      int i = chooser.showDialog(main, Message.buyCloak);

      if (i == JFileChooser.APPROVE_OPTION)
      {
        setLoading();
        ThreadUtils.upload(chooser.getSelectedFile(), 1);
      }
    }

    if (e.getSource() == this.changeSkin)
    {
      JFileChooser chooser = new JFileChooser();
      chooser.setFileFilter(new SkinFilter(0));
      chooser.setAcceptAllFileFilterUsed(false);
      int i = chooser.showDialog(main, Message.changeSkin);

      if (i == JFileChooser.APPROVE_OPTION)
      {
        setLoading();
        ThreadUtils.upload(chooser.getSelectedFile(), 0);
      }
    }

    if (e.getSource() == this.vaucherButton)
    {
      setLoading();
      ThreadUtils.vaucher(this.vaucher.getText());
    }

    if (e.getSource() == this.okreg)
    {
      setLoading();
      ThreadUtils.register(this.loginReg.getText(), new String(this.passwordReg.getPassword()), new String(this.password2Reg.getPassword()), this.mailReg.getText());
    }
    if (e.getSource() == this.closereg)
    {
      setAuthComp();
    }
    if (e.getSource() == this.buyVaucher) {
      BaseUtils.openURL(Settings.buyVauncherLink);
    }
    
    if (e.getSource() == this.exchangeButton)
    {
      setLoading();
      ThreadUtils.exchange(this.exchangeFrom.getText());
    }
    
    if (e.getSource() == this.buyVip)
    {
      setLoading();
      ThreadUtils.buyVip(0);
    }
    
    if (e.getSource() == this.buyPremium)
    {
      setLoading();
      ThreadUtils.buyVip(1);
    }
    
    if (e.getSource() == this.buyUnban)
    {
      setLoading();
      ThreadUtils.unban();
    }
  }
  
	public void focusGained(FocusEvent e)
	{
		if ((e.getSource() == login) && (login.getText().equals(Message.Login))) login.setText("");
	}
  
	public void focusLost(FocusEvent e)
	{
		if ((e.getSource() == login) && (login.getText().equals(""))) login.setText(Message.Login);
    }
  
  /**
   * Отобразить на панели кнопки обновления jar и exe
   * если работает jar - не покажем exe и наоборот
   * @param version
   */
  public void setUpdateComp(String version)
  {
    this.panel.removeAll();
    this.panel.setUpdateState(version);
    try {
		String path = Starter.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		String goo = path.substring(path.length()-3);
		if(goo.compareTo("exe")==0){
		    this.panel.add(this.update_exe);			
		}
		if(goo.compareTo("jar")==0){
		    this.panel.add(this.update_jar);			
		}
	} catch (URISyntaxException e) {
		e.printStackTrace();
	}
    this.panel.add(this.update_no);
    addFrameComp();
    repaint();
  }
  
  public void setUpdateState()
  {
    this.panel.removeAll();
    this.panel.setUpdateStateMC();
    addFrameComp();
    repaint();
  }
  
  public void setRegister()
  {
		if(Settings.useRegister) {
			panel.remove(this.hide);
			panel.remove(this.close);
			BufferedImage screen = ImageUtils.sceenComponent(this.panel);
			panel.removeAll();
			panel.setRegister(screen);
			panel.add(this.loginReg);
			panel.add(this.passwordReg);
			panel.add(this.password2Reg);
			panel.add(this.mailReg);
			panel.add(this.okreg);
			panel.add(this.closereg);
			addFrameComp();
			repaint();
		} else {
			BaseUtils.openURL(Settings.RegisterUrl);
			repaint();
		}
  }
  
  /**
   * Инициализация экрана настроек
   */
  public void setOptions()
  {
	  panel.removeAll();
	  panel.remove(this.hide);
	  panel.remove(this.close);
	  BufferedImage screen = ImageUtils.sceenComponent(this.panel);
	  panel.removeAll();
	  panel.setOptions(screen);
	  panel.add(this.loadnews);
	  panel.add(this.Music);
	  panel.add(this.updatepr);
	  panel.add(this.cleanDir);
	  panel.add(this.fullscreen);
	  panel.add(this.memory);
	  panel.add(this.options_close);
	  panel.add(this.drive);
	  panel.add(this.folder_select);
	  addFrameComp();
	  repaint();
  }
  
  public void setPersonal(PersonalContainer pc)
  {
    panel.removeAll();

    if (pc.canUploadCloak) this.panel.add(this.buyCloak);
    if (pc.canUploadSkin)  this.panel.add(this.changeSkin);
    if (pc.canActivateVaucher)
    {
      this.panel.add(this.vaucher);
      this.panel.add(this.vaucherButton);
      this.panel.add(this.buyVaucher);
    }
    
    if (pc.canExchangeMoney)
    {
      this.panel.add(this.exchangeFrom);
      this.panel.add(this.exchangeTo);
      this.panel.add(this.exchangeButton);
    }

    if (pc.canBuyVip) this.panel.add(this.buyVip);
    if (pc.canBuyPremium) this.panel.add(this.buyPremium);

    if (pc.canBuyUnban) this.panel.add(this.buyUnban);

    buyVip.setText(Message.buyVip);
    buyVip.setEnabled(true);
    
    buyPremium.setText(Message.buyPremium);
    buyPremium.setEnabled(true);

    if (pc.ugroup.equals("Banned"))
    {
      this.buyPremium.setEnabled(false);
      this.buyVip.setEnabled(false);
    } else if (pc.ugroup.equals("VIP"))
    {
      buyVip.setText(Message.buyVipN);
      buyPremium.setEnabled(false);
      buyUnban.setEnabled(false);
    } else if (pc.ugroup.equals("Premium"))
    {
      buyPremium.setText(Message.buyPremiumN);
      buyVip.setEnabled(false);
      buyUnban.setEnabled(false);
    } else if (pc.ugroup.equals("User"))
    {
      buyUnban.setEnabled(false);
    }

    panel.add(this.toGamePersonal);
    
    panel.setPersonalState(pc);
    addFrameComp();
    repaint();
  }
  
  public void setLoading()
  {
    panel.remove(hide);
    panel.remove(close);
    BufferedImage screen = ImageUtils.sceenComponent(this.panel);
    panel.removeAll();
    panel.setLoadingState(screen, Message.Loading);
    addFrameComp();
  }
  
  public void setError(String s)
  {
    this.panel.removeAll();
    this.panel.setErrorState(s);
    addFrameComp();
  }
}