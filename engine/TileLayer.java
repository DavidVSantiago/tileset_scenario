package engine;

import java.awt.Graphics;
import org.json.JSONArray;

/** Representa o Layer de Tiles */
public class TileLayer extends Layer{
    // atributos -------------------------------------------------------
    public int qtdColunasLayer, qtdLinhasLayer; // dimensões do layer
    public Tileset tileset; // tileset do layer
    
    public float fatorParalaxeX,fatorParalaxeY;
    public int[] tileIDs; // ids de mapeamento do cenário
    public Tile[] tilesDestino; // retangulos de recorte dos tiles na tela

    // construtor
    public TileLayer(JSONArray data, int qtdLinhasLayer, int qtdColunasLayer,
                    float fatorParalaxeX,float fatorParalaxeY) {
        super();
        this.qtdColunasLayer = qtdColunasLayer;
        this.qtdLinhasLayer = qtdLinhasLayer;
        this.fatorParalaxeX = fatorParalaxeX;
        this.fatorParalaxeY = fatorParalaxeY;
        tileIDs = new int[data.length()];
        // cria a lista dos tiles de destino (os tiles desenhados na tela)
        for (int i = 0; i < data.length(); i++) {
            tileIDs[i] = data.getInt(i);
        }
    }

    @Override
    public void handlerEvents(){}

    @Override
    public void update() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void render(Graphics g) {
        // percorre e desenha todos os tiles de destino
        for (int i = 0; i < tilesDestino.length; i++) {
            // obtem o tile de destino
            Tile tileDestino = obterTileDestino(i);
            if(tileDestino.ID==0)continue; // id 0 se refere a nenhum tile
            // obtém a origem de cada tile de destino
            Tile tileOrigem = tileset.obterTileOrigem(tileDestino.ID-1); // correção de indice
            // se o tile de destino estiver fora da camera, não o desenha
            Camera camera = Recursos.getInstance().camera;
            if(camera.tileForaDaCamera(tileDestino,fatorParalaxeX,fatorParalaxeY)) continue;
            // desenha cada tile em sua respectiva posição
            g.drawImage(tileset.img,(int)((tileDestino.x1+posX)-camera.posX*fatorParalaxeX), (int)((tileDestino.y1+posY)-camera.posY*fatorParalaxeY),
                                    (int)((tileDestino.x2+posX)-camera.posX*fatorParalaxeX), (int)((tileDestino.y2+posY)-camera.posY*fatorParalaxeY),
                                    (int)(tileOrigem.x1), (int)tileOrigem.y1,
                                    (int)(tileOrigem.x2), (int)tileOrigem.y2, null);
        }
    }

    // Métodos ----------------------------------------------------
    public void normalizaIDs(){
        for (int i = 0; i < tileIDs.length; i++) {
            if(tileIDs[i]==0) continue;
                tileIDs[i] = (tileIDs[i]-tileset.firstGridId)+1;
        }
    }
    public void criaTilesDestino(){
        // cria a lista dos tiles de destino (os tiles desenhados na tela)
        tilesDestino = new Tile[tileIDs.length];
        for (int i = 0; i < tileIDs.length; i++) {
            int dx1 = (i%qtdColunasLayer) * tileset.larguraTile;
            int dy1 = (i/qtdColunasLayer) * tileset.alturaTile;
            int dx2 = dx1 + tileset.larguraTile;
            int dy2 = dy1 + tileset.alturaTile;
            tilesDestino[i] = new Tile(dx1, dy1, dx2, dy2, tileIDs[i]);
        }
    }
    public Tile obterTileDestino(int tileId){
        return tilesDestino[tileId];
    }
}