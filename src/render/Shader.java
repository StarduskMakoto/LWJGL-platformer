package render;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int program;
    private int vertex_shader;
    private int frag_shader;

    public Shader(String filename) {
        program = glCreateProgram();

        vertex_shader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertex_shader, readFile(filename+".vs"));
        glCompileShader(vertex_shader);
        if (glGetShaderi(vertex_shader, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(vertex_shader));
            System.exit(1);
        }

        frag_shader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(frag_shader, readFile(filename+".fs"));
        glCompileShader(frag_shader);
        if (glGetShaderi(frag_shader, GL_COMPILE_STATUS) != 1) {
            System.err.println(glGetShaderInfoLog(frag_shader));
            System.exit(1);
        }

        glAttachShader(program, vertex_shader);
        glAttachShader(program, frag_shader);

        glBindAttribLocation(program, 0, "vertices");
        glBindAttribLocation(program, 1, "textures");

        glLinkProgram(program);
        if(glGetProgrami(program, GL_LINK_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(program));
            System.exit(1);
        }

        glValidateProgram(program);
        if(glGetProgrami(program, GL_VALIDATE_STATUS) != 1) {
            System.err.println(glGetProgramInfoLog(program));
            System.exit(1);
        }
    }

    public void setUniform(String name, int value) {
        int location = glGetUniformLocation(program, name);
        if (location == -1) { return; }
        glUniform1i(location, value);
    }

    public void setUniform(String name, Matrix4f value) {
        int location = glGetUniformLocation(program, name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(4*4);
        value.get(buffer);
        if (location == -1) { return; }
        glUniformMatrix4fv(location, false, buffer);
    }

    public void bind() {
        glUseProgram(program);
    }

    private String readFile(String filename) {
        StringBuilder string = new StringBuilder();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(new File("./shaders/" + filename)));
            String line;
            while((line = br.readLine()) != null) {
                string.append(line);
                string.append("\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return string.toString();
    }
}
