package net.launcher.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JButton;
import net.launcher.MusPlay;
import net.launcher.run.Settings;

public class Dragbutton extends JButton implements MouseListener, MouseMotionListener {

   private static final long serialVersionUID = 1L;
   public BufferedImage img1 = (BufferedImage)this.createImage(1, 1);
   public BufferedImage img2 = (BufferedImage)this.createImage(1, 1);
   public BufferedImage img3 = (BufferedImage)this.createImage(1, 1);
   private boolean entered = false;
   private boolean pressed = false;


   public Dragbutton() {
      this.addMouseListener(this);
      this.addMouseMotionListener(this);
      this.setBorderPainted(false);
      this.setContentAreaFilled(false);
      this.setFocusPainted(false);
      this.setOpaque(false);
      this.setFocusable(false);
      this.setCursor(Cursor.getPredefinedCursor(12));
   }

   protected void paintComponent(Graphics maing) {
      Graphics2D g = (Graphics2D)maing.create();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      if(Settings.drawTracers) {
         g.setColor(Color.CYAN);
         g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
      }

      if(this.entered && !this.pressed) {
         g.drawImage(this.img2, 0, 0, this.getWidth(), this.getHeight(), (ImageObserver)null);
      }

      if(!this.entered) {
         g.drawImage(this.img1, 0, 0, this.getWidth(), this.getHeight(), (ImageObserver)null);
      }

      if(this.pressed && this.entered) {
         this.entered = false;

         try {
            new MusPlay("click.mp3");
         } catch (Exception var4) {
            ;
         }

         g.drawImage(this.img3, 0, 0, this.getWidth(), this.getHeight(), (ImageObserver)null);
         this.pressed = false;
      }

      g.dispose();
      super.paintComponent(maing);
   }

   public void mouseDragged(MouseEvent e) {}

   public void mouseMoved(MouseEvent e) {}

   public void mouseClicked(MouseEvent e) {}

   public void mousePressed(MouseEvent e) {
      this.pressed = !this.pressed;
      this.repaint();
   }

   public void mouseReleased(MouseEvent e) {}

   public void mouseEntered(MouseEvent e) {
      this.entered = true;
      this.repaint();
   }

   public void mouseExited(MouseEvent e) {
      this.entered = false;
      this.repaint();
   }
}
