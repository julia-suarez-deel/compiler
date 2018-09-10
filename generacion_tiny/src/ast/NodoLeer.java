package ast;

public class NodoLeer extends NodoBase {
	private NodoBase variable;

	public NodoLeer(NodoBase variable){
		super();
		this.variable = variable;
	}

	public NodoLeer() {
		super();
		variable=null;
	}

	public NodoBase getVariable() {
		return variable;
	}

	public void setVariable(NodoBase variable) {
		this.variable = variable;
	}

}
