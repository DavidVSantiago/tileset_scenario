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
    public Camera camera;
    public KeyState keyState;

    // construtor
    public Level(String arquivoLevel,String imagensDir,Camera camera,KeyState keyState,Person person) {
        this.imagensDir = imagensDir;
        this.camera = camera;
        this.keyState = keyState;
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

        // normaliza os tileIDs dos tilelayers
        for(int i=0;i<listaTileLayers.length;i++){
            for(int j=0;j<listaTileLayers[i].tileIDs.length;j++){
                if(listaTileLayers[i].tileIDs[j]==0) continue;
                listaTileLayers[i].tileIDs[j] = (listaTileLayers[i].tileIDs[j]-listaTileLayers[i].tileset.firstGridId)+1;
            }
        }
        camera.levelAtual = this;
        camera.posY = alturaLevel-camera.altura; // camera, por padrão, começa no canto inferior esquerdo do cenário       
    }

    /** Implemente este método e especifique quais os tilesets associados a quais layers do level */
    public abstract void atribuiTilesetsLayers();

    // métodos gameloop ***********************************************
    public final void handlerEvents() {
        /** Modificar a velocidade do person de acordo com a movimentação dos direcionais */
        person.velX = 0;
        person.velY = 0;
        if (keyState.k_direita) {
            person.velX = person.velBase;
            if (keyState.k_cima) {
                person.velY = -person.velBase;
            } else if (keyState.k_baixo) {
                person.velY = person.velBase;
            }
        } else if (keyState.k_esquerda) {
            person.velX = -person.velBase;
            if (keyState.k_cima) {
                person.velY = -person.velBase;
            } else if (keyState.k_baixo) {
                person.velY = person.velBase;
            }
        } else if (keyState.k_cima) {
            person.velY = -person.velBase;
        } else if (keyState.k_baixo) {
            person.velY = person.velBase;
        }

        handlerEventsLevel(); // especificidade de quem herda
    }

    public final void update() {
        
        // atualiza a camera com base na velocidade do personagem ******************************
        if(camera.cameraEsquerdaLevel()){ // camera na esquerda do level
            // movimentação horizontal ---------------------------------
            System.out.println("Centro X: "+person.getCentroX()+person.velX);
            System.out.println("Limite H: "+person.limiteHorizontal);
            if(person.getCentroX()+person.velX<=person.limiteHorizontal){ // personagem dentro da primeira metade da tela
                person.posX+=person.velX;
            }else{
                camera.posX+=person.velX;
            }
            // movimentação vertical -----------------------------------
            if(camera.cameraInferiorLevel()){ // camera na parte inferior esquerda do level
                if(person.getCentroY()+person.velY>=person.limiteVertical){ // personagem na parte infeior da tela
                    person.posY+=person.velY;
                }else{
                    camera.posY+=person.velY;
                }
            }else if(camera.cameraSuperiorLevel()){ // camera na parte superior esquerda do level
                if(person.getCentroY()+person.velY<=person.limiteVertical){ // personagem na parte superior da tela
                    person.posY+=person.velY;
                }else{
                    camera.posY+=person.velY;
                }
            }else{ // camera apenas na parte esquerda da tela 
                camera.posY+=person.velY;
            }
        }else  if(camera.cameraDireitaLevel()){ // camera na direita do level
            // movimentação horizontal ---------------------------------
            if(person.getCentroX()+person.velX>=person.limiteHorizontal){ // personagem dentro da segunda metade da tela
                person.posX+=person.velX;
            }else{
                camera.posX+=person.velX;
            }
            // movimentação vertical -----------------------------------
            if(camera.cameraInferiorLevel()){ // camera na parte inferior direita do level
                if(person.getCentroY()+person.velY>=person.limiteVertical){ // personagem na parte infeior da tela
                    person.posY+=person.velY;
                }else{
                    camera.posY+=person.velY;
                }
            }else if(camera.cameraSuperiorLevel()){ // camera na parte superior direita do level
                if(person.getCentroY()+person.velY<=person.limiteVertical){ // personagem na parte superior da tela
                    person.posY+=person.velY;
                }else{
                    camera.posY+=person.velY;
                }
            }else{ // camera apenas na parte direita da tela 
                camera.posY+=person.velY;
            }
        }else{ // camera no centro horizontal
            camera.posX+=person.velX;
            // movimentação vertical -----------------------------------
            if(camera.cameraInferiorLevel()){ // camera na parte inferior do level
                if(person.getCentroY()+person.velY>=person.limiteVertical){ // personagem na parte infeior da tela
                    person.posY+=person.velY;
                }else{
                    camera.posY+=person.velY;
                }
            }else if(camera.cameraSuperiorLevel()){ // camera na parte superior do level
                if(person.getCentroY()+person.velY<=person.limiteVertical){ // personagem na parte superior da tela
                    person.posY+=person.velY;
                }else{
                    camera.posY+=person.velY;
                }
            }else{ // camera livre no meio da tela
                camera.posY+=person.velY;
            }
        }
        camera.checarColisao();
            
        // atualiza todos os layers do level
        for (int i = 0; i < listaTileLayers.length; i++) {
            listaTileLayers[i].update();
        }
        
        person.update();

        updateLevel(); // especificidade de quem herda
    }

    public final void render(Graphics g) {
        // renderiza os npcs
        g.setColor(Color.RED);
        listaTileLayers[0].render(g); // renderiza Layer01-sky
        listaTileLayers[1].render(g); // renderiza Layer02-sky
        listaTileLayers[2].render(g); // renderiza Layer03-back
        person.render(g); // renderiza o personagem
        listaTileLayers[3].render(g); // renderiza Layer04-front

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
}