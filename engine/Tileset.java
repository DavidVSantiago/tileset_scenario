package engine;

import java.awt.image.BufferedImage;

/** Representa o TileSet ***************************************************/
public class Tileset {
    // atributos
    public BufferedImage img; // imagem qo tileset
    int firstGridId;
    int larguraTile, alturaTile; // dimensões de cada tile do tileset (em pixel)
    int espacoTiles, margemTiles;
    int larguraTileset, alturaTileset; // dimensões da imagem do tileset (em pixel)
    int qtdTiles; // quantidade total de tiles no tileset
    int qtdColunasTileset; // quantidade de colunas do tileset
    public Tile[] tilesOrigem; // retangulos de recorte do tileset

    // construtor
    public Tileset(BufferedImage img, int firstGridId,
            int larguraTile, int alturaTile,
            int espacoTiles, int margemTiles, int larguraTileset, int alturaTileset,
            int qtdTiles, int qtdColunasTileset) {
        this.firstGridId = firstGridId;
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
        tilesOrigem = new Tile[qtdTiles];
        for (int i = 0; i < qtdTiles; i++) {
            int x1 = (i % qtdColunasTileset) * larguraTile;
            int y1 = (i / qtdColunasTileset) * alturaTile;
            int x2 = x1 + larguraTile;
            int y2 = y1 + alturaTile;
            tilesOrigem[i] = new Tile(x1, y1, x2, y2, i);
        }
    }

    public Tile obterTileOrigem(int tileId) {
        return tilesOrigem[tileId];
    }

}