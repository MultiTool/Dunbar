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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    Bleh();
  }
  /* ********************************************************************************* */
  public static class DrawingPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener, ComponentListener, KeyListener {
    MainGui BigApp;
    int ScreenMouseX = 0, ScreenMouseY = 0;
    Cluster cluster0 = new Cluster();
    Cluster cluster1 = new Cluster();
    Cluster cluster2 = new Cluster();
    /* ********************************************************************************* */
    public DrawingPanel() {
      if (true) {
        int NDims = 9;
        if (false) {
          RepeatRandom(NDims);
        }
//        cluster.Create_Random(1 << NDims, NDims);
//        System.out.print("Create_Random");
        for (NDims = 4; NDims < 6; NDims++) {//12
          TestDimension(NDims);
        }
      } else {
        int Num_Nodes = 64;// 64 is 2^6. 
        int Connections_Per_Node = 3;// 8, cube
        Connections_Per_Node = 6;// 64, MaxDist:13.0, hcube is 6 jumps, 64/6=10.666 MaxDist:6.0
        Connections_Per_Node = 8;// 256, MaxDist:19.0, hcube is 8 jumps, 256/8=32 

        Num_Nodes = (int) Math.pow(2, Connections_Per_Node);

        double Fred = Num_Nodes / Connections_Per_Node;
        Fred = Fred / 2;

        cluster0.Fill_With_Nodes(Num_Nodes);
        cluster0.ConnectInnerSparse(Connections_Per_Node);
        cluster0.Medir();
      }
      System.out.print("Done.");
    }
    /* ********************************************************************************* */
    public void RepeatRandom(int NDims) {
      int NumNodes = 1 << NDims;
      double Min = Double.MAX_VALUE, Max = Double.MIN_VALUE;

      System.out.print(" NDims:" + NDims + ", NumNodes:" + NumNodes + ",");
      System.out.println();
      for (int cnt = 0; cnt < 200; cnt++) {
        cluster0.Create_Random(NumNodes, NDims);
        this.cluster0.Medir();
        double Alien2 = this.cluster0.Get_Adjusted_Alienation_Number();
        double InEq2 = this.cluster0.Measure_Inequality();
        Min = Math.min(Min, this.cluster0.Get_Min_Alienation());
        Max = Math.max(Max, this.cluster0.Get_Max_Alienation());
        this.cluster0.Colorize();
        System.out.print(" Alien:" + Alien2 + ", InEq:" + InEq2 + ",");
        System.out.println();
      }
    }
    /* ********************************************************************************* */
    public void TestDimension(int NDims) {
      int NumNodes = 1 << NDims;
      double MaxConnections = NumNodes * (NumNodes - 1);
      double Min = Double.MAX_VALUE, Max = Double.MIN_VALUE;

      System.out.print(" NDims:" + NDims + ", NumNodes:" + NumNodes + ",");

      if (true) {
        cluster0.ConnectHypercube(NDims);
        this.cluster0.Medir();
        double Alien0 = this.cluster0.Get_Adjusted_Alienation_Number();
        double InEq0 = this.cluster0.Measure_Inequality();
        Min = Math.min(Min, this.cluster0.Get_Min_Alienation());
        Max = Math.max(Max, this.cluster0.Get_Max_Alienation());
        this.cluster0.Colorize();
        System.out.print(" Alien0:" + Alien0 + ", InEq0:" + InEq0 + ",");
      }
      if (true) {
        cluster1.Create_Heirarchy(NumNodes, NDims);
        this.cluster1.Medir();
        double Alien1 = this.cluster1.Get_Adjusted_Alienation_Number();
        double InEq1 = this.cluster1.Measure_Inequality();
        Min = Math.min(Min, this.cluster1.Get_Min_Alienation());
        Max = Math.max(Max, this.cluster1.Get_Max_Alienation());
        this.cluster1.Colorize();
        System.out.print(" Alien1:" + Alien1 + ", InEq1:" + InEq1 + ",");
      }
//      System.out.println(""); System.out.println("Create_Random start");//      System.out.println("Create_Random end");
      if (true) {
        cluster2.Create_Random(NumNodes, NDims);
        this.cluster2.Medir();
        double Alien2 = this.cluster2.Get_Adjusted_Alienation_Number();
        double InEq2 = this.cluster2.Measure_Inequality();
        Min = Math.min(Min, this.cluster2.Get_Min_Alienation());
        Max = Math.max(Max, this.cluster2.Get_Max_Alienation());
//        this.cluster.Colorize(Min, Max);
        this.cluster2.Colorize();
        System.out.print(" Alien2:" + Alien2 + ", InEq2:" + InEq2 + ",");
      }
      this.cluster0.Colorize(Min, Max);
      this.cluster1.Colorize(Min, Max);
      this.cluster2.Colorize(Min, Max);
      System.out.println();

//      double Ratio = SumDist0 / SumDist1;
//      double AdjustedScore0 = SumDist0 / MaxConnections;
//      double AdjustedScore1 = SumDist1 / MaxConnections;
//      double AdjustedScore2 = SumDist2 / MaxConnections;
//      System.out.println(" Ratio:" + Ratio);
      //System.out.println(" Ratio:" + Ratio + " AdjustedScore0:" + AdjustedScore0 + " AdjustedScore1:" + AdjustedScore1 + " AdjustedScore2:" + AdjustedScore2);
    }
    /* ********************************************************************************* */
    public void Draw_Me(Graphics2D g2d) {
      DrawingContext dc = new DrawingContext(g2d);
      int wdt, hgt;
      wdt = this.getWidth();
      hgt = this.getHeight();

      Rectangle2D rect = new Rectangle2D.Float();
      rect.setRect(0, 0, wdt, hgt);
      Stroke oldStroke = g2d.getStroke();
      BasicStroke bs = new BasicStroke(5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
      g2d.setStroke(bs);
      g2d.setColor(Color.green);
      //g2d.draw(rect);// green rectangle confidence check for clipping
      g2d.setStroke(oldStroke);

      this.cluster0.Draw_Me(dc);
      this.cluster1.Draw_Me(dc);
      this.cluster2.Draw_Me(dc);
    }
    /* ********************************************************************************* */
    @Override public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      Draw_Me(g2d);// redrawing everything is overkill for every little change or move. to do: optimize this
      this.repaint();
    }
    /* ********************************************************************************* */
    public void save() {//http://stackoverflow.com/questions/8202253/saving-a-java-2d-graphics-image-as-png-file
      BufferedImage BImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
      Graphics2D cg = BImg.createGraphics();
      this.paintAll(cg);
      try {
        if (ImageIO.write(BImg, "png", new File("./output_image.png"))) {
          System.out.println("-- saved");
        }
      } catch (IOException e) {// TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    /* ********************************************************************************* */
    public void save2() {//http://www.java2s.com/Code/Java/2D-Graphics-GUI/SavingaGeneratedGraphictoaPNGorJPEGFile.htm
      int width = 1300;
      int height = 1000;

      BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = bufferedImage.createGraphics();
      this.Draw_Me(g2d);
//      g2d.setColor(Color.white);
//      g2d.fillRect(0, 0, width, height);
//      g2d.setColor(Color.black);
//      g2d.fillOval(0, 0, width, height);

      g2d.dispose();
      RenderedImage rendImage = bufferedImage;
      try {
        File file = new File("newimage.png");
        ImageIO.write(rendImage, "png", file);

        file = new File("newimage.jpg");
        ImageIO.write(rendImage, "jpg", file);
      } catch (IOException e) {
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
      this.save();
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
  public void Bleh() {// https://tips4java.wordpress.com/2009/08/30/global-event-listeners/
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
        //this.drawpanel.save();
        this.drawpanel.save2();
      } else if (keycode == KeyEvent.VK_DELETE) {
      } else if ((keycode == KeyEvent.VK_Q) && CtrlPress) {
        System.exit(0);
      } else if ((keycode == KeyEvent.VK_S && !CtrlPress)) {
      } else if ((keycode == KeyEvent.VK_X) && CtrlPress) {
      } else if ((keycode == KeyEvent.VK_T) && CtrlPress) {
      } else if ((keycode == KeyEvent.VK_E) && CtrlPress) {
      } else if ((keycode == KeyEvent.VK_S) && CtrlPress) {// ctrl S means save
      } else if ((keycode == KeyEvent.VK_O) && CtrlPress) {// ctrl O means open
      } else if (keycode == KeyEvent.VK_ESCAPE) {
      }
      KeysEnabled = true;
    }
    System.out.println(ke.getID());
  }
}
