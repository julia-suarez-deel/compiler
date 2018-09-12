package ast;

public class NodoVector extends NodoBase {
	private NodoBase identificador;
	private NodoBase expresion;
	private boolean declaracion;

	public NodoVector(String identificador) {
		super();
		this.identificador = new NodoIdentificador(identificador);
		this.expresion = null;
	}
	
	public NodoVector(String identificador, NodoBase expresion) {
		super();
		this.identificador = new NodoIdentificador(identificador);
		this.expresion = expresion;
	}

	public NodoVector(NodoBase identificador, NodoBase expresion) {
		super();
		this.identificador = identificador;
		this.expresion = expresion;
	}

	public NodoBase getIdentificador() {
		return identificador;
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

	public boolean isDeclaracion() {
		return declaracion;
	}

	public void setDeclaracion(boolean declaracion) {
		this.declaracion = declaracion;
	}
}
