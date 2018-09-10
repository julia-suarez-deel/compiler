package ast;

public class NodoLeer extends NodoBase {
	private NodoBase identificador;

	public NodoLeer(String identificador) {
		super();
		this.identificador = new NodoIdentificador(identificador);
	}

	public NodoLeer(NodoBase identificador){
		super();
		this.identificador = identificador;
	}

	public NodoLeer() {
		super();
		identificador=null;
	}

	public NodoBase getIdentificador() {
		return identificador;
	}

	public void setExpresion(NodoBase identificador) {
		this.identificador = identificador;
	}

}
