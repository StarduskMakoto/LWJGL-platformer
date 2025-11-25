package gui;

import io.Input;
import io.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import physics.AABB;
import render.Camera;
import render.Shader;
import render.SpriteSheet;

public abstract class GuiElement {

    protected AABB bounding_box;

    protected static Matrix4f transform = new Matrix4f();

    protected SpriteSheet sheet;

    public GuiElement(Vector2f position, Vector2f scale, String sheetName, Vector2i numTiles) {
        this.bounding_box = new AABB(position, scale);
        sheet = new SpriteSheet(sheetName, numTiles);
    }

    public abstract void render(Camera camera, Shader shader);

    public abstract void update(Window window, Input input);
}
