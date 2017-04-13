package net.launcher.components;

import java.awt.Color;
//import java.awt.Font;
//import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.Timer;
import net.launcher.run.Settings;
import net.launcher.theme.DraggerTheme;
import net.launcher.theme.ErrorTheme;
//import net.launcher.theme.FontBundle;
import net.launcher.theme.LoginTheme;
import net.launcher.theme.Message;
import net.launcher.theme.OptionsTheme;
import net.launcher.theme.PersonalTheme;
import net.launcher.theme.RegTheme;
import net.launcher.theme.UpdateTheme;
import net.launcher.theme.UpdaterTheme;
import net.launcher.utils.BaseUtils;
import net.launcher.utils.ImageUtils;
import net.launcher.utils.ThreadUtils;
import net.launcher.utils.UpdaterThread;

public class Panel extends JPanel
{
  private static final long serialVersionUID = 1L;
  public static BufferedImage background = BaseUtils.getLocalImage("background");
//  public static BufferedImage background_personal = BaseUtils.getLocalImage("background_personal");
  public static BufferedImage background_dialog = BaseUtils.getLocalImage("background_dialog");
  public static BufferedImage background_download = BaseUtils.getLocalImage("background_download");
  public static BufferedImage bar = BaseUtils.getLocalImage("bar");
  public static BufferedImage bar_label = BaseUtils.getLocalImage("bar_label");
  public static BufferedImage extpanel = BaseUtils.getLocalImage("extpanel");
  public int type;
  public BufferedImage tmpImage;
  public String tmpString = "";
  public Color tmpColor;
  public Timer timer;
  public PersonalContainer pc;
  private int tindex = 0;
  
  public Panel(int type)
  {
    setOpaque(false);
    setLayout(null);
    setDoubleBuffered(true);
    setBorder(null);
    setFocusable(false);
    this.type = type;
  }
  
  public void paintComponent(Graphics gmain)
  {
    Graphics2D g = (Graphics2D)gmain;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    try
    {
      g.setColor(this.tmpColor);
    }
    catch (Exception e) {}
    if (this.type == 0)
    {
      g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
    }
    else if (this.type == 1)
    {
      g.drawImage(this.tmpImage, 0, 0, getWidth(), getHeight(), null);
      g.drawImage(ImageUtils.getByIndex(Files.wait, 128, this.tindex), getWidth() / 2 - 64, getHeight() / 2 - 64, 128, 128, null);
      g.drawString(this.tmpString, getWidth() / 2 - g.getFontMetrics().stringWidth(this.tmpString) / 2, getHeight() / 2 + 80);
    }
    else if ((this.type == 2) || (this.type == 8) || (this.type == 9))
    {
      g.setFont(g.getFont().deriveFont(LoginTheme.fonttitlesize));
      g.drawImage(background_dialog, 0, 0, getWidth(), getHeight(), null);
      g.drawString(Message.update, getWidth() / 2 - g.getFontMetrics().stringWidth(Message.update) / 2, UpdateTheme.stringsY);
      g.setFont(g.getFont().deriveFont(LoginTheme.fontbasesize));
      g.drawString(Message.str1, UpdateTheme.stringsX, UpdateTheme.stringsY + 20);
      g.drawString(Message.str2, UpdateTheme.stringsX, UpdateTheme.stringsY + 40);
      g.drawString(Message.str3, UpdateTheme.stringsX, UpdateTheme.stringsY + 60);
      g.drawString(Message.str4, UpdateTheme.stringsX, UpdateTheme.stringsY + 80);
      g.drawString(Message.str5, UpdateTheme.stringsX, UpdateTheme.stringsY + 100);
      g.drawString(Message.str6.replace("%%", Settings.masterVersion), UpdateTheme.stringsX, UpdateTheme.stringsY + 120);
      g.drawString(Message.str7.replace("%%", this.tmpString), UpdateTheme.stringsX, UpdateTheme.stringsY + 140);
      if ((this.type == 8) || (this.type == 9))
      {
        g.setColor(Color.RED);
        g.drawString(this.type == 8 ? Message.str8 : Message.str9, UpdateTheme.stringsX, UpdateTheme.stringsY + 160);
      }
    }
    else if (this.type == 3)
    {
      g.setFont(g.getFont().deriveFont(LoginTheme.fonttitlesize));
      g.setColor(Color.BLACK);
      g.drawImage(background_dialog, 0, 0, getWidth(), getHeight(), null);
      g.drawString(Message.messerr, getWidth() / 2 - g.getFontMetrics().stringWidth(Message.messerr) / 2, ErrorTheme.stringsY);
      g.setFont(g.getFont().deriveFont(LoginTheme.fontbasesize));
      g.drawString(Message.err1, ErrorTheme.stringsX, ErrorTheme.stringsY + 20);
      g.setFont(g.getFont().deriveFont(12.0F));
      for (int i = 0; i < this.tmpString.split("<:>").length; i++) {
        g.drawString(Message.err2.replace("%%", this.tmpString.split("<:>")[i]), ErrorTheme.stringsX, ErrorTheme.stringsY + 40 + 20 * i);
      }
    }
    else if (this.type == 4)
    {
      g.drawImage(background_download, 0, 0, getWidth(), getHeight(), null);
      UpdaterThread t = ThreadUtils.updaterThread;
      
      int leftTime = 0;
      try
      {
        leftTime = (int)((t.totalsize - t.currentsize) / (t.downloadspeed * 100));
      }
      catch (Exception e) {}
      g.setFont(UpdaterTheme.updaterDesc.font);
      g.setColor(UpdaterTheme.updaterDesc.color);
      
      g.drawString(Message.currentfile.replace("%%", t.currentfile.substring(t.currentfile.lastIndexOf(File.separator) + 1)), UpdaterTheme.stringsX, UpdaterTheme.stringsY);
      g.drawString(Message.totalsize.replace("%%", Long.toString(t.totalsize)), UpdaterTheme.stringsX, UpdaterTheme.stringsY + 20);
      g.drawString(Message.currentsize.replace("%%", Long.toString(t.currentsize)), UpdaterTheme.stringsX, UpdaterTheme.stringsY + 40);
      g.drawString(Message.downloadspeed.replace("%%", Long.toString(t.downloadspeed)), UpdaterTheme.stringsX, UpdaterTheme.stringsY + 60);
      g.drawString(Message.McDir.replace("%%", BaseUtils.getMcDir().getAbsolutePath().substring(BaseUtils.getMcDir().getAbsolutePath().lastIndexOf(File.separator) + 1)), UpdaterTheme.stringsX, UpdaterTheme.stringsY + 80);
      g.drawString(Message.state.replace("%%", t.state), UpdaterTheme.stringsX, UpdaterTheme.stringsY + 100);
      g.drawString(Message.leftTime.replace("%%", Long.toString(leftTime)), UpdaterTheme.stringsX, UpdaterTheme.stringsY + 120);
      if (t.error) {
        return;
      }
      BufferedImage img = ImageUtils.genButton(UpdaterTheme.loadbarW, UpdaterTheme.loadbarH, bar);
      try
      {
        int percentw = t.procents * UpdaterTheme.loadbarW / 100;
        g.drawImage(img.getSubimage(0, 0, percentw, UpdaterTheme.loadbarH), UpdaterTheme.loadbarX, UpdaterTheme.loadbarY, null);
        g.drawImage(bar_label, UpdaterTheme.loadbarX + percentw - bar_label.getWidth() / 2 - 10, UpdaterTheme.loadbarY - bar_label.getHeight(), null);
        g.drawString(t.procents + "%", UpdaterTheme.loadbarX + percentw - g.getFontMetrics().stringWidth(t.procents + "%") / 2, UpdaterTheme.loadbarY - bar_label.getHeight() / 2);
      }
      catch (Exception e) {}
    }
    else if (this.type == 5)
    {
      g.drawImage(this.tmpImage, 0, 0, getWidth(), getHeight(), null);
      g.drawImage(ImageUtils.genPanel(OptionsTheme.panelOpt.w, OptionsTheme.panelOpt.h, extpanel), OptionsTheme.panelOpt.x, OptionsTheme.panelOpt.y, OptionsTheme.panelOpt.w, OptionsTheme.panelOpt.h, null);
      g.setFont(g.getFont().deriveFont(LoginTheme.fonttitlesize));
      g.setColor(OptionsTheme.memory.textColor);
      g.drawString(Message.options, OptionsTheme.titleX, OptionsTheme.titleY);
      g.setFont(g.getFont().deriveFont(LoginTheme.fontbasesize));
      g.drawString(Message.memory, OptionsTheme.memory.x, OptionsTheme.memory.y - 5);
    }
    else if (this.type == 55)
    {
      g.drawImage(this.tmpImage, 0, 0, getWidth(), getHeight(), null);
      g.drawImage(ImageUtils.genPanel(OptionsTheme.panelOpt.w, OptionsTheme.panelOpt.h, extpanel), OptionsTheme.panelOpt.x, OptionsTheme.panelOpt.y, OptionsTheme.panelOpt.w, OptionsTheme.panelOpt.h, null);
      g.setFont(g.getFont().deriveFont(LoginTheme.fonttitlesize));
      g.setColor(OptionsTheme.memory.textColor);
      g.drawString(Message.register, RegTheme.titleRegX, RegTheme.titleRegY);
      
      g.setFont(g.getFont().deriveFont(LoginTheme.fontbasesize));
      String textloginReg1 = Message.textloginReg1;
      g.drawString(textloginReg1, RegTheme.textloginReg.x - g.getFontMetrics().stringWidth(textloginReg1), RegTheme.textloginReg.y + 18);
      String textpasswordReg1 = Message.textpasswordReg1;
      g.drawString(textpasswordReg1, RegTheme.textpasswordReg.x - g.getFontMetrics().stringWidth(textpasswordReg1), RegTheme.textpasswordReg.y + 18);
      String textpassword2Reg1 = Message.textpassword2Reg1;
      g.drawString(textpassword2Reg1, RegTheme.textpassword2Reg.x - g.getFontMetrics().stringWidth(textpassword2Reg1), RegTheme.textpassword2Reg.y + 18);
      String textmailReg1 = Message.textmailReg1;
      g.drawString(textmailReg1, RegTheme.textmailReg.x - g.getFontMetrics().stringWidth(textmailReg1), RegTheme.textmailReg.y + 18);
    }
    else if (this.type == 6)
    {
      g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
      g.drawImage(this.pc.skin, PersonalTheme.skinX, PersonalTheme.skinY, PersonalTheme.skinW, PersonalTheme.skinH, null);
      if (Settings.drawTracers)
      {
        g.setColor(Color.GREEN);
        g.drawRect(PersonalTheme.skinX, PersonalTheme.skinY, PersonalTheme.skinW - 1, PersonalTheme.skinH - 1);
      }
      g.drawImage(this.pc.cloak, PersonalTheme.cloakX, PersonalTheme.cloakY, PersonalTheme.cloakW, PersonalTheme.cloakH, null);
      if (Settings.drawTracers)
      {
        g.setColor(Color.GREEN);
        g.drawRect(PersonalTheme.cloakX, PersonalTheme.cloakY, PersonalTheme.cloakW - 1, PersonalTheme.cloakH - 1);
      }
      String ugroupLBL = this.pc.ugroup.equals("Banned") ? Message.ban : this.pc.ugroup.equals("VIP") ? Message.vip : this.pc.ugroup.equals("User") ? Message.user : Message.prem;
      g.setColor(PersonalTheme.ugroup.color);
      g.setFont(BaseUtils.getFont(PersonalTheme.ugroup.fontName, PersonalTheme.ugroup.fontSize));
      g.drawString(ugroupLBL, PersonalTheme.ugroup.x + (PersonalTheme.ugroup.w / 2 - g.getFontMetrics().stringWidth(ugroupLBL) / 2), PersonalTheme.ugroup.y + g.getFontMetrics().getHeight());
      if (this.pc.canUploadCloak)
      {
        g.setColor(PersonalTheme.cloakPrice.color);
        g.setFont(BaseUtils.getFont(PersonalTheme.cloakPrice.fontName, PersonalTheme.cloakPrice.fontSize));
        
        String cloakPriceSTR = Message.realmoney.replace("%%", Long.toString(this.pc.cloakPrice));
        
        g.drawString(cloakPriceSTR, PersonalTheme.cloakPrice.x - g.getFontMetrics().stringWidth(cloakPriceSTR), PersonalTheme.cloakPrice.y + g.getFontMetrics().getHeight());
      }
      String iconmoney = Message.iconmoney.replace("%%", Double.toString(this.pc.iconmoney));
      g.setColor(PersonalTheme.iConomy.color);
      g.setFont(BaseUtils.getFont(PersonalTheme.iConomy.fontName, PersonalTheme.iConomy.fontSize));
      g.drawString(iconmoney, PersonalTheme.iConomy.x - g.getFontMetrics().stringWidth(iconmoney), PersonalTheme.iConomy.y + g.getFontMetrics().getHeight());
      
      String realmoneySTR = Message.realmoney.replace("%%", Long.toString(this.pc.realmoney));
      g.setColor(PersonalTheme.realmoney.color);
      g.setFont(BaseUtils.getFont(PersonalTheme.realmoney.fontName, PersonalTheme.realmoney.fontSize));
      g.drawString(realmoneySTR, PersonalTheme.realmoney.x - g.getFontMetrics().stringWidth(realmoneySTR), PersonalTheme.realmoney.y + g.getFontMetrics().getHeight());
      
      g.setColor(PersonalTheme.prices.color);
      g.setFont(BaseUtils.getFont(PersonalTheme.prices.fontName, PersonalTheme.prices.fontSize));
      
      int j = 0;
      if (this.pc.canBuyVip)
      {
        g.drawString(Message.vipPrice.replace("%%", Long.toString(this.pc.vipPrice)), PersonalTheme.prices.x, PersonalTheme.prices.y + g.getFontMetrics().getHeight() * (j + 1));j++;
      }
      if (this.pc.canBuyPremium)
      {
        g.drawString(Message.premiumPrice.replace("%%", Long.toString(this.pc.premiumPrice)), PersonalTheme.prices.x, PersonalTheme.prices.y + g.getFontMetrics().getHeight() * (j + 1));j++;
      }
      if (this.pc.canBuyUnban)
      {
        g.drawString(Message.unbanPrice.replace("%%", Long.toString(this.pc.unbanPrice)), PersonalTheme.prices.x, PersonalTheme.prices.y + g.getFontMetrics().getHeight() * (j + 1));j++;
      }
      if (this.pc.canExchangeMoney)
      {
        g.drawString(Message.exchangeRate.replace("%%", Long.toString(this.pc.exchangeRate)), PersonalTheme.prices.x, PersonalTheme.prices.y + g.getFontMetrics().getHeight() * (j + 1));j++;
      }
      if (!this.pc.dateofexpire.contains("01.01.1970"))
      {
        g.drawString(Message.dateofexpire.replace("%%", this.pc.dateofexpire), PersonalTheme.prices.x, PersonalTheme.prices.y + g.getFontMetrics().getHeight() * (j + 1));j++;
      }
      if ((this.pc.jobexp != -1) && (this.pc.joblvl != -1))
      {
        g.drawString(Message.jobname.replace("%%", this.pc.jobname), PersonalTheme.prices.x, PersonalTheme.prices.y + g.getFontMetrics().getHeight() * (j + 1));j++;
        g.drawString(Message.joblvl.replace("%%", Integer.toString(this.pc.joblvl)), PersonalTheme.prices.x, PersonalTheme.prices.y + g.getFontMetrics().getHeight() * (j + 1));j++;
        g.drawString(Message.jobexp.replace("%%", Integer.toString(this.pc.jobexp)), PersonalTheme.prices.x, PersonalTheme.prices.y + g.getFontMetrics().getHeight() * (j + 1));j++;
      }
    }
    else if (this.type == 7)
    {
      g.setFont(g.getFont().deriveFont(LoginTheme.fonttitlesize));
      g.drawImage(background_dialog, 0, 0, getWidth(), getHeight(), null);
    }
    if (Settings.drawTracers)
    {
      g.setColor(Color.ORANGE);
      g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
  }

  public void setAuthState(BufferedImage screen) {
      this.reset();
      this.tmpImage = screen;
      this.tmpString = Message.tmpString;
      this.tmpColor = Color.WHITE;
      this.type = 1;
      this.timer = new Timer(50, new ActionListener(){
          boolean used;

          @Override
          public void actionPerformed(ActionEvent e) {
              Panel.this.tindex++;
              if (!this.used) {
                  if (Panel.this.tindex > 10) {
                      this.used = true;
                  }
                  Panel.this.tmpImage.getGraphics().drawImage(ImageUtils.getByIndex(Files.colors, 1, 0), 0, DraggerTheme.dragger.h, Panel.this.getWidth(), Panel.this.getHeight() - DraggerTheme.dragger.h, null);
              }
              if (Panel.this.tindex == 12) {
                  Panel.this.tindex = 0;
              }
              Panel.this.repaint();
          }
      });
      this.timer.start();
  }
  /*
  public void setAuthState(BufferedImage screen)
  {
    reset();
    this.tmpImage = screen;
    this.tmpString = Message.tmpString;
    this.tmpColor = Color.WHITE;
    this.type = 1;
    this.timer = new Timer(50, new ActionListener()
    {
      boolean used = false;
      
      public void actionPerformed(ActionEvent e)
      {
        Panel.access$008(Panel.this);
        if (!this.used)
        {
          if (Panel.this.tindex > 10) {
            this.used = true;
          }
          Panel.this.tmpImage.getGraphics().drawImage(ImageUtils.getByIndex(Files.colors, 1, 0), 0, DraggerTheme.dragger.h, Panel.this.getWidth(), Panel.this.getHeight() - DraggerTheme.dragger.h, null);
        }
        if (Panel.this.tindex == 12) {
          Panel.this.tindex = 0;
        }
        Panel.this.repaint();
      }
    });
    this.timer.start();
  }
  */
  public void reset()
  {
    if (this.timer != null) {
      this.timer.stop();
    }
    this.timer = null;
    this.tmpImage = null;
    this.tmpColor = Color.WHITE;
    this.tmpString = null;
    this.tindex = 0;
  }
  
  public void setUpdateState(String version)
  {
    reset();
    this.tmpString = version;
    this.type = 2;
  }
  
  public void setUpdateStateMC()
  {
    reset();
    this.type = 4;
    this.timer = new Timer(50, new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        Panel.this.repaint();
      }
    });
    this.timer.start();
  }
  
  public void setRegister(BufferedImage screen) {
      this.reset();
      this.tmpImage = screen;
      this.type = 55;
      this.timer = new Timer(50, new ActionListener(){

          @Override
          public void actionPerformed(ActionEvent e) {
              Panel.this.tindex++;
              if (Panel.this.tindex > 10) {
                  Panel.this.timer.stop();
              }
              Panel.this.tmpImage.getGraphics().drawImage(ImageUtils.getByIndex(Files.colors, 1, 0), 0, DraggerTheme.dragger.h, Panel.this.getWidth(), Panel.this.getHeight() - DraggerTheme.dragger.h, null);
              Panel.this.repaint();
          }
      });
      this.timer.start();
  }

  public void setOptions(BufferedImage screen)
  {
    this.reset();
    this.tmpImage = screen;
    this.type = 5;
    this.timer = new Timer(50, new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        //Panel.access$008(Panel.this);
        Panel.this.tindex++;
        if (Panel.this.tindex > 10) {
          Panel.this.timer.stop();
        }
        Panel.this.tmpImage.getGraphics().drawImage(ImageUtils.getByIndex(Files.colors, 1, 0), 0, DraggerTheme.dragger.h, Panel.this.getWidth(), Panel.this.getHeight() - DraggerTheme.dragger.h, null);
        Panel.this.repaint();
      }
    });
    this.timer.start();
  }
  
  
  public void setPersonalState(PersonalContainer pc)
  {
    reset();
    this.pc = pc;
    this.type = 6;
  }
  
  public void setLoadingState(BufferedImage screen, String s)
  {
    reset();
    this.tmpImage = screen;
    this.tmpString = s;
    this.tmpColor = Color.WHITE;
    this.type = 1;
    this.timer = new Timer(50, new ActionListener()
    {
      boolean used = false;
      
      public void actionPerformed(ActionEvent e)
      {
    	Panel.this.tindex++;
        if (!this.used)
        {
          if (Panel.this.tindex > 10) {
            this.used = true;
          }
          Panel.this.tmpImage.getGraphics().drawImage(ImageUtils.getByIndex(Files.colors, 1, 0), 0, DraggerTheme.dragger.h, Panel.this.getWidth(), Panel.this.getHeight() - DraggerTheme.dragger.h, null);
        }
        if (Panel.this.tindex == 12) {
          Panel.this.tindex = 0;
        }
        Panel.this.repaint();
      }
    });
    this.timer.start();
  }
  
  public void setErrorState(String s)
  {
    reset();
    this.type = 3;
    this.tmpString = s;
    repaint();
  }
}
