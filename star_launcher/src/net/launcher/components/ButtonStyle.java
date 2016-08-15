package net.launcher.components;

import java.awt.Color;
import java.awt.image.BufferedImage;

import net.launcher.utils.BaseUtils;

public class ButtonStyle {

   public int x = 0;
   public int y = 0;
   public int w = 0;
   public int h = 0;
   public String fontName = "";
   public float fontSize = 1.0F;
   public Color color;
   public boolean visible = false;
   public Align align;
   public BufferedImage texture;


   public ButtonStyle(int x, int y, int w, int h, String fontName, String texture, float fontSize, Color color, boolean visible, Align align) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.fontName = fontName;
      this.fontSize = fontSize;
      this.color = color;
      this.visible = visible;
      this.align = align;
      this.texture = BaseUtils.getLocalImage(texture);
   }

   public void apply(Button button) {
      button.setVisible(this.visible);
      button.setBounds(this.x, this.y, this.w, this.h);
      button.setForeground(this.color);
      button.setFont(BaseUtils.getFont(this.fontName, this.fontSize));
      button.setHorizontalAlignment(this.align == Align.LEFT?2:(this.align == Align.CENTER?0:4));

      int i = this.texture.getHeight() / 4;

      button.defaultTX = this.texture.getSubimage(0, 0, this.texture.getWidth(), i);
      button.rolloverTX = this.texture.getSubimage(0, i, this.texture.getWidth(), i);
      button.pressedTX = this.texture.getSubimage(0, i * 2, this.texture.getWidth(), i);
      button.lockedTX = this.texture.getSubimage(0, i * 3, this.texture.getWidth(), i);
   }
}
