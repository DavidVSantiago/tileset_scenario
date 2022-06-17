package game;

import java.awt.Color;
import java.awt.Graphics;

import engine.EstadoPerson;
import engine.Person;

public class Personagem extends Person{
    // atributos ---------------------------------------------------

    // construtor --------------------------------------------------
    public Personagem(){
        velBase = 2;
    }

    // Métodos gameloop --------------------------------------------

    @Override
    public void render(Graphics g) {
        g.fillRect((int)posX,(int)posY,largura,altura);
        g.setColor(Color.GREEN);
        g.drawRect(caixaColisao.x1,caixaColisao.y1,caixaColisao.x2-caixaColisao.x1,caixaColisao.y2-caixaColisao.y1);
    }
     // Métodos ----------------------------------------------------
     
}
