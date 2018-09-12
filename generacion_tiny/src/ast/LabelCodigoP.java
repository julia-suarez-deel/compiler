/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

/**
 *
 * @author loreanaisabel
 */
public class LabelCodigoP {
    private int numeroLinea;
    private String etiqueta;
    
    public LabelCodigoP(int numeroLinea, String etiqueta){
        this.numeroLinea=numeroLinea;
        this.etiqueta=etiqueta;
    }
    
    public LabelCodigoP(String etiqueta){
        this.numeroLinea=-1;
        this.etiqueta=etiqueta;
    }
    
    public LabelCodigoP(){
        this.numeroLinea=-1;
        this.etiqueta=null;
    }
    
    public void setNumeroLinea(int numeroLinea){
        this.numeroLinea=numeroLinea;
    }
    
    public void setEtiqueta(String etiqueta){
        this.etiqueta=etiqueta;
    }
    
    public int getNumeroLinea(){
        return numeroLinea;
    }
    
    public String getEtiqueta(){
        return etiqueta;
    }
    
    
}
