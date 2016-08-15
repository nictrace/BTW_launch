package net.launcher.components;

import java.awt.Color;
import net.launcher.components.Serverbar;
import net.launcher.utils.BaseUtils;

public class ServerbarStyle {

   public int x = 0;
   public int y = 0;
   public int w = 0;
   public int h = 0;
   public String fontName = "";
   public float fontSize = 1.0F;
   public Color textColor;
   public boolean useIcon = true;


   public ServerbarStyle(int x, int y, int w, int h, String fontName, float fontSize, Color textColor, boolean useIcon) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.fontName = fontName;
      this.fontSize = fontSize;
      this.textColor = textColor;
      this.useIcon = useIcon;
   }

   public void apply(Serverbar serverbar) {
      serverbar.setBounds(this.x, this.y, this.w, this.h);
      serverbar.setFont(BaseUtils.getFont(this.fontName, this.fontSize));
      serverbar.setBackground(this.textColor);
      serverbar.setForeground(this.textColor);
      serverbar.sb = this;
   }
}
