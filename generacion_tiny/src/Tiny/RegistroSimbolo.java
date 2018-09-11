package Tiny;

public class RegistroSimbolo {
	private String identificador;
	private int DireccionMemoria;
	
	public RegistroSimbolo(String identificador, int direccionMemoria) {
		super();
		this.identificador = identificador;
		this.DireccionMemoria = direccionMemoria;
	}

	public String getIdentificador() {
		return this.identificador;
	}

	public int getDireccionMemoria() {
		return this.DireccionMemoria;
	}

	public void setDireccionMemoria(int direccionMemoria) {
		this.DireccionMemoria = direccionMemoria;
	}
}
