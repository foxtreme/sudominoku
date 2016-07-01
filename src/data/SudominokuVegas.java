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
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chrisecc
 */
public class SudominokuVegas {

    int [][] tablero;
    ArrayList <Pieza> piezas;
    boolean solucion;
    
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
    
    public static void main(String args[]){
        SudominokuVegas sv = new SudominokuVegas();
        //sv.imprimirPiezas();
        sv.cargarTablero("tablero1");
        sv.imprimirTablero();
        Point p = sv.buscarPrimeraCasillaLibre();
        System.out.println(p.toString());
    }
    
}
