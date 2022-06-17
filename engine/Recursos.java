package engine;

import java.awt.Dimension;

public class Recursos {
	private static Recursos singleton = null;
	// atributos
	public Dimension tamanhoTela;
	public Camera camera;
	public KeyState keyState;

	private Recursos() {
	}
	// Métodos --------------------------------------------
	public void initRecursos(int larguraTela,int alturaTela){
		tamanhoTela = new Dimension(larguraTela,alturaTela);
		camera = new Camera(0,0,larguraTela,alturaTela);
		keyState = new KeyState();
	}

	// Métodos estáticos --------------------------------------------
	public static Recursos getInstance(){
		if(singleton==null)
			singleton = new Recursos();
		return singleton;
	}
}