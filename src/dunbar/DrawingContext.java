package dunbar;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 *
 * @author MultiTool
 */
// Every IDrawable has a bounding box, and every DrawingContext also has a bounding box for clipping. 
// Drawing will always be called from the top, and the bounding box will define what to draw. 
/* ********************************************************************************* */
public final class DrawingContext {// Let's be final until we can't anymore
  public Graphics2D gr;
  public int RecurseDepth;
  /* ********************************************************************************* */
  public DrawingContext() {
    this.RecurseDepth = 0;
  }
  /* ********************************************************************************* */
  public DrawingContext(Graphics2D gr2d) {
    this();
    this.gr = gr2d;
  }
  /* ********************************************************************************* */
  public DrawingContext(DrawingContext Fresh_Parent) {
    this.gr = Fresh_Parent.gr;
    this.RecurseDepth = Fresh_Parent.RecurseDepth + 1;
  }
}
