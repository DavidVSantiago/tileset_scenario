package game;

import engine.Level;
import engine.Person;
import java.awt.Graphics;

public class Fase extends Level{
    // atributos -------------------------------------------------------

    // construtor -------------------------------------------------------
    public Fase(String mapaFase,String imagemDir,Person person){
        super(mapaFase,imagemDir,person);
    }

    // métodos gameloop indiretos ---------------------------------------
    @Override
    public void handlerEventsLevel() {
      
    }
    @Override
    public void updateLevel() {
        
    }
    @Override
    public void renderLevel(Graphics g) {
        
    }

    // métodos ----------------------------------------------------------
    @Override
    public void atribuiTilesetsLayers() {
        // atribui os tilesets aos seus respectivos layers
        listaTileLayers[0].tileset = listaTilesets[0]; // Layer01-sky
        listaTileLayers[1].tileset = listaTilesets[1]; // Layer02-sky
        listaTileLayers[2].tileset = listaTilesets[2]; // Layer03-back
        listaTileLayers[3].tileset = listaTilesets[2]; // Layer04-front
        listaTileLayers[4].tileset = listaTilesets[2]; // Layer05-collision
    }
}
