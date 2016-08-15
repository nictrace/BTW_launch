package net.launcher.components;

import java.awt.image.BufferedImage;
import net.launcher.components.Dragbutton;
import net.launcher.utils.BaseUtils;

public class DragbuttonStyle {

   public int hx = 0;
   public int hy = 0;
   public int hw = 0;
   public int hh = 0;
   public int cx = 0;
   public int cy = 0;
   public int cw = 0;
   public int ch = 0;
   public boolean visible = false;
   public BufferedImage texture;


   public DragbuttonStyle(int hx, int hy, int hw, int hh, int cx, int cy, int cw, int ch, String texture, boolean visible) {
      this.hx = hx;
      this.hy = hy;
      this.hw = hw;
      this.hh = hh;
      this.cx = cx;
      this.cy = cy;
      this.cw = cw;
      this.ch = ch;
      this.visible = visible;
      this.texture = BaseUtils.getLocalImage(texture);
   }

   public void apply(Dragbutton hide, Dragbutton close) {
      hide.setVisible(this.visible);
      hide.setBounds(this.hx, this.hy, this.hw, this.hh);
      close.setVisible(this.visible);
      close.setBounds(this.cx, this.cy, this.cw, this.ch);
      hide.img1 = this.texture.getSubimage(0, 0, this.texture.getWidth() / 3, this.texture.getHeight() / 2);
      hide.img2 = this.texture.getSubimage(this.texture.getWidth() / 3, 0, this.texture.getWidth() / 3, this.texture.getHeight() / 2);
      hide.img3 = this.texture.getSubimage(this.texture.getWidth() / 3 * 2, 0, this.texture.getWidth() / 3, this.texture.getHeight() / 2);
      close.img1 = this.texture.getSubimage(0, this.texture.getHeight() / 2, this.texture.getWidth() / 3, this.texture.getHeight() / 2);
      close.img2 = this.texture.getSubimage(this.texture.getWidth() / 3, this.texture.getHeight() / 2, this.texture.getWidth() / 3, this.texture.getHeight() / 2);
      close.img3 = this.texture.getSubimage(this.texture.getWidth() / 3 * 2, this.texture.getHeight() / 2, this.texture.getWidth() / 3, this.texture.getHeight() / 2);
   }
}
