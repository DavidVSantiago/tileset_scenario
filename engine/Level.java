package engine;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/** Tem o objetivo de atualizar e renderizar os Layers */
public abstract class Level implements IGameloop{
    // atributos
    public String imagensDir;
    public TileLayer[] listaTileLayers; // lista com todos os layers do cenário
    public Tileset[] listaTilesets; // lista com todos os layers do cenário
    public int qtdColunasLevel, qtdLinhasLevel; // largura e altura de todo o cenario (em tiles)
    public int larguraLevel,alturaLevel;
    public Person person;

    // construtor
    public Level(String arquivoLevel,String imagensDir,Person person) {
        this.imagensDir = imagensDir;
        this.person = person;
        person.ESTADO=EstadoPerson.PULANDO;
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
        // atribui os tilesets aos seus respectivos layers
        atribuiTilesetsLayers();

        // normaliza os tileIDs e cria os tiles de destino (os desenhados na tela)
        for(int i=0;i<listaTileLayers.length;i++){
            listaTileLayers[i].normalizaIDs();
            listaTileLayers[i].criaTilesDestino();
        }

        Camera camera = Recursos.getInstance().camera;
        camera.posY = alturaLevel-camera.altura; // camera, por padrão, começa no canto inferior esquerdo do cenário       
        camera.levelAtual = this; // obrigatório, para a camera poder calcular o seu deslocamento dentro do mapa
    }

    /** Implemente este método e especifique quais os tilesets associados a quais layers do level */
    public abstract void atribuiTilesetsLayers();

    // métodos gameloop ***********************************************
    @Override
    public final void handlerEvents() {
        person.handlerEvents();
    }

    @Override
    public final void update() {
        person.update();
        colisaoPersonLevel(); // Testa a colisão do personagem com os tiles do cenário
        //System.out.println(person.ESTADO);
    }

    @Override
    public final void render(Graphics g) {
        // renderiza os npcs
        g.setColor(Color.RED);
        listaTileLayers[0].render(g); // renderiza Layer01-sky
        listaTileLayers[1].render(g); // renderiza Layer02-sky
        listaTileLayers[2].render(g); // renderiza Layer03-back
        person.render(g); // renderiza o personagem
        listaTileLayers[3].render(g); // renderiza Layer04-front
        listaTileLayers[4].render(g); // renderiza Layer05-colliders
    }


    // métodos *********************************************************
    private void parseTileLayer(JSONArray jsonLayers) {
        listaTileLayers = new TileLayer[jsonLayers.length()];
        for (int i = 0; i < jsonLayers.length(); i++) {
            JSONObject tileLayer = jsonLayers.getJSONObject(i);
            JSONArray data = tileLayer.getJSONArray("data");
            int qtdLinhasLayer = tileLayer.getInt("height");
            int qtdColunasLayer = tileLayer.getInt("width");
            float fatorParalaxeX = (tileLayer.has("parallaxx"))?tileLayer.getFloat("parallaxx"):1.0f;
            float fatorParalaxeY = (tileLayer.has("parallaxy"))?tileLayer.getFloat("parallaxy"):1.0f;
            listaTileLayers[i] = new TileLayer(data, qtdLinhasLayer, qtdColunasLayer,fatorParalaxeX,fatorParalaxeY);
        }
    }

    private void parseTilesets(JSONArray jsonTilesets) {
        listaTilesets = new Tileset[jsonTilesets.length()];
        for (int i = 0; i < jsonTilesets.length(); i++) {
            JSONObject jsonTileset = jsonTilesets.getJSONObject(i);
            int firstGridId = jsonTileset.getInt("firstgid");
            // captura os dados de cada tileset
            BufferedImage img = Recursos.carregarImagem(imagensDir + jsonTileset.getString("image"));
            int larguraTile = jsonTileset.getInt("tilewidth");
            int alturaTile = jsonTileset.getInt("tileheight");
            int espacoTiles = jsonTileset.getInt("spacing");
            int margemTiles = jsonTileset.getInt("margin");
            int larguraTileset = jsonTileset.getInt("imagewidth");
            int alturaTileset = jsonTileset.getInt("imageheight");
            int qtdTiles = jsonTileset.getInt("tilecount");
            int qtdColunasTileset = jsonTileset.getInt("columns");
            // cria um novo tileset com os dados capturados do arquivo
            Tileset tileset = new Tileset(img, firstGridId, larguraTile, alturaTile, espacoTiles, margemTiles, larguraTileset,
                    alturaTileset, qtdTiles, qtdColunasTileset);
            listaTilesets[i] = tileset;
        }
    }
    
    // métodos de colisão *******************************************

    /** Verifica se o tile está fora da área de cobertura do personagem */
    public boolean tileForaAreaColisao(Tile tile, Camera camera){
        if(tile.x1-camera.posX>person.caixaMove.x2 ||
            tile.x2-camera.posX<person.caixaMove.x1 ||  
            tile.y1-camera.posY>person.caixaMove.y2 ||
            tile.y2-camera.posY<person.caixaMove.y1){
                return true;
        }
        return false;
    }

    /** Testa a colisão do personagem com os tiles do cenário */
    public void colisaoPersonLevel(){
        TileLayer layer05 = listaTileLayers[4]; // Layer05-collision (colidível)
        Tile[] tilesDestino = layer05.tilesDestino; // obtem a lista de tiles de destino do layer05
        for(int i=0; i<tilesDestino.length;i++){ // percorre todos os tiles de destino do layer05
            Tile tileDestino = tilesDestino[i];
            if(tileDestino.ID==0)continue; // id 0 se refere a nenhum tile
            Camera camera = Recursos.getInstance().camera;
            // se o tile de destino estiver fora da camera, não testa colisão
            if(tileForaAreaColisao(tileDestino,camera)) continue;
            //if(camera.tileForaDaCamera(tileDestino,layer05.fatorParalaxeX,layer05.fatorParalaxeY)) continue;
            // se não for um tile de colisão, também ignora o teste
            if(tileDestino.ID-1>7)continue;
            // testa a colisão do personagem com cada tile dentro da camera
            if(tileDestino.ID-1==0) // se o person colide de cima para baixo
                checaColisaoYellow(tileDestino, camera);
            else if(tileDestino.ID-1==1) // se o person colide de cima para baixo
                checaColisaoOrange(tileDestino,camera);
            else if(tileDestino.ID-1==2) // se o person colide de cima para baixo
                checaColisaoRed(tileDestino,camera);
            else if(tileDestino.ID-1==3)
                checaColisaoPink(tileDestino,camera); // se o person colide na quina superior direita
            else if(tileDestino.ID-1==4)
                checaColisaoPurple(tileDestino,camera); // se o person colide da direita -> esquerda
        }
    }

    public void checaColisaoRed(Tile tile, Camera camera){
        float tileX1 = tile.x1-camera.posX;
        float tileX2 = tile.x2-camera.posX;
        float tileY1 = tile.y1-camera.posY;
        float tileY2 = tile.y2-camera.posY;
        float pX1 = person.caixaColisao.x1;
        float pY1 = person.caixaColisao.y1;
        float pX2 = person.caixaColisao.x2;
        float pY2 = person.caixaColisao.y2;
        float cVelX = camera.velX;
        float cVelY = camera.velY; 
        // se o personagem está horizontalmente fora do tile
        if(pX2<tileX1 || pX1>tileX2) return;
        // se o personagem está vericalmente abaixo do tile
        if(pY2>tileY2) return;
        // verifica se colide de cima para baixo
        if(pY2>tileY1 && pY2-cVelY < pY2){
            // recoloca o personagem acima do tile
            float altura = person.caixaColisao.y2-person.caixaColisao.y1;
            person.caixaColisao.y2=tileY1;
            person.caixaColisao.y1=person.caixaColisao.y2-altura;
            person.atualizaCaixaColisao();
            KeyState keyState = Recursos.getInstance().keyState;
            if(keyState.k_esquerda||keyState.k_direita)
                person.entraEstadoCORRENDO();
            else
                person.entraEstadoPARADO();
        }
    }
    public void checaColisaoPink(Tile tile, Camera camera){
        float tileX1 = tile.x1-camera.posX;
        float tileX2 = tile.x2-camera.posX;
        float tileY1 = tile.y1-camera.posY;
        float tileY2 = tile.y2-camera.posY;
        float pX1 = person.caixaColisao.x1;
        float pY1 = person.caixaColisao.y1;
        float pX2 = person.caixaColisao.x2;
        float pY2 = person.caixaColisao.y2;
        float cVelX = camera.velX;
        float cVelY = camera.velY;
        // se o personagem está horizontalmente e vericalmente fora quina do tile
        if(pX2<tileX1 || pY1>tileY2) return;
        // se o personagem está no ponto cego da quina
        if(pX1>tileX2 && pY2<tileY1) return;
        // se o personagem não colide com o tile
        if(!(pX1<tileX2 && pY2>tileY1)) return;
        // calcula o fator de aproximação horizontal (mais sobre a quina ou mais ao lado da quina)
        pY2-=camera.velY; // correção de queda na quina
        float fatorH = Math.abs(pX1-tileX2);
        float fatorV = Math.abs(pY2-tileY1);
        if(fatorH>fatorV){ // aproximação por cima
            // recoloca o personagem acima do tile
            float altura = person.caixaColisao.y2-person.caixaColisao.y1;
            person.caixaColisao.y2=tileY1;
            person.caixaColisao.y1=person.caixaColisao.y2-altura;
            person.atualizaCaixaColisao();
            KeyState keyState = Recursos.getInstance().keyState;
            if(keyState.k_esquerda||keyState.k_direita)
                person.entraEstadoCORRENDO();
            else
                person.entraEstadoPARADO();
        }else{ // aproximação pelo lado direito
            /// recoloca o personagem na posição anterior
            float largura = person.caixaColisao.x2-person.caixaColisao.x1;
            person.caixaColisao.x1=tileX2-1;
            person.caixaColisao.x2=person.caixaColisao.x1+largura;
            person.atualizaCaixaColisao();
            person.entraEstadoPAREDE();
        }
    }
    public void checaColisaoOrange(Tile tile, Camera camera){
        float tileX1 = tile.x1-camera.posX;
        float tileX2 = tile.x2-camera.posX;
        float tileY1 = tile.y1-camera.posY;
        float tileY2 = tile.y2-camera.posY;
        float pX1 = person.caixaColisao.x1;
        float pY1 = person.caixaColisao.y1;
        float pX2 = person.caixaColisao.x2;
        float pY2 = person.caixaColisao.y2;
        float cVelX = camera.velX;
        float cVelY = camera.velY; 
        // se o personagem está horizontalmente e vericalmente fora quina do tile
        if(pX1>tileX2 || pY1>tileY2) return;
        // se o personagem está no ponto cego da quina
        if(pX2<tileX1 && pY2<tileY1) return;
        // se o personagem não colide com o tile
        if(!(pX2>tileX1 && pY2>tileY1)) return;
        // calcula o fator de aproximação horizontal (mais sobre a quina ou mais ao lado da quina)
        pY2-=camera.velY; // correção de queda na quina
        float fatorH = Math.abs(tileX1-pX2);
        float fatorV = Math.abs(pY2-tileY1);
        if(fatorH>fatorV){ // aproximação por cima
            // recoloca o personagem acima do tile
            float altura = person.caixaColisao.y2-person.caixaColisao.y1;
            person.caixaColisao.y2=tileY1;
            person.caixaColisao.y1=person.caixaColisao.y2-altura;
            person.atualizaCaixaColisao();
            KeyState keyState = Recursos.getInstance().keyState;
            if(keyState.k_esquerda||keyState.k_direita)
                person.entraEstadoCORRENDO();
            else
                person.entraEstadoPARADO();
        }else{ // aproximação pelo lado esquerdo
            /// recoloca o personagem na posição anterior
            float largura = person.caixaColisao.x2-person.caixaColisao.x1;
            person.caixaColisao.x2=tileX1;
            person.caixaColisao.x1=person.caixaColisao.x2-largura;
            person.atualizaCaixaColisao();
            person.entraEstadoPAREDE();
        }
    }
    public void checaColisaoPurple(Tile tile, Camera camera){
        float tileX1 = tile.x1-camera.posX;
        float tileX2 = tile.x2-camera.posX;
        float tileY1 = tile.y1-camera.posY;
        float tileY2 = tile.y2-camera.posY;
        float pX1 = person.caixaColisao.x1;
        float pY1 = person.caixaColisao.y1;
        float pX2 = person.caixaColisao.x2;
        float pY2 = person.caixaColisao.y2;
        float cVelX = camera.velX;
        float cVelY = camera.velY; 
        // se o personagem está verticalmente fora do tile
        if(pY2<tileY1 || pY1>tileY2)
            return;
        // se o personagem está horizontalmente à esquerda (após o tile)
        if(pX1<tileX1)
            return;
        // verifica se colide da direita para a esquerda
        if(pX1<tileX2 && pX1-cVelX >= pX1){
            // recoloca o personagem na posição anterior
            float largura = person.caixaColisao.x2-person.caixaColisao.x1;
            person.caixaColisao.x1=tileX2-1;
            person.caixaColisao.x2=person.caixaColisao.x1+largura;
            person.atualizaCaixaColisao();
            person.entraEstadoPAREDE();
        }
    }

    public void checaColisaoYellow(Tile tile, Camera camera){
        float tileX1 = tile.x1-camera.posX;
        float tileX2 = tile.x2-camera.posX;
        float tileY1 = tile.y1-camera.posY;
        float tileY2 = tile.y2-camera.posY;
        float pX1 = person.caixaColisao.x1;
        float pY1 = person.caixaColisao.y1;
        float pX2 = person.caixaColisao.x2;
        float pY2 = person.caixaColisao.y2;
        float cVelX = camera.velX;
        float cVelY = camera.velY; 
        // se o personagem está verticalmente fora do tile
        if(pY2<tileY1 || pY1>tileY2)
            return;
        // se o personagem está horizontalmente à direita (após o tile)
        if(pX2>tileX2)
            return;
        // verifica se colide da esquerda para a direita
        if(pX2>tileX1 && pX2-cVelX <= pX2){
            // recoloca o personagem na posição anterior
            float largura = person.caixaColisao.x2-person.caixaColisao.x1;
            person.caixaColisao.x2=tileX1;
            person.caixaColisao.x1=person.caixaColisao.x2-largura;
            person.atualizaCaixaColisao();
            person.entraEstadoPAREDE();
        }
    }
}