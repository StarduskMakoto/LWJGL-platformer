package gui;

import static org.lwjgl.glfw.GLFW.*;

import assets.Assets;
import io.Input;
import io.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import physics.AABB;
import physics.Collision;
import render.*;

public class Button extends GuiElement {

    private State selectedState = State.IDLE;

    public Button(Vector2f position, Vector2f scale, String sheetName, Vector2i numTiles) {
        super(position, scale, sheetName, numTiles);
    }

    public void render(Camera camera, Shader shader) {
        Vector2f position = bounding_box.getCenter();
        Vector2f scale = bounding_box.getHalfExtent();

        transform.identity().translate(position.x, position.y, 0).scale(scale.x, scale.y, 1); // Fill

        renderTile(transform, camera, shader, sheet, 1, 1);

        renderEdges(position, scale, camera, sheet, shader);
        renderCorners(position, scale, camera, sheet, shader);
    }

    @Override
    public void update(Window window, Input input) {
        //Vector2f mouseRawPos = Input.getMousePosition();
        //Vector2f mouseScaledPos = new Vector2f(mouseRawPos.x * 2.f - window.getWidth(), mouseRawPos.y * 2.f - window.getHeight());

        Vector2f mouseScaledPos = Input.getMousePosition(window);

        Collision mouseData = bounding_box.getCollision(mouseScaledPos);
        if (!mouseData.isIntersecting) {return;}
        if (!input.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {return;}

        System.out.println("CLICKED!");
    }

    public void renderEdges(Vector2f position, Vector2f scale, Camera camera, SpriteSheet sheet, Shader shader) {
        transform.identity().translate(position.x, position.y + scale.y - 16, 0).scale(scale.x, 16, 1); // Top
        renderTile(transform, camera, shader, sheet, 1, 0);

        transform.identity().translate(position.x, position.y - scale.y + 16, 0).scale(scale.x, 16, 1); // Bottom
        renderTile(transform, camera, shader, sheet, 1, 2);

        transform.identity().translate(position.x - scale.x + 16, position.y, 0).scale(16, scale.y, 1); // Left
        renderTile(transform, camera, shader, sheet, 0, 1);

        transform.identity().translate(position.x + scale.x - 16, position.y, 0).scale(16, scale.y, 1); // Right
        renderTile(transform, camera, shader, sheet, 2, 1);
    }

    public void renderCorners(Vector2f position, Vector2f scale, Camera camera, SpriteSheet sheet, Shader shader) {
        transform.identity().translate(position.x - scale.x + 16, position.y + scale.y - 16, 0).scale(16, 16, 1); // TopLeft
        renderTile(transform, camera, shader, sheet, 0, 0);

        transform.identity().translate(position.x - scale.x + 16, position.y - scale.y + 16, 0).scale(16, 16, 1); // BottomLeft
        renderTile(transform, camera, shader, sheet, 0, 2);

        transform.identity().translate(position.x + scale.x - 16, position.y - scale.y + 16, 0).scale(16, 16, 1); // BottomRight
        renderTile(transform, camera, shader, sheet, 2, 2);

        transform.identity().translate(position.x + scale.x - 16, position.y + scale.y - 16, 0).scale(16, 16, 1); // TopRight
        renderTile(transform, camera, shader, sheet, 2, 0);
    }

    private void renderTile(Matrix4f transform, Camera camera, Shader shader, SpriteSheet sheet, int tileX, int tileY) {
        shader.setUniform("projection", camera.getProjection().mul(transform));
        sheet.bindTile(shader, tileX, tileY);
        Assets.getModel().render();
    }

    enum State {
        IDLE,
        HOVERED,
        PRESSED
    }
}
