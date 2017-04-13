package net.launcher.components;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.launcher.components.Frame;

public class Dragger extends JPanel {

   private static final long serialVersionUID = 1L;
   private int x = 0;
   private int y = 0;


   public Dragger() {
      this.setOpaque(false);
      this.setLayout(new BorderLayout());
      this.setBorder(new EmptyBorder(0, 10, 0, 10));
      this.addMouseMotionListener(new MouseMotionAdapter() {
         public void mouseDragged(MouseEvent e) {
            Frame.main.setLocation(e.getX() + Frame.main.getX() - Dragger.this.x, e.getY() + Frame.main.getY() - Dragger.this.y);
         }
      });
      this.addMouseListener(new MouseListener() {
         public void mousePressed(MouseEvent e) {
            Dragger.this.x = e.getX();
            Dragger.this.y = e.getY();
         }
         public void mouseClicked(MouseEvent event) {}
         public void mouseEntered(MouseEvent arg0) {}
         public void mouseExited(MouseEvent arg0) {}
         public void mouseReleased(MouseEvent arg0) {}
      });
   }
}
