package ast;

public class NodoListaArgs extends NodoBase {
	private NodoBase variable;

	public NodoListaArgs(NodoBase variable){
		super();
		this.variable = variable;
	}

	public NodoListaArgs() {
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
