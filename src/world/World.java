package world;

import entity.Entity;
import entity.Player;
import entity.Transform;
import io.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics.AABB;
import render.Animation;
import render.Camera;
import render.Shader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class World {
    private final int view = 24;
    private byte[] tiles;
    private AABB[] bounding_boxes;
    private List<Entity> entities;
    private int width;
    private int height;
    private int scale;

    private Matrix4f world;

    public World(String world) {
        try {
            BufferedImage tile_sheet = ImageIO.read(new File("./levels/" + world + "_tiles.png"));
            //BufferedImage entity_sheet = ImageIO.read(new File("./levels/" + world + "_entities.png"));

            width = tile_sheet.getWidth();
            height = tile_sheet.getHeight();
            scale = 16;

            int[] colorTileSheet = tile_sheet.getRGB(0, 0, width, height,
                    null, 0, width);

            tiles = new byte[width * height];
            bounding_boxes = new AABB[width * height];
            entities = new ArrayList<>();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int red = (colorTileSheet[x + y * width] >> 16) & 0xFF;

                    Tile t;

                    try {
                       t = Tile.tiles[red];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        t = null;
                    }

                    if (t == null) {continue;}

                    setTile(t, x, y);
                }
            }

            //TODO
            Transform p_transform = new Transform();
            p_transform.scale = new Vector3f(1, 2, 0);
            entities.add(new Player(p_transform, new Vector2f(1)));

            Transform test_transform = new Transform();
            test_transform.pos.set(5 * 2, -4 * 2, 0);
            entities.add(new Entity(new Animation(1, 1, "CharacterWalkFrame"),
                    test_transform, new Vector2f(1)){
                @Override
                public void update(float delta, Window window, Camera camera, World world) {
                    move(new Vector2f(5*delta, 0));
                }
            });

            this.world = new Matrix4f().setTranslation(new Vector3f(0));
            this.world.scale(scale);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public World() {
        this.width = 64;
        this.height = 64;
        scale = 16;

        tiles = new byte[width * height];
        bounding_boxes = new AABB[width * height];

        world = new Matrix4f().setTranslation(new Vector3f(0));
        world.scale(scale);
    }

    public Matrix4f getWorldMatrix() {
        return world;
    }

    public void render(TileRenderer render, Shader shader, Camera camera, Window window) {
        int posX = ((int)camera.getPosition().x + (window.getWidth()/2)) / (scale * 2);
        int posY = ((int)camera.getPosition().y - (window.getHeight()/2)) / (scale * 2);

        for(int i = 0; i < view; i ++) {
            for(int j = 0; j < view; j++) {
                Tile t = getTile(i - posX, j + posY);
                if (t == null) {continue;}
                render.renderTile(t, i - posX, -j - posY, shader, world, camera);
            }
        }

        for(Entity entity : entities) {
            entity.render(shader, camera, this);
        }

    }

    public void update(float delta, Window window, Camera camera) {
        for(Entity entity : entities) {
            entity.update(delta, window, camera, this);
        }

        for(int i = 0; i < entities.size(); i++) {
            entities.get(i).collideWithWorld(this);
        }
    }

    public void correctCamera(Camera camera, Window window) {
        Vector3f pos = camera.getPosition();

        int w = -width * scale * 2;
        int h = height * scale * 2;

        if (pos.x > -(window.getWidth()/2) + scale) {
            pos.x = -(window.getWidth()/2) + scale;
        }

        if (pos.x < w + (window.getWidth()/2) + scale) {
            pos.x = w + (window.getWidth()/2) + scale;
        }

        if (pos.y < (window.getHeight()/2) - scale) {
            pos.y = (window.getWidth()/2) - scale;
        }

        if (pos.y > h - (window.getWidth()/2) - scale) {
            pos.y = h - (window.getWidth()/2) - scale;
        }
    }

    public void setTile(Tile tile, int x, int y) {
        if (x > width || y > height) {
            throw new IllegalStateException("Tried to set tile outside of World Boundaries");
        }
        tiles[x + y * width] = tile.getId();
        if (tile.isSolid()) {
            bounding_boxes[x + y * width] = new AABB(new Vector2f(x * 2, -y), new Vector2f(0.5f, 0.5f));
        } else {
            bounding_boxes[x + y * width] = null;
        }
    }

    public Tile getTile(int x, int y) {
        try {
            return Tile.tiles[tiles[x + y * width]];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public AABB getTileBoundingBox(int x, int y) {
        try {
            return bounding_boxes[x + y * width];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public int getScale() {
        return scale;
    }
}
