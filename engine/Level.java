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
        // carrega o arquivo json do cenario
        JSONObject fullJson = carregarJson(arquivoLevel);
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
    public final void handlerEvents() {
        KeyState keyState = Recursos.getInstance().keyState;
        Camera camera = Recursos.getInstance().camera;
        camera.velX=0;
        //if(!person.isPulando())
            //camera.velY=0;
        // atualiza a velocidade da camera
        if (keyState.k_direita) {
            camera.velX = camera.velBaseX;
        } else if (keyState.k_esquerda) {
            camera.velX = -camera.velBaseX;
        }
        if (keyState.k_cima && !person.isPulando()) {
            person.ESTADO = EstadoPerson.PULANDO;
            camera.velY=-camera.velBaseY;
        }
    
        handlerEventsLevel(); // especificidade de quem herda
    }

    public final void update() {
        Camera camera = Recursos.getInstance().camera;
        // atualiza a movimento da camera
        
        if(person.colideCaixaMoveDireita() ||
           person.colideCaixaMoveEsquerda()){ // se o person colide com os limites esquerdo e direito
            camera.posX += camera.velX; // move a camera
        }else{ // se o person não colide com os limites esquerdo e direito
            person.posX += camera.velX; // move o personagem horizontalmente
        }
        
        if(person.colideCaixaMoveCima() ||
           person.colideCaixaMoveBaixo()){ // se o person colide com os limites superior e inferior
            camera.posY += camera.velY; // move a camera
        }else{ // se o person não colide com os limites superior e inferior
            person.posY += camera.velY; // move o personagem verticalmente
        }

        if(person.isPulando() && camera.velY<=camera.limiteVelY){
            camera.velY+=camera.decremVelY;
        }
            
        // atualiza todos os layers do level (ainda sem função)
        for (int i = 0; i < listaTileLayers.length; i++) {
            listaTileLayers[i].update();
        }
        person.updateCaixaColisao();
        updateLevel(); // especificidade de quem herda

        colisaoPersonLevel(); // Testa a colisão do personagem com os tiles do cenário
    }

    public final void render(Graphics g) {
        // renderiza os npcs
        g.setColor(Color.RED);
        listaTileLayers[0].render(g); // renderiza Layer01-sky
        listaTileLayers[1].render(g); // renderiza Layer02-sky
        listaTileLayers[2].render(g); // renderiza Layer03-back
        person.render(g); // renderiza o personagem
        listaTileLayers[3].render(g); // renderiza Layer04-front
        listaTileLayers[4].render(g); // renderiza Layer04-front

        renderLevel(g); // especificidade de quem herda
    }

    // métodos abstratos ***********************************************
    public abstract void handlerEventsLevel();
    public abstract void updateLevel();
    public abstract void renderLevel(Graphics g);

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
            BufferedImage img = carregarImagem(imagensDir + jsonTileset.getString("image"));
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

    public static BufferedImage carregarImagem(String file){
		BufferedImage img = null;
		try {
			img = ImageIO.read(Level.class.getResource(file));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}

    public static JSONObject carregarJson(String arquivo) throws NullPointerException{
		InputStream inputStream = Level.class.getResourceAsStream(arquivo);
		if (inputStream ==null)
			throw new NullPointerException("Arquivo "+ arquivo +" não existe!");
		return new JSONObject(new JSONTokener(inputStream));
	}
    
    // métodos de colisão *******************************************
    /** Testa a colisão do personagem com os tiles do cenário */
    public void colisaoPersonLevel(){
        TileLayer layer05 = listaTileLayers[4]; // Layer05-collision (colidível)
        Tile[] tilesDestino = layer05.tilesDestino; // obtem a lista de tiles de destino do layer05
        for(int i=0; i<tilesDestino.length;i++){ // percorre todos os tiles de destino do layer05
            Tile tileDestino = tilesDestino[i];
            if(tileDestino.ID==0)continue; // id 0 se refere a nenhum tile
            // se o tile de destino estiver fora da camera, não testa colisão
            Camera camera = Recursos.getInstance().camera;
            if(camera.tileForaDaCamera(tileDestino,layer05.fatorParalaxeX,layer05.fatorParalaxeY)) continue;
            if(tileDestino.ID-1!=2)continue; // se não for um tile de colisão
            // testa a colisão do personagem com cada tile dentro da camera

            if(checaColisaoRed(tileDestino,camera)){ // se o person colide com a parte de cima
                // recoloca o personagem acima do tile
                person.posY=(tileDestino.y1-person.altura)-camera.posY;
                person.updateCaixaColisao();
                person.ESTADO = EstadoPerson.PARADO;
            }
        }
    }

    public boolean checaColisaoRed(Tile tile, Camera camera){
        float tileX1 = tile.x1-camera.posX;
        float tileX2 = tile.x2-camera.posX;
        float tileY1 = tile.y1-camera.posY;
        float tileY2 = tile.y2-camera.posY;
        int pX1 = person.caixaColisao.x1;
        int pY1 = person.caixaColisao.y1;
        int pX2 = person.caixaColisao.x2;
        int pY2 = person.caixaColisao.y2;
        float cVelX = camera.velX;
        float cVelY = camera.velY; 
        // se o personagem está horizontalmente fora do tile
        if(pX2<tileX1 || pX1>tileX2) return false;
        // se o personagem está vericalmente abaixo do tile
        if(pY1>tileY2) return false;
        // verifica se colide de cima para baixo
        if(pY2>tileY1 && pY2-cVelY < pY2)
            return true;
        return false;
    }
}