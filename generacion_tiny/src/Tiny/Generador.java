package Tiny;

import ast.*;
import java.util.Stack;

public class Generador {
    private static int desplazamientoTmp = 0;
    private static TablaSimbolos tablaSimbolos = null;
    /*Contador de labels*/
    private static int LB = 0; 
    private static Stack st_fjp = new Stack();
    private static Stack st_ujp = new Stack();
    
    public static void setTablaSimbolos(TablaSimbolos tabla){
        tablaSimbolos = tabla;
    }
    
    public static void generarCodigoObjeto(NodoBase raiz){
        System.out.println();
        System.out.println();
        System.out.println("------ CODIGO INTERMEDIO P DEL LENGUAJE TINY ------");
        System.out.println();
        System.out.println();
        generarPreludioEstandar();
        generar(raiz);
        /*Genero el codigo de finalizacion de ejecucion del codigo*/   
        UtGen.emitirComentario("Fin de la ejecucion.");
        UtGen.emitirOp("STN", "");
        System.out.println();
        System.out.println();
        System.out.println("------ FIN DEL CODIGO INTERMEDIO P DEL LENGUAJE TINY ------");
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
        String lbElse, lbIf;
        if(UtGen.debug)	UtGen.emitirComentario("-> if");
        generar(n.getPrueba());
        /*Genero la parte THEN*/
        lbElse=generarLabel();
        UtGen.emitirOpId("FJP", lbElse, "if false: jmp hacia else");
        /*Inserto label en la pila fjp*/
        st_fjp.push(lbElse);
        generar(n.getParteThen());
        /*Genero la parte ELSE*/
        if(n.getParteElse()!=null){
            lbIf=generarLabel(); 
            UtGen.emitirOpId("UJP", lbIf, "definicio label ujp");
            /*Inserto label en la pila ujp*/
            st_ujp.push(lbIf);
        }
        /*Saco valor del ultimo label que salta hacia el else*/
        lbElse = (String)st_fjp.pop();
        UtGen.emitirOpId("LAB", lbElse, "definicio label jmp");
        if(n.getParteElse()!=null){
            generar(n.getParteElse());
            lbIf = (String)st_ujp.pop();
            UtGen.emitirOpId("LAB", lbIf, "definicio label ujp");
        }
        if(UtGen.debug)	UtGen.emitirComentario("<- if");
    }
    
    private static void generarRepeat(NodoBase nodo){
        NodoRepeat n = (NodoRepeat)nodo;
        int localidadSaltoInicio;
        if(UtGen.debug)	UtGen.emitirComentario("-> repeat");
            localidadSaltoInicio = UtGen.emitirSalto(0);
            UtGen.emitirComentario("repeat: el salto hacia el final (luego del cuerpo) del repeat debe estar aqui");
            /* Genero el cuerpo del repeat */
            generar(n.getCuerpo());
            /* Genero el codigo de la prueba del repeat */
            generar(n.getPrueba());
            UtGen.emitirRM_Abs("JEQ", UtGen.AC, localidadSaltoInicio, "repeat: jmp hacia el inicio del cuerpo");
        if(UtGen.debug)	UtGen.emitirComentario("<- repeat");
    }			
    
    private static void generarAsignacion(NodoBase nodo){
        NodoAsignacion n = (NodoAsignacion)nodo;
        int direccion;
        UtGen.emitirOpId("LDA", n.getIdentificador() , "cargar direccion de identificador: "+n.getIdentificador());
        generar(n.getExpresion());
        UtGen.emitirOp("STO", "asignacion: almaceno el valor para el id "+n.getIdentificador());
    }
    
    private static void generarLeer(NodoBase nodo){
        NodoLeer n = (NodoLeer)nodo;
        int direccion;
        if(UtGen.debug)	UtGen.emitirComentario("-> leer");
        UtGen.emitirRO("IN", UtGen.AC, 0, 0, "leer: lee un valor entero ");
        direccion = tablaSimbolos.getDireccion(n.getIdentificador());
     /*   UtGen.emitirRM("ST", UtGen.AC, direccion, UtGen.GP, "leer: almaceno el valor entero leido en el id "+n.getIdentificador());
        */if(UtGen.debug)	UtGen.emitirComentario("<- leer");
    }
    
    private static void generarEscribir(NodoBase nodo){
        NodoEscribir n = (NodoEscribir)nodo;
        if(UtGen.debug)	UtGen.emitirComentario("-> escribir");
        /* Genero el codigo de la expresion que va a ser escrita en pantalla */
        generar(n.getExpresion());
        /* Ahora genero la salida */
        UtGen.emitirRO("OUT", UtGen.AC, 0, 0, "escribir: genero la salida de la expresion");
        if(UtGen.debug)	UtGen.emitirComentario("<- escribir");
    }
    
    private static void generarValor(NodoBase nodo){
        NodoValor n = (NodoValor)nodo;
        UtGen.emitirConstante("LDC", n.getValor(), "cargar constante: "+n.getValor());
    }
    
    private static void generarIdentificador(NodoBase nodo){
        NodoIdentificador n = (NodoIdentificador)nodo;
        UtGen.emitirOpId("LOD", n.getNombre() , "cargar valor de identificador: "+n.getNombre());
       
    }

    private static void generarOperacion(NodoBase nodo){
        NodoOperacion n = (NodoOperacion) nodo;
       
        /* Genero la expresion izquierda de la operacion */
        generar(n.getOpIzquierdo());
     
        /* Genero la expresion derecha de la operacion */
        generar(n.getOpDerecho());
   
         switch(n.getOperacion()){
            case	mas:	UtGen.emitirOp("ADI", "op: +");		
                            break;
            case	menos:	UtGen.emitirOp("SBI",  "op: -");
                            break;
            case	por:	UtGen.emitirOp("MPI", "op: *");
                            break;
            case	entre:	UtGen.emitirOp("DVI", "op: /");
                            break;		
            case	menor:	UtGen.emitirOp("MENOR",  "op: <");
                            break;
            case	igual:	UtGen.emitirOp("EQU", "op: ==");
                            break;	
            default:
                            UtGen.emitirComentario("BUG: tipo de operacion desconocida");
        }
        
    }
    
    //TODO: enviar preludio a archivo de salida, obtener antes su nombre
    private static void generarPreludioEstandar(){
        UtGen.emitirComentario("Compilacion TINY para generar codigo P");
        UtGen.emitirComentario("Archivo: "+ "NOMBRE_ARREGLAR");
        UtGen.emitirComentario("Codigo P:");
    }

    public static String generarLabel() {
        LB++;
        return "LB"+LB;
    }
}
