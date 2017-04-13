package net.launcher.components;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.border.Border;
import net.launcher.components.Textfield;
import net.launcher.utils.BaseUtils;

public class TextfieldStyle {

   public int x = 0;
   public int y = 0;
   public int w = 0;
   public int h = 0;
   public String fontName = "";
   public float fontSize = 1.0F;
   public Color textColor;
   public Color caretColor;
   public BufferedImage texture;
   public Border border;


   public TextfieldStyle(int x, int y, int w, int h, String texture, String fontName, float fontSize, Color textColor, Color caretColor, Border border) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.fontName = fontName;
      this.fontSize = fontSize;
      this.textColor = textColor;
      this.caretColor = caretColor;
      this.texture = BaseUtils.getLocalImage(texture);
      this.border = border;
   }

   public void apply(Textfield text) {
      text.setBounds(this.x, this.y, this.w, this.h);
      text.texture = this.texture;
      text.setFont(BaseUtils.getFont(this.fontName, this.fontSize));
      text.setCaretColor(this.caretColor);
      text.setBackground(this.textColor);
      text.setForeground(this.textColor);
      text.setBorder(this.border);
   }
}
