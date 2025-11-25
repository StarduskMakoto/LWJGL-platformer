package io;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjglx.debug.org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private long window;

    private int width, height;
    private boolean fullscreen;
    private boolean hasResized;
    private GLFWWindowSizeCallback windowSizeCallback;

    private Input input;

    public static void setCallbacks() {
        glfwSetErrorCallback(new GLFWErrorCallback() {
            @Override
            public void invoke(int error, long description) {
                throw new IllegalStateException(GLFWErrorCallback.getDescription(description));
            }
        });
    }

    private void setLocalCallbacks() {
        windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                Window.this.width = width;
                Window.this.height = height;
                Window.this.hasResized = true;
            }
        };

        glfwSetWindowSizeCallback(window, windowSizeCallback);


    }

    public Window() {
        setSize(600, 600);
        setFullscreen(false);
        hasResized = false;
    }

    public void createWindow(String title) {
        window = glfwCreateWindow(width,
                height,
                title,
                fullscreen ? glfwGetPrimaryMonitor() : 0,
                0);

        if (window == NULL) {
            throw new IllegalStateException("Failed to create GLFW io.Window");
        }

        if (!fullscreen) {
            GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window,
                    (vid.width() - width) / 2,
                    (vid.height() - height) / 2);
        }

        glfwShowWindow(window);
        glfwMakeContextCurrent(window);

        input = new Input(window);
        setLocalCallbacks();
    }

    public void cleanUp() {
        windowSizeCallback.close();
    }

    public void destroy() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void update() {
        hasResized = false;
        input.update();
        glfwPollEvents();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean hasResized() {
        return hasResized;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public long getWindow() {
        return window;
    }

    public Input getInput() {
        return input;
    }
}
