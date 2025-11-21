package entity;

import io.*;
import org.joml.Vector2f;
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

    public Player(Transform transform, Vector2f hitbox) {
        super(ANIM_SIZE, transform, hitbox);
        //this.transform.scale = new Vector3f(1, 2, 0);
        setAnimation(ANIM_IDLE, new Animation(1, 10, "player/walk"));
        setAnimation(ANIM_WALK, new Animation(4, 10, "player/walk"));
        setAnimation(ANIM_JUMP, new Animation(1, 10, "player/jump"));
        setAnimation(ANIM_FALL, new Animation(1, 10, "player/fall"));

        useAnimation(ANIM_WALK);
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        Vector2f movement = new Vector2f();

        if (window.getInput().isKeyDown(GLFW_KEY_W)) {
            movement.add(new Vector2f(0, SPEED*delta));
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

        if (movement.lengthSquared() > 0) {
            useAnimation(ANIM_WALK);
        } else
            useAnimation(ANIM_IDLE);

        this.move(movement);



        camera.getPosition().lerp(transform.pos.mul(-world.getScale(), -world.getScale() * 2, -world.getScale(), new Vector3f()), 0.05f);
    }
}
