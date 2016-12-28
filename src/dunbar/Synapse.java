package dunbar;

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
    int x0, y0, x1, y1;
    x0 = (int) this.USNode.XLoc;
    y0 = (int) this.USNode.YLoc;
    x1 = (int) this.DSNode.XLoc;
    y1 = (int) this.DSNode.YLoc;
    GradientPaint gradient = new GradientPaint(x0, y0, this.USNode.color, x1, y1, this.DSNode.color);
    ParentDC.gr.setPaint(gradient);
    //ParentDC.gr.setColor(Color.black);
    ParentDC.gr.drawLine(x0, y0, x1, y1);
  }
}
