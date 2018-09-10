package ast;

public class NodoAsignacion extends NodoBase {
	private NodoBase variable;
	private NodoBase expresion;
	
	public NodoAsignacion(String variable) {
		super();
		this.variable = new NodoIdentificador(variable);
		this.expresion = null;
	}

	public NodoAsignacion(NodoBase variable,NodoBase expresion){
		super();
		this.variable = variable;
		this.expresion = expresion;
	}

	public NodoBase getVariable() {
		return this.variable;
	}

	public void setvariable(NodoBase variable) {
		this.variable = variable;
	}

	public NodoBase getExpresion() {
		return expresion;
	}

	public void setExpresion(NodoBase expresion) {
		this.expresion = expresion;
	}
	
	
	
}
