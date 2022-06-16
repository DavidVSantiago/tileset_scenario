package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import scenario.Level;

public class Person{
    // atributos ---------------------------------------------------
    public BufferedImage img;
	public int largura, altura;
	public float posX, posY;
	public float velX, velY, velBase;
    public float limiteHorizontal,limiteVertical;

    // construtor --------------------------------------------------
    public Person(){
        largura=15;
		altura=25;
		posX = (Recursos.LARGURA_TELA/2)-(largura/2);
		posY = (Recursos.ALTURA_TELA/2)-(altura/2);
        velX = velY = 0;
        velBase = 2;
        limiteHorizontal = (Recursos.LARGURA_TELA/2.0f);
        limiteVertical = (Recursos.ALTURA_TELA/2.0f);
    }

    // Métodos gameloop --------------------------------------------
    public void update(){
        checarColisaoLevel();
    }
    public void render(Graphics g) {
        g.fillRect((int)posX,(int)posY,largura,altura);
    }

    // Métodos ----------------------------------------------------
    public void checarColisaoLevel(){
        // colisão com limites do Level
        if(posX<0) posX=0;
        if(posX+largura>Recursos.LARGURA_TELA) posX=Recursos.LARGURA_TELA-largura;
    }

    public float getCentroX(){
        return posX + (largura/2.0f);
    }
    public float getCentroY(){
        return posY + (altura/2.0f);
    }
}