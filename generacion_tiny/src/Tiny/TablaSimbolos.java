package Tiny;

import java.util.*;

import ast.NodoArgumento;
import ast.NodoAsignacion;
import ast.NodoBase;
import ast.NodoEscribir;
import ast.NodoLeer;
import ast.NodoIdentificador;
import ast.NodoIf;
import ast.NodoOperacion;
import ast.NodoRepeat;
import ast.NodoVector;
import ast.NodoFuncion;

public class TablaSimbolos {

    private ArrayList<HashMap<String, RegistroSimbolo>> secciones;
    private HashMap<String, RegistroSimbolo> tabla;
    int direccion;
    
    public TablaSimbolos() {
        super();
        this.direccion = 0;
        this.secciones = new ArrayList<HashMap<String, RegistroSimbolo>>();
    }

    public void cargarTabla(NodoBase raiz, int bloque){
            
            while (raiz != null) {
                if (raiz instanceof NodoIdentificador){
                    InsertarSimbolo(((NodoIdentificador)raiz).getNombre(),bloque);
                    // TODO: Añadir el numero de linea y localidad de memoria correcta
                }

                /* Hago el recorrido recursivo */
                else if(raiz instanceof NodoVector){
                    //cargarTabla(((NodoVector)raiz).getIdentificador(), bloque);
                    cargarTabla(((NodoVector)raiz).getExpresion(), bloque);
                }else if (raiz instanceof  NodoIf){
                    cargarTabla(((NodoIf)raiz).getPrueba(), bloque);
                    cargarTabla(((NodoIf)raiz).getParteThen(), bloque);
                    if(((NodoIf)raiz).getParteElse()!=null){
                        cargarTabla(((NodoIf)raiz).getParteElse(), bloque);
                    }
                }
                else if (raiz instanceof  NodoRepeat){
                    cargarTabla(((NodoRepeat)raiz).getCuerpo(), bloque);
                    cargarTabla(((NodoRepeat)raiz).getPrueba(), bloque);
                }
                else if (raiz instanceof  NodoAsignacion){
                    cargarTabla(((NodoAsignacion)raiz).getVariable(), bloque);
                    cargarTabla(((NodoAsignacion)raiz).getExpresion(), bloque);
                }
                else if (raiz instanceof  NodoEscribir)
                    cargarTabla(((NodoEscribir)raiz).getExpresion(), bloque);
                else if (raiz instanceof  NodoLeer)
                    cargarTabla(((NodoLeer)raiz).getVariable(), bloque);
                else if (raiz instanceof NodoOperacion){
                    cargarTabla(((NodoOperacion)raiz).getOpIzquierdo(), bloque);
                    cargarTabla(((NodoOperacion)raiz).getOpDerecho(), bloque);
                }else if (raiz instanceof NodoFuncion){
                    if(((NodoFuncion) raiz).getCuerpo() != null){
                        cargarTabla(((NodoFuncion) raiz).getIdentificador(), bloque);
                        cargarTabla(((NodoFuncion) raiz).getArgumentos(), ((NodoFuncion) raiz).getNroBloque());
                        cargarTabla(((NodoFuncion) raiz).getCuerpo(), ((NodoFuncion) raiz).getNroBloque());
                        //cargarTabla(((NodoFuncion) raiz).getRetorno(), bloque + 1);
                    }else{
                        cargarTabla(((NodoFuncion) raiz).getArgumentos(), bloque);
                    }
                }else if(raiz instanceof NodoArgumento){
                    cargarTabla(((NodoArgumento) raiz).getIdentificador(), bloque);
                }
                raiz = raiz.getHermanoDerecha();
            }
    }
    
    //true es nuevo no existe se insertara, false ya existe NO se vuelve a insertar 
    public boolean InsertarSimbolo(String identificador, int bloque){
        HashMap<String, RegistroSimbolo> seccion;
        try {
            //si la seccion existe
            seccion = secciones.get(bloque);
        } catch (IndexOutOfBoundsException e) {
            seccion = new HashMap<String, RegistroSimbolo>();
            secciones.add(seccion);
        }
        return InsertarSimboloEnSeccion(identificador, seccion);
    }

    // true es nuevo no existe se insertara, false ya existe NO se vuelve a insertar
    private boolean InsertarSimboloEnSeccion(String identificador, HashMap<String, RegistroSimbolo> seccion){
        RegistroSimbolo simbolo;
        if (seccion.containsKey(identificador)) {
            return false;
        } else {
            simbolo = new RegistroSimbolo(identificador, direccion++);
            seccion.put(identificador, simbolo);
            return true;
        }
    }
    
    public RegistroSimbolo BuscarSimbolo(String identificador, int bloque){
        HashMap<String, RegistroSimbolo> seccion;
        try {
            // si la seccion existe
            seccion = secciones.get(bloque);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        RegistroSimbolo simbolo=(RegistroSimbolo)seccion.get(identificador);
        return simbolo;
    }
    
    public void ImprimirTabla(){
        System.out.println("*** Tabla de Simbolos ***");
        for(int i = 0; i<secciones.size(); i++){
            for (String s : secciones.get(i).keySet()) {
                System.out.println("Bloque: " + i + " Nombre: "+ s + " Con direccion: " + BuscarSimbolo(s,i).getDireccionMemoria());
            }
        }
                
    }

    public int getDireccion(String Clave, int bloque){
        return BuscarSimbolo(Clave,bloque).getDireccionMemoria();
    }
    
    /*
     * TODO:
     * 1. Crear lista con las lineas de codigo donde la variable es usada.
     * */
}
