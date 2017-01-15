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
  public static Cluster Evolve() {
    int Dunbar_Limit = 4;//5;//4;//3;
    int NumNodes = 1 << Dunbar_Limit;
    return Evolve(NumNodes, Dunbar_Limit);
  }
  /* ********************************************************************************* */
  public static Cluster Evolve(int NumNodes, int Dunbar_Limit) {
    int Num_Clusters = 100;
    int Replace_Num = Num_Clusters * 4 / 5;/// 2;//
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
      // sort and reproduce
      Collections.sort(farm, new Comparator<Cluster>() {
        @Override public int compare(Cluster cluster0, Cluster cluster1) {
          if (true) {
            return -Double.compare(cluster0.AlienationNumber, cluster1.AlienationNumber);// breeding for goodness
          } else {
            return Double.compare(cluster0.AlienationNumber, cluster1.AlienationNumber);// breeding for badness
          }
        }
      });
      for (int cnt = 0; cnt < Num_Clusters; cnt++) {
        seed = farm.get(cnt);
        Alien = seed.AlienationNumber;
        InEq = seed.Inequality;
        System.out.println(" Alien:" + Alien + ", InEq:" + InEq + ",");
      }
      winner = farm.get(Num_Clusters - 1);
      // go through all the highest (worst) alienation numbers and replace with muatated copies from the best
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
}
