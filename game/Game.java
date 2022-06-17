package game;

import java.awt.Graphics;
import engine.GameLoop;
import engine.Level;
import engine.Person;

public class Game extends GameLoop{
	private Level level;
	
	public Game() {
		super(Recursos.keyState, Recursos.tamanhoTela, "Jogo Tiled");
		Recursos.carregaAssets();
		level = new Fase("/assets/cenario_01.tmj","/assets/",new Person());
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
}