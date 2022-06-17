package engine;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class Person{
    // atributos ---------------------------------------------------
    public BufferedImage img;
	public int largura, altura;
	public float posX, posY;
	public float velX, velY, velBase;
    public float limiteHorizontal,limiteVertical;
    public EstadoPerson ESTADO;
    public Rectangle caixaColisao;

    // construtor --------------------------------------------------
    public Person(){
        largura=32; // com base na imagem
		altura=32; // // com base na imagem
        posX=(Recursos.getInstance().tamanhoTela.width/2.0f)-(largura/2);
        posY=(Recursos.getInstance().tamanhoTela.height/2.0f)-(altura/2);
        velBase = 1;
        velY=0;
        limiteHorizontal = (Recursos.getInstance().tamanhoTela.width/2.0f);
        limiteVertical = (Recursos.getInstance().tamanhoTela.height/2.0f);
        ESTADO = EstadoPerson.PARADO;
        caixaColisao = new Rectangle((int)(posX+2), (int)(posY+2), (int)((posX+largura)-2), (int)(posY+altura));
    }

    // Métodos gameloop --------------------------------------------
    public void handlerEvents(){
        /** Modificar a velocidade do person de acordo com a movimentação dos direcionais */
        KeyState keyState = Recursos.getInstance().keyState;
        velX = 0;

        if (keyState.k_direita) {
            velX = velBase;
        } else if (keyState.k_esquerda) {
            velX = -velBase;
        }
        // se pressionar pra cima e não estiver pulando
        if (keyState.k_cima && !isPulando()) {
            iniciarPulo();
        }else if (keyState.k_baixo && !isPulando()) {
            velY = +velBase;
        }
    }

    public void update(){
        posY+=velY;
        if(isPulando()){
            velY+=0.2f;
        }
        updateCamera();
        checarColisaoLevel();

        updateCaixaColisao();
    }
    public abstract void render(Graphics g);

    // Métodos --------------------------------------------
    public float getCentroX(){
        return posX + (largura/2.0f);
    }

    public float getCentroY(){
        return posY + (altura/2.0f);
    }

    public void iniciarPulo(){
        ESTADO = EstadoPerson.PULANDO;
        velY = -3;
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

    public void updateCamera(){
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