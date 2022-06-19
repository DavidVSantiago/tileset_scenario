package engine;

import java.awt.Dimension;

public class Recursos {
	private static Recursos singleton = null;
	// atributos
	public Dimension tamanhoTela;
	public Camera camera;
	public KeyState keyState;
	public boolean permiteMoverH,permiteMoverV;

	private Recursos() {
	}
	// Métodos --------------------------------------------
	public void initRecursos(int larguraTela,int alturaTela){
		tamanhoTela = new Dimension(larguraTela,alturaTela);
		camera = new Camera(0,0,larguraTela,alturaTela);
		keyState = new KeyState();
		permiteMoverH = true;
		permiteMoverV = true;
	}

	public void desabilitaMoverH(int tempo){
		this.permiteMoverH = false;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(tempo);
				} catch (InterruptedException e) {}
				permiteMoverH=true;
			}
		}).start();
	}
	// Métodos estáticos --------------------------------------------
	public static Recursos getInstance(){
		if(singleton==null)
			singleton = new Recursos();
		return singleton;
	}
}