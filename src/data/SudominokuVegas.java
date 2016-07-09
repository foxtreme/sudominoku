/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ejemplo de posiciones de piezas
 * 
 * 0°   = [m-M]
 * --------------
 *        [m]
 * 90°  = [M]   
 * ---------------
 * 180° = [M-m]
 * ---------------
 * 270° = [M]
 *        [m] 
 * @author chrisecc
 */
public class SudominokuVegas {

    int [][] tablero;
    ArrayList <Pieza> piezas;
    boolean solucion;
    Random prng = new Random(System.currentTimeMillis());
    
    /**
     * Inicializa las variables
     */
    public SudominokuVegas(){
        tablero = new int[9][9];
        piezas = new ArrayList<Pieza>(36);
        solucion = false;
        //se crean las 36 piezas del domino
        for(int i=1;i<9;i++){
            for(int j=i+1;j<10;j++){
                Pieza nuevaPieza = new Pieza(i,j);
                piezas.add(nuevaPieza);
            }
        }      
    }
    
    /**
     * carga el tablero del archivo a la matriz 
     * @param nombreArchivo por ejemplo "tablero1" sin la extension .txt
     */
    public void cargarTablero(String nombreArchivo){
        try {
            Scanner input = new Scanner(new File("src/data/"+nombreArchivo+".txt"));
            for(int i = 0; i < 9; ++i){//filas
                for(int j = 0; j < 9; ++j){//columnas
                    if(input.hasNextInt()){
                        tablero[i][j] = input.nextInt();
                    }
                }
            }            
        }catch (FileNotFoundException ex) {
            Logger.getLogger(SudominokuVegas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * imprime las fichas de domino disponibles
     */
    public void imprimirPiezas(){
        for(int i = 0; i< piezas.size(); i++){
            System.out.println("pieza_"+(i+1)+": ["+piezas.get(i).getValorA()+"-"+piezas.get(i).getValorB()+"]");
        }
    }
    
    /**
     * Imprime el tablero
     */
    public void imprimirTablero(){
        System.out.println("------------");
        for(int i = 0; i<9; i++){
            for(int j = 0; j<9; j++){
                System.out.print(tablero[i][j]);
                if((j+1)%3==0){
                    System.out.print("|");
                }
            }
            System.out.println();
            if((i+1)%3==0){
                System.out.println("------------");
            }
        }
        
    }
    
    /**
     * Retorna la posicion de la primera casilla libre 
     * del tablero, recorrido de izq a der y de arriba a abajo
     * @return Posicion de la primera casilla libre
     */
    public Point buscarPrimeraCasillaLibre(){
        Point posicionLibre = new Point();
        boolean encontrado = false;
        for(int i= 0; i<9 && (!encontrado); i++){
            for(int j= 0; j<9 && (!encontrado) ; j++){
                if(tablero[i][j]==0){
                    posicionLibre.setLocation(i,j);
                    encontrado = true;
                }
            }
        }
        return posicionLibre;
    }
    
    /**
     * devuelve una pieza aleatorizada para verificarla luego
     * @param pieza pieza a acomodar
     * @param posicionLibre casilla libre pivote
     */
    public Pieza aleatorizarPieza(Pieza pieza, Point posicionLibre){
        //se verifican los vecinos derecho y abajo de la posicion libre
        int fila=posicionLibre.x+1,col=posicionLibre.y+1;//primero se verifican los limites del tablero
        boolean libreDerecha=false, libreAbajo=false, ascendente = prng.nextBoolean(); 
        ArrayList <Integer> orientacionesDisponibles = new ArrayList<Integer>();
        if(col<9){//si col es un espacio valido en el tablero
            if(tablero[fila-1][col] == 0){//si se puede poner la pieza horizontalmente
                if(ascendente)
                {orientacionesDisponibles.add(0);pieza.ordenPieza("ASC");}
                else{orientacionesDisponibles.add(180);pieza.ordenPieza("DSC");}
            }
        }
        if(fila<9){//si fila es un espacio valido en el tablero
            if(tablero[fila][col-1] == 0){//si se puede poner la pieza verticalmente
                if(ascendente){orientacionesDisponibles.add(90);pieza.ordenPieza("ASC");}
                else{orientacionesDisponibles.add(270);pieza.ordenPieza("DSC");}
            }
        }
        try{//puede que no haya espacio libre, pero si lo hay seleccionar una orientacion aleatoria
            int orientacion = prng.nextInt(orientacionesDisponibles.size());//selecciona una orientacion al azar
            pieza.setOrientacion(orientacionesDisponibles.get(orientacion));
            //System.out.println("Pieza: ["+pieza.getValorA()+","+pieza.getValorB()+"] orientacion: "+pieza.getOrientacion());
        }catch(Exception e){
            System.out.println("No hay espacio para ubicar la pieza");
            pieza = null;
        }
        return pieza;
    }
    
    /**
     * Selecciona al azar una de las piezas disponibles del domino
     * @return retorna la pieza seleccionada al azar
     */
    public Pieza seleccionarPieza(){
        Pieza seleccionada= new Pieza();
        int range = piezas.size();
        int indicePieza = prng.nextInt(range);
        seleccionada = piezas.remove(indicePieza);
        //System.out.println("pieza seleccionada ["+seleccionada.getValorA()+","+seleccionada.getValorB()+"]");
        return seleccionada;
    }
    
    /**
     * Verifica que una pieza en una posicion dada, no irrumpa las reglas para filas
     * @param pieza pieza a verificar
     * @param posicion posicion pivote
     * @return True si cumple con las reglas para filas
     */
    public boolean validarFila(Pieza pieza, Point posicion){
        boolean esValida = true;
        int orientacion = pieza.getOrientacion();
        int valorA = pieza.getValorA();
        int valorB = pieza.getValorB();
        for(int col=0;(col<9)&&esValida;col++){//si la pieza irrumpe con alguna regla para filas sale del ciclo sin revisar mas
            esValida = (tablero[posicion.x][col]!=valorA);//revisa toda la fila para valorA
            if(esValida && ((orientacion==0) || (orientacion==180))){//si esta horizontal revisa tambien la fila para valorB
                 esValida= (tablero[posicion.x][col]!=valorB);
            }
            if(esValida && ((orientacion==90)|| (orientacion==270))){//si esta vertical revisa la fila de abajo para valorB
                esValida= (tablero[posicion.x+1][col]!=valorB);
            }
        }
        return esValida;
    }
    
    /**
     * Verifica que una pieza en una posicion dada, no irrumpa las reglas para columnas
     * @param pieza pieza a verificar
     * @param posicion posicion pivote
     * @return True si cumple con las reglas para filas
     */
    public boolean validarCol(Pieza pieza, Point posicion){
        boolean esValida = true;
        int orientacion = pieza.getOrientacion();
        int valorA = pieza.getValorA();
        int valorB = pieza.getValorB();
        for(int fila=0;(fila<9)&&esValida;fila++){//si la pieza irrumpe con alguna regla para filas sale del ciclo sin revisar mas
            esValida = (tablero[fila][posicion.y]!=valorA);//revisa toda la columna para valorA
            if(esValida && ((orientacion==90) || (orientacion==270))){//si esta horizontal revisa tambien la fila para valorB
                 esValida= (tablero[fila][posicion.y]!=valorB);
            }
            if(esValida && ((orientacion==0)|| (orientacion==180))){//si esta vertical revisa la fila de abajo para valorB
                esValida= (tablero[fila][posicion.y+1]!=valorB);
            }
        }
        return esValida;
    }
    
    public static void main(String args[]){
        SudominokuVegas sv = new SudominokuVegas();
        //sv.imprimirPiezas();
        sv.cargarTablero("tablero1");
        sv.imprimirTablero();
        Point p = sv.buscarPrimeraCasillaLibre();
        System.out.println("Primera casilla libre: "+p.toString());
        Pieza pieza = sv.seleccionarPieza();
        pieza = sv.aleatorizarPieza(pieza, p);
        boolean validarFila = sv.validarFila(pieza, p);
        boolean validarCol = sv.validarCol(pieza, p);
        System.out.println("la Pieza: ["+pieza.getValorA()+","+pieza.getValorB()+"] orientacion: "+pieza.getOrientacion());
        System.out.println("cumple con las filas? -"+validarFila);
        System.out.println("cumple con las columnas? -"+validarCol);
    }
    
}
