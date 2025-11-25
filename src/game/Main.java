package game;

import assets.Assets;
import entity.Entity;
import entity.Player;
import entity.Transform;
import gui.Button;
import gui.Gui;
import io.*;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import physics.AABB;
import render.*;
import world.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    // The window handle
    //private long window;
    private Window window;

    //private Vector2f pos = new Vector2f();
    final private float STEP = 3f;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        //glfwFreeCallbacks(window);
        //glfwDestroyWindow(window);
        window.destroy();

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Set up an error callback. The default implementation
        // will print the error message in System.err.
        //GLFWErrorCallback.createPrint(System.err).set();
        Window.setCallbacks();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Creating io.Window Object
        window = new Window();
        window.setSize(600, 600);
        window.setFullscreen(false);

        window.createWindow("Hello World!!!");
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        Camera camera = new Camera(window.getWidth(), window.getHeight());

        glEnable(GL_TEXTURE_2D);

        TileRenderer tiles = new TileRenderer();
        Assets.initAsset();

        Shader shader = new Shader("shader");

        World world = new World("Level1Test", camera);
        world.calculateView(window);

        Gui gui = new Gui(window);
        gui.appendElement(new Button(
                new Vector2f(100, 0),
                new Vector2f(128, 64),
                "ButtonSheet",
                new Vector2i(9, 3)
        ));

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        double frame_cap = 1.0/60.0;
        double frame_time = 0;
        int frames = 0;

        double time = Timer.getTime();
        double unprocessed = 0;

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !window.shouldClose() ) { // !glfwWindowShouldClose(window)
            boolean can_render = false;

            double time_2 = Timer.getTime();
            double passed = time_2 - time;
            unprocessed += passed;
            frame_time += passed;

            time = time_2;

            while (unprocessed >= frame_cap) {
                if (window.hasResized()) {
                    camera.setProjection(window.getWidth(), window.getHeight());
                    world.calculateView(window);
                    gui.resizeCamera(window);
                    glViewport(0, 0, window.getWidth(), window.getHeight());
                }

                unprocessed -= frame_cap;
                can_render = true;

                handleInputs(camera);

                gui.update(window, window.getInput());


                // Poll for window events. The key callback above will only be
                // invoked during this call.
                //glfwPollEvents();
                window.update();
                if (frame_time >= 1.0) {
                    frame_time = 0;
                    System.out.println("FPS: " + frames);
                    frames = 0;
                }
            }

            if (!can_render) {continue;}

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            world.update((float) frame_cap, window, camera);

            world.correctCamera(camera, window);

            world.render(tiles, shader, camera);

            gui.render();

            frames++;

            window.swapBuffers();
        }

        Assets.deleteAsset();
    }

    private void handleInputs(Camera camera) {
        if (window.getInput().isKeyPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(window.getWindow(), true); // We will detect this in the rendering loop
        }

    }

    public static void main(String[] args) {
        new Main().run();
    }
}