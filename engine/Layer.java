package engine;

public abstract class Layer implements IGameloop{
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

    // métodos ----------------------------------------------------------
}