package io;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private long window;

    private boolean keys[];
    private boolean mouseButtons[];

    private static Vector2f mouse_pos;

    public Input(long window) {
        this.window = window;
        this.keys = new boolean[GLFW_KEY_LAST];
        for (int i = 0; i < GLFW_KEY_LAST; i++) {
            this.keys[i] = false;
        }
        this.mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];
        mouse_pos = new Vector2f(0);

        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                Input.mouse_pos = new Vector2f((float)xpos, (float)ypos);
                //System.out.println(Input.mouse_pos.x +", "+ Input.mouse_pos.y);
            }
        });
    }

    public boolean isKeyDown(int key) {
        return glfwGetKey(window, key) == GLFW_PRESS;
    }

    public boolean isKeyPressed(int key) {
        return (isKeyDown(key) && !keys[key]);
    }

    public boolean isKeyReleased(int key) {
        return (!isKeyDown(key) && keys[key]);
    }

    public boolean isMouseButtonDown(int button) {
        return glfwGetMouseButton(window, button) == GLFW_PRESS;
    }

    public boolean isMouseButtonPressed(int button) {
        return (isMouseButtonDown(button) && !mouseButtons[button]);
    }

    public boolean isMouseButtonReleased(int button) {
        return (!isMouseButtonDown(button) && mouseButtons[button]);
    }

    public void update() {
        for (int i = 32; i < GLFW_KEY_LAST; i++) {
            keys[i] = isKeyDown(i);
        }
        for (int i = 0; i < GLFW_MOUSE_BUTTON_LAST; i++) {
            mouseButtons[i] = isMouseButtonDown(i);
        }
    }

    public static Vector2f getMouseRawPosition() {
        return new Vector2f(mouse_pos);
    }

    public static Vector2f getMousePosition(Window window) {
        Vector2f result = mouse_pos.mul(2.f, new Vector2f()).sub(window.getWidth(), window.getHeight());
        return result;
    }

}
