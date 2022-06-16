package scenario;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;
import org.json.JSONArray;
import org.json.JSONObject;
import game.Recursos;
import game.Person;

/** Tem o objetivo de atualizar e renderizar os Layers */
public class Level {
    // atributos
    public TileLayer[] listaTileLayers; // lista com todos os layers do cenário
    public int qtdColunasLevel, qtdLinhasLevel; // largura e altura de todo o cenario (em tiles)
    public int larguraLevel,alturaLevel;
    public Person person; 

    // construtor
    public Level(String arquivoLevel) {
        // carrega o arquivo json do cenario
        JSONObject fullJson = Recursos.carregarJson(arquivoLevel);
        qtdColunasLevel = fullJson.getInt("width");
        qtdLinhasLevel = fullJson.getInt("height");
        larguraLevel= qtdColunasLevel*fullJson.getInt("tilewidth");
        alturaLevel = qtdLinhasLevel*fullJson.getInt("tileheight");
        // faz o parser dos layers do cenário
        parseTileLayer(fullJson.getJSONArray("layers"));
        // faz o parser dos tilesets do cenário
        parseTilesets(fullJson.getJSONArray("tilesets"));

        // inicializa a camera
        Recursos.initCamera(this,0, (alturaLevel*qtdLinhasLevel),
                            Recursos.LARGURA_TELA, Recursos.ALTURA_TELA);

        // inicializa os npcs
        person = new Person();
    }

    // métodos gameloop ***********************************************
    public void handlerEvents() {
        /** Modificar a velocidade do person de acordo com a movimentação dos direcionais */
        person.velX = 0;
        person.velY = 0;
        if (Recursos.keyState.k_direita) {
            person.velX = person.velBase;
            if (Recursos.keyState.k_cima) {
                person.velY = -person.velBase;
            } else if (Recursos.keyState.k_baixo) {
                person.velY = person.velBase;
            }
        } else if (Recursos.keyState.k_esquerda) {
            person.velX = -person.velBase;
            if (Recursos.keyState.k_cima) {
                person.velY = -person.velBase;
            } else if (Recursos.keyState.k_baixo) {
                person.velY = person.velBase;
            }
        } else if (Recursos.keyState.k_cima) {
            person.velY = -person.velBase;
        } else if (Recursos.keyState.k_baixo) {
            person.velY = person.velBase;
        }
    }

    public void update() {
        
        // atualiza a camera com base na velocidade do personagem ******************************
        if(Recursos.camera.cameraEsquerdaLevel()){ // camera na esquerda do level
            // movimentação horizontal ---------------------------------
            if(person.getCentroX()+person.velX<=person.limiteHorizontal){ // personagem dentro da primeira metade da tela
                person.posX+=person.velX;
            }else{
                Recursos.camera.posX+=person.velX;
            }
            // movimentação vertical -----------------------------------
            if(Recursos.camera.cameraInferiorLevel()){ // camera na parte inferior esquerda do level
                if(person.getCentroY()+person.velY>=person.limiteVertical){ // personagem na parte infeior da tela
                    person.posY+=person.velY;
                }else{
                    Recursos.camera.posY+=person.velY;
                }
            }else if(Recursos.camera.cameraSuperiorLevel()){ // camera na parte superior esquerda do level
                if(person.getCentroY()+person.velY<=person.limiteVertical){ // personagem na parte superior da tela
                    person.posY+=person.velY;
                }else{
                    Recursos.camera.posY+=person.velY;
                }
            }else{ // camera apenas na parte esquerda da tela 
                Recursos.camera.posY+=person.velY;
            }
        }else  if(Recursos.camera.cameraDireitaLevel()){ // camera na direita do level
            // movimentação horizontal ---------------------------------
            if(person.getCentroX()+person.velX>=person.limiteHorizontal){ // personagem dentro da segunda metade da tela
                person.posX+=person.velX;
            }else{
                Recursos.camera.posX+=person.velX;
            }
            // movimentação vertical -----------------------------------
            if(Recursos.camera.cameraInferiorLevel()){ // camera na parte inferior direita do level
                if(person.getCentroY()+person.velY>=person.limiteVertical){ // personagem na parte infeior da tela
                    person.posY+=person.velY;
                }else{
                    Recursos.camera.posY+=person.velY;
                }
            }else if(Recursos.camera.cameraSuperiorLevel()){ // camera na parte superior direita do level
                if(person.getCentroY()+person.velY<=person.limiteVertical){ // personagem na parte superior da tela
                    person.posY+=person.velY;
                }else{
                    Recursos.camera.posY+=person.velY;
                }
            }else{ // camera apenas na parte direita da tela 
                Recursos.camera.posY+=person.velY;
            }
        }else{ // camera no centro horizontal
            Recursos.camera.posX+=person.velX;
            // movimentação vertical -----------------------------------
            if(Recursos.camera.cameraInferiorLevel()){ // camera na parte inferior do level
                if(person.getCentroY()+person.velY>=person.limiteVertical){ // personagem na parte infeior da tela
                    person.posY+=person.velY;
                }else{
                    Recursos.camera.posY+=person.velY;
                }
            }else if(Recursos.camera.cameraSuperiorLevel()){ // camera na parte superior do level
                if(person.getCentroY()+person.velY<=person.limiteVertical){ // personagem na parte superior da tela
                    person.posY+=person.velY;
                }else{
                    Recursos.camera.posY+=person.velY;
                }
            }else{ // camera livre no meio da tela
                Recursos.camera.posY+=person.velY;
            }
        }
        Recursos.camera.checarColisao();
            
        // atualiza todos os layers do level
        for (int i = 0; i < listaTileLayers.length; i++) {
            listaTileLayers[i].update();
        }

        person.update();
    }

    public void render(Graphics g) {
        // renderiza os npcs
        g.setColor(Color.RED);

        // renderiza todos os layers
        for (int i = 0; i < listaTileLayers.length; i++) {
            listaTileLayers[i].render(g);
        }

        // renderiza o personagem
        person.render(g);
    }

    // métodos *********************************************************
    private void parseTileLayer(JSONArray jsonLayers) {
        listaTileLayers = new TileLayer[jsonLayers.length()];
        for (int i = 0; i < jsonLayers.length(); i++) {
            JSONObject tileLayer = jsonLayers.getJSONObject(i);
            JSONArray data = tileLayer.getJSONArray("data");
            int qtdLinhasLayer = tileLayer.getInt("height");
            int qtdColunasLayer = tileLayer.getInt("width");
            listaTileLayers[i] = new TileLayer(data, qtdLinhasLayer, qtdColunasLayer);
        }
    }

    private void parseTilesets(JSONArray tilesets) {
        for (int i = 0; i < tilesets.length(); i++) {
            // carrega cada um dos tilesets usados no cenário
            String source = tilesets.getJSONObject(i).getString("source");
            JSONObject jsonTileset = Recursos.carregarJson("/assets/" + source);
            // captura os dados de cada tileset
            BufferedImage img = Recursos.carregarImagem("/assets/" + jsonTileset.getString("image"));
            int larguraTile = jsonTileset.getInt("tilewidth");
            int alturaTile = jsonTileset.getInt("tileheight");
            int espacoTiles = jsonTileset.getInt("spacing");
            int margemTiles = jsonTileset.getInt("margin");
            int larguraTileset = jsonTileset.getInt("imagewidth");
            int alturaTileset = jsonTileset.getInt("imageheight");
            int qtdTiles = jsonTileset.getInt("tilecount");
            int qtdColunasTileset = jsonTileset.getInt("columns");
            // cria um novo tileset com os dados capturados do arquivo
            Tileset tileset = new Tileset(img, larguraTile, alturaTile, espacoTiles, margemTiles, larguraTileset,
                    alturaTileset, qtdTiles, qtdColunasTileset);
            // atribui on tileset ao seu respectivo tilelayer
            listaTileLayers[i].setTileset(tileset);
        }
    }

    // *************************************************************************
    // *************************************************************************

    /** Representa o Layer de Tiles */
    public class TileLayer {
        // atributos
        public int qtdColunasLayer, qtdLinhasLayer; // dimensões do layer
        public Tileset tileset; // tileset do layer
        public float posX, posY; // posição de desenho do layer
        public int[] tileIDs; // ids de mapeamento do cenário

        // construtor
        public TileLayer(JSONArray data, int qtdLinhasLayer, int qtdColunasLayer) {
            posX=0;
            posY=0;
            this.qtdColunasLayer = qtdColunasLayer;
            this.qtdLinhasLayer = qtdLinhasLayer;
            tileIDs = new int[data.length()];
            // converte par array unidimensional para bidimensional e preenche os dados de
            // ids do cenário
            for (int i = 0; i < data.length(); i++) {
                tileIDs[i] = data.getInt(i);
            }
        }

        public void setTileset(Tileset tileset) {
            this.tileset = tileset;
        }

        // métodos
        public void update() {

        }

        public void render(Graphics g) {
            for (int i = 0; i < tileIDs.length; i++) { // linhas
                int valor = tileIDs[i];
                if(valor==0)continue; // id 0 se refere a nenhum tile
                valor--; // correção de id
                // obtem o tile de origem do tileset
                Tile tile = tileset.getTile(valor);
                // prepara o tile de destino para ser desenhado
                int dx1 = (i%qtdColunasLayer) * tileset.larguraTile;
                int dy1 = (i/qtdColunasLayer) * tileset.alturaTile;
                int dx2 = dx1 + tileset.larguraTile;
                int dy2 = dy1 + tileset.alturaTile;
                // se o tile de destino estiver fora da camera, não o desenha
                if(Recursos.camera.tileForaDaCamera(dx1,dy1,dx2,dy2)) continue;
                // desenha cada tile em sua respectiva posição
                g.drawImage(tileset.img, (int)((dx1+posX)-Recursos.camera.posX), (int)((dy1+posY)-Recursos.camera.posY),
                                         (int)((dx2+posX)-Recursos.camera.posX), (int)((dy2+posY)-Recursos.camera.posY),
                        tile.x1, tile.y1, tile.x2, tile.y2,
                        null);
            }
        }

    }

    // *************************************************************************
    /** Representa o TileSet */
    public class Tileset {
        // atributos
        public BufferedImage img; // imagem qo tileset
        int larguraTile, alturaTile; // dimensões de cada tile do tileset (em pixel)
        int espacoTiles, margemTiles;
        int larguraTileset, alturaTileset; // dimensões da imagem do tileset (em pixel)
        int qtdTiles; // quantidade total de tiles no tileset
        int qtdColunasTileset; // quantidade de colunas do tileset
        public Tile[] tiles; // retangulos de recorte do tileset

        // construtor
        public Tileset(BufferedImage img, int larguraTile, int alturaTile,
                int espacoTiles, int margemTiles, int larguraTileset, int alturaTileset,
                int qtdTiles, int qtdColunasTileset) {
            this.img = img;
            this.larguraTile = larguraTile;
            this.alturaTile = alturaTile;
            this.espacoTiles = espacoTiles;
            this.margemTiles = margemTiles;
            this.larguraTileset = larguraTileset;
            this.alturaTileset = alturaTileset;
            this.qtdTiles = qtdTiles;
            this.qtdColunasTileset = qtdColunasTileset;
            // inicializa todos os tiles de recorte do tileset
            tiles = new Tile[qtdTiles];
            for (int i = 0; i < qtdTiles; i++) {
                int x1 = (i % qtdColunasTileset) * larguraTile;
                int y1 = (i / qtdColunasTileset) * alturaTile;
                int x2 = x1 + larguraTile;
                int y2 = y1 + alturaTile;
                tiles[i] = new Tile(x1, y1, x2, y2);
                // System.out.println("x1:"+x1);
            }
        }

        public Tile getTile(int tileId) {
            return tiles[tileId];
        }

    }

    // *************************************************************************
    /** Representa um tile */
    public class Tile {
        // atributos
        public int x1, y1;
        public int x2, y2;

        // construtor
        public Tile(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }
}