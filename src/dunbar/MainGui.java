/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dunbar;

import java.awt.AWTEvent;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 * @author MultiTool
 */
//public class MainGui {}
// From http://www.tutorialspoint.com/javaexamples/gui_polygon.htm
public class MainGui {
  public JFrame frame;
  public DrawingPanel drawpanel;
  /* ********************************************************************************* */
  public MainGui() {
  }
  /* ********************************************************************************* */
  public void Init() {
    this.frame = new JFrame();
    this.frame.setTitle("Dunbar");
    this.frame.setSize(1500, 800);
    this.frame.addWindowListener(new WindowAdapter() {
      @Override public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    Container contentPane = this.frame.getContentPane();
    this.drawpanel = new DrawingPanel();
    contentPane.add(this.drawpanel);
    this.drawpanel.BigApp = this;
    frame.setVisible(true);
    AddKeyboardHandlers();// not really used for anything
    this.GenerateNetworks();
  }
  /* ********************************************************************************* */
  public void GenerateNetworks() {
    int NDims = 4;
    this.drawpanel.tg.TestDimension(NDims);
  }
  /* ********************************************************************************* */
  public static class DrawingPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener, ComponentListener, KeyListener {
    MainGui BigApp;
    int ScreenMouseX = 0, ScreenMouseY = 0;
    TestGroup tg = new TestGroup();
    /* ********************************************************************************* */
    public DrawingPanel() {
    }
    /* ********************************************************************************* */
    public void Draw_Me(Graphics2D g2d) {
      this.tg.Draw_Me(g2d);
    }
    /* ********************************************************************************* */
    @Override public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      Draw_Me(g2d);// redrawing everything is overkill for every little change or move. to do: optimize this
      this.repaint();
    }
    /* ********************************************************************************* */
    public void ScreenGrab() {//http://stackoverflow.com/questions/8202253/saving-a-java-2d-graphics-image-as-png-file
      BufferedImage BImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
      Graphics2D cg = BImg.createGraphics();
      this.paintAll(cg);
      try {
        if (ImageIO.write(BImg, "png", new File("./ScreenGrab.png"))) {
          System.out.println("-- saved");
        }
      } catch (IOException e) {// TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    /* ********************************************************************************* */
    @Override public void mouseDragged(MouseEvent me) {
    }
    @Override public void mouseMoved(MouseEvent me) {
      this.ScreenMouseX = me.getX();
      this.ScreenMouseY = me.getY();
    }
    /* ********************************************************************************* */
    @Override public void mouseClicked(MouseEvent me) {
      //this.ScreenGrab();
    }
    @Override public void mousePressed(MouseEvent me) {
      this.repaint();
    }
    @Override public void mouseReleased(MouseEvent me) {
    }
    @Override public void mouseEntered(MouseEvent me) {
    }
    @Override public void mouseExited(MouseEvent me) {
    }
    /* ********************************************************************************* */
    @Override public void mouseWheelMoved(MouseWheelEvent mwe) {
    }
    /* ********************************************************************************* */
    @Override public void componentResized(ComponentEvent ce) {
    }
    @Override public void componentMoved(ComponentEvent ce) {
    }
    @Override public void componentShown(ComponentEvent ce) {
    }
    @Override public void componentHidden(ComponentEvent ce) {
    }
    /* ********************************************************************************* */
    @Override public void keyTyped(KeyEvent ke) {
    }
    @Override public void keyPressed(KeyEvent ke) {
    }
    @Override public void keyReleased(KeyEvent ke) {
    }
  }
  /* ********************************************************************************* */
  public void AddKeyboardHandlers() {// https://tips4java.wordpress.com/2009/08/30/global-event-listeners/
    long EventMask = AWTEvent.MOUSE_MOTION_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK + AWTEvent.KEY_EVENT_MASK;
    EventMask = AWTEvent.KEY_EVENT_MASK;
    //final DrawingPanel dp = this.drawpanel;
    Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
      @Override public void eventDispatched(AWTEvent Event) {
        KeyEvent ke = (KeyEvent) Event;
        HandleKeys(ke);
      }
    }, EventMask);
  }
  boolean KeysEnabled = true;
  /* ********************************************************************************* */
  public void HandleKeys(KeyEvent ke) {
    DrawingPanel dpnl = this.drawpanel;
    System.out.println("keyPressed:" + ke.getKeyCode() + ":" + ke.getExtendedKeyCode() + ":" + ke.getModifiers() + ":" + ke.getKeyChar() + ":" + ke.getModifiersEx());
    char ch = Character.toLowerCase(ke.getKeyChar());
    int keycode = ke.getKeyCode();
    int mod = ke.getModifiers();
    String JsonTxt = "";
    if (KeysEnabled) {
      KeysEnabled = false;// to do: think of a better debouncer
      boolean CtrlPress = ((mod & KeyEvent.CTRL_MASK) != 0);
      if ((keycode == KeyEvent.VK_C) && CtrlPress) {
        //this.drawpanel.ScreenGrab();
//        this.drawpanel.Save_Pictures();
      } else if (keycode == KeyEvent.VK_DELETE) {
      } else if ((keycode == KeyEvent.VK_Q) && CtrlPress) {
        System.exit(0);
      } else if ((keycode == KeyEvent.VK_S && !CtrlPress)) {
      } else if ((keycode == KeyEvent.VK_X) && CtrlPress) {
      } else if ((keycode == KeyEvent.VK_T) && CtrlPress) {
      } else if ((keycode == KeyEvent.VK_E) && CtrlPress) {
      } else if ((keycode == KeyEvent.VK_S) && CtrlPress) {// ctrl S means ScreenGrab
      } else if ((keycode == KeyEvent.VK_O) && CtrlPress) {// ctrl O means open
      } else if (keycode == KeyEvent.VK_ESCAPE) {
      }
      KeysEnabled = true;
    }
    System.out.println(ke.getID());
  }
}
