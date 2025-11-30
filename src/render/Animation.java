package render;

import io.Timer;

public class Animation {
    private Texture[] frames;
    private int pointer;

    private double elapsedTime;
    private double currentTime;
    private double lastTime;
    private double fps;
    private SpriteSheet sheet;

    public Animation(int amount, int fps, String filename) {
        this.pointer = 0;
        this.elapsedTime = 0;
        this.currentTime = 0;
        this.lastTime = Timer.getTime();
        this.fps = 1.0/(double)fps;

        this.sheet = null;

        this.frames = new Texture[amount];
        for(int i = 0; i < amount; i++) {
            this.frames[i] = new Texture("anim/"+filename+"/"+i+".png");
        }
    }

    public Animation(int amount, int fps, SpriteSheet sheet) {
        this.pointer = 0;
        this.elapsedTime = 0;
        this.currentTime = 0;
        this.lastTime = Timer.getTime();
        this.fps = 1.0/(double)fps;
        this.sheet = sheet;
    }

    public void bind(Shader shader) {bind(0, shader);}

    public void bind(int sampler, Shader shader) {
        this.currentTime = Timer.getTime();
        this.elapsedTime += currentTime - lastTime;

        if(elapsedTime >= fps) {
            elapsedTime = 0;
            pointer++;
        }

        if (sheet != null) {
            //sheet.bindTile(shader, pointer);
            bindSheet(shader);
            return;
        }

        bindFrames(sampler);
        //frames[pointer].bind(sampler);
    }

    private void bindFrames(int sampler) {
        if (pointer >= frames.length) {pointer = 0;}

        this.lastTime = currentTime;

        frames[pointer].bind(sampler);
    }

    private void bindSheet(Shader shader) {
        if (pointer >= sheet.getTileAmount()) {pointer = 0;}

        this.lastTime = currentTime;
        sheet.bindTile(shader, pointer);
    }
}
