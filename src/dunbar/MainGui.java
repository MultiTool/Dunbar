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
import java.util.ArrayList;
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
    this.frame.setTitle("BackpropJ");
    this.frame.setSize(700, 800);
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
  }
  /* ********************************************************************************* */
  public static class DrawingPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener, ComponentListener, KeyListener {
    MainGui BigApp;
    int ScreenMouseX = 0, ScreenMouseY = 0;
    Cluster cluster = new Cluster();
    /* ********************************************************************************* */
    public DrawingPanel() {
      if (true) {
        for (int NDims = 11; NDims < 16; NDims++) {
          TestDimension(NDims);
        }
      } else if (true) {
        //cluster.Create_Heirarchy(1 << 3, 3);
        //cluster.Create_Heirarchy(1 << 4, 4);
        //.Create_Heirarchy(1 << 5, 5);
        cluster.Create_Heirarchy(1 << 8, 8);
        //cluster.ConnectHypercube(3);
        this.cluster.Medir();
        double SumDist = this.cluster.Get_Alienation_Number();
        System.out.print("SumDist:" + SumDist);
        // SumDist:146.0 sum of everyone's route table, alienation number for heirarchy 
        // cube is (1*3 + 2*3 + 3*1)*8 = 96 alienation number. this is correct
        // SumDist:96.0 for 3d cube
        // SumDist:130.0 for hierarchy of 3d cube
        // SumDist:512.0 for 4d cube
        // SumDist:696.0 for hierarchy of 4d cube
        // SumDist:2560.0 for 5d cube
        // SumDist:3474.0 for hierarchy of 5d cube
        // 2560/3474 = 0.73690270581

        // SumDist0:262144.0 SumDist1:325490.0 Ratio:0.8053826538449722 AdjustedScore0:4.0 AdjustedScore1:4.966583251953125
        // SumDist:262144.0 for 8d cube
        // SumDist:407826.0 for hierarchy of 8d cube 
        // 262144/407826 = 0.64278393236 
        // SumDist:24.0 for 2d cube (1*2 + 2*1)*4 = 16 = wrong!!!
        //  for square, wrong is (1*2 + 2*2paths)*4 = 6*4 = 24, counting multi equal paths? 
        //  wrong cube is (1*3 + 2*6 + 3*6)*8 = 264
      } else {
        int Num_Nodes = 64;// 64 is 2^6. 
        int Connections_Per_Node = 3;// 8, cube
        Connections_Per_Node = 6;// 64, MaxDist:13.0, hcube is 6 jumps, 64/6=10.666 MaxDist:6.0
        Connections_Per_Node = 8;// 256, MaxDist:19.0, hcube is 8 jumps, 256/8=32 

        Num_Nodes = (int) Math.pow(2, Connections_Per_Node);

        double Fred = Num_Nodes / Connections_Per_Node;
        Fred = Fred / 2;

        cluster.Fill_With_Nodes(Num_Nodes);
        cluster.ConnectInnerSparse(Connections_Per_Node);
        Medir();
      }
      boolean nop = true;
      System.out.print("Done.");
    }
    /* ********************************************************************************* */
    public void TestDimension(int NDims) {
      int NumNodes = 1 << NDims;
      double MaxConnections = NumNodes * (NumNodes - 1);

      System.out.print(" NDims:" + NDims + " NumNodes:" + NumNodes);

      cluster.ConnectHypercube(NDims);
      this.cluster.Medir();
      double SumDist0 = this.cluster.Get_Alienation_Number();
      System.out.print(" SumDist0:" + SumDist0);

      cluster.Create_Heirarchy(NumNodes, NDims);
      this.cluster.Medir();
      double SumDist1 = this.cluster.Get_Alienation_Number();
      System.out.print(" SumDist1:" + SumDist1);

      double Ratio = SumDist0 / SumDist1;
      double AdjustedScore0 = SumDist0 / MaxConnections;
      double AdjustedScore1 = SumDist1 / MaxConnections;

      System.out.println(" Ratio:" + Ratio + " AdjustedScore0:" + AdjustedScore0 + " AdjustedScore1:" + AdjustedScore1);
    }
    /* ********************************************************************************* */
    public void Medir() {
      int gencnt = 0;
      //System.out.println("SendFirstPacket");
      cluster.SendFirstPacket();
      while (cluster.Medir_Gen()) {
        //System.out.println("Medir_Gen:" + gencnt);
        gencnt++;
      }
      Node nd = this.cluster.NodeList.get(5);
      double MaxDist = nd.ReportStats();
      System.out.println("MaxDist:" + MaxDist);//MaxDist:37.0
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
      g2d.draw(rect);// green rectangle confidence check for clipping
      g2d.setStroke(oldStroke);

      this.cluster.Draw_Me(dc);
    }
    /* ********************************************************************************* */
    @Override public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      Draw_Me(g2d);// redrawing everything is overkill for every little change or move. to do: optimize this
      this.repaint();
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

}
