package engine;

import java.awt.Graphics;

public interface IGameloop {
    public void handlerEvents(long tempoDelta);
    public void update(long tempoDelta);
    public void render(Graphics g);
}