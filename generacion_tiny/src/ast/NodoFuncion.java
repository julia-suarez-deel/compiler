package ast;

public class NodoFuncion extends NodoBase {
    private NodoBase identificador;
    private NodoBase argumentos;
    private NodoBase cuerpo;
    private NodoBase retorno;
    private int NroBloque;
    
    public NodoFuncion(NodoBase identificador){
        super();
        this.identificador = identificador;
        this.argumentos = null;
        this.cuerpo = null;
        this.retorno = null;
        this.NroBloque = 0;
    }
    
    public NodoFuncion(NodoBase identificador, NodoBase argumentos){
        super();
        this.identificador = identificador;
        this.argumentos = argumentos;
        this.cuerpo = null;
        this.retorno = null;
        this.NroBloque = 0;
    }
    
    public NodoFuncion(NodoBase identificador, NodoBase cuerpo, NodoBase retorno){
        super();
        this.identificador = identificador;
        this.argumentos = null;
        this.cuerpo = cuerpo;
        this.retorno = retorno;
        this.NroBloque = 0;
    }
    
    public NodoFuncion(NodoBase identificador, NodoBase argumentos, NodoBase cuerpo, NodoBase retorno){
        super();
        this.identificador = identificador;
        this.argumentos = argumentos;
        this.cuerpo = cuerpo;
        this.retorno = retorno;
        this.NroBloque = 0;
    }

    public NodoFuncion(NodoBase identificador, NodoBase argumentos, NodoBase cuerpo, NodoBase retorno, int NroBloque) {
        super();
        this.identificador = identificador;
        this.argumentos = argumentos;
        this.cuerpo = cuerpo;
        this.retorno = retorno;
        this.NroBloque = NroBloque;
    }
    
    
    public NodoBase getIdentificador(){
        return identificador;
    }
    
    public NodoBase getArgumentos(){
        return argumentos;
    }
    
    public NodoBase getCuerpo(){
        return cuerpo;
    }
    
    public NodoBase getRetorno(){
        return retorno;
    }
    
    public int getNroBloque(){
        return NroBloque;
    }
    
    public void setIdentificador(NodoBase identificador){
        this.identificador = identificador;
    }
    
    public void setArgumentos(NodoBase argumentos){
        this.argumentos = argumentos;
    }
    
    public void setCuerpo(NodoBase cuerpo){
        this.cuerpo = cuerpo;
    }
    
    public void setRetorno(NodoBase retorno){
        this.retorno = retorno;
    }
}
