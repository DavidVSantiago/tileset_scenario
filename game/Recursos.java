package game;

import engine.Camera;
import engine.KeyState;
import java.awt.Dimension;

public class Recursos {	
	// atributos
	public static final Dimension tamanhoTela = new Dimension(426,240);
	public static Camera camera = new Camera(0,0,tamanhoTela.width,tamanhoTela.height);
	public static KeyState keyState = new KeyState();

	private Recursos() {}
	// Métodos --------------------------------------------
	public static void carregaAssets(){

	}
}
