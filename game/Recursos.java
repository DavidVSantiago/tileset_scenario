package game;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.json.JSONObject;
import org.json.JSONTokener;
import scenario.Level;

public class Recursos {	
	// atributos
	public static final int LARGURA_TELA = 426;
	public static final int ALTURA_TELA = 240;
	public static Camera camera = new Camera(); 
	public static KeyState keyState = new KeyState();

	private Recursos() {}
	// Métodos --------------------------------------------
	public static JSONObject carregarJson(String arquivo) throws NullPointerException{
		InputStream inputStream = Recursos.class.getResourceAsStream(arquivo);
		if (inputStream ==null)
			throw new NullPointerException("Arquivo "+ arquivo +" não existe!");
		return new JSONObject(new JSONTokener(inputStream));
	}

	public static BufferedImage carregarImagem(String file){
		BufferedImage img = null;
		try {
			img = ImageIO.read(Recursos.class.getResource(file));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}

	public static void initCamera(Level levelAtual, float posX,float posY,int largura,int altura){
		camera.posX = posX;
		camera.posY = posY;
		camera.largura = largura;
		camera.altura = altura;
		camera.velBase=2;
		camera.velX=0;
		camera.velY=0;
		camera.levelAtual = levelAtual;
	}
	
	// Inner Classes ****************************************
	public static class KeyState{
		// atributos
		public boolean k_cima = false;
		public boolean k_baixo = false;
		public boolean k_direita = false;
		public boolean k_esquerda = false;
	}
	// ******************************************************
	public static class Camera {
		// atributos
		public float posX,posY;
		public float velX,velY,velBase;
		public int largura, altura;
		public Level levelAtual;
	
		// construtor
		private Camera(){}
	
		// metodos gameloop ------------------------
		public void handlerEvents() {
			
		}
		// metodos --------------------------------

		public void checarColisao(){
			// colisão com limites do Level
			if(posX<0) posX=0;
			if(posY<0) posY=0;
			if(posX+largura>levelAtual.larguraLevel) posX=levelAtual.larguraLevel-largura;
			if(posY+altura>levelAtual.alturaLevel) posY=levelAtual.alturaLevel-altura;
		}

		public boolean tileForaDaCamera(int dx1,int dy1,int dx2,int dy2){
			if(dx1>posX+largura ||
			   dx2<posX ||
			   dy1>posY+altura ||
			   dy2<posY){
					return true;
			}
			return false;
		}

		public boolean cameraEsquerdaLevel(){
			if(posX<=0) return true;
			return false;
		}
		public boolean cameraDireitaLevel(){
			if(posX+largura>=levelAtual.larguraLevel) return true;
			return false;
		}
		public boolean cameraSuperiorLevel(){
			if(posY<=0) return true;
			return false;
		}
		public boolean cameraInferiorLevel(){
			if(posY+altura>=levelAtual.alturaLevel) return true;
			return false;
		}
	}
}
