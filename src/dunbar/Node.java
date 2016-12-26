package dunbar;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author MultiTool
 */
public class Node implements IDrawable {
  public static int MaxNbrs = 3;// max allowable neighbors for Dunbar number
  public ArrayList<Synapse> USLinks = new ArrayList<Synapse>();
  public ArrayList<Synapse> DSLinks = new ArrayList<Synapse>();
  public java.util.HashMap<Integer, RouteEntry> RouteTable = new java.util.HashMap<Integer, RouteEntry>();
  //public LinkedList<BlastPacket> BlastPacketInBuf, BlastPacketOutBuf;
  public ArrayList<BlastPacket> BlastPacketInBuf, BlastPacketOutBuf;
  public double XLoc = 0, YLoc = 0;
  public int NodeId;
  public static int NodeCounter = 0;
  public Color color;
  public static Random RandomGenerator = new Random();
  /* ********************************************************************** */
  Node() {
    color = ToRainbow(RandomGenerator.nextDouble());
    this.NodeId = NodeCounter++;
    this.BlastPacketInBuf = new ArrayList<BlastPacket>();
    this.BlastPacketOutBuf = new ArrayList<BlastPacket>();
    {// put self in route table with distance of 0 to block self-packets
      RouteEntry selfroute = new RouteEntry(this, 0);
      this.RouteTable.put(this.NodeId, selfroute);
    }
  }
  /* ********************************************************************************* */
  public void Pack_Route_Table(Cluster Network) {
    RouteEntry route;
    Node other;
    int len = Network.NodeList.size();
    for (int cnt = 0; cnt < len; cnt++) {
      other = Network.NodeList.get(cnt);
      route = new RouteEntry(other, Double.POSITIVE_INFINITY);
      this.RouteTable.put(this.NodeId, route);
    }
    route = this.RouteTable.get(this.NodeId);// make route to self be distance 0 from the start.
    route.Distance = 0;
  }
  /* ********************************************************************************* */
  public boolean IsConnectedTo(Node other) {// inefficent test for connectedness
    boolean found = false;
    int len = this.DSLinks.size();
    Synapse syn;
    for (int cnt = 0; cnt < len; cnt++) {
      syn = this.DSLinks.get(cnt);
      if (syn.DSNode == other) {
        found = true;
        break;
      }
    }
    return found;
  }
  /* ********************************************************************************* */
  public Synapse ConnectIn(Node other) {
    Synapse syn = new Synapse();
    syn.DSNode = this;
    syn.USNode = other;
    this.USLinks.add(syn);
    other.DSLinks.add(syn);
    return syn;
  }
  /* ********************************************************************************* */
  public void ConnectTwoWay(Node other) {
    Synapse MySyn = this.ConnectIn(other);
    Synapse YouSyn = other.ConnectIn(this);
    MySyn.MyMirror = YouSyn;
    YouSyn.MyMirror = MySyn;
  }
  /* ********************************************************************** */
  double ReportStats() {
    double MaxDistance = 0, SumDistance = 0;// total alienation number
    RouteEntry route;
    int WorldSize = 0;
    Iterator it = this.RouteTable.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();
      route = (RouteEntry) pair.getValue();
      //System.out.println(pair.getKey() + " = " + pair.getValue());
      SumDistance += route.Distance;
      if (MaxDistance < route.Distance) {
        MaxDistance = route.Distance;
        //System.out.println("MaxDistance:" + MaxDistance);
      }
      WorldSize++;
      //it.remove(); // avoids a ConcurrentModificationException
    }
    //System.out.println("WorldSize:" + WorldSize);
    return SumDistance;
    //return MaxDistance;
  }
  /* ********************************************************************** */
  double Get_Alienation_Number() {
    double SumDistance = 0;// total alienation number
    RouteEntry route;
    int WorldSize = 0;
    Iterator it = this.RouteTable.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();
      route = (RouteEntry) pair.getValue();
      SumDistance += route.Distance;
      WorldSize++;
    }
    //System.out.println("WorldSize:" + WorldSize);
    return SumDistance;
  }
  /* ********************************************************************** */
  void Collect_Blast() {
    Synapse ups;
    Node usnode;
    this.BlastPacketInBuf.clear();
    int NumIns = this.USLinks.size();
    for (int incnt = 0; incnt < NumIns; incnt++) {
      ups = this.USLinks.get(incnt);
      usnode = ups.USNode;
      //this.BlastPacketInBuf.addAll(usnode.BlastPacketOutBuf);
      for (int ocnt = 0; ocnt < usnode.BlastPacketOutBuf.size(); ocnt++) {
        BlastPacket bp = usnode.BlastPacketOutBuf.get(ocnt);
        this.BlastPacketInBuf.add(bp.CloneMe());
      }
    }
  }
  /* ********************************************************************************************************* */
  public boolean ProcessBlastPackets() {// process all packets for distance updates
    BlastPacket InPacket, OutPacket = null;
    boolean StillProcessing = false;
    this.BlastPacketOutBuf.clear();
    int len = this.BlastPacketInBuf.size();
    for (int cnt = 0; cnt < len; cnt++) {
      InPacket = this.BlastPacketInBuf.get(cnt);
      if ((OutPacket = ProcessBlastPacket(InPacket)) != null) {
        StillProcessing = true;
      }
    }
    return StillProcessing;
  }
  /* ********************************************************************************************************* */
  public BlastPacket ProcessBlastPacket(BlastPacket pkt) {// process a packet for distance updates
    BlastPacket PktNext = null;
    RouteEntry MyKnowledgeOfEndpoint;

    double NextDist = pkt.Distance + 1;// increase by one jump, the one it took to reach me

    // when I get a blast packet, I look up its origin in the routing table. 
    if (this.RouteTable.containsKey(pkt.NodeId)) {
      MyKnowledgeOfEndpoint = this.RouteTable.get(pkt.NodeId);
    } else {// if no entry is found, create one and add it to the table
      MyKnowledgeOfEndpoint = new RouteEntry();
      MyKnowledgeOfEndpoint.ConsumePacket(pkt);
      this.RouteTable.put(pkt.NodeId, MyKnowledgeOfEndpoint);
    }

    if (MyKnowledgeOfEndpoint.Distance > NextDist) {// now we see if the new packet is closer
      MyKnowledgeOfEndpoint.Distance = NextDist;
      PktNext = pkt.CloneMe();
      PktNext.Distance = NextDist; //PktNext.UpdateForResend();
      this.BlastPacketOutBuf.add(PktNext);
    }

    //pkt.DeleteMe();
    // otherwise return null and the packet will be discarded.
    return PktNext;
  }
  /* ********************************************************************************************************* */
  public void SendFirstPacket() {
    BlastPacket mine = new BlastPacket();
    mine.NodeId = this.NodeId;
    mine.Distance = 0;
    mine.OriginNode = this;
    this.BlastPacketOutBuf.add(mine);
  }

  /*
   go through all of my nbrs, and hand each one of them my whole list of outgoing packets.
   after we are done, wipe the list of packets. 
      
   each nbr has a consumepackets fn, which processes all the packets and then fills up its own out buffer. 
   (need 2 out buffers, outbuf_now and outbuf_next.)
      
   so how to do this?
      
   everybody at once:
   collect: go through all of my upstreamers and pull all of their out buffers into my in buffer. merge them.
      
   everybody at once:
   process: go through my in buffer, process all of the incoming packets. 
      
      
   */
  /* ********************************************************************** */
  void Push_Fire() {
    /*
     everybody at once:
     collect: go through all of my upstreamers and pull all of their out buffers into my in buffer. merge them.
      
     everybody at once:
     process: go through my in buffer, process all of the incoming packets. 
      
     */
  }
  public void AssignLoc(double XPos, double YPos) {
    this.XLoc = XPos;
    this.YLoc = YPos;
  }
  /* ********************************************************************************* */
  @Override
  public void Draw_Me(DrawingContext ParentDC) {
    int NumDs = DSLinks.size();
    Synapse syn;
    ParentDC.gr.setColor(this.color);
    int Radius = 2, Diameter = Radius * 2;
    ParentDC.gr.drawOval(((int) this.XLoc) - Radius, ((int) this.YLoc) - Radius, Diameter, Diameter);
    for (int scnt = 0; scnt < NumDs; scnt++) {
      syn = this.DSLinks.get(scnt);
      syn.Draw_Me(ParentDC);
    }
  }
  /* ********************************************************************************* */
  public static Color ToRainbow(double Fraction) {
    if (Fraction < 0.5) {
      Fraction *= 2;
      return new Color((float) (1.0 - Fraction), (float) Fraction, 0);
    } else {
      Fraction = Math.min((Fraction - 0.5) * 2, 1.0);
      return new Color(0, (float) (1.0 - Fraction), (float) Fraction);
    }
  }
}
