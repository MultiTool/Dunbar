package dunbar;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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
  public double AlienationNumber = 0;
  public double XLoc = 0, YLoc = 0;
  int Radius = 10, Diameter = Radius * 2;
  public int NodeId;
  public static int NodeCounter = 0;
  public Color color;
  public static Random RandomGenerator = new Random();
  /* ********************************************************************** */
  Node() {
    color = Base.ToRainbow(RandomGenerator.nextDouble());
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
    RouteEntry route;// if the network is split into two or more islands, the node will at least know that some other nodes are infinitely far away
    Node other;
    int len = Network.NodeList.size();
    for (int cnt = 0; cnt < len; cnt++) {
      other = Network.NodeList.get(cnt);
      route = new RouteEntry(other, Double.POSITIVE_INFINITY);
      this.RouteTable.put(other.NodeId, route);
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
  public boolean IsOpen() {// inefficent test for connectedness
    return this.DSLinks.size() < Node.MaxNbrs;// if true, not quite full yet
  }
  /* ********************************************************************************* */
  public boolean OverConnectCheck() {
    if (this.DSLinks.size() > Node.MaxNbrs) {
      return true;
    } else if (this.USLinks.size() > Node.MaxNbrs) {
      return true;
    }
    return false;
  }
  /* ********************************************************************************* */
  public Node FindRandomOther(Cluster cluster) {// inefficent search for other to connect to
    ArrayList<Node> NodeList = cluster.NodeList;
    int len = NodeList.size();
    int RandDex = Base.RandomGenerator.nextInt(len);
    Node possible, found = null;
    int cnt = RandDex;
    while (true) {
      possible = NodeList.get(cnt);
      if (possible.IsOpen()) {
        if (possible != this) {
          if (!possible.IsConnectedTo(this)) {
            found = possible;
            break;
          }
        }
      }
      cnt++;
      if (cnt >= len) {
        cnt = 0;
      }
      if (cnt == RandDex) {
        break;
      }
    }
    return found;
  }
  /* ********************************************************************************* */
  public int FindRandomOtherIndex(Cluster cluster) {// inefficent search for other to connect to
    ArrayList<Node> NodeList = cluster.NodeList;
    int len = NodeList.size();
    int RandDex = Base.RandomGenerator.nextInt(len);
    Node other;
    int cnt = (RandDex + 1) % len;
    while (cnt != RandDex) {
      if (cnt >= len) {
        cnt = 0;
      }
      other = NodeList.get(cnt);
      if (other.IsOpen()) {
        if (other != this) {
          if (!other.IsConnectedTo(this)) {
            break;
          }
        }
      }
      cnt++;
    }
    return cnt;
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
  /* ********************************************************************************* */
  public void Disconnect(Node other) {// rough draft
    Synapse syn;
    int loc = -1;
    for (int cnt = 0; cnt < this.USLinks.size(); cnt++) {
      syn = this.USLinks.get(cnt);
      if (syn.USNode == other) {
        loc = cnt;
        break;
      }
    }
    this.USLinks.remove(loc);

    loc = -1;
    for (int cnt = 0; cnt < other.DSLinks.size(); cnt++) {
      syn = other.DSLinks.get(cnt);
      if (syn.DSNode == this) {
        loc = cnt;
        break;
      }
    }
    other.DSLinks.remove(loc);
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
  public double Get_Adjusted_Alienation_Number(double ClusterSize) {//Assign_Alienation_Number
    this.AlienationNumber = this.Get_Alienation_Number() / ClusterSize;
    return this.AlienationNumber;
  }
  /* ********************************************************************** */
  public double Get_Alienation_Number() {
    double SumDistance = 0;// total alienation number
    RouteEntry route;
    Iterator it = this.RouteTable.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();
      route = (RouteEntry) pair.getValue();
      SumDistance += route.Distance;
    }
    this.AlienationNumber = SumDistance;
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
  @Override public void Draw_Me(DrawingContext ParentDC) {
    Graphics2D g2d = ParentDC.gr;
    int NumDs = DSLinks.size();
    Synapse syn;
    for (int scnt = 0; scnt < NumDs; scnt++) {
      syn = this.DSLinks.get(scnt);
      syn.Draw_Me(ParentDC);
    }
    g2d.setColor(this.color);
    g2d.fillOval(((int) this.XLoc) - Radius, ((int) this.YLoc) - Radius, Diameter, Diameter);

    String txt = String.format("%1$.1f", this.AlienationNumber);
    g2d.setColor(Color.green);

    //Font font = ParentDC.gr.getFont();
    g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
    g2d.setColor(Color.white);
    DrawCenteredString(g2d, (int) this.XLoc, (int) this.YLoc, txt);
    g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
    g2d.setColor(Color.green);
    DrawCenteredString(g2d, (int) this.XLoc, (int) this.YLoc, txt);
    //ParentDC.gr.drawString(txt, (float) this.XLoc, (float) this.YLoc);
  }
  /* ********************************************************************************* */
  public void Draw_Body(DrawingContext ParentDC) {
    Graphics2D g2d = ParentDC.gr;
    g2d.setColor(this.color);
    g2d.fillOval(((int) this.XLoc) - Radius, ((int) this.YLoc) - Radius, Diameter, Diameter);
    String txt = String.format("%1$.1f", this.AlienationNumber);
    g2d.setColor(Color.green);
    //Font font = ParentDC.gr.getFont();
    g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
    g2d.setColor(Color.white);
    DrawCenteredString(g2d, (int) this.XLoc, (int) this.YLoc, txt);
    g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
    g2d.setColor(Color.green);
    DrawCenteredString(g2d, (int) this.XLoc, (int) this.YLoc, txt);
    //ParentDC.gr.drawString(txt, (float) this.XLoc, (float) this.YLoc);
  }
  /* ********************************************************************************* */
  public void Draw_Connections(DrawingContext ParentDC) {
    int NumDs = DSLinks.size();
    Synapse syn;
    for (int scnt = 0; scnt < NumDs; scnt++) {
      syn = this.DSLinks.get(scnt);
      syn.Draw_Me(ParentDC);
    }
  }
  /* ********************************************************************************* */
  public void DrawCenteredString(Graphics2D g2d, int XCtr, int YCtr, String text) {
    // Get the FontMetrics  http://stackoverflow.com/questions/27706197/how-can-i-center-graphics-drawstring-in-java
    Font font = g2d.getFont();
    FontMetrics metrics = g2d.getFontMetrics(font);//font
    // Determine the X coordinate for the text
    int x = XCtr - (metrics.stringWidth(text)) / 2;
    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
    int y = YCtr - ((metrics.getHeight()) / 2) + metrics.getAscent();
    // Draw the String
    g2d.drawString(text, x, y);
  }

}
