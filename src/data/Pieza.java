/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

/**
 *
 * @author chrisecc
 */
class Pieza {
    
    private int valorA; //valor primera mitad de la pieza
    private int valorB; //valor segunda mitad de la pieza
    private int orientacion; //0°, 90°, 180°, 270°
    
    /**
     * Constructor no parametrizado
     */
    public Pieza(){       
    }
    
    /**
     * Constructor Parametrizado
     * @param valorA valor de la primera mitad de la pieza
     * @param valorB valor de la segunda mitad de la pieza
     */
    public Pieza(int valorA, int valorB){
        this.valorA = valorA;
        this.valorB = valorB;
    }
    
    /**
     * define el orden de los numeros de la pieza
     * @param orden "ASC" para que la pieza esté menor a mayor 
     * "DCS" para que la pieza esté mayor a menor
     */
    public void ordenPieza(String orden){
        int tempA = valorA, tempB = valorB;
        if(orden.equals("ASC")){
            valorA = Math.min(tempA, tempB);
            valorB = Math.max(tempA, tempB);
        }
        if(orden.equals("DSC")){
            valorA = Math.max(tempA, tempB);
            valorB = Math.min(tempA, tempB);
        }
        System.out.println("a: "+valorA+" b: "+valorB);
    }

    public int getValorA() {
        return valorA;
    }

    
    public int getValorB() {
        return valorB;
    }

    
    public int getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(int orientacion) {
        this.orientacion = orientacion;
    }
    
    
}
