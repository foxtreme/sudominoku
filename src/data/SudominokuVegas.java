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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ejemplo de posiciones de piezas
 *
 * 0° = [m-M] -------------- [m] 90° = [M] --------------- 180° = [M-m]
 * --------------- 270° = [M] [m]
 *
 * @author chrisecc
 */
public class SudominokuVegas {

    private int[][] tablero;
    private ArrayList<Pieza> piezas;
    boolean solucion;
    Random prng = new Random(System.currentTimeMillis());

    /**
     * Inicializa las variables
     */
    public SudominokuVegas() {
        tablero = new int[9][9];
        piezas = new ArrayList<Pieza>(36);
        solucion = false;
        //se crean las 36 piezas del domino
        for (int i = 1; i < 9; i++) {
            for (int j = i + 1; j < 10; j++) {
                Pieza nuevaPieza = new Pieza(i, j);
                piezas.add(nuevaPieza);
            }
        }
    }

    /**
     * carga el tablero del archivo a la matriz
     *
     * @param nombreArchivo por ejemplo "tablero1" sin la extension .txt
     */
    public void cargarTablero(String nombreArchivo) {
        try {
            Scanner input = new Scanner(new File("src/data/" + nombreArchivo + ".txt"));
            for (int i = 0; i < 9; ++i) {//filas
                for (int j = 0; j < 9; ++j) {//columnas
                    if (input.hasNextInt()) {
                        tablero[i][j] = input.nextInt();
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SudominokuVegas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * imprime las fichas de domino disponibles
     */
    public void imprimirPiezas() {
        for (int i = 0; i < piezas.size(); i++) {
            System.out.println("pieza_" + (i + 1) + ": [" + piezas.get(i).getValorA() + "-" + piezas.get(i).getValorB() + "]");
        }
    }

    /**
     * Imprime el tablero
     */
    public void imprimirTablero(int[][] tableroImp) {
        System.out.println("------------");
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(tableroImp[i][j]);
                if ((j + 1) % 3 == 0) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if ((i + 1) % 3 == 0) {
                System.out.println("------------");
            }
        }

    }

    /**
     * Retorna la posicion de la primera casilla libre del tablero,
     * recorrido de izq a der y de arriba a abajo
     *
     * @return Posicion de la primera casilla libre
     */
    public Point buscarPrimeraCasillaLibre() {
        Point posicionLibre = new Point();
        boolean encontrado = false;
        for (int i = 0; i < 9 && (!encontrado); i++) {
            for (int j = 0; j < 9 && (!encontrado); j++) {
                if (tablero[i][j] == 0) {
                    posicionLibre.setLocation(i, j);
                    encontrado = true;
                }
            }
        }
        return posicionLibre;
    }

    /**
     * devuelve una pieza aleatorizada(el elemento aleatorio es la
     * orientación) para verificarla luego
     *
     * @param pieza pieza a acomodar
     * @param posicionLibre casilla libre pivote
     */
    public Pieza aleatorizarPieza(Pieza pieza, Point posicionLibre) {
        //se verifican los vecinos derecho y abajo de la posicion libre
        int fila = posicionLibre.x + 1, col = posicionLibre.y + 1;//primero se verifican los limites del tablero
        boolean libreDerecha = false, libreAbajo = false, ascendente = prng.nextBoolean();
        ArrayList<Integer> orientacionesDisponibles = new ArrayList<Integer>();
        if (col < 9) {//si col es un espacio valido en el tablero
            if (tablero[fila - 1][col] == 0) {//si se puede poner la pieza horizontalmente
                if (ascendente) {
                    orientacionesDisponibles.add(0);
                    pieza.ordenPieza("ASC");
                } else {
                    orientacionesDisponibles.add(180);
                    pieza.ordenPieza("DSC");
                }
            }
        }
        if (fila < 9) {//si fila es un espacio valido en el tablero
            if (tablero[fila][col - 1] == 0) {//si se puede poner la pieza verticalmente
                if (ascendente) {
                    orientacionesDisponibles.add(90);
                    pieza.ordenPieza("ASC");
                } else {
                    orientacionesDisponibles.add(270);
                    pieza.ordenPieza("DSC");
                }
            }
        }
        try {//puede que no haya espacio libre, pero si lo hay seleccionar una orientacion aleatoria
            int orientacion = prng.nextInt(orientacionesDisponibles.size());//selecciona una orientacion al azar
            pieza.setOrientacion(orientacionesDisponibles.get(orientacion));
            //System.out.println("Pieza: ["+pieza.getValorA()+","+pieza.getValorB()+"] orientacion: "+pieza.getOrientacion());
        } catch (Exception e) {
            System.out.println("No hay espacio para ubicar la pieza");
            pieza = null;
        }
        return pieza;
    }

    /**
     * Selecciona al azar una de las piezas disponibles del domino
     *
     * @return retorna la pieza seleccionada al azar
     */
    public Pieza seleccionarPieza() {
        Pieza seleccionada = new Pieza();
        int range = piezas.size();
        int indicePieza = prng.nextInt(range);
        seleccionada = piezas.get(indicePieza);
        //System.out.println("pieza seleccionada ["+seleccionada.getValorA()+","+seleccionada.getValorB()+"]");
        return seleccionada;
    }

    /**
     * Verifica que una pieza en una posicion dada, no irrumpa las reglas
     * para filas
     *
     * @param pieza pieza a verificar
     * @param posicion posicion pivote
     * @return True si cumple con las reglas para filas
     */
    public boolean validarFila(Pieza pieza, Point posicion) {
        boolean esValida = true;
        int orientacion = pieza.getOrientacion();
        int valorA = pieza.getValorA();
        int valorB = pieza.getValorB();
        for (int col = 0; (col < 9) && esValida; col++) {//si la pieza irrumpe con alguna regla para filas sale del ciclo sin revisar mas
            esValida = (tablero[posicion.x][col] != valorA);//revisa toda la fila para valorA
            if (esValida && ((orientacion == 0) || (orientacion == 180))) {//si esta horizontal revisa tambien la fila para valorB
                esValida = (tablero[posicion.x][col] != valorB);
            }
            if (esValida && ((orientacion == 90) || (orientacion == 270))) {//si esta vertical revisa la fila de abajo para valorB
                esValida = (tablero[posicion.x + 1][col] != valorB);
            }
        }
        return esValida;

    }

    /**
     * Verifica que una pieza en una posicion dada, no irrumpa las reglas
     * para columnas
     *
     * @param pieza pieza a verificar
     * @param posicion posicion pivote
     * @return True si cumple con las reglas para filas
     */
    public boolean validarCol(Pieza pieza, Point posicion) {
        boolean esValida = true;
        int orientacion = pieza.getOrientacion();
        int valorA = pieza.getValorA();
        int valorB = pieza.getValorB();
        for (int fila = 0; (fila < 9) && esValida; fila++) {//si la pieza irrumpe con alguna regla para filas sale del ciclo sin revisar mas
            esValida = (tablero[fila][posicion.y] != valorA);//revisa toda la columna para valorA
            if (esValida && ((orientacion == 90) || (orientacion == 270))) {//si esta horizontal revisa tambien la fila para valorB
                esValida = (tablero[fila][posicion.y] != valorB);
            }
            if (esValida && ((orientacion == 0) || (orientacion == 180))) {//si esta vertical revisa la fila de abajo para valorB
                esValida = (tablero[fila][posicion.y + 1] != valorB);
            }
        }
        return esValida;
    }

    /**
     *
     * @param p Casilla libre
     * @param sv sudominoku actual
     * @return Pieza valida seleccionada con aleatorización de pizas y
     * orientación
     */
    private Pieza seleccionarPieza2(Point p) {
        System.out.println("====================================================================================");
        Pieza seleccionada = new Pieza();
        ArrayList<Pieza> piezasTemporal = new ArrayList<>(piezas);
        //Se crea para verificar cada una de las piezas disponibles
        //Si la pieza no se puede poner,
        //      se elimina de la lista y se selecciona otra
        //Si la pieza se puede poner
        //      se selecciona y se borra de la lista REAL
        boolean piezaEncontrada = false;
        boolean casillaDisponible = true;
        while (piezasTemporal.size() > 0 && !piezaEncontrada && casillaDisponible) {
            //Inicializacion de la pieza
            int range = piezasTemporal.size();
            int indicePieza = prng.nextInt(range);
            seleccionada = piezasTemporal.get(indicePieza);
            System.out.println("====================================================================================");
            System.out.println("Pieza a verificar [" + seleccionada.getValorA() + "," + seleccionada.getValorB() + "]");

            //Seleccion aleatorizada        
            //Cargar orientaciones Disponibles    
            ArrayList<Integer> orientacionesDisponibles = cargarOrientaciones(p);
            if (orientacionesDisponibles.size() == 0) {
                System.out.println("ERROR: No se encontraron orientaciones disponibles");
                casillaDisponible = false;
                return new Pieza(-1, -1);
            }
            System.out.println("Se encontraron " + orientacionesDisponibles.size() + " orientaciones disponibles.");
            boolean piezaValida = false;//Si la pieza se puede poner
            //Se prueban todas las orientaciones posibles
            while (orientacionesDisponibles.size() > 0 && !piezaValida) {
                System.out.println("Inicia Validación pieza");

                int orientacion = prng.nextInt(orientacionesDisponibles.size());//selecciona indice de una orientacion al azar
                seleccionada.setOrientacion(orientacionesDisponibles.get(orientacion));//cambia la orientacion                

                //cambia el orden de los números
                switch (orientacionesDisponibles.get(orientacion)) {
                    case 0:
                    case 90:
                        System.out.println("Vertical: Orientación escogida al azar: " + orientacionesDisponibles.get(orientacion));
                        seleccionada.ordenPieza("ASC");
                        break;
                    case 180:
                    case 270:
                        System.out.println("Horizontal: Orientación escogida al azar: " + orientacionesDisponibles.get(orientacion));
                        seleccionada.ordenPieza("DSC");
                        break;

                }
                //Antes de validar fila, columna y subMatriz agregar el elemento a un tablero ficticio
                boolean validarFila = validarFila(seleccionada, p);
                System.out.println("Validar Fila: " + validarFila);
                boolean validarCol = validarCol(seleccionada, p);
                System.out.println("Validar Columna: " + validarCol);
                boolean validarSubMatriz = validarSubMatriz(seleccionada, p);
                System.out.println("Validar Submatriz: " + validarSubMatriz);
                //Ficha valida
                if (validarCol && validarFila && validarSubMatriz) {
                    piezaValida = true;
                } //La ficha es invalida
                else {
                    orientacionesDisponibles.remove(orientacion);
                }
            }

            if (piezaValida) {
                piezaEncontrada = true;
                piezas.remove(indicePieza);
            } else {
                piezasTemporal.remove(indicePieza);
            }

        }

        if (piezaEncontrada && casillaDisponible) {
            //retorna la encontrada
            return seleccionada;
        } else {
            //Retorna pieza vacia
            return new Pieza(-1, -1);
        }
    }

    /**
     *
     * @param p Casilla libre.
     * @return Lista de orientaciones disponibles
     */
    public ArrayList<Integer> cargarOrientaciones(Point p) {
        //se verifican los vecinos derecho y abajo de la posicion libre
        int fila = p.x + 1, col = p.y + 1;//primero se verifican los limites del tablero 
        ArrayList<Integer> orientacionesDisponibles = new ArrayList<Integer>();
        if (col < 9) {//si col es un espacio valido en el tablero
            if (tablero[fila - 1][col] == 0) {//si se puede poner la pieza horizontalmente                
                orientacionesDisponibles.add(0);
                //pieza.ordenPieza("ASC");                
                orientacionesDisponibles.add(180);
                //pieza.ordenPieza("DSC");
            }
        }

        if (fila < 9) {//si fila es un espacio valido en el tablero
            if (tablero[fila][col - 1] == 0) {//si se puede poner la pieza verticalmente                
                orientacionesDisponibles.add(90);
                //pieza.ordenPieza("ASC");                
                orientacionesDisponibles.add(270);
                //pieza.ordenPieza("DSC");

            }
        }
        return orientacionesDisponibles;
    }

    /**
     * Agregar casilla segun su orientación.
     *
     * @param pieza nueva
     * @param p casilla libre
     */
    private void agregarPieza(Pieza pieza, Point p) {
        int orientacion = pieza.getOrientacion();
        switch (orientacion) {
            case 0:
            case 180:
                tablero[p.x][p.y] = pieza.getValorA();
                tablero[p.x][p.y + 1] = pieza.getValorB();
                break;
            case 90:
            case 270:
                tablero[p.x][p.y] = pieza.getValorA();
                tablero[p.x + 1][p.y] = pieza.getValorB();
                break;
        }
    }

    public static void main(String args[]) {
        try {
            int b = 0;
            int m = 0;
            for (int j = 0; j < 1; j++) {
                SudominokuVegas sv = new SudominokuVegas();
                if (sv.run() == false) {
                    m++;
                } else {
                    b++;
                }
            }
            System.out.println("Ejecuciones Exitosas: "+b);
            System.out.println("Ejecuciones Fracasadas: "+m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean run() {
        System.out.println("====================================================================================");
        //sv.imprimirPiezas();        
        cargarTablero("tablero1");

        boolean resultado = false;
        boolean termina = false;
        while (!termina) {
            System.out.println("Tablero Actual");
            imprimirTablero(tablero);
            Point p = buscarPrimeraCasillaLibre();
            System.out.println("Primera casilla libre: " + p.toString());
            //Seleccionar pieza, aleatorizarla y eliminarla de la lista
            Pieza pieza = seleccionarPieza2(p);

            if (pieza.getValorA() == -1) {
                System.out.println("====================================================================================");
                System.out.println("No hay solución posible para el tablero actualmente:");
                imprimirTablero(tablero);
                termina = true;
                resultado = false;
                return false;
            }
            if (piezas.isEmpty()) {
                termina = true;
                resultado = true;
                System.out.println("Se encuentra solución posible");
                return true;
            }
            //Agregar pieza al tablero
            agregarPieza(pieza, p);
            System.out.println("====================================================================================");
            System.out.println("Pieza agregada: [" + pieza.getValorA() + "," + pieza.getValorB() + "] orientacion: " + pieza.getOrientacion());
            System.out.println("====================================================================================");

        }
        return resultado;
    }

    private boolean validarSubMatriz(Pieza pieza, Point p) {
        //System.out.println("-----------------------//--------------------");
        //System.out.println("Inicia Validar SubMatriz");
        boolean esValida = true;
        //Crer tablero ficticio y agregar piezas
        int[][] tableroFicticio = new int[9][9];
        byte n = (byte) tablero.length;
        for (int i = 0; i < n; i++) {
            System.arraycopy(tablero[i], 0, tableroFicticio[i], 0, n);
        }

        int i1 = p.x;
        int j1 = p.y;

        int i2 = 0;
        int j2 = 0;

        int orientacion = pieza.getOrientacion();
        switch (orientacion) {
            case 0:
            case 180:
                if (p.y + 1 > 8) {
                    return false;
                }
                tableroFicticio[p.x][p.y] = pieza.getValorA();
                tableroFicticio[p.x][p.y + 1] = pieza.getValorB();
                if (tableroFicticio[p.x][p.y + 1] != 0) {
                    i2 = p.x;
                    j2 = p.y + 1;
                }
                break;
            case 90:
            case 270:
                if (p.x + 1 > 8) {
                    return false;
                }
                tableroFicticio[p.x][p.y] = pieza.getValorA();
                tableroFicticio[p.x + 1][p.y] = pieza.getValorB();
                if (tableroFicticio[p.x + 1][p.y] != 0) {
                    i2 = p.x + 1;
                    j2 = p.y;
                }
                break;
        }

        //Verificar caja Punto 1 y punto 2
        //Hallar caja con puntos iniciales
        //Ubicar inicio caja1
        if (i1 >= 0 && i1 < 3) {
            i1 = 0;
        } else if (i1 >= 3 && i1 < 6) {
            i1 = 3;
        } else if (i1 >= 6 && i1 < 10) {
            i1 = 6;
        }
        if (j1 >= 0 && j1 < 3) {
            j1 = 0;
        } else if (j1 >= 3 && j1 < 6) {
            j1 = 3;
        } else if (j1 >= 6 && j1 < 10) {
            j1 = 6;
        }

        //Ubircar inicio caja2
        if (i2 >= 0 && i2 < 3) {
            i2 = 0;
        } else if (i2 >= 3 && i2 < 6) {
            i2 = 3;
        } else if (i2 >= 6 && i2 < 10) {
            i2 = 6;
        }
        if (j2 >= 0 && j2 < 3) {
            j2 = 0;
        } else if (j2 >= 3 && j2 < 6) {
            j2 = 3;
        } else if (j2 >= 6 && j2 < 10) {
            j2 = 6;
        }

        //Solo verificar una caja sino verificar las dos cajas
        if (i1 == i2 && j1 == j2) {

            List<Integer> caja = crearCaja(i1, j1, tableroFicticio);
            //System.out.println("Caja encontrada" + caja);
            for (int i = 0; i < caja.size(); i++) {
                for (int j = i + 1; j < caja.size(); j++) {
                    if (caja.get(i) == caja.get(j)) {
                        return false;
                    }
                }
            }

        } else {
            List caja1 = crearCaja(i1, j1, tableroFicticio);
            List caja2 = crearCaja(i2, j2, tableroFicticio);
            //System.out.println("Caja1 encontrada" + caja1);
            //System.out.println("Caja2 encontrada" + caja2);
            //Verificar caja 1
            for (int i = 0; i < caja1.size(); i++) {
                for (int j = i + 1; j < caja1.size(); j++) {
                    if (caja1.get(i) == caja1.get(j)) {
                        return false;
                    }
                }
            }
            //Verificar caja 2
            for (int i = 0; i < caja2.size(); i++) {
                for (int j = i + 1; j < caja2.size(); j++) {
                    if (caja2.get(i) == caja2.get(j)) {
                        return false;
                    }
                }
            }
        }
        return esValida;
    }

    private List<Integer> crearCaja(int i1, int j1, int[][] tableroFicticio) {
        //System.out.println("Crear caja: [" + i1 + ", " + j1 + "]");
        List<Integer> caja = new LinkedList();
        for (int i = i1; i < i1 + 3; i++) {
            for (int j = j1; j < j1 + 3; j++) {
                if (tableroFicticio[i][j] != 0) {
                    caja.add(tableroFicticio[i][j]);
                }
            }
        }
        return caja;
    }

}
