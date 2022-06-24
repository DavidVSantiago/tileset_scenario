package engine;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Recursos {
	private static Recursos singleton = null;
	// atributos
	public Dimension tamanhoTela;
	public Camera camera;
	public KeyState keyState;
	public boolean permiteMoverH,permiteMoverV;
	long quadroAtual,contadorQuadros;

	private Recursos() {
	}
	// Métodos --------------------------------------------
	public void initRecursos(int larguraTela,int alturaTela){
		tamanhoTela = new Dimension(larguraTela,alturaTela);
		camera = new Camera(0,0,larguraTela,alturaTela);
		keyState = new KeyState();
		permiteMoverH = true;
		permiteMoverV = true;
		contadorQuadros = 0;
	}

	// Métodos estáticos --------------------------------------------
	public static Recursos getInstance(){
		if(singleton==null)
			singleton = new Recursos();
		return singleton;
	}

	public static BufferedImage carregarImagem(String file){
		BufferedImage img = null;
		try {
			img = ImageIO.read(Level.class.getResource(file));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}

    public static JSONObject carregarJson(String arquivo) throws NullPointerException{
		InputStream inputStream = Level.class.getResourceAsStream(arquivo);
		if (inputStream ==null)
			throw new NullPointerException("Arquivo "+ arquivo +" não existe!");
		return new JSONObject(new JSONTokener(inputStream));
	}
}