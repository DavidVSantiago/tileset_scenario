package engine;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;

public abstract class Person{
    // atributos ---------------------------------------------------
    public BufferedImage img;
	public int largura, altura;
	public float posX, posY;
	public float velX, velY, velBaseX, velBaseY;
    public float limiteHorizontal,limiteVertical;
    public EstadoPerson ESTADO;
    public Rectangle caixaColisao, caixaMove;

    // construtor --------------------------------------------------
    public Person(){
        largura=32; // com base na imagem
		altura=32; // // com base na imagem
        posX=(Recursos.getInstance().tamanhoTela.width/2.0f)-(largura/2);
        posY=(Recursos.getInstance().tamanhoTela.height/2.0f)-(altura/2);
        velBaseX = 1;
        velBaseY = 3;
        velY=0;
        limiteHorizontal = (Recursos.getInstance().tamanhoTela.width/2.0f);
        limiteVertical = (Recursos.getInstance().tamanhoTela.height/2.0f);
        ESTADO = EstadoPerson.PARADO;
        caixaColisao = new Rectangle((int)(posX+2), (int)(posY+2), (int)((posX+largura)-2), (int)(posY+altura));
        caixaMove = new Rectangle((int)(Recursos.getInstance().tamanhoTela.width*0.40),
                                  (int)(Recursos.getInstance().tamanhoTela.height*0.38),
                                  (int)(Recursos.getInstance().tamanhoTela.width*0.6),
                                  (int)(Recursos.getInstance().tamanhoTela.height*0.72));
    }

    // Métodos gameloop --------------------------------------------
    public void handlerEvents(){
        
    }

    public void update(){
        
        checarColisaoLevel();

        updateCaixaColisao();
    }

    public void render(Graphics g) {
        g.fillRect((int)posX,(int)posY,largura,altura);
        g.setColor(Color.GREEN);
        g.drawRect(caixaColisao.x1,caixaColisao.y1,caixaColisao.x2-caixaColisao.x1,caixaColisao.y2-caixaColisao.y1);
        g.setColor(Color.WHITE);
        g.drawRect(caixaMove.x1,caixaMove.y1,caixaMove.x2-caixaMove.x1,caixaMove.y2-caixaMove.y1);
    }

    // Métodos --------------------------------------------
    public float getCentroX(){
        return posX + (largura/2.0f);
    }

    public float getCentroY(){
        return posY + (altura/2.0f);
    }

    public void iniciarPulo(){
        
    }

    public void checarColisaoLevel(){
        // colisão com limites do Level
        if(posX<0) posX=0;
        if(posX+largura>Recursos.getInstance().tamanhoTela.width)
            posX=Recursos.getInstance().tamanhoTela.width-largura;
    }

    public void updateCaixaColisao(){
        caixaColisao.x1 = (int)posX+2;
        caixaColisao.x2 = (int)(posX+largura)-2;
        caixaColisao.y1 = (int)posY+2;
        caixaColisao.y2 = (int)(posY+altura);
    }

    // Métodos Verificadores de ESTADO ----------------------------------
    public boolean isParado(){
        return ESTADO==EstadoPerson.PARADO;
    }
    public boolean isPulando(){
        return ESTADO==EstadoPerson.PULANDO;
    }
    public boolean isCorrendo(){
        return ESTADO==EstadoPerson.CORRENDO;
    }
    public boolean isDano(){
        return ESTADO==EstadoPerson.DANO;
    }
    public boolean isMorrendo(){
        return ESTADO==EstadoPerson.MORRENDO;
    }

    public boolean colideCaixaMoveDireita(){
        Camera camera = Recursos.getInstance().camera;
        if(posX+largura+camera.velX>=caixaMove.x2){
            posX = (caixaMove.x2-largura);
            return true;
        }
        return false;
    }

    public boolean colideCaixaMoveEsquerda(){
        Camera camera = Recursos.getInstance().camera;
        if(posX+camera.velX<=caixaMove.x1){
            posX = caixaMove.x1;
            return true;
        }
        return false;
    }

    public boolean colideCaixaMoveBaixo(){
        Camera camera = Recursos.getInstance().camera;
        if(posY+altura+camera.velY>=caixaMove.y2){
            posY = caixaMove.y2-altura;
            return true;
        }
        return false;
    }

    public boolean colideCaixaMoveCima(){
        Camera camera = Recursos.getInstance().camera;
        if(posY+camera.velY<=caixaMove.y1){
            posY = caixaMove.y1;
            return true;
        }
        return false;
    }

    public void updateCamera(){

    }

    public void updateCameraOld(){
        // atualiza a camera com base na velocidade do personagem ******************************
        Camera camera = Recursos.getInstance().camera;
        if(camera.cameraEsquerdaLevel()){ // camera na esquerda do level
            // movimentação horizontal ---------------------------------
            if(getCentroX()+velX<=limiteHorizontal){ // personagem dentro da primeira metade da tela
                posX+=velX;
            }else{
                camera.posX+=velX;
            }
            // movimentação vertical -----------------------------------
            if(camera.cameraInferiorLevel()){ // camera na parte inferior esquerda do level
                if(getCentroY()+velY>=limiteVertical){ // personagem na parte infeior da tela
                    posY+=velY;
                }else{
                    camera.posY+=velY;
                }
            }else if(camera.cameraSuperiorLevel()){ // camera na parte superior esquerda do level
                if(getCentroY()+velY<=limiteVertical){ // personagem na parte superior da tela
                    posY+=velY;
                }else{
                    camera.posY+=velY;
                }
            }else{ // camera apenas na parte esquerda da tela 
                camera.posY+=velY;
            }
        }else  if(camera.cameraDireitaLevel()){ // camera na direita do level
            // movimentação horizontal ---------------------------------
            if(getCentroX()+velX>=limiteHorizontal){ // personagem dentro da segunda metade da tela
                posX+=velX;
            }else{
                camera.posX+=velX;
            }
            // movimentação vertical -----------------------------------
            if(camera.cameraInferiorLevel()){ // camera na parte inferior direita do level
                if(getCentroY()+velY>=limiteVertical){ // personagem na parte infeior da tela
                    posY+=velY;
                }else{
                    camera.posY+=velY;
                }
            }else if(camera.cameraSuperiorLevel()){ // camera na parte superior direita do level
                if(getCentroY()+velY<=limiteVertical){ // personagem na parte superior da tela
                    posY+=velY;
                }else{
                    camera.posY+=velY;
                }
            }else{ // camera apenas na parte direita da tela 
                camera.posY+=velY;
            }
        }else{ // camera no centro horizontal
            camera.posX+=velX;
            // movimentação vertical -----------------------------------
            if(camera.cameraInferiorLevel()){ // camera na parte inferior do level
                if(getCentroY()+velY>=limiteVertical){ // personagem na parte infeior da tela
                    posY+=velY;
                }else{
                    camera.posY+=velY;
                }
            }else if(camera.cameraSuperiorLevel()){ // camera na parte superior do level
                if(getCentroY()+velY<=limiteVertical){ // personagem na parte superior da tela
                    posY+=velY;
                }else{
                    camera.posY+=velY;
                }
            }else{ // camera livre no meio da tela
                camera.posY+=velY;
            }
        }
        camera.checarColisao();
    }
}