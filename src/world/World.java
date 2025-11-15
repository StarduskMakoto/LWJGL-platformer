package world;

import io.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import render.Camera;
import render.Shader;

public class World {
    private final int view = 12;
    private byte[] tiles;
    private int width;
    private int height;
    private int scale;

    private Matrix4f world;

    public World() {
        this.width = 64;
        this.height = 64;
        scale = 64;

        tiles = new byte[width * height];

        world = new Matrix4f().setTranslation(new Vector3f(0));
        world.scale(scale);
    }

    public void render(TileRenderer render, Shader shader, Camera camera, Window window) {
        int posX = ((int)camera.getPosition().x + (window.getWidth()/2)) / scale;
        int posY = ((int)camera.getPosition().y - (window.getHeight()/2)) / scale;

        for(int i = 0; i < view; i ++) {
            for(int j = 0; j < view; j++) {
                Tile t = getTile(i - posX, j + posY);
                if (t == null) {continue;}
                render.renderTile(t, i - posX, -j - posY, shader, world, camera);
            }
        }

    }

    public void correctCamera(Camera camera, Window window) {
        Vector3f pos = camera.getPosition();

        int w = -width * scale;
        int h = height * scale;

        if (pos.x > -(window.getWidth()/2) + scale / 2) {
            pos.x = -(window.getWidth()/2) + scale / 2;
        }

        if (pos.x < w + (window.getWidth()/2) + scale / 2) {
            pos.x = w + (window.getWidth()/2) + scale / 2;
        }

        if (pos.y < (window.getHeight()/2) - scale / 2) {
            pos.y = (window.getWidth()/2) - scale / 2;
        }

        if (pos.y > h - (window.getWidth()/2) - scale / 2) {
            pos.y = h - (window.getWidth()/2) - scale / 2;
        }
    }

    public void setTile(Tile tile, int x, int y) {
        if (x > width || y > height) {
            throw new IllegalStateException("Tried to set tile outside of World Boundaries");
        }
        tiles[x + y * width] = tile.getId();
    }

    public Tile getTile(int x, int y) {
        try {
            return Tile.tiles[tiles[x + y * width]];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
