/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chris
 */
public class Gem {
    static final long m = 2147483647L;
    static final long a = 48271L;
    static final long q = 44488L;
    static final long r = 3399L;

 
    /**
     *
     * Constructor clase Gem
     */
    public Gem() {
    }

    /**
     * *
     * Generador de numeros pseudoaleatorios entre 0 y 1
     * @return numero pseudoaleatorio
     */
    public double generarAleatorio() {
        double numeroGenerado = 0.0;
        //esto se hace para trucar el overflow de multiplicar numeros tan grandes
        long r_seed = System.currentTimeMillis();
        long hi = r_seed / q;
        long lo = r_seed - q * hi;
        long t = a * lo - r * hi;
        if (t > 0) {
            r_seed = t;
        } else {
            r_seed = t + m;
        }
        numeroGenerado = ((double) r_seed / (double) m);
        return numeroGenerado;
    }
}
