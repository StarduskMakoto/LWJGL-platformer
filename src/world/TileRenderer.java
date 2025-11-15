package world;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import render.*;

import java.util.HashMap;

public class TileRenderer {
    private HashMap<String, Texture> tile_textures;
    private Model model;

    public TileRenderer() {
        tile_textures = new HashMap<>();

        float[] vertices = new float[] {
                -0.5f, 0.5f, 0,  // TOP LEFT     0
                0.5f, 0.5f, 0,   // TOP RIGHT    1
                0.5f, -0.5f, 0,  // BOTTOM RIGHT 2
                -0.5f, -0.5f, 0, // BOTTOM LEFT  3
        };

        float[] texture = new float[] {
                0, 0, // TOP LEFT
                1, 0, // TOP RIGHT
                1, 1, // BOTTOM RIGHT
                0, 1  // BOTTOM LEFT
        };

        int[] indices = new int[] {
                0, 1, 2,
                0, 2, 3
        };

        model = new Model(vertices, texture, indices);

        for(int i = 0; i < Tile.tiles.length; i++) {
            if (Tile.tiles[i] == null) { continue; }

            if (!tile_textures.containsKey(Tile.tiles[i].getTexture())) {
                String tex = Tile.tiles[i].getTexture();
                tile_textures.put(tex, new Texture(tex+".png"));
            }
        }
    }

    public void renderTile(Tile tile, int x, int y, Shader shader, Matrix4f world, Camera camera) {
        shader.bind();
        if (tile_textures.containsKey(tile.getTexture())) {
            tile_textures.get(tile.getTexture()).bind(0);
        }

        Matrix4f tile_pos = new Matrix4f().translate(new Vector3f(x, y, 0));
        Matrix4f target = new Matrix4f();

        camera.getProjection().mul(world, target);
        target.mul(tile_pos);

        shader.setUniform("sampler", 0);
        shader.setUniform("projection", target);

        model.render();

    }
}
