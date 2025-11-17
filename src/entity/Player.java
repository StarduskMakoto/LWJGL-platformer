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

public class Player {
    private Model model;
    private AABB bounding_box;
    //private Texture texture;
    private Animation texture;
    private Transform transform;
    private float SPEED = 10f;

    public Player() {
        float[] vertices = new float[] {
                -1f, 1f, 0,  // TOP LEFT     0
                1f, 1f, 0,   // TOP RIGHT    1
                1f, -1f, 0,  // BOTTOM RIGHT 2
                -1f, -1f, 0, // BOTTOM LEFT  3
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

        model = new Model(vertices, texture, indices);
        //this.texture = new Texture("CharacterTestTexture.png");
        this.texture = new Animation(4, 5, "CharacterWalkFrame");

        transform = new Transform();
        transform.scale = new Vector3f(16, 32, 1);

        bounding_box = new AABB(new Vector2f(transform.pos.x, transform.pos.y), new Vector2f(1, 1));
    }

    public void update(float delta, Window window, Camera camera, World world) {
        if (window.getInput().isKeyDown(GLFW_KEY_W)) {
            //pos.y -= STEP;
            transform.pos.add(new Vector3f(0, SPEED*delta, 0));
        }
        if (window.getInput().isKeyDown(GLFW_KEY_A)) {
            //pos.x += STEP;
            transform.pos.add(new Vector3f(-SPEED*delta, 0, 0));
        }
        if (window.getInput().isKeyDown(GLFW_KEY_S)) {
            //pos.y += STEP;
            transform.pos.add(new Vector3f(0, -SPEED*delta, 0));
        }
        if (window.getInput().isKeyDown(GLFW_KEY_D)) {
            //pos.x -= STEP;
            transform.pos.add(new Vector3f(SPEED*delta, 0, 0));
        }

        bounding_box.getCenter().set(transform.pos.x, transform.pos.y);

        AABB[] boxes = new AABB[25];
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                boxes[i + j * 5] = world.getTileBoundingBox(
                        (int)(((transform.pos.x / 2) + 0.5f) - (5/2)) + i,
                        (int)(((-transform.pos.y / 2) + 0.5f) - (5/2)) + j
                );
            }
        }

        //AABB box = null;
        for(int i = 0; i < boxes.length; i++) {
            if(boxes[i] != null) {
//                if(box == null) box = boxes[i];
//
//                Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
//                Vector2f length2 = boxes[i].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
//
//                if(length1.lengthSquared() > length2.lengthSquared()) {
//                    box = boxes[i];
//                }
                Collision data = bounding_box.getCollision(boxes[i]);
                if(!data.isIntersecting) { continue; }
                bounding_box.correctPosition(boxes[i], data);
                transform.pos.set(bounding_box.getCenter(), 0);
            }
        }
//        if(box != null) {
//            Collision data = bounding_box.getCollision(box);
//            if(data.isIntersecting) {
//                bounding_box.correctPosition(box, data);
//                transform.pos.set(bounding_box.getCenter(), 0);
//            }
//
//            for(int i = 0; i < boxes.length; i++) {
//                if(boxes[i] != null) {
//                    if(box == null) box = boxes[i];
//
//                    Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
//                    Vector2f length2 = boxes[i].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
//
//                    if(length1.lengthSquared() > length2.lengthSquared()) {
//                        box = boxes[i];
//                    }
//                }
//            }
//
//            data = bounding_box.getCollision(box);
//            if(data.isIntersecting) {
//                bounding_box.correctPosition(box, data);
//                transform.pos.set(bounding_box.getCenter(), 0);
//            }
//        }

        //camera.setPosition(transform.pos.mul(-world.getScale(), new Vector3f()));
        camera.getPosition().lerp(transform.pos.mul(transform.scale.mul(-1, new Vector3f()), new Vector3f()), 0.05f);
    }

    public void render(Shader shader, Camera camera) {
        shader.bind();
        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(camera.getProjection()));
        texture.bind(0);
        model.render();
    }
}
