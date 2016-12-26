/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dunbar;

/**
 *
 * @author MultiTool
 */
public class Tracer {
  public Node Sender = null;
  public double Distance = 0;
  public int NumHops = 0;
  public Tracer Clone_Me() {
    Tracer child = new Tracer();
    child.Sender = this.Sender;
    child.Distance = this.Distance;
    child.NumHops = this.NumHops;
    return child;
  }
}
