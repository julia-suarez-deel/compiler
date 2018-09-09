package Tiny;

import ast.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Generador {
    private static int desplazamientoTmp = 0;
    private static TablaSimbolos tablaSimbolos = null;
    private static String archivo;
    private static BufferedWriter bw = null;
    
    public static void setTablaSimbolos(TablaSimbolos tabla){
        tablaSimbolos = tabla;
    }
    
    public static void generarCodigoObjeto(NodoBase raiz, String archivoEntrada, String archivoSalida){
        System.out.println();
        System.out.println();
        generarPreludioEstandar(archivoEntrada, archivoSalida);
        System.out.println();
        System.out.println();
        if(archivoSalida != null){
            File fileOut = new File(archivoSalida);
            fileOut.getParentFile().mkdirs();
            try {
                bw = new BufferedWriter(new FileWriter(fileOut));
            } catch (IOException ex) {
                System.out.println("ADVERTENCIA!!: El archivo "+ archivoSalida + " no pudo ser creado. \n               El codigo se imprimirá por salida estandar.\n\n");
            }
        }else {
            System.out.println("------     CODIGO INTERMEDIO P DEL LENGUAJE TINY     ------");
            System.out.println();
            System.out.println();
        }
        generar(raiz);
        if(archivoSalida == null){
            System.out.println();
            System.out.println();
            System.out.println("------ FIN DEL CODIGO INTERMEDIO P DEL LENGUAJE TINY ------");
        }else{
            try {
                bw.close();
            } catch (IOException ex) {
                System.out.println("---------");
            }
        }
        
        System.out.println("Compilación terminada.");
    }
    
    //Funcion principal de generacion de codigo
    //prerequisito: Fijar la tabla de simbolos antes de generar el codigo objeto 
    private static void generar(NodoBase nodo){
    if(tablaSimbolos!=null){
        if (nodo instanceof  NodoIf){
            generarIf(nodo);
        }else if (nodo instanceof  NodoRepeat){
            generarRepeat(nodo);
        }else if (nodo instanceof  NodoAsignacion){
            generarAsignacion(nodo);
        }else if (nodo instanceof  NodoLeer){
            generarLeer(nodo);
        }else if (nodo instanceof  NodoEscribir){
            generarEscribir(nodo);
        }else if (nodo instanceof NodoValor){
            generarValor(nodo);
        }else if (nodo instanceof NodoIdentificador){
            generarIdentificador(nodo);
        }else if (nodo instanceof NodoOperacion){
            generarOperacion(nodo);
        }else{
            System.out.println("BUG: Tipo de nodo a generar desconocido");
        }
        /*Si el hijo de extrema izquierda tiene hermano a la derecha lo genero tambien*/
        if(nodo.TieneHermano())
            generar(nodo.getHermanoDerecha());
    }else
        System.out.println("���ERROR: por favor fije la tabla de simbolos a usar antes de generar codigo objeto!!!");
}

    private static void generarIf(NodoBase nodo){
        NodoIf n = (NodoIf)nodo;
        int localidadSaltoElse,localidadSaltoEnd,localidadActual;
        if(UtGen.debug)	UtGen.emitirComentario("-> if", bw);
        /*Genero el codigo para la parte de prueba del IF*/
        generar(n.getPrueba());
        localidadSaltoElse = UtGen.emitirSalto(1);
        UtGen.emitirComentario("If: el salto hacia el else debe estar aqui", bw);
        /*Genero la parte THEN*/
        generar(n.getParteThen());
        localidadSaltoEnd = UtGen.emitirSalto(1);
        UtGen.emitirComentario("If: el salto hacia el final debe estar aqui", bw);
        localidadActual = UtGen.emitirSalto(0);
        UtGen.cargarRespaldo(localidadSaltoElse, bw);
        //UtGen.emitirRM_Abs("JEQ", UtGen.AC, localidadActual, "if: jmp hacia else");
        UtGen.restaurarRespaldo();
        /*Genero la parte ELSE*/
        if(n.getParteElse()!=null){
            generar(n.getParteElse());
            localidadActual = UtGen.emitirSalto(0);
            UtGen.cargarRespaldo(localidadSaltoEnd, bw);
        //    UtGen.emitirRM_Abs("LDA", UtGen.PC, localidadActual, "if: jmp hacia el final");
            UtGen.restaurarRespaldo();
        }
        
        if(UtGen.debug)	UtGen.emitirComentario("<- if", bw);
    }
    
    private static void generarRepeat(NodoBase nodo){
        NodoRepeat n = (NodoRepeat)nodo;
        int localidadSaltoInicio;
        if(UtGen.debug)	UtGen.emitirComentario("-> repeat", bw);
            localidadSaltoInicio = UtGen.emitirSalto(0);
            UtGen.emitirComentario("repeat: el salto hacia el final (luego del cuerpo) del repeat debe estar aqui", bw);
            /* Genero el cuerpo del repeat */
            generar(n.getCuerpo());
            /* Genero el codigo de la prueba del repeat */
            generar(n.getPrueba());
            UtGen.emitirRM_Abs("JEQ", UtGen.AC, localidadSaltoInicio, "repeat: jmp hacia el inicio del cuerpo", bw);
        if(UtGen.debug)	UtGen.emitirComentario("<- repeat", bw);
    }			
    
    private static void generarAsignacion(NodoBase nodo){
        NodoAsignacion n = (NodoAsignacion)nodo;
        int direccion;
        UtGen.emitirInstruccion("LDA", n.getIdentificador() , "cargar direccion de identificador: "+n.getIdentificador(), bw);
        generar(n.getExpresion());
        UtGen.emitirInstruccion("STO", "asignacion: almaceno el valor para el id "+n.getIdentificador(), bw);
    }
    
    private static void generarLeer(NodoBase nodo){
        NodoLeer n = (NodoLeer)nodo;
        int direccion;
        if(UtGen.debug)	UtGen.emitirComentario("-> leer", bw);
        UtGen.emitirRO("IN", UtGen.AC, 0, 0, "leer: lee un valor entero ", bw);
        direccion = tablaSimbolos.getDireccion(n.getIdentificador());
     /*   UtGen.emitirRM("ST", UtGen.AC, direccion, UtGen.GP, "leer: almaceno el valor entero leido en el id "+n.getIdentificador());
        */if(UtGen.debug)	UtGen.emitirComentario("<- leer", bw);
    }
    
    private static void generarEscribir(NodoBase nodo){
        NodoEscribir n = (NodoEscribir)nodo;
        if(UtGen.debug)	UtGen.emitirComentario("-> escribir", bw);
        /* Genero el codigo de la expresion que va a ser escrita en pantalla */
        generar(n.getExpresion());
        /* Ahora genero la salida */
        UtGen.emitirRO("OUT", UtGen.AC, 0, 0, "escribir: genero la salida de la expresion", bw);
        if(UtGen.debug)	UtGen.emitirComentario("<- escribir", bw);
    }
    
    private static void generarValor(NodoBase nodo){
        NodoValor n = (NodoValor)nodo;
        UtGen.emitirInstruccion("LDC", n.getValor(), "cargar constante: "+n.getValor(), bw);
    }
    
    private static void generarIdentificador(NodoBase nodo){
        NodoIdentificador n = (NodoIdentificador)nodo;
        UtGen.emitirInstruccion("LOD", n.getNombre() , "cargar valor de identificador: "+n.getNombre(), bw);
       
    }

    private static void generarOperacion(NodoBase nodo){
        NodoOperacion n = (NodoOperacion) nodo;
        
        if (n.getOperacion() == tipoOp.menor){
            generar(n.getOpDerecho());
            generar(n.getOpIzquierdo());
            UtGen.emitirInstruccion("GRT", "A mayor que B: B<A", bw);
            return;
        }
        
        /* Genero la expresion izquierda de la operacion */
        generar(n.getOpIzquierdo());

        /* Genero la expresion derecha de la operacion */
        generar(n.getOpDerecho());

         switch(n.getOperacion()){
            case	mas:	UtGen.emitirInstruccion("ADI", "Operación suma: +", bw);		
                            break;
            case	menos:	UtGen.emitirInstruccion("SBI",  "Operación resta: -", bw);
                            break;
            case	por:	UtGen.emitirInstruccion("MPI", "Operación multiplicación: *", bw);
                            break;
            case	entre:	UtGen.emitirInstruccion("DVI", "Operación división: /", bw);
                            break;
            case	igual:	UtGen.emitirInstruccion("EQU", "Igual a: ==", bw);
                            break;
            /*case	menor:	UtGen.emitirInstruccion("MENOR", "Menor que: <", bw);
                            break;*/
            default:
                            UtGen.emitirComentario("BUG: tipo de operacion desconocida", bw);
        }
    }
    
    //Genera el Preludio del archivo
    private static void generarPreludioEstandar(String archivoEntrada, String archivoSalida){
        System.out.println("Compilacion TINY para generar codigo P");
        if(archivoEntrada != null)
            System.out.println("Archivo fuente: "+ archivoEntrada);
        else
            System.out.println("Archivo fuente: "+ "Entrada estandar.");
        if(archivoSalida != null)
            System.out.println("Codigo P: "+ archivoSalida);
        else
            System.out.println("Codigo P: "+ "Salida estandar");
    }

}
