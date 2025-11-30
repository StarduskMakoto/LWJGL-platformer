package entity;

import assets.Assets;
import io.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import physics.AABB;
import physics.Collision;
import render.*;
import world.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class Player extends Entity {
    public static final int ANIM_IDLE = 0;
    public static final int ANIM_WALK = 1;
    public static final int ANIM_JUMP = 2;
    public static final int ANIM_FALL = 3;
    public static final int ANIM_SIZE = 4;

    private boolean isJumping = false;
    private boolean grounded = false;
    private float MAX_JUMP_FORCE = 6f;
    private float JUMP_STEP = 12f;
    private float JUMP_LIMIT = 0f;
    private float GRAVITY_COUNTER = 1.f;

    private Shader shader;

    public Player(Transform transform, Vector2f hitbox) {
        super(ANIM_SIZE, transform, hitbox);
        //this.transform.scale = new Vector3f(1, 2, 0);
        setAnimation(ANIM_IDLE, new Animation(1, 10, "player/walk"));
        //setAnimation(ANIM_WALK, new Animation(4, 10, "player/walk"));
        setAnimation(ANIM_WALK, new Animation(4, 10, new SpriteSheet("player/CharacterWalkTest", new Vector2i(4, 1))));
        setAnimation(ANIM_JUMP, new Animation(1, 10, "player/jump"));
        setAnimation(ANIM_FALL, new Animation(1, 10, "player/fall"));

        useAnimation(ANIM_WALK);

        shader = new Shader("sheetshader");
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        if (window.getInput().isKeyDown(GLFW_KEY_W) && (grounded || (JUMP_LIMIT > 0.f && JUMP_LIMIT < MAX_JUMP_FORCE))) {
            movement.add(new Vector2f(0, JUMP_STEP*delta));
            JUMP_LIMIT += JUMP_STEP*delta;
            isJumping = true;
        } else {
            isJumping = false;
            JUMP_LIMIT = 0;
        }

        if (window.getInput().isKeyDown(GLFW_KEY_A)) {
            movement.add(new Vector2f(-SPEED*delta, 0));
        }
        if (window.getInput().isKeyDown(GLFW_KEY_S)) {
            movement.add(new Vector2f(0, -SPEED*delta));
        }
        if (window.getInput().isKeyDown(GLFW_KEY_D)) {
            movement.add(new Vector2f(SPEED*delta, 0));
        }

        AABB tile = world.getTileBoundingBox(
                (int)(((transform.pos.x / 2) + 0.5f)),
                (int)(((-transform.pos.y) + 0.5f)) + 1
        );

        grounded = tile != null;

        if (!isJumping && !grounded) {
            movement.add(new Vector2f(0, -9.8f*delta*GRAVITY_COUNTER));
            GRAVITY_COUNTER += delta;
        } else { GRAVITY_COUNTER = 1f; }

        if (movement.y > 0) {
            useAnimation(ANIM_JUMP);
        }
        else if (movement.y < 0) {
            useAnimation(ANIM_FALL);
        }
        else if (movement.lengthSquared() > 0) {
            useAnimation(ANIM_WALK);
        } else
            useAnimation(ANIM_IDLE);

        this.move(movement);

        if (transform.pos.y < -world.getHeight()) {
            die(world);
        }


        camera.getPosition().lerp(transform.pos.mul(-world.getScale(), -world.getScale() * 2, -world.getScale(), new Vector3f()), 0.05f);
    }

    @Override
    public void die(World world) {
        isAlive = false;
        respawn(world);
    }

    @Override
    public void respawn(World world) {
        transform.pos.set(spawn_pos);
        isAlive = true;
    }

    private final Matrix4f texModifier = new Matrix4f();

    @Override
    public void render(Shader shader, Camera camera, World world) {
        if (!isAlive) {return;}

        Matrix4f target = camera.getProjection(); // Projection Matrix
        target.mul(world.getWorldMatrix()); // View Matrix

        this.shader.bind();
        this.shader.setUniform("sampler", 0);
        this.shader.setUniform("projection", transform.getProjection(target)); // Projection * View * Model
        this.shader.setUniform("texModifier", texModifier);
        animations[use_animation].bind(0, this.shader);
        Assets.getModel().render();
    }
}
