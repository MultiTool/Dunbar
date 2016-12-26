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
public class RouteEntry {
  public int NodeId = Integer.MIN_VALUE;
  public Node OriginNode = null;
  public double Distance = Double.POSITIVE_INFINITY;
  public RouteEntry() {
  }
  public RouteEntry(Node OriginNode0, double Distance0) {
    this.OriginNode = OriginNode0;
    this.NodeId = OriginNode0.NodeId;
    this.Distance = Distance0;
  }
  public void ConsumePacket(BlastPacket pkt) {
    //this.Distance = pkt.Distance + 1;// ??
    this.NodeId = pkt.NodeId;
    this.OriginNode = pkt.OriginNode;
  }
}
