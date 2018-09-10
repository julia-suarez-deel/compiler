package ast;

public class NodoLeer extends NodoBase {
	private NodoBase identificador;

	public NodoLeer(String identificador) {
		super();
		this.identificador = new NodoIdentificador(identificador);
	}

	public NodoLeer() {
		super();
		identificador=null;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setExpresion(NodoBase identificador) {
		this.identificador = identificador;
	}

}
