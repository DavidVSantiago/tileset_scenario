package engine;

import java.awt.Graphics;
import org.json.JSONArray;
import game.Recursos;

/** Representa o Layer de Tiles */
public class TileLayer extends Layer{
    // atributos -------------------------------------------------------
    public int qtdColunasLayer, qtdLinhasLayer; // dimensões do layer
    public Tileset tileset; // tileset do layer
    
    public float fatorParalaxeX,fatorParalaxeY;
    public int[] tileIDs; // ids de mapeamento do cenário

    // construtor
    public TileLayer(JSONArray data, int qtdLinhasLayer, int qtdColunasLayer,
                    float fatorParalaxeX,float fatorParalaxeY) {
        super();
        this.qtdColunasLayer = qtdColunasLayer;
        this.qtdLinhasLayer = qtdLinhasLayer;
        this.fatorParalaxeX = fatorParalaxeX;
        this.fatorParalaxeY = fatorParalaxeY;
        tileIDs = new int[data.length()];
        // converte par array unidimensional para bidimensional e preenche os dados de
        // ids do cenário
        for (int i = 0; i < data.length(); i++) {
            tileIDs[i] = data.getInt(i);
        }
    }

    @Override
    public void handlerEvents(){}

    @Override
    public void render(Graphics g) {
        for (int i = 0; i < tileIDs.length; i++) { // linhas
            int valor = tileIDs[i];
            if(valor==0)continue; // id 0 se refere a nenhum tile
            valor--;// = (valor - tileset.firstGridId); // correção de id
            // obtem o tile de origem do tileset
            Tile tile = tileset.getTile(valor);
            // prepara o tile de destino para ser desenhado
            int dx1 = (i%qtdColunasLayer) * tileset.larguraTile;
            int dy1 = (i/qtdColunasLayer) * tileset.alturaTile;
            int dx2 = dx1 + tileset.larguraTile;
            int dy2 = dy1 + tileset.alturaTile;
            // if(valor==0){
            //      System.out.println("dy2: "+(dy2+posY));
            //      System.out.println("Camera y: "+Recursos.camera.posY*fatorParalaxeY);
            // }
            // se o tile de destino estiver fora da camera, não o desenha
            if(Recursos.camera.tileForaDaCamera(dx1,dy1,dx2,dy2,fatorParalaxeX,fatorParalaxeY)) continue;
            // desenha cada tile em sua respectiva posição
            g.drawImage(tileset.img,(int)((dx1+posX)-Recursos.camera.posX*fatorParalaxeX), (int)((dy1+posY)-Recursos.camera.posY*fatorParalaxeY),
                                    (int)((dx2+posX)-Recursos.camera.posX*fatorParalaxeX), (int)((dy2+posY)-Recursos.camera.posY*fatorParalaxeY),
                                    (int)(tile.x1), tile.y1,
                                    (int)(tile.x2), tile.y2, null);
        }
    }

}
