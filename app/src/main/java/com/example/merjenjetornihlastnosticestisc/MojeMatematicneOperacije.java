package com.example.merjenjetornihlastnosticestisc;

public class MojeMatematicneOperacije {

    public static double sum(double[] ob) {
        double sum = 0;
        for (int i = 0; i < ob.length; i++) {
            sum += ob[i];
        }
        return sum;
    }

    public static double izracunRezultante(double vrednostX, double vrednostY, double vrednostZ){
        double rezultanta;
        rezultanta = Math.sqrt( Math.pow(vrednostX, 2) + Math.pow(vrednostY, 2)  + Math.pow(vrednostZ, 2) );
        return rezultanta;
    }

    public static boolean sprozilecPozitivni(double opazovanaVrednost, double pogojnaVelicina) {
        if (opazovanaVrednost > pogojnaVelicina) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean sprozilecNegativni(double opazovanaVrednost, double pogojnaVelicina) {
        if (opazovanaVrednost < pogojnaVelicina) {
            return true;
        } else {
            return false;
        }
    }
}
