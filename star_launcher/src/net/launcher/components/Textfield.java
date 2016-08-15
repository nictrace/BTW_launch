package net.launcher.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JTextField;
import net.launcher.run.Settings;
import net.launcher.utils.ImageUtils;

public class Textfield extends JTextField {

   private static final long serialVersionUID = 1L;
   public BufferedImage texture;


   public Textfield() {
      this.setOpaque(false);
   }

   protected void paintComponent(Graphics maing) {
      Graphics2D g = (Graphics2D)maing.create();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.drawImage(ImageUtils.genButton(this.getWidth(), this.getHeight(), this.texture), 0, 0, this.getWidth(), this.getHeight(), (ImageObserver)null);
      if(Settings.drawTracers) {
         g.setColor(Color.PINK);
         g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
      }

      g.dispose();
      super.paintComponent(maing);
   }
}
