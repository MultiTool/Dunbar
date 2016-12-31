package dunbar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;

/**
 *
 * @author MultiTool
 */
public class Synapse implements IDrawable {
  public Node USNode, DSNode;
  public Synapse MyMirror = null;
  public Synapse() {
  }
  /* ********************************************************************************* */
  public double GetFire() {
    return 0;
  }
  /* ********************************************************************************* */
  @Override public void Draw_Me(DrawingContext ParentDC) {
    int x0, y0, x1, y1, Radius0, Radius1;
    double dx = this.DSNode.XLoc - this.USNode.XLoc;
    double dy = this.DSNode.YLoc - this.USNode.YLoc;
    x0 = (int) this.USNode.XLoc;
    y0 = (int) this.USNode.YLoc;
    x1 = (int) this.DSNode.XLoc;
    y1 = (int) this.DSNode.YLoc;
    Radius0 = (int) this.USNode.Radius;
    Radius1 = (int) this.DSNode.Radius;
    double dist = Math.hypot(dx, dy);
    double rescale = (dist - Radius1) / dist;
    dx = dx * rescale;
    dy = dy * rescale;
    x1 = (int) (x0 + dx);
    y1 = (int) (y0 + dy);

    GradientPaint gradient = new GradientPaint(x0, y0, this.USNode.color, x1, y1, this.DSNode.color);
    ParentDC.gr.setPaint(gradient);
    //ParentDC.gr.setColor(Color.black);
    ParentDC.gr.setStroke(new BasicStroke(4));
    ParentDC.gr.drawLine(x0, y0, x1, y1);
  }
}
