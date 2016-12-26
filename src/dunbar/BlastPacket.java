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
/* ********************************************************************************************************* */
public class BlastPacket {// this is a blast packet
  public double Distance;
  public int NodeId;
  public Node OriginNode = null;
  public BlastPacket() {
  }
  public BlastPacket CloneMe() {
    BlastPacket child = new BlastPacket();
    child.Distance = this.Distance;
    child.NodeId = this.NodeId;
    child.OriginNode = this.OriginNode;
    return child;
  }
  public void UpdateForResend() {
    this.Distance += 1;// now packet's distance will be my distance from endpoint
  }
  public void DeleteMe() {// wreck everything
    NodeId = Integer.MIN_VALUE;
    Distance = Double.POSITIVE_INFINITY;
    this.OriginNode = null;
  }
}
