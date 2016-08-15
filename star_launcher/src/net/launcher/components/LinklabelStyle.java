package net.launcher.components;

import java.awt.Color;
import net.launcher.components.LinkLabel;
import net.launcher.utils.BaseUtils;

public class LinklabelStyle {

   public int x = 0;
   public int y = 0;
   public int margin = 0;
   public String fontName = "";
   public float fontSize = 1.0F;
   public Color idleColor;
   public Color activeColor;


   public LinklabelStyle(int x, int y, int margin, String fontName, float fontSize, Color idleColor, Color activeColor) {
      this.x = x;
      this.y = y;
      this.fontName = fontName;
      this.fontSize = fontSize;
      this.idleColor = idleColor;
      this.activeColor = activeColor;
   }

   public void apply(LinkLabel link) {
      link.setBounds(this.x, this.y, 0, 0);
      link.idleColor = this.idleColor;
      link.activeColor = this.activeColor;
      link.setFont(BaseUtils.getFont(this.fontName, this.fontSize));
   }
}
