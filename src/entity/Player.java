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
    public Player(Transform transform, Vector2f hitbox) {
        super(new Animation(4, 10, "CharacterWalkFrame"), transform, hitbox);
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

        this.move(movement);

        super.update(delta, window, camera, world);

        Vector3f target = new Vector3f();
        transform.scale.mul(world.getScale(), target);

        camera.getPosition().lerp(transform.pos.mul(target.mul(-1, new Vector3f()), new Vector3f()), 0.05f);
    }
}
