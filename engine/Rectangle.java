package engine;

public class Rectangle {
    // atributos ----------------------------------------------
    public int x1, y1;
    public int x2, y2;

    // construtor ---------------------------------------------
    public Rectangle(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
    // métodos ------------------------------------------------
    public boolean intersedeEsquerda(Rectangle outro){
        return x2>=outro.x1;
    }
    public boolean intersedeDireita(Rectangle outro){
        return x1<=outro.x2;
    }
    public boolean intersedeCima(Rectangle outro){
        return y2>=outro.y1;
    }
    public boolean intersedeBaixo(Rectangle outro){
        return y1<=outro.y2;
    }
    public boolean intersedeTodo(Rectangle outro){
        return (intersedeEsquerda(outro) &&
                intersedeDireita(outro) &&
                intersedeCima(outro) &&
                intersedeBaixo(outro));
    }
}
