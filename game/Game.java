package game;

import java.awt.Graphics;
import engine.GameLoop;
import engine.Level;

public class Game extends GameLoop{
	private Level level;
	
	public Game() {
		super(Recursos.keyState, Recursos.tamanhoTela, "Jogo Tiled");
		Recursos.carregaAssets();
		level = new Fase("/assets/cenario_01.tmj","/assets/",new Personagem());
		iniciarJogo();
	}
	// GAMELOOP -------------------------------
	@Override
	public void handlerEvents() {
		level.handlerEvents();
	}
	@Override
	public void update() {
		level.update();
		testeColisoes();
	}
	@Override
	public void render(Graphics g) {
		level.render(g);
		
	}
	
	// OUTROS METODOS -------------------------
	public void testeColisoes() {
		
	}

	public static void main(String[] args) {
		new Game(); // dispara a aplica��o
	}
}