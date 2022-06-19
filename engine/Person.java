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
    public int fatorDiminuicaoColisao;


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
        fatorDiminuicaoColisao = 5;
        caixaColisao = new Rectangle((int)(posX+fatorDiminuicaoColisao), (int)(posY+fatorDiminuicaoColisao), (int)((posX+largura)-fatorDiminuicaoColisao), (int)(posY+altura));
        caixaMove = new Rectangle((int)(Recursos.getInstance().tamanhoTela.width*0.40),
                                  (int)(Recursos.getInstance().tamanhoTela.height*0.38),
                                  (int)(Recursos.getInstance().tamanhoTela.width*0.6),
                                  (int)(Recursos.getInstance().tamanhoTela.height*0.72));
    }

    // Métodos gameloop --------------------------------------------
    public void handlerEvents(){
        KeyState keyState = Recursos.getInstance().keyState;
        Camera camera = Recursos.getInstance().camera;
        if (Recursos.getInstance().permiteMoverH) { // permite movimento horizontal
            camera.velX = 0;
            if (keyState.k_direita) {
                camera.velX = camera.velBaseX;
            } else if (keyState.k_esquerda) {
                camera.velX = -camera.velBaseX;
            }
        }
        // se pressionou para cima e não está pulando
        if (keyState.k_cima && !isPULANDO()) {
            if(isPAREDE()){
                Recursos.getInstance().desabilitaMoverH(150);
                camera.velX=camera.velBaseX;
                entraEstadoPULANDOdaPAREDE();
            }else{
                entraEstadoPULANDO();
            }
        }
    }

    public void update(){
        ESTADO = EstadoPerson.PULANDO;
        Camera camera = Recursos.getInstance().camera;
        // atualiza a movimento da camera
        if(colideCaixaMoveDireita() ||
           colideCaixaMoveEsquerda()){ // se o person colide com os limites esquerdo e direito
            camera.moverHorizontal(); // move a camera horizontalmente
        }else{ // se o person não colide com os limites esquerdo e direito
            moverHorizontal(camera); // move o personagem horizontalmente
        }
        
        if(colideCaixaMoveCima() ||
           colideCaixaMoveBaixo()){ // se o person colide com os limites superior e inferior
            camera.moverVertical(); // move a camera verticalmente
        }else{ // se o person não colide com os limites superior e inferior
            moverVertical(camera); // move o personagem verticalmente
        }
        System.out.println(ESTADO);
        if(isPULANDO() && camera.velY<=camera.limiteVelY){
            camera.velY+=camera.decremVelY; // decrementa a velocidade vertical, para o personagem descer
        }
        atualizaCaixaColisao();
    }

    public void render(Graphics g) {
        //g.fillRect((int)posX,(int)posY,largura,altura);
        g.setColor(Color.GREEN);
        g.drawRect((int)caixaColisao.x1,(int)caixaColisao.y1,(int)(caixaColisao.x2-caixaColisao.x1),(int)(caixaColisao.y2-caixaColisao.y1));
        g.setColor(Color.WHITE);
        g.drawRect((int)caixaMove.x1,(int)caixaMove.y1,(int)(caixaMove.x2-caixaMove.x1),(int)(caixaMove.y2-caixaMove.y1));
    }

    // Métodos posicionamento --------------------------------------------

    public void moverHorizontal(Camera camera){
        caixaColisao.x1 += camera.velX;
        caixaColisao.x2 += camera.velX;
    }

    public void moverVertical(Camera camera){
        caixaColisao.y1 += camera.velY;
        caixaColisao.y2 += camera.velY;
    }

    public float getCentroX(){
        return posX + (largura/2.0f);
    }
    
    public float getCentroY(){
        return posY + (altura/2.0f);
    }
    
    // Métodos de colisão --------------------------------------------
    public void checarColisaoLevel(){
        // colisão com limites do Level
        if(posX<0) posX=0;
        if(posX+largura>Recursos.getInstance().tamanhoTela.width)
            posX=Recursos.getInstance().tamanhoTela.width-largura;
    }

    public void atualizaCaixaColisao(){
        // atualiza a posição do sprite em relação a atualização da caixa de colisão
        //caixaColisao.x1 = posX+fatorDiminuicaoColisao;
        //caixaColisao.x2 = (posX+largura)-fatorDiminuicaoColisao;
        //caixaColisao.y1 = posY+fatorDiminuicaoColisao;
        //caixaColisao.y2 = posY+altura;
    }

    // Métodos Verificadores de ESTADO ----------------------------------
    public boolean isPARADO(){
        return ESTADO==EstadoPerson.PARADO;
    }
    public boolean isPULANDO(){
        return ESTADO==EstadoPerson.PULANDO;
    }
    public boolean isCORRENDO(){
        return ESTADO==EstadoPerson.CORRENDO;
    }
    public boolean isDANO(){
        return ESTADO==EstadoPerson.DANO;
    }
    public boolean isMORRENDO(){
        return ESTADO==EstadoPerson.MORRENDO;
    }
    public boolean isPAREDE(){
        return ESTADO==EstadoPerson.PAREDE;
    }
    // Métodos de modificação de ESTADO ----------------------------------
    public void entraEstadoPARADO(){
        Camera camera = Recursos.getInstance().camera;
        camera.velY=camera.velBaseY*0.3f; // velicidade de caida mais lenta quando parado, para diminuir a velocidade de caída das plataformas
        ESTADO = EstadoPerson.PARADO;
    }
    public void entraEstadoPAREDE(){
        Camera camera = Recursos.getInstance().camera;
        camera.velY=camera.velBaseYParede;
        ESTADO = EstadoPerson.PAREDE;
    }
    public void entraEstadoPULANDO(){
        Camera camera = Recursos.getInstance().camera;
        camera.velY=-camera.velBaseY;
        ESTADO = EstadoPerson.PULANDO;
    }
    public void entraEstadoPULANDOdaPAREDE(){
        Camera camera = Recursos.getInstance().camera;
        camera.velY=-(camera.velBaseY/2);
        ESTADO = EstadoPerson.PULANDO;
    }

    // Métodos da caixa de colisão ---------------------------------------

    public boolean colideCaixaMoveDireita(){
        Camera camera = Recursos.getInstance().camera;
        if(caixaColisao.x2+camera.velX>caixaMove.x2){ // colide à direita do limite de movimentação
            float largura = caixaColisao.x2-caixaColisao.x1;
            caixaColisao.x2 = caixaMove.x2;
            caixaColisao.x1=caixaColisao.x2-largura;
            return true;
        }
        return false;
    }

    public boolean colideCaixaMoveEsquerda(){
        Camera camera = Recursos.getInstance().camera;
        if(caixaColisao.x1+camera.velX<caixaMove.x1){ // colide à esquerda do limite de movimentação
            float largura = caixaColisao.x2-caixaColisao.x1;
            caixaColisao.x1 = caixaMove.x1;
            caixaColisao.x2=caixaColisao.x1+largura;
            return true;
        }
        return false;
    }

    public boolean colideCaixaMoveBaixo(){
        Camera camera = Recursos.getInstance().camera;
        if(caixaColisao.y2+camera.velY>caixaMove.y2){
            float altura = caixaColisao.y2-caixaColisao.y1;
            caixaColisao.y2 = caixaMove.y2;
            caixaColisao.y1=caixaColisao.y2-altura;
            return true;
        }
        return false;
    }

    public boolean colideCaixaMoveCima(){
        Camera camera = Recursos.getInstance().camera;
        if(caixaColisao.y1+camera.velY<caixaMove.y1){
            float altura = caixaColisao.y2-caixaColisao.y1;
            caixaColisao.y1 = caixaMove.y1;
            caixaColisao.y2=caixaColisao.y1+altura;
            return true;
        }
        return false;
    }
}