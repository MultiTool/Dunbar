/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dunbar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author MultiTool
 */
public class TestGroup {
  Cluster cluster0 = new Cluster();
  Cluster cluster1 = new Cluster();
  Cluster cluster2 = new Cluster();
  double Min = Double.MAX_VALUE, Max = Double.MIN_VALUE;
  /* ********************************************************************************* */
  public void RunTests() {
    int NDims = 2;
    while (NDims < 7) {// Up to 6 dimensions, 64 nodes are drawable
      this.Save_Pictures(NDims);
      NDims++;
    }
    while (NDims < 12) {// Up to 11 dimensions, 2048 nodes.  Beyond that we blow out memory. 
      this.TestDimension(NDims);
      NDims++;
    }
  }
  /* ********************************************************************************* */
  public void TestDimension(int NDims) {
    int NumNodes = 1 << NDims;
    Min = Double.MAX_VALUE;
    Max = Double.MIN_VALUE;

    System.out.print(" NDims:" + NDims + ", NumNodes:" + NumNodes + ",");

    if (true) {
      cluster0.ConnectHypercube(NDims);
      this.cluster0.Medir();
      double Alien0 = this.cluster0.GetSave_Adjusted_Alienation_Number();
      double InEq0 = this.cluster0.GetSave_Inequality();
      Min = Math.min(Min, this.cluster0.Get_Min_Alienation());
      Max = Math.max(Max, this.cluster0.Get_Max_Alienation());
      System.out.print(" Alien0:" + Alien0 + ", InEq0:" + InEq0 + ",");
    }
    if (true) {
      cluster1.Create_Hierarchy(NumNodes, NDims);
      this.cluster1.Medir();
      double Alien1 = this.cluster1.GetSave_Adjusted_Alienation_Number();
      double InEq1 = this.cluster1.GetSave_Inequality();
      Min = Math.min(Min, this.cluster1.Get_Min_Alienation());
      Max = Math.max(Max, this.cluster1.Get_Max_Alienation());
      System.out.print(" Alien1:" + Alien1 + ", InEq1:" + InEq1 + ",");
    }
    if (true) {
      cluster2.Create_Random(NumNodes, NDims);
      this.cluster2.Medir();
      double Alien2 = this.cluster2.GetSave_Adjusted_Alienation_Number();
      double InEq2 = this.cluster2.GetSave_Inequality();
      Min = Math.min(Min, this.cluster2.Get_Min_Alienation());
      Max = Math.max(Max, this.cluster2.Get_Max_Alienation());
      System.out.print(" Alien2:" + Alien2 + ", InEq2:" + InEq2 + ",");
    }
    if (false) {
      cluster2 = Farm.Evolve(NumNodes, NDims);// instead of just random, use genalg to evolve an optimized random network
      this.cluster2.Medir();
      double Alien2 = this.cluster2.GetSave_Adjusted_Alienation_Number();
      double InEq2 = this.cluster2.GetSave_Inequality();
      Min = Math.min(Min, this.cluster2.Get_Min_Alienation());
      Max = Math.max(Max, this.cluster2.Get_Max_Alienation());
      System.out.print(" Alien2:" + Alien2 + ", InEq2:" + InEq2 + ",");
    }
    this.cluster0.Colorize(Min, Max);
    this.cluster1.Colorize(Min, Max);
    this.cluster2.Colorize(Min, Max);
    System.out.println();
  }
  /* ********************************************************************************* */
  public void Save_Pictures(int NDims) {//http://www.java2s.com/Code/Java/2D-Graphics-GUI/SavingaGeneratedGraphictoaPNGorJPEGFile.htm
    this.TestDimension(NDims);
    int scale = 4;
    int width = 1300 * scale;
    int height = 1000 * scale;

    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = bufferedImage.createGraphics();
    g2d.scale(scale, scale);
    this.Draw_Me(g2d);
    g2d.dispose();
    RenderedImage rendImage = bufferedImage;
    String numtxt = String.format("%d", NDims);
    try {
      File file = new File("Dunbar_Dim" + numtxt + ".png");
      ImageIO.write(rendImage, "png", file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  /* ********************************************************************************* */
  public void Draw_Me(Graphics2D g2d) {
    DrawingContext dc = new DrawingContext(g2d);
    this.cluster0.Draw_Me(dc);
    if (true) {
      this.cluster1.Draw_Me(dc);
      this.cluster2.Draw_Me(dc);
    }
    this.Draw_Scale_Bar(dc, 900, 400, this.Min, this.Max);
  }
  /* ********************************************************************************* */
  public void Draw_Scale_Bar(DrawingContext ParentDC, int XLoc, int YLoc, double Min, double Max) {
    Graphics2D g2d = ParentDC.gr;
    int wdt = 50, hgt = 200;
    float[] dist = {0.0f, 0.5f, 1.0f};
    Color[] colors = {Base.ToBlackBody(1.0), Base.ToBlackBody(0.5), Base.ToBlackBody(0.0)};
    LinearGradientPaint lgp = new LinearGradientPaint(XLoc, YLoc, XLoc, YLoc + hgt, dist, colors);
    g2d.setPaint(lgp);
    g2d.fillRect(XLoc, YLoc, wdt, hgt);

    // plot numbers
    String MinTxt = String.format("%1$.1f", Min);
    String MaxTxt = String.format("%1$.1f", Max);
    g2d.setColor(Color.black);
    g2d.drawString(MinTxt, XLoc + wdt + 2, YLoc + hgt);
    g2d.setColor(Color.black);
    g2d.drawString(MaxTxt, XLoc + wdt + 2, YLoc + 0);
  }
}
