package gui;

import assets.Assets;
import io.Input;
import io.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import render.Camera;
import render.Shader;
import render.SpriteSheet;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;

public class Gui {
    private Shader shader;
    private Camera camera;
    //private SpriteSheet sheet;

    //private GuiElement temporary;
    private List<GuiElement> elements;

    public Gui(Window window) {
        shader = new Shader("gui");
        camera = new Camera(window.getWidth(), window.getHeight());

        elements = new ArrayList<>();
        //sheet = new SpriteSheet("ButtonSheet", new Vector2i(9, 3));
//        temporary = new Button(new Vector2f(0, 0),
//                new Vector2f(128, 64),
//                "ButtonSheet",
//                new Vector2i(9, 3));
    }

    public void resizeCamera(Window window) {
        camera.setProjection(window.getWidth(), window.getHeight());
    }

    public void render() {
        shader.bind();
        for (GuiElement e : elements) {
            if (e == null) {continue;}
            e.render(camera, shader);
        }
        //temporary.render(camera, shader);
    }

    public void update(Window window,Input input) {
        for (GuiElement e : elements) {
            if (e == null) {continue;}
            e.update(window, input);
        }
    }

    public void appendElement(GuiElement element) {
        elements.add(element);
    }
}
