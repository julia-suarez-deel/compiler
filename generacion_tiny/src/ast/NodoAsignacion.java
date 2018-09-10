package ast;

public class NodoAsignacion extends NodoBase {
	private NodoBase identificador;
	private NodoBase expresion;
	
	public NodoAsignacion(String identificador) {
		super();
		this.identificador = new NodoIdentificador(identificador);
		this.expresion = null;
	}
	
	public NodoAsignacion(String identificador, NodoBase expresion) {
		super();
		this.identificador = new NodoIdentificador(identificador);
		this.expresion = expresion;
	}

	public NodoAsignacion(NodoBase nodoVector,NodoBase expresion){
		super();
		this.identificador = nodoVector;
		this.expresion = expresion;
	}

	public NodoBase getIdentificador() {
		return this.identificador;
	}

	public void setIdentificador(NodoBase identificador) {
		this.identificador = identificador;
	}

	public NodoBase getExpresion() {
		return expresion;
	}

	public void setExpresion(NodoBase expresion) {
		this.expresion = expresion;
	}
	
	
	
}
