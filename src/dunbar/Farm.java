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
public class Farm {
  /* ********************************************************************************* */
  public static ArrayList<Cluster> Create_Farm(int Num_Clusters, int NumNodes, int Dunbar_Limit) {
    ArrayList<Cluster> farm = new ArrayList<Cluster>();
    Cluster seed;
    for (int cnt = 0; cnt < Num_Clusters; cnt++) {
      seed = new Cluster();
      seed.Create_Random(NumNodes, Dunbar_Limit);
      farm.add(seed);
    }
    return farm;
  }
  /* ********************************************************************************* */
  public static Cluster Evolve() {// genalg optimization of random networks
    int Dunbar_Limit = 3;//4;//5;//4;//3;
    int NumNodes = 1 << Dunbar_Limit;
    NumNodes *= 100;
    NumNodes = 200;
    return Evolve(NumNodes, Dunbar_Limit);
  }
  /* ********************************************************************************* */
  public static Cluster Evolve(int NumNodes, int Dunbar_Limit) {// genalg optimization of random networks
    int Num_Clusters = 100;// population of networks (number of societies to reproduce and measure at once)
    int Replace_Num = Num_Clusters * 4 / 5;/// 2;//  rate of replacement of old generation with new generation
    int Donor_Num = Num_Clusters - Replace_Num;
    System.out.println(" Dunbar_Limit:" + Dunbar_Limit + ", NumNodes:" + NumNodes + "");

    Cluster seed, winner = null;
    ArrayList<Cluster> farm = Create_Farm(Num_Clusters, NumNodes, Dunbar_Limit);

    double Alien, InEq;
    for (int gen = 0; gen < 1000; gen++) {
      // measure, score
      for (int cnt = 0; cnt < Num_Clusters; cnt++) {
        seed = farm.get(cnt);
        seed.Medir();
        Alien = seed.GetSave_Adjusted_Alienation_Number();
        InEq = seed.GetSave_Inequality();
        seed.Colorize();
      }
      // sort, select and reproduce
      Collections.sort(farm, new Comparator<Cluster>() {
        @Override public int compare(Cluster cluster0, Cluster cluster1) {
          if (true) {
            return -Double.compare(cluster0.AlienationNumber, cluster1.AlienationNumber);// breeding for goodness
          } else {
            return Double.compare(cluster0.AlienationNumber, cluster1.AlienationNumber);// breeding for badness, to test if breeding has any effect 
          }
        }
      });
      for (int cnt = 0; cnt < Num_Clusters; cnt++) {
        seed = farm.get(cnt);
        Alien = seed.AlienationNumber;
        InEq = seed.Inequality;
        System.out.println(" Alien:" + Alien + ", InEq:" + InEq + ",");
      }
      winner = farm.get(Num_Clusters - 1);// pick a top winner for later measurement
      // Go through all the highest (worst) alienation numbers and replace with muatated copies from the best
      for (int cnt = 0; cnt < Replace_Num; cnt++) {
        int DonorDex = (Replace_Num + (cnt % Donor_Num));
        seed = farm.get(DonorDex);
        seed = seed.Clone_Me();
        seed.Mutate(0.3);
        farm.set(cnt, seed);
      }
      System.out.println("**************************************");
    }
    System.out.println("Done");
    return winner;
  }
  /* ********************************************************************************* */
//    public void RepeatRandom(int NDims) {// make a bunch of random networks to see the range of goodness and badness possible
//      int NumNodes = 1 << NDims;
//      Min = Double.MAX_VALUE;
//      Max = Double.MIN_VALUE;
//
//      System.out.print(" NDims:" + NDims + ", NumNodes:" + NumNodes + ",");
//      System.out.println();
//      for (int cnt = 0; cnt < 200; cnt++) {
//        //System.out.println("cluster0.Create_Random");
//        cluster0.Create_Random(NumNodes, NDims);
//        //System.out.println("cluster0.Medir");
//        this.cluster0.Medir();
//        //System.out.println("cluster0.GetSave_Adjusted_Alienation_Number");
//        double Alien2 = this.cluster0.GetSave_Adjusted_Alienation_Number();
//        double InEq2 = this.cluster0.GetSave_Inequality();
//        //System.out.println("cluster0.Get_Min_Alienation");
//        Min = Math.min(Min, this.cluster0.Get_Min_Alienation());
//        Max = Math.max(Max, this.cluster0.Get_Max_Alienation());
//        //System.out.println("cluster0.Colorize");
//        this.cluster0.Colorize();
//        System.out.print(" Alien:" + Alien2 + ", InEq:" + InEq2 + ",");
//        System.out.println();
//      }
//    }
}
