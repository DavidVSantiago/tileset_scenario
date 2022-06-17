package engine;

class Camera {
    public static Camera singleton = null;
    // atributos
    public float posX,posY;
    public float velX,velY,velBase;
    public int largura, altura;
    public Level levelAtual;

    // construtor
    public Camera(float posX,float posY,int largura,int altura){
        this.posX = posX;
		this.posY = posY;
		this.largura = largura;
		this.altura = altura;
		this.velBase=2;
		this.velX=0;
		this.velY=0;
    }

    // metodos gameloop ------------------------
    public void handlerEvents() {
        
    }
    // metodos --------------------------------

    public void checarColisao(){
        // colisão com limites do Level
        if(posX<0) posX=0;
        if(posY<0) posY=0;
        if(posX+largura>levelAtual.larguraLevel) posX=levelAtual.larguraLevel-largura;
        if(posY+altura>levelAtual.alturaLevel) posY=levelAtual.alturaLevel-altura;
    }

    public boolean tileForaDaCamera(Tile tile,float fatorParalaxeX,float fatorParalaxeY){
        if(tile.x1>(posX*fatorParalaxeX)+largura ||
            tile.x2<posX*fatorParalaxeX ||  
            tile.y1>(posY*fatorParalaxeY)+altura ||
            tile.y2<posY*fatorParalaxeY){
                return true;
        }
        return false;
    }

    public boolean cameraEsquerdaLevel(){
        if(posX<=0) return true;
        return false;
    }
    public boolean cameraDireitaLevel(){
        if(posX+largura>=levelAtual.larguraLevel) return true;
        return false;
    }
    public boolean cameraSuperiorLevel(){
        if(posY<=0) return true;
        return false;
    }
    public boolean cameraInferiorLevel(){
        if(posY+altura>=levelAtual.alturaLevel) return true;
        return false;
    }
}