package net.launcher.components;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import net.launcher.utils.BaseUtils;

public class CheckboxStyle {

   public int x = 0;
   public int y = 0;
   public int w = 0;
   public int h = 0;
   public String fontName;
   public float fontSize = 1.0F;
   public Color color;
   public boolean visible = false;
   public BufferedImage texture;


   public CheckboxStyle(int x, int y, int w, int h, String fontName, String texture, float fontSize, Color color, boolean visible) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.fontName = fontName;
      this.fontSize = fontSize;
      this.color = color;
      this.visible = visible;
      this.texture = BaseUtils.getLocalImage(texture);
   }

   public void apply(Checkbox checkbox) {
      checkbox.setVisible(this.visible);
      checkbox.setBounds(this.x, this.y, this.w, this.h);
      checkbox.setForeground(this.color);
      checkbox.setFont(BaseUtils.getFont(this.fontName, this.fontSize));
      
      int i = this.texture.getWidth() / 4;
      
      checkbox.defaultTX = this.texture.getSubimage(0, 0, i, i);
      checkbox.rolloverTX = this.texture.getSubimage(i, 0, i, i);
      checkbox.selectedTX = this.texture.getSubimage(i * 2, 0, i, i);
      checkbox.selectedRolloverTX = this.texture.getSubimage(i * 3, 0, i, i);

      checkbox.setIcon(new ImageIcon(checkbox.defaultTX));
      checkbox.setRolloverIcon(new ImageIcon(checkbox.rolloverTX));
      checkbox.setSelectedIcon(new ImageIcon(checkbox.selectedTX));
      checkbox.setRolloverSelectedIcon(new ImageIcon(checkbox.selectedRolloverTX));
   }
}
