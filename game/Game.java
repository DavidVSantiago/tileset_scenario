package game;

import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import scenario.Level;

public class Game extends JPanel{
	private Level level;
	
	
	public Game() {
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				
			}			
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP: Recursos.keyState.k_cima=false; break;
				case KeyEvent.VK_DOWN: Recursos.keyState.k_baixo=false; break;
				case KeyEvent.VK_LEFT: Recursos.keyState.k_esquerda=false; break;
				case KeyEvent.VK_RIGHT: Recursos.keyState.k_direita=false; break;
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP: Recursos.keyState.k_cima=true; break;
				case KeyEvent.VK_DOWN: Recursos.keyState.k_baixo=true; break;
				case KeyEvent.VK_LEFT: Recursos.keyState.k_esquerda=true; break;
				case KeyEvent.VK_RIGHT: Recursos.keyState.k_direita=true; break;
				}
			}
		});
		setFocusable(true);
		setLayout(null);
		
		level = new Level("/assets/cenario_01.tmj");
		new Thread(new Runnable() { // instancia da Thread + classe interna anônima
			@Override
			public void run() {
				gameloop(); // inicia o gameloop
			}
		}).start(); // dispara a Thread
	}
	// GAMELOOP -------------------------------
	public void gameloop() {
		while(true) { // repetição intermitente do gameloop
			handlerEvents();
			update();
			render();
			try {
				Thread.sleep(17);
			}catch (Exception e) {}
		}
	}
	public void handlerEvents() {
		level.handlerEvents();
	}
	public void update() {
		level.update();
		testeColisoes();
	}
	public void render() {
		repaint();
	}
	
	// OUTROS METODOS -------------------------
	public void testeColisoes() {
		
	}
		
	// METODO SOBRESCRITO ---------------------
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.LIGHT_GRAY);
		level.render(g);
		//g.drawImage(inimigo.img, inimigo.posX, inimigo.posY, null);
		Toolkit.getDefaultToolkit().sync(); // bug do linux	
	}
}