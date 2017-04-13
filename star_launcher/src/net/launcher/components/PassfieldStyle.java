package net.launcher.components;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.border.Border;
import net.launcher.components.Passfield;
import net.launcher.utils.BaseUtils;

public class PassfieldStyle {

   public int x = 0;
   public int y = 0;
   public int w = 0;
   public int h = 0;
   public String fontName = "";
   public String echoChar = "";
   public float fontSize = 1.0F;
   public Color textColor;
   public Color caretColor;
   public BufferedImage texture;
   public Border border;


   public PassfieldStyle(int x, int y, int w, int h, String texture, String fontName, float fontSize, Color textColor, Color caretColor, String echoChar, Border border) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.fontName = fontName;
      this.fontSize = fontSize;
      this.textColor = textColor;
      this.caretColor = caretColor;
      this.echoChar = echoChar;
      this.texture = BaseUtils.getLocalImage(texture);
      this.border = border;
   }

   public void apply(Passfield pass) {
      pass.setBounds(this.x, this.y, this.w, this.h);
      pass.texture = this.texture;
      pass.setFont(BaseUtils.getFont(this.fontName, this.fontSize));
      pass.setCaretColor(this.caretColor);
      pass.setBackground(this.textColor);
      pass.setForeground(this.textColor);
      pass.setBorder(this.border);
   }
}
