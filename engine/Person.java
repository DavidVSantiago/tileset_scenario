package engine;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class Person{
    // atributos ---------------------------------------------------
    public BufferedImage img;
	public int largura, altura;
	public float posX, posY;
	public float velX, velY, velBase;
    public float limiteHorizontal,limiteVertical;

    // construtor --------------------------------------------------
    public Person(int telaLargura, int telaAltura){
        largura=32; // com base na imagem
		altura=32; // // com base na imagem
        posX=(telaLargura/2.0f)-(largura/2);
        posY=(telaAltura/2.0f)-(altura/2);
        velBase = 1;
        limiteHorizontal = (telaLargura/2.0f);
        limiteVertical = (telaAltura/2.0f);
    }

    // Métodos gameloop --------------------------------------------
    public abstract void update();
    public abstract void render(Graphics g);

    // Métodos --------------------------------------------
    public float getCentroX(){
        return posX + (largura/2.0f);
    }
    public float getCentroY(){
        return posY + (altura/2.0f);
    }
}