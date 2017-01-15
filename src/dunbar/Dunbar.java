/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dunbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author MultiTool
 */
public class Dunbar {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    if (true){
       Cluster winner = Farm.Evolve();
    }
    if (false) {
      MainGui mg = new MainGui();
      mg.Init();
      if (true) {
        Cluster winner = Farm.Evolve();
        winner.ReadyToDraw = false;
        mg.drawpanel.cluster0 = winner;
        winner.ReadyToDraw = true;
      }
    }

    /*
     Next steps:
     color coded nodes and lines, based on alienation number - done
     thicker lines - done
     measure inequality - figure out how. maybe take average of all nodes above average, and average of all below, and report difference?  - done in a simple way
     . Gini coefficient?  or sort nodes, compare average of top half to average of bottom half. 
     spray of random networks to see range of performance - done
     genalg breeding of random networks to try for improvement - next 
     output networks as SVG
     basket hierarchy
     plot cube, hierarchy and random networks with same color scale to make relative alienation for each node and network visible. 
     use spring physics to self-sort random networks
     
     */
    //ConnectStuff();
    /*
     new alg:
     create array of nodes, size N. define number of connections per node, NCons.  
     NCons must be at least 2, preferably more. 
     othersize = N-1; 
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
     next figure out how to not overconnect after first node is connected. 
     maybe just do half of each fan of connections?  if NCons is odd, what about the middle one? 
    
     order is:
     create all, maybe start with a circle
     start with single node.
     broadcast metric signal from single node.
     either iterate through whole network
     well create a collection of tracers.
     when hit a node (starting with me), if I have N neighbors, create N tracers and send them out.
     how to keep tracer from going backward? 
     when tracer hits a node, look up src/dest in its routing table. 
     if node is not yet in the table, add it with a distance of infinity. 
     if the node in the routing table is closer than (or equal to) the tracer's hop count, cancel tracer.
     so tracer can go back the way it came anyway? it will encounter a closer path and be cancelled. 
     so: hit a node, check for self in existing routes, if redundant then cancel.
     if not redudant, clone N new tracers from self and send them to neighbors. add all N tracers to list, remove self. 
     everywhere we hit a local maximum, that is where tracers get killed(?), gets added to our farthest nbr list. 
     after all the tracers are dead (list is empty), create connection to farthest nbr. 
     create connections to all farthest nbrs I guess? would changing one fartherst nbr change another automatically?
    
     loop {
     . after all farthest nbr(s) is/are connected, run tracers first to them, then outward from them until all tracers are dead again.
     . have farthest nbrs again, repeat.
     }
    
     one issue is that few of the people you know will know other people you know, right? 
     this is not a network of all friends. it is a network of government structure. 
     perhaps all may have similar topics to talk about.
     or well your natural network of friends may extend or intraconnect the gov network.
    
     bigger question: how can the gov network make decisions?  
     in a neural network every node receives votes from its neighbors. the winning consensus that node gets is then re-sent to other nbrs.
     kind of tenuous, but say a person has to make a 
     really, how can a whole network of people even act on something? some people are hands, they do the stuff, but ultimately everyone else lives with the results
     choose the location of a house? 
     if online, meet with X group of people, or 1 person. decide together whether up or down. 
     could pool common resource. put in a dollar, you get more rights. 
     pay one dollar, your vote counts. for each of your connections, you must both agree to a decision on something. 
     the whole network then polls for each pairvote. winning pairvote is how resource is spent. so every meeting is a consensus cell.
    
     other issue: how to create ideas? can be proposed at meetings, and on common forum. 
    
     or it's a game: everyone elects for the actions of a giant robot. 
     or each meeting is an art/sculpture meeting?
    
     */
  }
  /* ********************************************************************************* */
  public static void ConnectStuff() {
    Cluster pop = new Cluster();
    int Num_Nodes = 10;
    int Connections_Per_Node = 4;
    pop.Fill_With_Nodes(Num_Nodes);
    pop.ConnectInnerSparse(Connections_Per_Node);

    DrawingContext ParentDC = new DrawingContext();
    pop.Draw_Me(ParentDC);
  }
}
