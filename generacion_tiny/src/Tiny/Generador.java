package Tiny;

import ast.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Generador {
    private static TablaSimbolos tablaSimbolos = null;
    private static BufferedWriter bw = null;
    /*Contador de labels*/
    private static int LB = 0;
    private static int bloqueActual = 0;
    private static ArrayList<LabelCodigoP> etiquetas = new ArrayList<LabelCodigoP>();
    private static ArrayList<NodoFuncion> funciones = new ArrayList<NodoFuncion>();
    //private static LabelCodigoP et = new LabelCodigoP();
    public static void setTablaSimbolos(TablaSimbolos tabla){
        tablaSimbolos = tabla;
    }
   
    
    public static void generarCodigoObjeto(NodoBase raiz, String archivoEntrada, String archivoSalida) throws IOException{
        System.out.println();
        System.out.println();
        generarPreludioEstandar(archivoEntrada, archivoSalida);
        System.out.println();
        System.out.println();

        String archivoSalidaTemp = archivoSalida + ".tmp";

        File fileOut = new File(archivoSalidaTemp);
        fileOut.getParentFile().mkdirs();
        try {
            bw = new BufferedWriter(new FileWriter(fileOut));
        } catch (IOException ex) {
            System.out.println("ERROR!!: El archivo "+ archivoSalidaTemp + " no pudo ser creado.");
            throw ex;
        }

        if(UtGen.debug)	UtGen.emitirComentario("-> Inicio de código", bw);
        generar(raiz);
        if(UtGen.debug)	UtGen.emitirInstruccion("STP", "finaliza el código", bw);

        generarDeclaracionFunciones();
        if(UtGen.debug)	UtGen.emitirComentario("<- Fin de código", bw);
        
        try {
            bw.close();
            poscompilacion(archivoSalida, archivoSalidaTemp);
        } catch (IOException ex) {
            System.out.println("---------");
            fileOut.delete();
            throw ex;
        }
        fileOut.delete();
        System.out.println("-------- Etiquetas ------------");
        getEtiquetas();
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
        }else if (nodo instanceof NodoFuncion){
            generarFuncion(nodo);
        }else if (nodo instanceof NodoArgumento){
            generarListaArgs(nodo);
        }else if (nodo instanceof NodoValor){
            generarValor(nodo);
        }else if (nodo instanceof NodoVector){
            if(!((NodoVector)nodo).isDeclaracion())
            generarVector(nodo);
        }else if (nodo instanceof NodoIdentificador){
            generarIdentificador(nodo);
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
        LabelCodigoP et = new LabelCodigoP(UtGen.numeroLinea(),lbElse);
        //et.setEtiqueta(lbElse);
        //et.setNumeroLinea(UtGen.numeroLinea());
        etiquetas.add(et);
        
        if(lbFalse!=null){
            generar(n.getParteElse());
            UtGen.emitirInstruccion("LAB", lbFalse, "definicio label ujp", bw);
            LabelCodigoP et1 = new LabelCodigoP(UtGen.numeroLinea(),lbFalse);
            //et.setEtiqueta(lbFalse);
            //et.setNumeroLinea(UtGen.numeroLinea());
            etiquetas.add(et1);
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
        LabelCodigoP et1 = new LabelCodigoP(UtGen.numeroLinea(),localidadSaltoInicio);
        etiquetas.add(et1);
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
            int direccion = tablaSimbolos.getDireccion(variable.getNombre(),bloqueActual);
            UtGen.emitirInstruccion("LDA", direccion , "cargar direccion de identificador: "+variable.getNombre(), bw);
            generar(n.getExpresion());
            UtGen.emitirInstruccion("STO", "asignacion: almaceno el valor para el id "+variable.getNombre(), bw);
        }
        else if(n.getVariable()  instanceof  NodoVector){
            NodoVector variable = (NodoVector)n.getVariable();
            NodoIdentificador identificador_vector = (NodoIdentificador)variable.getIdentificador();
            int direccion = tablaSimbolos.getDireccion(identificador_vector.getNombre(),bloqueActual);
            UtGen.emitirInstruccion("LDA", direccion , "cargar direccion de identificador: "+identificador_vector.getNombre(), bw);
            
            if (variable.getExpresion() instanceof NodoOperacion){
                generarOperacion(variable.getExpresion(), true);
            }else{
                generar(variable.getExpresion());
            }
            
            UtGen.emitirInstruccion("IXA", "1" , "cargar direccion de identificador: "+identificador_vector.getNombre(), bw);
            generar(n.getExpresion());
            UtGen.emitirInstruccion("STO", "asignacion: almaceno el valor para el id "+identificador_vector.getNombre(), bw);
        }
    }
    
    private static void generarLeer(NodoBase nodo){
        NodoLeer n = (NodoLeer)nodo;
        if(UtGen.debug)	UtGen.emitirComentario("-> leer", bw);
        if(n.getVariable() instanceof NodoIdentificador){
            NodoIdentificador id = (NodoIdentificador)n.getVariable();
            int direccion = tablaSimbolos.getDireccion(id.getNombre(),bloqueActual);
            UtGen.emitirInstruccion("LDA", direccion , "cargar direccion de identificador: "+id.getNombre(), bw);
            UtGen.emitirInstruccion("RDI", "leer el valor para el identificador "+id.getNombre(), bw);
        }else if(n.getVariable() instanceof NodoVector){
            NodoVector vector = (NodoVector)n.getVariable();
            NodoIdentificador id = (NodoIdentificador)vector.getIdentificador();
            int direccion = tablaSimbolos.getDireccion(id.getNombre(),bloqueActual);
            UtGen.emitirInstruccion("LDA", direccion , "cargar direccion de la variable: "+id.getNombre(), bw);
            
            if (vector.getExpresion() instanceof NodoOperacion){
                generarOperacion(vector.getExpresion(), true);
            }else{
                generar(vector.getExpresion());
            }
            UtGen.emitirInstruccion("IXA", "1" , "cargar la direccion de la posicion del vector: "+id.getNombre(), bw);
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
        int direccion = tablaSimbolos.getDireccion(ni.getNombre(),bloqueActual);
        UtGen.emitirInstruccion("LDA",direccion, "cargar direccion de la variable: "+ni.getNombre(), bw);
        
        if (n.getExpresion() instanceof NodoOperacion){
            generarOperacion(n.getExpresion(), true);
        }else{
            generar(n.getExpresion());
        }
        
        UtGen.emitirInstruccion("IXA", "1", "cargar la direccion de la posicion del vector: "+ni.getNombre(), bw);
        UtGen.emitirInstruccion("IND", 0,"cargar el valor de la direccion anterior", bw);
    }

    private static void generarIdentificador(NodoBase nodo){
        NodoIdentificador n = (NodoIdentificador)nodo;
        int direccion = tablaSimbolos.getDireccion(n.getNombre(),bloqueActual);
        //if(UtGen.debug)	UtGen.emitirComentario("true", bw);
        UtGen.emitirInstruccion("LOD", direccion , "cargar valor de identificador: "+n.getNombre(), bw);
       
    }
    
    private static void generarFuncion(NodoBase nodo){
        NodoFuncion n= (NodoFuncion)nodo;
        String nombre;

        if (n.getRetorno()==null){
            nombre = ((NodoIdentificador)n.getIdentificador()).getNombre();
            generarLamada(nombre,n.getArgumentos());
        }else if (n.getRetorno()!=null) {
            funciones.add(n);
        }
    }

    private static void generarLamada(String nombre,NodoBase argumentos){
        if(UtGen.debug)	UtGen.emitirComentario("-> llamada a funcion", bw);
        if(argumentos != null){
            UtGen.emitirInstruccion("MST", " inicio de lista de argumentos", bw);
            generar(argumentos);
        }
        UtGen.emitirInstruccion("CUP", "LB"+nombre, " llamada a funcion: "+ nombre, bw);
        if(UtGen.debug)	UtGen.emitirComentario("<- llamada a funcion", bw);
    }

    private static void generarListaArgs(NodoBase nodo){
        NodoArgumento n = (NodoArgumento)nodo;
        generar(n.getIdentificador());
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

    //Genera la declaracion de funciones
    private static void generarDeclaracionFunciones(){
        String label;
        int bloqueAnterior;
        for(NodoFuncion funcion: funciones){
            bloqueAnterior = bloqueActual;
            UtGen.emitirInstruccion("ENT", tablaSimbolos.getDireccion(((NodoIdentificador) funcion.getIdentificador()).getNombre(),bloqueActual), "Punto de entrada a la función", bw);
            bloqueActual = funcion.getNroBloque();
            label = "LB" + ((NodoIdentificador) funcion.getIdentificador()).getNombre();
            etiquetas.add(new LabelCodigoP(UtGen.numeroLinea(),label));
            generar(funcion.getCuerpo());
            if (funcion.getRetorno() instanceof NodoIdentificador) {
                generarIdentificador(funcion.getRetorno());
            } else if (funcion.getRetorno() instanceof NodoVector) {
                generarVector(funcion.getRetorno());
            }
            UtGen.emitirInstruccion("RET", "Retorno valor del tope de la pila", bw);
            bloqueActual = bloqueAnterior;
        }
    }

    public static String generarLabel() {
        LB++;
        return "LB"+LB;
    }
    
    private static void poscompilacion(String archivoSalida, String archivoSalidaTemp) throws IOException{
        Matcher m;
        int instruccion;
        String patron = "LB[0-9a-zA-Z]+";
        Pattern p = Pattern.compile(patron);
        File fichero = new File(archivoSalidaTemp);
        Scanner s = null;
        BufferedWriter out = null;
        File fileOut = new File(archivoSalida);
        fileOut.getParentFile().mkdirs();
        try {
            out = new BufferedWriter(new FileWriter(fileOut));
        } catch (IOException ex) {
            System.out.println("ERROR!!: El archivo " + archivoSalida + " no pudo ser creado.");
            throw ex;
        }
        try {
            s = new Scanner(fichero);
            while (s.hasNextLine()) {
                String linea = s.nextLine();
                m = p.matcher(linea);
                String[] palabra = linea.split(" ");
                if(palabra[0].equals("LAB") || palabra[0].equals("FJP") || palabra[0].equals("UJP") || palabra[0].equals("CUP")){
                    if(m.find()){
                        instruccion = direccionEtiqueta(m.group());
                        linea = m.replaceAll(String.valueOf(instruccion));
                    }
                }
                escribir(linea, out);
                escribir("true", out);
            }
        } catch (Exception ex) {
            System.out.println("El archivo " + archivoSalidaTemp + " no pudo ser leido");
            if (s != null)
                s.close();
            out.close();
            throw ex;
        }
        s.close();
        out.close();
    }
    
     
    
    private static void escribir(String cadenaSalida, BufferedWriter out){
         if(out != null) {                
            try {
                if(cadenaSalida.equals("true"))
                    out.newLine();
                else
                    out.write(cadenaSalida);

            } catch (IOException ex) {
                Logger.getLogger(UtGen.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }
    
    public static void getEtiquetas(){
        Iterator<LabelCodigoP> itrEtiquetas = etiquetas.iterator();
        while(itrEtiquetas.hasNext()){
            LabelCodigoP et = itrEtiquetas.next();
            System.out.println(et.getEtiqueta() + " "
			+ et.getNumeroLinea());
        }
    }
    
    private static int direccionEtiqueta(String label){
        Iterator<LabelCodigoP> itrEtiquetas = etiquetas.iterator();
        while(itrEtiquetas.hasNext()){
            LabelCodigoP et = itrEtiquetas.next();
            if (et.getEtiqueta().equals(label)) {
                return et.getNumeroLinea();
            }
        }
        return -1;
    }
}
