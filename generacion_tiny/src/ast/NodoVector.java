package ast;

public class NodoVector extends NodoBase {
	private NodoBase identificador;
	private NodoBase expresion;
	
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
	
	
	
}
