package render;

import org.joml.Matrix4f;

public class SpriteSheet {
    private Texture texture;

    private Matrix4f scale;
    private Matrix4f translation;

    private int amountOfTiles;

    public SpriteSheet(String texture, int amountOfTiles) {
        this.texture = new Texture("SpriteSheets/" + texture + ".png");

        scale = new Matrix4f().scale(1.0f/(float)amountOfTiles);
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
        int x = index % amountOfTiles;
        int y = index / amountOfTiles;

        bindTile(shader, x, y);

    }
}
