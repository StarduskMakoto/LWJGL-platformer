import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
    // The window handle
    private long window;

    private Vector2f pos = new Vector2f();
    final private float STEP = 1f;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Set up an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(600, 600, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
        /*glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });*/

        glfwSetKeyCallback(window, this::key);

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        //glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // glMatrixMode(GL_PROJECTION);
        // glOrtho(-300, 300, -300, 300, 0, 0);
        // glMatrixMode(GL_MODELVIEW);

        Camera camera = new Camera(600, 600);

        glEnable(GL_TEXTURE_2D);

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

        Model model = new Model(vertices, texture, indices);

        Shader shader = new Shader("shader");

        Texture tex = new Texture("./res/SimpleSquareTexture.png");

        //Matrix4f projection = new Matrix4f()
                //.ortho2D(-300, 300, -300, 300);
        Matrix4f scale = new Matrix4f()
                .translate(new Vector3f(100, 0, 0))
                .scale(100);

        Matrix4f target = new Matrix4f();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        double frame_cap = 1.0/60.0;
        double frame_time = 0;
        int frames = 0;

        double time = Timer.getTime();
        double unprocessed = 0;

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            boolean can_render = false;

            double time_2 = Timer.getTime();
            double passed = time_2 - time;
            unprocessed += passed;
            frame_time += passed;

            time = time_2;

            while (unprocessed >= frame_cap) {
                unprocessed -= frame_cap;
                can_render = true;
                target = scale;

                // Poll for window events. The key callback above will only be
                // invoked during this call.
                glfwPollEvents();
                if (frame_time >= 1.0) {
                    frame_time = 0;
                    System.out.println("FPS: " + frames);
                    frames = 0;
                }
            }

            if (!can_render) {continue;}

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            //projection.rotate(-0.05f, 0, 0, 1);
            //projection.mul(scale, target);

            camera.setPosition(new Vector3f(pos, 0));

            shader.bind();
            shader.setUniform("sampler", 0);
            shader.setUniform("projection", camera.getProjection().mul(target));
            tex.bind(0);
            model.render();

            frames++;

            glfwSwapBuffers(window); // swap the color buffers
        }
    }

    private void key(long window, int key, int scancode, int action, int mods) {
        switch (key) {
            case GLFW_KEY_ESCAPE:
                if (action == GLFW_RELEASE )
                    glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
                break;
            case GLFW_KEY_W:
                pos.y -= STEP;
                break;
            case GLFW_KEY_A:
                pos.x += STEP;
                break;
            case GLFW_KEY_S:
                pos.y += STEP;
                break;
            case GLFW_KEY_D:
                pos.x -= STEP;
                break;
        }

    }

    public static void main(String[] args) {
        new Main().run();
    }
}