package Tiny;

import ast.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

public class Generador {
    private static TablaSimbolos tablaSimbolos = null;
    private static BufferedWriter bw = null;
    /*Contador de labels*/
    private static int LB = 0; 
    private static Stack st_fjp = new Stack();
    private static Stack st_ujp = new Stack();
    
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
        }else if (nodo instanceof NodoVector){
            generarVector(nodo);
        }else if (nodo instanceof NodoIdentificador){
            generarIdentificador(nodo);
        }else if (nodo instanceof NodoFuncion){
            generarFuncion(nodo);
        }else if (nodo instanceof NodoOperacion){
            generarOperacion(nodo, false);
        }else{
            System.out.println("BUG: Tipo de nodo a generar desconocido");
        }
        /*Si el hijo de extrema izquierda tiene hermano a la derecha lo genero tambien*/
        if(nodo.TieneHermano())
            generar(nodo.getHermanoDerecha());
    }else
        System.out.println("���ERROR: por favor fije la tabla de simbolos a usar antes de generar codigo objeto!!!");
}
    
    private static void generarIfConPila(NodoBase nodo){
        NodoIf n = (NodoIf)nodo;
        String lbElse, lbIf;
        if(UtGen.debug)	UtGen.emitirComentario("-> if", bw);
        generar(n.getPrueba());
        
        
        /*Genero la parte THEN*/
        lbElse=generarLabel();
        UtGen.emitirInstruccion("FJP", lbElse, "if false: jmp hacia else", bw);
        /*Inserto label en la pila fjp*/
        st_fjp.push(lbElse);
        generar(n.getParteThen());
        /*Genero la parte ELSE*/
        if(n.getParteElse()!=null){
            lbIf=generarLabel(); 
            UtGen.emitirInstruccion("UJP", lbIf, "definicio label ujp", bw);
            /*Inserto label en la pila ujp*/
            st_ujp.push(lbIf);
        }
        /*Saco valor del ultimo label que salta hacia el else*/
        lbElse = (String)st_fjp.pop();
        UtGen.emitirInstruccion("LAB", lbElse, "definicio label jmp", bw);
        if(n.getParteElse()!=null){
            generar(n.getParteElse());
            lbIf = (String)st_ujp.pop();
            UtGen.emitirInstruccion("LAB", lbIf, "definicio label ujp", bw);
        }
        if(UtGen.debug)	UtGen.emitirComentario("<- if", bw);
        
        
    }
    
    private static void generarIf(NodoBase nodo){
        NodoIf n = (NodoIf)nodo;
        String lbElse, lbFalse=null;
        if(UtGen.debug)	UtGen.emitirComentario("-> if", bw);
        
        generar(n.getPrueba());
        
        lbElse = generarLabel();
        UtGen.emitirInstruccion("FJP", lbElse, "if false: jmp hacia else", bw);
        
        generar(n.getParteThen());
        
        if(n.getParteElse()!=null){
            lbFalse=generarLabel(); 
            UtGen.emitirInstruccion("UJP", lbFalse, "definicion label ujp", bw);
        }
        
        UtGen.emitirInstruccion("LAB", lbElse, "definicion label jmp", bw);
        
        if(lbFalse!=null){
            generar(n.getParteElse());
            UtGen.emitirInstruccion("LAB", lbFalse, "definicio label ujp", bw);
        }
        
        if(UtGen.debug)	UtGen.emitirComentario("<- if", bw);
    }
    
    private static void generarRepeat(NodoBase nodo){
        NodoRepeat n = (NodoRepeat)nodo;
        String localidadSaltoInicio;
        if(UtGen.debug)	UtGen.emitirComentario("-> Repeat", bw);
        
        localidadSaltoInicio = generarLabel();
        /* Genero el label */
        UtGen.emitirInstruccion("LAB", localidadSaltoInicio, "Definicion label para repeat", bw);
        /* Genero el cuerpo del repeat */
        generar(n.getCuerpo());
        /* Genero el codigo de la prueba del repeat */
        generar(n.getPrueba());
        UtGen.emitirInstruccion("FJP", localidadSaltoInicio, "repeat: Salto hacia el inicio del cuerpo", bw);
            
        if(UtGen.debug)	UtGen.emitirComentario("<- repeat", bw);
    }			
    
    private static void generarAsignacion(NodoBase nodo){
        NodoAsignacion n = (NodoAsignacion)nodo;
        if (n.getVariable() instanceof NodoIdentificador){
            NodoIdentificador variable = (NodoIdentificador)n.getVariable();
            int direccion = tablaSimbolos.getDireccion(variable.getNombre());
            UtGen.emitirInstruccion("LDA", direccion , "cargar direccion de identificador: "+variable.getNombre(), bw);
            generar(n.getExpresion());
            UtGen.emitirInstruccion("STO", "asignacion: almaceno el valor para el id "+variable.getNombre(), bw);
        }
        else if(n.getVariable()  instanceof  NodoVector){
            NodoVector variable = (NodoVector)n.getVariable();
            NodoIdentificador identificador_vector = (NodoIdentificador)variable.getIdentificador();
            int direccion = tablaSimbolos.getDireccion(identificador_vector.getNombre());
            UtGen.emitirInstruccion("LDA", direccion , "cargar direccion de identificador: "+identificador_vector.getNombre(), bw);
            
            if (variable.getExpresion() instanceof NodoOperacion){
                generarOperacion(variable.getExpresion(), true);
            }else{
                generar(variable.getExpresion());
            }
            
            UtGen.emitirInstruccion("IXA elem_size("+direccion+")", direccion , "cargar direccion de identificador: "+identificador_vector.getNombre(), bw);
            generar(n.getExpresion());
            UtGen.emitirInstruccion("STO", "asignacion: almaceno el valor para el id "+identificador_vector.getNombre(), bw);
        }
    }
    
    private static void generarLeer(NodoBase nodo){
        NodoLeer n = (NodoLeer)nodo;
        if(UtGen.debug)	UtGen.emitirComentario("-> leer", bw);
        if(n.getVariable() instanceof NodoIdentificador){
            NodoIdentificador id = (NodoIdentificador)n.getVariable();
            int direccion = tablaSimbolos.getDireccion(id.getNombre());
            UtGen.emitirInstruccion("LDA", direccion , "cargar direccion de identificador: "+id.getNombre(), bw);
            UtGen.emitirInstruccion("RDI", "leer el valor para el identificador "+id.getNombre(), bw);
        }else if(n.getVariable() instanceof NodoVector){
            NodoVector vector = (NodoVector)n.getVariable();
            NodoIdentificador id = (NodoIdentificador)vector.getIdentificador();
            int direccion = tablaSimbolos.getDireccion(id.getNombre());
            UtGen.emitirInstruccion("LDA", direccion , "cargar direccion de la variable: "+id.getNombre(), bw);
            
            if (vector.getExpresion() instanceof NodoOperacion){
                generarOperacion(vector.getExpresion(), true);
            }else{
                generar(vector.getExpresion());
            }
            UtGen.emitirInstruccion("IXA elem_size("+direccion+")", direccion , "cargar la direccion de la posicion del vector: "+id.getNombre(), bw);
            UtGen.emitirInstruccion("RDI", "leer el valor para el identificador "+id.getNombre(), bw);
        }
        if(UtGen.debug)	UtGen.emitirComentario("<- leer", bw);
    }
    
    private static void generarEscribir(NodoBase nodo){
        NodoEscribir n = (NodoEscribir)nodo;
        if(UtGen.debug)	UtGen.emitirComentario("-> escribir", bw);
        generar(n.getExpresion());
        UtGen.emitirInstruccion("WRI", "escribir valor del tope", bw);
        if(UtGen.debug)	UtGen.emitirComentario("<- escribir", bw);
    }
    
    private static void generarValor(NodoBase nodo){
        NodoValor n = (NodoValor)nodo;
        UtGen.emitirInstruccion("LDC", n.getValor(), "cargar constante: "+n.getValor(), bw);
    }

    private static void generarVector(NodoBase nodo){
        NodoVector n = (NodoVector) nodo;
        NodoIdentificador ni = (NodoIdentificador) n.getIdentificador();
        int direccion = tablaSimbolos.getDireccion(ni.getNombre());
        UtGen.emitirInstruccion("LDA",direccion, "cargar direccion de la variable: "+ni.getNombre(), bw);
        
        if (n.getExpresion() instanceof NodoOperacion){
            generarOperacion(n.getExpresion(), true);
        }else{
            generar(n.getExpresion());
        }
        
        UtGen.emitirInstruccion("IXA elem_size("+direccion+")", "cargar la direccion de la posicion del vector: "+ni.getNombre(), bw);
        UtGen.emitirInstruccion("IND 0", "cargar el valor de la direccion anterior", bw);
    }

    private static void generarIdentificador(NodoBase nodo){
        NodoIdentificador n = (NodoIdentificador)nodo;
        int direccion = tablaSimbolos.getDireccion(n.getNombre());
        //if(UtGen.debug)	UtGen.emitirComentario("true", bw);
        UtGen.emitirInstruccion("LOD", direccion , "cargar valor de identificador: "+n.getNombre(), bw);
       
    }
    
    private static void generarFuncion(NodoBase nodo){
    
    }

    private static void generarOperacion(NodoBase nodo, boolean vector){
        NodoOperacion n = (NodoOperacion) nodo;
        
        if (n.getOperacion() == tipoOp.menor && vector==false){
            generar(n.getOpDerecho());
            generar(n.getOpIzquierdo());
            UtGen.emitirInstruccion("GRT", "A mayor que B: B<A", bw);
            return;
        }
        
        if ((n.getOperacion() == tipoOp.menor || n.getOperacion() == tipoOp.igual) && vector==true){
            if(UtGen.debug) UtGen.emitirComentario("Error fatal! vector con argumento < o =", bw);
            System.out.print("Error fatal! vector con argumento del vector < o =");
            try {
                bw.close();
            } catch (IOException ex) {
                System.out.println("---------");
            }
            System.exit(0); 
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

    public static String generarLabel() {
        LB++;
        return "LB"+LB;
    }
}
