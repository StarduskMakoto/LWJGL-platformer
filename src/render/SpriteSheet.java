package render;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class SpriteSheet {
    private Texture texture;

    private Matrix4f scale;
    private Matrix4f translation;

    private Vector2i amountOfTiles;

    public SpriteSheet(String texture, Vector2i amountOfTiles) {
        this.texture = new Texture("SpriteSheets/" + texture + ".png");

        scale = new Matrix4f().scale(1.0f/(float)amountOfTiles.x, 1.0f/(float)amountOfTiles.y, 0);
        translation = new Matrix4f();
        this.amountOfTiles = amountOfTiles;
    }

    public void bindTile(Shader shader, int x, int y) {
        scale.translate(x, y, 0, translation);

        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("texModifier", translation);
        texture.bind(0);

    }

    public void bindTile(Shader shader, int index) {
        int x = index % (amountOfTiles.x*amountOfTiles.y);
        int y = index / (amountOfTiles.x*amountOfTiles.y);

        bindTile(shader, x, y);

    }

    public int getTileAmount() {
        return amountOfTiles.x * amountOfTiles.y;
    }
}
