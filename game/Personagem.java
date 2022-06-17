package game;

import java.awt.Graphics;
import engine.Person;

public class Personagem extends Person{
    // atributos ---------------------------------------------------

    // construtor --------------------------------------------------
    public Personagem(){
        super(Recursos.tamanhoTela.width, Recursos.tamanhoTela.height);
        velBase = 5;
    }

    // Métodos gameloop --------------------------------------------

    @Override
    public void update(){
        checarColisaoLevel();
    }
    @Override
    public void render(Graphics g) {
        g.fillRect((int)posX,(int)posY,largura,altura);
    }
     // Métodos ----------------------------------------------------
     public void checarColisaoLevel(){
        // colisão com limites do Level
        if(posX<0) posX=0;
        if(posX+largura>Recursos.tamanhoTela.width) posX=Recursos.tamanhoTela.width-largura;
    }
}
