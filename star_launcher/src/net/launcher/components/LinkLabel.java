/*
 * Decompiled with CFR 0_114.
 */
package net.launcher.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import net.launcher.run.Settings;
import net.launcher.utils.BaseUtils;

public class LinkLabel
extends JLabel {
    private static final long serialVersionUID = 1;
    public Color idleColor = Color.WHITE;
    public Color activeColor = Color.WHITE;

    public LinkLabel(String title, final String url) {
        this.setText(title);
        this.setOpaque(false);
        this.setForeground(this.idleColor);
        this.setCursor(Cursor.getPredefinedCursor(12));
        this.addMouseListener(new MouseListener(){

            @Override
            public void mouseReleased(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
                LinkLabel.this.setForeground(LinkLabel.this.idleColor);
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
                LinkLabel.this.setForeground(LinkLabel.this.activeColor);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1) {
                    BaseUtils.openURL(url);
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (Settings.drawTracers) {
            g.setColor(Color.GRAY);
            g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
        }
    }

}

