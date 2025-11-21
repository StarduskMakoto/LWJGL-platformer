package gui;

import assets.Assets;
import io.Window;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import render.Camera;
import render.Shader;
import render.SpriteSheet;

public class Gui {
    private Shader shader;
    private Camera camera;
    private SpriteSheet sheet;

    public Gui(Window window) {
        shader = new Shader("gui");
        camera = new Camera(window.getWidth(), window.getHeight());
        sheet = new SpriteSheet("CharacterWalkTest", new Vector2i(4, 2));
    }

    public void resizeCamera(Window window) {
        camera.setProjection(window.getWidth(), window.getHeight());
    }

    public void render() {
        Matrix4f mat = new Matrix4f();
        camera.getProjection().scale(32, 64, 0, mat);
        mat.translate(-2, 0, 0);

        shader.bind();

        shader.setUniform("projection", mat);

        sheet.bindTile(shader, 1);
        //shader.setUniform("color", new Vector4f(0, 0, 0, 0.4f));

        Assets.getModel().render();
    }
}
