package engine;

import java.awt.Graphics;

public abstract class Layer {
    // atributos -------------------------------------------------------
    public float posX, posY; // posição de desenho do layer
    public float velX, velY;

    // construtor -------------------------------------------------------
    public Layer(){
        posX=posY=0;
        velX=velY=0;
    }

    // métodos gameloop -------------------------------------------------
    public void update() {
        posX+=velX;
        posY+=velY;
    }

    public abstract void render(Graphics g);

    // métodos ----------------------------------------------------------
}