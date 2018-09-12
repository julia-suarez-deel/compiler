package ast;

public class NodoArgumento extends NodoBase {
	private NodoIdentificador identificador;

	public NodoArgumento(NodoIdentificador identificador){
		super();
		this.identificador = identificador;
	}

	public NodoArgumento() {
		super();
		identificador=null;
	}

	public NodoIdentificador getIdentificador() {
		return identificador;
	}

	public void setIdentificador(NodoIdentificador identificador) {
		this.identificador = identificador;
	}

}
