package game;

import java.awt.Graphics;
import engine.GameLoop;
import engine.Level;
import engine.Person;
import engine.Recursos;

public class Game extends GameLoop{
	private Level level;
	private Person person;
	
	public Game() {
		Recursos.getInstance().initRecursos(426,240);
		person = new Personagem();
		level = new Fase("/assets/cenario_01.tmj","/assets/",person);	
		iniciarJogo("Jogo Tiled");
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