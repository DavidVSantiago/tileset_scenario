package engine;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Dimension;

public abstract class GameLoop extends JPanel implements IGameloop{
    // atributos -------------------------------------------------------
    private JFrame janela;
    protected Thread gameThread;
    protected EstadoJogo ESTADO;

    // construtor -------------------------------------------------------
    public GameLoop() {
        ESTADO = EstadoJogo.INICIANDO;
        janela = new JFrame(); // cria a janela
		janela.getContentPane().add(this); // adiciona a tela do jogo na janela
		janela.setResizable(false); // impede redimensionamento
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // modo de encerramento
		janela.setLocation(100, 100); // posi��o da janela na tela
		janela.setVisible(true); // torna a janela vis�vel
        
        setFocusable(true);
        setLayout(null);

        gameThread = new Thread(new Runnable() { // instancia da Thread + classe interna anônima
			@Override
			public void run() {
                ESTADO = EstadoJogo.EXECUTANDO;
				gameloop(); // inicia o gameloop
			}
		});
    }

    // métodos gameloop -------------------------------------------------
    public void gameloop() {
		while(true) { // repetição intermitente do gameloop
			handlerEvents();
			update();
            repaint();
			try {
				Thread.sleep(17);
			}catch (Exception e) {}
		}
	}

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.BLACK);
        if(ESTADO!=EstadoJogo.INICIANDO) render(g);
        Toolkit.getDefaultToolkit().sync(); // bug do linux	
    }
    // métodos ----------------------------------------------------------
    public void iniciarJogo(String nome){
        setPreferredSize(Recursos.getInstance().tamanhoTela);
        janela.setName(nome);
        janela.pack();
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        Recursos.getInstance().keyState.k_cima = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        Recursos.getInstance().keyState.k_baixo = false;
                        break;
                    case KeyEvent.VK_LEFT:
                        Recursos.getInstance().keyState.k_esquerda = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        Recursos.getInstance().keyState.k_direita = false;
                        break;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        Recursos.getInstance().keyState.k_cima = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        Recursos.getInstance().keyState.k_baixo = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        Recursos.getInstance().keyState.k_esquerda = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        Recursos.getInstance().keyState.k_direita = true;
                        break;
                }
            }
        });
        gameThread.start();
    }

}
