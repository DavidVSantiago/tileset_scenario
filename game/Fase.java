package game;

import engine.Level;
import engine.Person;

public class Fase extends Level{
    // atributos -------------------------------------------------------

    // construtor -------------------------------------------------------
    public Fase(String mapaFase,String imagemDir,Person person){
        super(mapaFase,imagemDir,Recursos.camera,Recursos.keyState,person);
    }

    // métodos gameloop -------------------------------------------------


    // métodos ----------------------------------------------------------
    @Override
    public void atribuiTilesetsLayers() {
        // atribui os tilesets aos seus respectivos layers
        listaTileLayers[0].tileset = listaTilesets[0]; // Layer01-sky
        listaTileLayers[1].tileset = listaTilesets[1]; // Layer02-sky
        listaTileLayers[2].tileset = listaTilesets[2]; // Layer03-back
        listaTileLayers[3].tileset = listaTilesets[2]; // Layer04-front
    }
}
