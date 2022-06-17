package engine;

import java.awt.Graphics;

public interface IGameloop {
    public void handlerEvents();
    public void update();
    public void render(Graphics g);
}
