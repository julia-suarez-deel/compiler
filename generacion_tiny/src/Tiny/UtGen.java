package Tiny;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/* La idea principal de esta clase (Utilidades de Generacion)es ayudar a emitir las 
 * sentencias en el asembler de la Tiny Machine (TM), haciendo mas sencilla la 
 * implementacion de un generador de codigo objeto para la misma.
 * Esta clase maneja y ayuda gestionar el numero de linea (localidad de sentencia) 
 * en la cual se debe emitir una instruccion TM.
 */

public class UtGen {
	
	public static boolean debug=true;
        public static int numeroLinea=0;

	
	/* Emite comentario */
	public static void emitirComentario(String c, BufferedWriter bw){
		if(debug){
                    UtGen.writeIns(";                          "+c+"\n", bw);
		    UtGen.writeIns("true", bw);
                }
	}

	
        
        /* Este procedimiento
	 * op p
	 * op = codigo de la operacion
	 * p = constante
         * c= comentario
	 */
	
	public static void emitirInstruccion(String op, int p, String c, BufferedWriter bw){
                numeroLinea++;
                UtGen.writeIns(op+" "+p, bw);
		if(debug)
                    UtGen.writeIns("        ;"+c, bw);
		UtGen.writeIns("true", bw);
	}
        
        /* Este procedimiento
	 * op p
	 * op = codigo de la operacion
	 * p = variable de operacion
         * c= comentario
	 */
        
        public static void emitirInstruccion(String op, String p, String c, BufferedWriter bw){
                numeroLinea++;
                UtGen.writeIns(op+" "+p, bw);
		if(debug)
                    UtGen.writeIns("        ;"+c, bw);
		UtGen.writeIns("true", bw);
	}
        
        /* Este procedimiento
	 * op 
	 * op = codigo de la operacion
         * c= comentario
	 */
        
         public static void emitirInstruccion(String op, String c, BufferedWriter bw){
            numeroLinea++;
            UtGen.writeIns(op, bw);
            if(debug)
                UtGen.writeIns("            ;"+c, bw);
            UtGen.writeIns("true", bw);	
	}
         
        public static int numeroLinea() {
            return numeroLinea;
        }
        
        /* Escribir el archivo o mostrarlo por consola */
         
        public static void writeIns(String cadenaSalida, BufferedWriter bw){
            if(bw != null){
                try {
                    if(cadenaSalida.equals("true"))
                        bw.newLine();
                    else
                        bw.write(cadenaSalida);
                    
                } catch (IOException ex) {
                    Logger.getLogger(UtGen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else{
                if(!cadenaSalida.equals("true"))
                    System.out.println(cadenaSalida);
            }
        }
}
