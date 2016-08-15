package net.launcher.components;

import java.awt.Color;
import net.launcher.components.Title;
import net.launcher.utils.BaseUtils;

public class TitleStyle {

   public int x = 0;
   public int y = 0;
   public int w = 0;
   public int h = 0;
   public String fontName = "";
   public float fontSize = 1.0F;
   public Color textColor;


   public TitleStyle(int x, int y, int w, int h, String fontName, float fontSize, Color textColor) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.fontName = fontName;
      this.fontSize = fontSize;
      this.textColor = textColor;
   }

   public void apply(Title title) {
      title.setBounds(this.x, this.y, this.w, this.h);
      title.setFont(BaseUtils.getFont(this.fontName, this.fontSize));
      title.setBackground(this.textColor);
      title.setForeground(this.textColor);
      title.tt = this;
   }
}
