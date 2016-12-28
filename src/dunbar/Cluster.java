package dunbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author MultiTool
 */
public class Cluster implements IDrawable {
  public ArrayList<Node> NodeList = new ArrayList<Node>();
  /* ********************************************************************** */
  Cluster() {
  }
  /* ********************************************************************** */
  Cluster(int Num_Nodes) {
    this();
    Fill_With_Nodes(Num_Nodes);
  }
  /* ********************************************************************** */
  void Fill_With_Nodes_Plain(int Num_Nodes) {
    Node ndp;
    int ncnt;
    this.NodeList.clear();
    for (ncnt = 0; ncnt < Num_Nodes; ncnt++) {
      ndp = new Node();
      this.NodeList.add(ndp);
    }
  }
  /* ********************************************************************** */
  void Fill_With_Nodes(int Num_Nodes) {
    Node ndp;
    int ncnt;
    this.NodeList.clear();
    double Radius = 120.0;
    double XOffset = Radius * 2, YOffset = Radius * 2;
    double FractAngle = 0.0, Angle;
    for (ncnt = 0; ncnt < Num_Nodes; ncnt++) {
      ndp = new Node();
      this.NodeList.add(ndp);
      FractAngle = ((double) ncnt) / ((double) Num_Nodes);
      Angle = FractAngle * Math.PI * 2.0;
      ndp.XLoc = XOffset + Math.cos(Angle) * Radius;
      ndp.YLoc = YOffset + Math.sin(Angle) * Radius;
    }
  }
  /* ********************************************************************** */
  void Make_Circular(double Radius) {
    Node ndp;
    int ncnt;
    double XOffset = Radius * 2, YOffset = Radius * 2;
    double FractAngle = 0.0, Angle;
    int Num_Nodes = this.NodeList.size();
    for (ncnt = 0; ncnt < Num_Nodes; ncnt++) {
      ndp = this.NodeList.get(ncnt);
      FractAngle = ((double) ncnt) / ((double) Num_Nodes);
      Angle = FractAngle * Math.PI * 2.0;
      ndp.XLoc = XOffset + Math.cos(Angle) * Radius;
      ndp.YLoc = YOffset + Math.sin(Angle) * Radius;
    }
  }
  /* ********************************************************************************* */
  public void ConnectHypercube(int NDims) {
    Node.MaxNbrs = NDims;
    int Num_Nodes = 1 << NDims;
    Fill_With_Nodes_Plain(Num_Nodes);
    for (int cnt = 0; cnt < Num_Nodes; cnt++) {
      Node me = this.NodeList.get(cnt);
      for (int shiftcnt = 0; shiftcnt < NDims; shiftcnt++) {
        int altaddr = cnt ^ (1 << shiftcnt);
        Node you = this.NodeList.get(altaddr);
        me.ConnectIn(you);//.ConnectTwoWay(you);
      }
    }
    this.Make_Circular(120.0);
  }
  /* ********************************************************************************* */
  public void ConnectInnerSparse(int ConnectionsPerNode) {// Dunbar's number
    int NumNodes = this.NodeList.size();
    int DemasNum = NumNodes - 1;// size of whole set minus the current node self
    int FinalCon = ConnectionsPerNode - 1;// index of final connection of a node (4 connections means range of 0 to 3).
    int HalfFan = FinalCon / 2;// not ready for odd numbers yet
    double FractAlong;
    int NbrDex;
    Node mine, yours;
//    HalfFan = FinalCon;
    for (int NCnt = 0; NCnt < NumNodes; NCnt++) {
//    for (int NCnt = 0; NCnt < 1; NCnt++) {
      mine = this.NodeList.get(NCnt);
      for (int CCnt = 0; CCnt <= HalfFan; CCnt++) {// HalfFan only works if number of connections is even
        FractAlong = ((double) CCnt) / ((double) FinalCon);// range 0.0 to 1.0, inclusive.  Is HalfFan right with FinalCon? 
        NbrDex = (int) Math.round(DemasNum * FractAlong);
        NbrDex = (NCnt + 1 + NbrDex) % NumNodes;// loop around
        yours = this.NodeList.get(NbrDex);
        mine.ConnectTwoWay(yours);// if we do half-fan, connect both ways
      }
    }
    /*
     is every connection going to be 2 way, or will there be 2 connections between every pair of nodes?
     advantage to 1 2-way connection: can manage connection both ways at once. guaranteed no hanging 1-way connections in network.
     con: how do you deal with an odd number of connections per node?  The first half of nodes hit will connect the middle, the last half will not? 
     advantage to 2 1-way connections: easier to create fan of connections. also symmetrical. 
     alternative: 2 1-way connections that point to each other. but then they need to be aware of each other at some point to connect. *** Doing this way right now.
    
     create array of nodes, size N. define number of connections per node, NCons.  
     NCons must be at least 2, preferably more. 
     othersize = N-1; 
     double JumpFract = (DemasNum / ConnectionsPerNode);
     jumpfract = (othersize / NCons);
     FinalCon = NCons-1;
     for each node {
     . for each connection CCnt=0 while CCnt<=FinalCon {
     .  fractalong = CCnt/(FinalCon);// range 0 to 1, inclusive
     .  nbrdex = Math.round(othersize*fractalong);
     .  // nbrdex = ((othersize*CCnt) / NCons);// jumpfract * CCnt;
     .  connect node to nbr at nbrdex, both ways
     . }
     }
     */
  }
  /* ********************************************************************************* */
  public void Create_Random(int Num_Nodes, int Dunbar_Limit) {
    Node.MaxNbrs = Dunbar_Limit;
    this.Fill_With_Nodes_Plain(Num_Nodes);
    /*
     next we need to find any two open nodes, and connect them together.
     how to quickly find two open nodes? 
     have a list of nodes that are open, randomly pick two, and pop the ones that are full?
    
     or we can pack like a snowball
     start with one, attach another
     create another, look for spot on existing network, plug in (once?) or dunbar times if possible
     so when a node is born, iterate through network (or just opens) and plug in to any random subset of opens. 
     whenever one is closed, pop it out of open.
     nah
     easier to make a matrix, then spray random dots in connection bins
     but dunbar limit must go both ways
     so for one node, we fill dn dots in its column.
     but no row can go above dn either.
     so pick node, spray dn dots in its column
     next node, spray dn dots in its column.
     Each time we spray any dot, see if a node row becomes full. if so, remove from row. AND from column. 
     matrix is symmetrical. 
    
     back to rolling random problem.
    
     */
    boolean Depleted;// we are Depleted if we've run out of things to connect to each other
    int OpenCnt;
    Node OtherNode;// easy, inefficient way
    while (true) {
      Depleted = true;
      OpenCnt = 0;
      for (int cnt = 0; cnt < Num_Nodes; cnt++) {
        Node nd = this.NodeList.get(cnt);
        if (nd.IsOpen()) {
          OpenCnt++;
          OtherNode = nd.FindRandomOther(this);
          if (OtherNode != null) {
            nd.ConnectTwoWay(OtherNode);
            Depleted = false;
          }
        }
      }
      if (Depleted) {
        break;
      }
    }
    if (OpenCnt > 0) {
      //System.out.println("SomeAreOpen");
    }
    boolean broken = this.OverConnectCheck();
    if (broken) {
      System.out.println("** OverConnected!!!");
    }
//    ArrayList<Node> open = new ArrayList<Node>();
//    for (int cnt = 0; cnt < Num_Nodes; cnt++) {
//      Node nd = this.NodeList.get(cnt);
//      OtherNode = nd.FindRandomOther(this);
//      if (OtherNode != null) {
//        nd.ConnectTwoWay(OtherNode);
//      }
//      open.add(nd);
//    }
//    int dex0, dex1;
//    dex0 = Base.RandomGenerator.nextInt(open.size());
//    dex1 = dex0;
//    while (dex1 != dex0) {// wrong they still might already connect
//      dex1 = Base.RandomGenerator.nextInt(open.size());
//    }
//    Node nd0 = open.get(dex0);
//    Node nd1 = open.get(dex1);
//    nd0.ConnectTwoWay(nd1);
    this.Make_Circular(120.0);
  }
  /* ********************************************************************************* */
  public void Create_Heirarchy(int Num_Nodes, int Dunbar_Limit) {
    int NodeCnt = 0, TierStartCnt, TierCnt;
    Node.MaxNbrs = Dunbar_Limit;
    int Dun_Small = Dunbar_Limit - 1;
    int Root_Start, Root_End, Child_Start = 0, Child_End;
    int Root_Span, Child_Span;
    int ChildCnt = 0;
    double XOrg, YOrg, XLoc, YLoc, XDif = 20, YDif = 20;
    Node nd, root, child;
    ArrayList<Node> nodes = new ArrayList<Node>();
    this.NodeList.clear();
    if (false) {
      for (int cnt = 0; cnt < Num_Nodes; cnt++) {
        nd = new Node();
        this.NodeList.add(nd);
        nodes.add(nd);
      }
      Root_Span = 1;
      Child_Span = Root_Span * Dun_Small;
      Root_Start = 0;
      Root_End = 1;
      while (ChildCnt < Num_Nodes) {// loop tiercnt
        for (int cnt = Root_Start; cnt < Root_End; cnt++) {
          Child_Span = Root_Span * Dun_Small;
          //ChildCnt = Child_Start;
          Child_End = Child_Start + Child_Span;
          Child_End = Math.min(Child_End, Num_Nodes);
          while (ChildCnt < Child_End) {
            ChildCnt++;
          }
        }
        Root_Span = Child_Span;
      }
    }
    ArrayList<Node> ParentBuf = new ArrayList<Node>();
    ArrayList<Node> ChildBuf = new ArrayList<Node>();
    if (true) {
      TierCnt = 0;
      XOrg = 30;
      YOrg = 30;
      YLoc = YOrg;
      // first tier
      XLoc = XOrg;
      root = new Node();
      this.NodeList.add(root);
      ParentBuf.add(root);// root node
      root.AssignLoc(XLoc, YLoc);
      NodeCnt++;
      TierCnt++;
      // second tier
      XLoc = XOrg;
      YLoc += YDif;
      for (int cnt = 0; cnt < Dunbar_Limit; cnt++) {// first tier, root does not point up, so has extra connection for children
        child = new Node();
        this.NodeList.add(child);
        ChildBuf.add(child);
        child.AssignLoc(XLoc, YLoc);
        XLoc += XDif;
        root.ConnectTwoWay(child);
        NodeCnt++;
      }
      ParentBuf = ChildBuf;// child row becomes parent row
      TierCnt++;
      while (NodeCnt < Num_Nodes) {// loop tiercnt
        // Nth tier
        XLoc = XOrg;
        YLoc += YDif;
        ChildBuf = new ArrayList<Node>();
        TierStartCnt = NodeCnt - 0;
        ChildCnt = 0;
        int ParentLen = ParentBuf.size();
        for (int ParentCnt = 0; ParentCnt < ParentLen; ParentCnt++) {
          root = ParentBuf.get(ParentCnt);
          Child_End = (ParentCnt + 1) * Dun_Small;
          Child_End = Math.min(TierStartCnt + Child_End, Num_Nodes) - TierStartCnt;// yuck
          while (ChildCnt < Child_End) {
            if (NodeCnt >= Num_Nodes) {
              break;
            }
            child = new Node();
            this.NodeList.add(child);
            ChildBuf.add(child);
            child.AssignLoc(XLoc, YLoc);
            XLoc += XDif;
            root.ConnectTwoWay(child);
            ChildCnt++;
            NodeCnt++;
          }
        }
        ParentBuf = ChildBuf;// child row becomes parent row
        TierCnt++;
      }
    }
    if (false) {
      // to do: make a basket heirarchy. all childless children must connect laterally.
      for (int cnt = 0; cnt < ChildBuf.size(); cnt++) {
        ChildBuf.get(cnt);
      }
      // more realistic would be a cul-de-sac basket heirarchy, where only children of the same parent can connect together. no inter-departmental connections.
    }
    System.out.print("");
    if (false) {
      root = nodes.get(0);
      for (int cnt = 0; cnt < Dunbar_Limit; cnt++) {
        child = nodes.get(cnt);
        root.ConnectTwoWay(child);
      }
      root = new Node();
      this.NodeList.add(root);
      NodeCnt++;
      for (int cnt = 0; cnt < Dunbar_Limit; cnt++) {
        child = new Node();
        this.NodeList.add(child);
        root.ConnectTwoWay(child);
        NodeCnt++;
      }
    }
  }
  /* ********************************************************************************* */
  public void ConnectInput(Cluster other) {// connect all to all
    int mylen = this.NodeList.size();
    int youlen = other.NodeList.size();
    Node mine, yours;
    for (int cnt0 = 0; cnt0 < mylen; cnt0++) {
      mine = this.NodeList.get(cnt0);
      for (int cnt1 = 0; cnt1 < youlen; cnt1++) {
        yours = other.NodeList.get(cnt1);
        mine.ConnectIn(yours);
      }
    }
  }
  /* ********************************************************************** */
  boolean Medir_Gen() {
    Collect_Blast();
    return ProcessBlastPackets();
  }
  /* ********************************************************************************* */
  public void Medir() {
    int gencnt = 0;
    //System.out.println("SendFirstPacket");
    this.SendFirstPacket();
    while (this.Medir_Gen()) {
      //System.out.println("Medir_Gen:" + gencnt);
      gencnt++;
    }
//    double SumDist = this.Get_Alienation_Number();
//    System.out.println("MaxDist:" + SumDist);
  }
  /* ********************************************************************** */
  public double Get_Adjusted_Alienation_Number() {
    Node ndp;
    double SumDistance = 0;// total alienation number
    int siz = this.NodeList.size();
    for (int cnt = 0; cnt < siz; cnt++) {
      ndp = this.NodeList.get(cnt);
      if (ndp.RouteTable.size() < this.NodeList.size()) {
        SumDistance += Double.POSITIVE_INFINITY;// If any node is unreachable from any other node, distance (alienation) is infinite.
      } else {
        SumDistance += ndp.Get_Adjusted_Alienation_Number(siz - 1);
      }
    }
    return SumDistance / (double) siz;
  }
  /* ********************************************************************** */
  public double Get_Alienation_Number() {
    Node ndp;
    double SumDistance = 0;// total alienation number
    int siz = this.NodeList.size();
    for (int cnt = 0; cnt < siz; cnt++) {
      ndp = this.NodeList.get(cnt);
      if (ndp.RouteTable.size() < this.NodeList.size()) {
        SumDistance += Double.POSITIVE_INFINITY;// If any node is unreachable from any other node, distance (alienation) is infinite.
      } else {
        SumDistance += ndp.Get_Alienation_Number();
      }
    }
    return SumDistance;
  }
  /* ********************************************************************************* */
  public boolean OverConnectCheck() {
    Node ndp;
    int siz = this.NodeList.size();
    for (int cnt = 0; cnt < siz; cnt++) {
      ndp = this.NodeList.get(cnt);
      if (ndp.OverConnectCheck()) {
        return true;
      }
    }
    return false;
  }
  /* ********************************************************************************* */
  public void Sort() {
    Collections.sort(this.NodeList, new Comparator<Node>() {
      @Override public int compare(Node nd0, Node nd1) {
        return Double.compare(nd0.AlienationNumber, nd1.AlienationNumber);
      }
    });
  }
  /* ********************************************************************************* */
  public double Measure_Inequality() {
    this.Sort();
    int siz = this.NodeList.size();
    int half = siz / 2;
    int cnt = 0;
    double Sum0 = 0, Sum1 = 0;
    while (cnt < half) {
      Sum0 += this.NodeList.get(cnt).AlienationNumber;
      cnt++;
    }
    Sum0 /= (double) half;
    while (cnt < siz) {
      Sum1 += this.NodeList.get(cnt).AlienationNumber;
      cnt++;
    }
    Sum1 /= (double) (siz - half);
    double Dist = Sum1 - Sum0;
    return Dist;
  }
  /* ********************************************************************************* */
  public double Get_Min_Alienation() {
    int siz = this.NodeList.size();
    double Alienation, Min = Double.POSITIVE_INFINITY;
    for (int cnt = 0; cnt < siz; cnt++) {
      Alienation = this.NodeList.get(cnt).AlienationNumber;
      if (Min > Alienation) {
        Min = Alienation;
      }
    }
    return Min;
  }
  /* ********************************************************************************* */
  public double Get_Max_Alienation() {
    int siz = this.NodeList.size();
    double Alienation, Max = -1;
    for (int cnt = 0; cnt < siz; cnt++) {
      Alienation = this.NodeList.get(cnt).AlienationNumber;
      if (Max < Alienation) {
        Max = Alienation;
      }
    }
    return Max;
  }
  /* ********************************************************************************* */
  public void Colorize(double Min, double Max) {
    if (Min == Max) {
      Max += Base.Fudge;
    }
    double Range = Max - Min;
    int siz = this.NodeList.size();
    Node nd;
    for (int cnt = 0; cnt < siz; cnt++) {
      nd = this.NodeList.get(cnt);
      nd.color = Base.ToRainbow(1.0 - ((nd.AlienationNumber - Min) / Range));
    }
  }
  /* ********************************************************************************* */
  public void Colorize() {
    double Min = this.Get_Min_Alienation();
    double Max = this.Get_Max_Alienation();
    this.Colorize(Min, Max);
  }
  /* ********************************************************************** */
  void SendFirstPacket() {
    Node ndp;
    int cnt;
    int siz = this.NodeList.size();
    for (cnt = 0; cnt < siz; cnt++) {
      ndp = this.NodeList.get(cnt);
      ndp.SendFirstPacket();
    }
  }
  /* ********************************************************************** */
  void Collect_Blast() {
    Node ndp;
    int cnt;
    int siz = this.NodeList.size();
    for (cnt = 0; cnt < siz; cnt++) {
      ndp = this.NodeList.get(cnt);
      ndp.Collect_Blast();
    }
  }
  /* ********************************************************************** */
  boolean ProcessBlastPackets() {
    boolean StillProcessing = false;
    Node ndp;
    int cnt;
    int siz = this.NodeList.size();
    for (cnt = 0; cnt < siz; cnt++) {
      ndp = this.NodeList.get(cnt);
      if (ndp.ProcessBlastPackets()) {
        StillProcessing = true;
      }
    }
    return StillProcessing;
  }
  /* ********************************************************************************* */
  @Override public void Draw_Me(DrawingContext ParentDC) {
    Node ndp;
    int cnt;
    DrawingContext ChildDC = new DrawingContext(ParentDC);
    int siz = this.NodeList.size();
    for (cnt = 0; cnt < siz; cnt++) {
      ndp = this.NodeList.get(cnt);
      ndp.Draw_Me(ChildDC);
    }
  }
}
