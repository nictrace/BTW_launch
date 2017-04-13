package net.launcher.components;

import java.awt.image.BufferedImage;
import net.launcher.utils.BaseUtils;

public class PersonalContainer {

   public BufferedImage skin;
   public BufferedImage cloak;
   public boolean canUploadSkin = false;
   public boolean canUploadCloak = false;
   public boolean canBuyVip = false;
   public boolean canBuyPremium = false;
   public boolean canBuyUnban = false;
   public boolean canActivateVaucher = false;
   public boolean canExchangeMoney = false;
   public String ugroup = "User";
   public String dateofexpire = "";
   public int cloakPrice = 0;
   public int vipPrice = 0;
   public int premiumPrice = 0;
   public int unbanPrice = 0;
   public int exchangeRate = 0;
   public double iconmoney = 0.0D;
   public int realmoney = 0;
   public String jobname = "Безработный";
   public int joblvl = 0;
   public int jobexp = 0;


   public PersonalContainer(String[] s, BufferedImage skinImage, BufferedImage cloakImage) {
      String rights = s[0];
      this.canUploadSkin = rights.charAt(0) == 49;
      this.canUploadCloak = rights.charAt(1) == 49;
      this.canBuyVip = rights.charAt(2) == 49;
      this.canBuyPremium = rights.charAt(3) == 49;
      this.canBuyUnban = rights.charAt(4) == 49;
      this.canActivateVaucher = rights.charAt(5) == 49;
      this.canExchangeMoney = rights.charAt(6) == 49;
      this.iconmoney = Double.parseDouble(s[1] + "D");

      try {
         this.realmoney = Integer.parseInt(s[2]);
      } catch (Exception var6) {
         this.realmoney = 0;
      }

      this.cloakPrice = Integer.parseInt(s[3]);
      this.vipPrice = Integer.parseInt(s[4]);
      this.premiumPrice = Integer.parseInt(s[5]);
      this.unbanPrice = Integer.parseInt(s[6]);
      this.exchangeRate = Integer.parseInt(s[7]);
      this.ugroup = s[8];
      this.dateofexpire = BaseUtils.unix2hrd(Long.parseLong(s[9]));
      this.jobname = s[10];
      this.joblvl = Integer.parseInt(s[11]);
      this.jobexp = Integer.parseInt(s[12]);
      this.skin = skinImage;
      this.cloak = cloakImage;
   }
}
