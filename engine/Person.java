package engine;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;

public abstract class Person{
    // atributos sprites -------------------------------------------
    public BufferedImage img;
    public Rectangle[] parado, correndo;
    public Rectangle pulando, parede;
    public int paradoQuadro, correndoQuadro;
    // atributos ---------------------------------------------------
	public int largura, altura;
	public float posX, posY;
	public float velX, velY, velBaseX, velBaseY;
    public float limiteHorizontal,limiteVertical;
    public EstadoPerson ESTADO;
    public Orientacao ORIENTACAO;
    public Rectangle caixaColisao, caixaMove;
    public int fatorDiminuicaoColisao;
    int acumuladorQuadro;
    boolean bloqueaMovimentoH;


    // construtor --------------------------------------------------
    public Person(){
        bloqueaMovimentoH = false;
        acumuladorQuadro = 0;
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
        ORIENTACAO = Orientacao.DIREITA;
        fatorDiminuicaoColisao = 5;
        caixaColisao = new Rectangle((int)(posX+fatorDiminuicaoColisao), (int)(posY+fatorDiminuicaoColisao), (int)((posX+largura)-fatorDiminuicaoColisao), (int)(posY+altura));
        caixaMove = new Rectangle((int)(Recursos.getInstance().tamanhoTela.width*0.40),
                                  (int)(Recursos.getInstance().tamanhoTela.height*0.38),
                                  (int)(Recursos.getInstance().tamanhoTela.width*0.6),
                                  (int)(Recursos.getInstance().tamanhoTela.height*0.72));
        // carrega o sprite e quadros
        img = Recursos.carregarImagem("/assets/charset.png");
        parado = new Rectangle[6];
        for(int i=0;i<parado.length;i++){
            parado[i] = new Rectangle(i*32, 0, (i*32)+32, 32);
        }
        paradoQuadro = 0;
        correndo = new Rectangle[8];
        for(int i=0;i<correndo.length;i++){
            correndo[i] = new Rectangle(i*32, 32, (i*32)+32, 64);
        }
        correndoQuadro = 0;
        pulando = new Rectangle(192, 0, 224, 32);
        parede = new Rectangle(224, 0, 256, 32);
    }

    public void handlerEvents(){
        // macanica que gerencia o bloqueio do movimento horizontal
        if(bloqueaMovimentoH){
            acumuladorQuadro++;
            if(acumuladorQuadro >=9){
                bloqueaMovimentoH=false;
                acumuladorQuadro=0;
            }
        }

        KeyState keyState = Recursos.getInstance().keyState;
        Camera camera = Recursos.getInstance().camera;
        if (!bloqueaMovimentoH) { // se o movimento Horizontal não estiver bloqueado
            camera.velX = 0;
            if (keyState.k_direita) {
                camera.velX = camera.velBaseX;
                ORIENTACAO = Orientacao.DIREITA;
            } else if (keyState.k_esquerda) {
                camera.velX = -camera.velBaseX;
                ORIENTACAO = Orientacao.ESQUERDA;
            }
        }
        // se pressionou para cima e não está pulando
        if (keyState.k_cima && !isPULANDO()) {
            if(isPAREDE()){ // se o pulo acontece a partir da parede
                bloqueaMovimentoH = true;
                if(ORIENTACAO==Orientacao.ESQUERDA) // se o personagem está na parede pela direita
                    camera.velX=camera.velBaseX; // move o personagem para direção esquerda
                else// se o personagem está na parede pela esquerda
                    camera.velX=-camera.velBaseX; // move o personagem para direção direita
                entraEstadoPULANDOdaPAREDE();
            }else{ // se o pulo acontece a partir do chão
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
        if(isPULANDO() && camera.velY<=camera.limiteVelY){
            camera.velY+=camera.decremVelY; // decrementa a velocidade vertical, para o personagem descer
        }
        atualizaCaixaColisao();
    }

    public void render(Graphics g) {
        //g.fillRect((int)posX,(int)posY,largura,altura);
        Rectangle sourceRect = getQuadro();
        g.drawImage(img, (int)caixaColisao.x1, (int)caixaColisao.y1, (int)(int)caixaColisao.x1+largura, (int)caixaColisao.y1+altura,
        (int)sourceRect.x1, (int)sourceRect.y1, (int)sourceRect.x2, (int)sourceRect.y2, null);
        g.setColor(Color.GREEN);
        g.drawRect((int)caixaColisao.x1,(int)caixaColisao.y1,(int)(caixaColisao.x2-caixaColisao.x1),(int)(caixaColisao.y2-caixaColisao.y1));
        g.setColor(Color.WHITE);
        g.drawRect((int)caixaMove.x1,(int)caixaMove.y1,(int)(caixaMove.x2-caixaMove.x1),(int)(caixaMove.y2-caixaMove.y1));
    }

    // Métodos quadros --------------------------------------------

    public int paradoQuadroCont=0;
    public int correndoQuadroCont=0;
    public Rectangle getQuadro(){
        if(ESTADO==EstadoPerson.PARADO){
            paradoQuadroCont++;
            if(paradoQuadroCont>10){
                paradoQuadroCont=0;
                paradoQuadro = (paradoQuadro==parado.length-1)?0:paradoQuadro+1; // próximo quadro
            }
            return parado[paradoQuadro];
        }else if(ESTADO==EstadoPerson.CORRENDO){
            correndoQuadroCont++;
            if(correndoQuadroCont>10){
                correndoQuadroCont=0;
                correndoQuadro = (correndoQuadro==correndo.length-1)?0:correndoQuadro+1; // próximo quadro
            }
            return parado[paradoQuadro];
        }else if(ESTADO==EstadoPerson.PULANDO){
            return pulando;
        }else{
            return parede;
        }
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
        camera.velY=-(camera.velBaseY/2); // a altura do pulo a partir da parede é menor
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

    public enum Orientacao{
        ESQUERDA,DIREITA;
    }
}