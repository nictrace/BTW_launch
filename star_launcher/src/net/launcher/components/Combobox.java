package net.launcher.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.JComponent;

import net.launcher.MusPlay;
import net.launcher.run.Settings;
import net.launcher.utils.BaseUtils;
import net.launcher.utils.ImageUtils;

public class Combobox extends JComponent implements MouseListener, MouseMotionListener {

   private static final long serialVersionUID = 1L;
   public final String[] elements;
   public int initialy = 0;
   private boolean entered = false;
   private boolean pressed = false;
   private int x = 0;
   private int y = 0;
   private int selected = 0;
   public BufferedImage defaultTX;
   public BufferedImage rolloverTX;
   public BufferedImage pressedTX;
   public BufferedImage selectedTX;
   public BufferedImage panelTX;


   public Combobox(String[] elements, int y) {
	   super();
       this.elements = elements;
       this.initialy = y;
       this.addMouseListener(this);
       this.addMouseMotionListener(this);
   }

   @Override
public void paintComponent(Graphics gmain)
   {
      Graphics2D g = (Graphics2D)gmain;
      int w = this.getWidth();
      if(this.pressed)
      {
         g.drawImage(ImageUtils.genButton(w, this.pressedTX.getHeight(), this.pressedTX), 0, this.getHeight() - this.pressedTX.getHeight(), w, this.pressedTX.getHeight(), (ImageObserver)null);
         int righth = this.pressedTX.getHeight() * (this.elements.length + 1);
         int righty = this.initialy + this.pressedTX.getHeight() - righth;

         if(this.getY() != righty || this.getHeight() != righth)
         {
            setLocation(this.getX(), righty);
            setSize(this.getWidth(), righth);
            y = this.getHeight();
            return;
         }

         g.drawImage(ImageUtils.genPanel(w, this.getHeight() - this.pressedTX.getHeight(), this.panelTX), 0, 0, w, this.getHeight() - this.pressedTX.getHeight(), (ImageObserver)null);
         if(this.entered && this.y / this.pressedTX.getHeight() < this.elements.length)
         {
            g.drawImage(ImageUtils.genButton(w, this.selectedTX.getHeight(), this.selectedTX), 0, 
            		y / this.pressedTX.getHeight() * this.pressedTX.getHeight(),
            		w, this.pressedTX.getHeight(),null);
         }

         for(int i = 0; i < this.elements.length; ++i)
         {
            g.drawString(this.elements[i], 5, this.selectedTX.getHeight() * (i + 1) - g.getFontMetrics().getHeight() / 2);
         }

         g.drawString(elements[selected], 5, selectedTX.getHeight() * (this.elements.length + 1) - g.getFontMetrics().getHeight() / 2);
      } else if(entered) {
         int righth = pressedTX.getHeight();
         if(this.getY() != this.initialy || this.getHeight() != righth)
         {
            this.setLocation(this.getX(), this.initialy);
            this.setSize(this.getWidth(), righth);
            return;
         }

         g.drawImage(ImageUtils.genButton(w, this.rolloverTX.getHeight(), this.rolloverTX), 0, 0, w, this.rolloverTX.getHeight(), (ImageObserver)null);
         g.drawString(this.elements[this.selected], 5, this.rolloverTX.getHeight() - g.getFontMetrics().getHeight() / 2);
      } else 
      {
         int righth = pressedTX.getHeight();
         if(this.getY() != this.initialy || this.getHeight() != righth) {
            this.setLocation(this.getX(), this.initialy);
            this.setSize(this.getWidth(), righth);
            return;
         }
         g.drawImage(ImageUtils.genButton(w, this.defaultTX.getHeight(), this.defaultTX), 0, 0, w, this.defaultTX.getHeight(), (ImageObserver)null);
         g.drawString(this.elements[this.selected], 5, this.rolloverTX.getHeight() - g.getFontMetrics().getHeight() / 2);
      }

      if(Settings.drawTracers) {
         g.setColor(Color.GREEN);
         g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
      }
      g.dispose();
   }

   @Override
public void mouseClicked(MouseEvent e) {
	   if(e.getButton() != MouseEvent.BUTTON1) return;
	   if(pressed && y / pressedTX.getHeight() < elements.length)
	   {
		   selected = y / pressedTX.getHeight();
           entered = BaseUtils.contains(this.x, this.y, this.getX(), this.getY(), this.getWidth(), this.getHeight());
       }
       try {
    	   new MusPlay("click.mp3");
       } catch (Exception e1) { }
       pressed = !pressed;
       repaint();
   }

	@Override
	public void mouseEntered(MouseEvent e) 
	{
	   entered = true;
	   repaint();
	}


	@Override
	public void mouseExited(MouseEvent e) {
      entered = false;
      repaint();
	}


	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
      y = e.getY();
      repaint();
	}

	public int getSelectedIndex() {
      return selected;
	}

   public String getSelected() {
      try {
         return elements[selected];
      } catch (Exception e) {
         return elements[0];
      }
   }

   public boolean setSelectedIndex(int i) {
      if(elements.length <= i) return false;
      selected = i;
      this.repaint();
      return true;
   }

   public boolean getPressed()
   {
      return pressed;
   }
}
