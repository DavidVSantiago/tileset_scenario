package engine;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Dimension;

public abstract class GameLoop extends JPanel implements IGameloop,KeyListener{
    // atributos -------------------------------------------------------
    private JFrame janela;
    protected Thread gameThread;
    protected EstadoJogo ESTADO;
    private long quadroAtual;
    private int interval = 17;

    // construtor -------------------------------------------------------
    public GameLoop(int larguraTela,int alturaTela) {
        Recursos.getInstance().initRecursos(larguraTela,alturaTela);
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
        quadroAtual = 0;
		while(true) { // repetição intermitente do gameloop
            quadroAtual++;
            Recursos.getInstance().quadroAtual++; // repassa a informação para a classe Recursos

            handlerEvents();
			update();
            repaint();

			try {
				Thread.sleep(interval);
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
        //setPreferredSize(Recursos.getInstance().tamanhoTela);
        setPreferredSize(new Dimension(Recursos.getInstance().tamanhoTela.width*2,Recursos.getInstance().tamanhoTela.height*2));
        janela.setName(nome);
        janela.pack();
        addKeyListener(this);
        gameThread.start();
    }

    // ************************************************************************
    @Override
    public void keyTyped(KeyEvent e) {}

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
            case KeyEvent.VK_A:
                Recursos.getInstance().keyState.k_atirando = false;
                break;
            case KeyEvent.VK_1:
                interval=17;
                break;
            case KeyEvent.VK_2:
                interval=100;
                break;
            case KeyEvent.VK_3:
                interval=250;
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
            case KeyEvent.VK_A:
                Recursos.getInstance().keyState.k_atirando = true;
                break;
        }
    }
}
