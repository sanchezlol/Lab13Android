package com.example.mac.a13;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class TextureGenerator {
    static int generate(Context context, int resourceId) {
        int textures[] = new int[1];

        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        //GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLUtils.getInternalFormat(bitmap), bitmap, GLUtils.getType(bitmap), 0);
        bitmap.recycle();

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);


Log.d("sad", bitmap.getWidth() + ", " + bitmap.getHeight());

        //ByteBuffer image = ByteBuffer.allocateDirect(bitmap.getByteCount())
                //.order(ByteOrder.nativeOrder())
                //.asIntBuffer()
                ;//.put(bitmap.copyPixelsToBuffer(););

        //bitmap.copyPixelsToBuffer(image);

        //vertexBuffer.position(0);
        //GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, 256, 256, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, image);


        return textures[0];
    }
}

class MyGLRenderer implements GLSurfaceView.Renderer {
    Context context;

    private String vertexShader;
    private String fragmentShader;

    private int shader;

    private int vertexLocation;
    private FloatBuffer vertexBuffer;

    private int viewMatrixLocation;
    private float viewMatrix[];

    private int textureCoordsLocation;
    private FloatBuffer textureCoordsBuffer;

    private int textureLocation;

    private int colorLocation;
    private FloatBuffer colorBuffer;

    private float[] position;

    int texture;


    public MyGLRenderer(float[] position, Context context) {
        super();

        this.context = context;
        this.position = position;

        this.vertexShader =
                "attribute vec3 vertex;" +
                        "attribute vec4 color;" +
                        "attribute vec2 texture_coordinate;" +

                        "uniform mat4 view_matrix;" +

                        "varying vec2 fs_texture_coordinate;" +
                        "varying vec4 fs_color;" +

                        "void main()" +
                        "{" +
                        "gl_Position = view_matrix * vec4(vertex, 1.0);" +
                        "fs_color = color;" +
                        "fs_texture_coordinate = texture_coordinate;" +
                        "}";

        this.fragmentShader =
                "precision mediump float;\n" +
                        "varying vec4 fs_color;" +
                        "varying vec2 fs_texture_coordinate;" +
                        "uniform sampler2D texture;" +
                        "void main()" +
                        "{" +
                        //"gl_FragColor = fs_color + texture2D(texture, fs_texture_coordinate);" +
                        "gl_FragColor = texture2D(texture, fs_texture_coordinate);" +
                        "}";
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        this.shader = this.createShaderProgram();
        vertexLocation = GLES20.glGetAttribLocation(this.shader, "vertex");
        colorLocation = GLES20.glGetAttribLocation(this.shader, "color");
        textureCoordsLocation = GLES20.glGetAttribLocation(this.shader, "texture_coordinate");
        textureLocation = GLES20.glGetUniformLocation(this.shader, "texture");
        viewMatrixLocation = GLES20.glGetUniformLocation(this.shader, "view_matrix");

        float vertieces[] = {
                -1.0f, -1.0f, -1.0f,
                 1.0f, -1.0f, -1.0f,
                 1.0f,  1.0f, -1.0f,

                -1.0f, -1.0f, -1.0f,
                 1.0f,  1.0f, -1.0f,
                -1.0f,  1.0f, -1.0f,

                //////////////////////

                -1.0f, -1.0f,  1.0f,
                -1.0f,  1.0f,  1.0f,
                 1.0f,  1.0f,  1.0f,

                -1.0f, -1.0f,  1.0f,
                 1.0f,  1.0f,  1.0f,
                 1.0f, -1.0f,  1.0f,

                ////////////////////

                 1.0f, -1.0f,  1.0f,
                 1.0f,  1.0f,  1.0f,
                 1.0f,  1.0f, -1.0f,

                 1.0f, -1.0f,  1.0f,
                 1.0f,  1.0f, -1.0f,
                 1.0f, -1.0f, -1.0f,

                ////////////////////

                -1.0f, -1.0f, -1.0f,
                -1.0f,  1.0f, -1.0f,
                -1.0f,  1.0f,  1.0f,

                -1.0f, -1.0f, -1.0f,
                -1.0f,  1.0f,  1.0f,
                -1.0f, -1.0f,  1.0f,

                /*,

                0.0f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.5f,
                0.0f, 0.5f, 0.0f,

                0.0f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.5f,
                0.0f, 0.0f, 0.5f,

                0.0f, 0.0f, 0.0f,
                0.5f, 0.5f, 0.5f,
                0.0f, 0.5f, 0.5f,

                0.0f, 0.0f, 0.0f,
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.0f,*/
        };

        float colors[] = {
                0.5f, 0.0f, 0.0f, 1.0f,
                0.5f, 0.5f, 0.0f, 1.0f,
                0.0f, 0.5f, 0.0f, 1.0f,

                0.5f, 0.0f, 0.0f, 1.0f,
                0.5f, 0.5f, 0.0f, 1.0f,
                0.0f, 0.5f, 0.0f, 1.0f/*,

                0.5f, 0.0f, 0.0f, 1.0f,
                0.5f, 0.5f, 0.0f, 1.0f,
                0.0f, 0.5f, 0.0f, 1.0f,

                0.5f, 0.0f, 0.0f, 1.0f,
                0.5f, 0.5f, 0.0f, 1.0f,
                0.0f, 0.5f, 0.0f, 1.0f,

                0.5f, 0.0f, 0.0f, 1.0f,
                0.5f, 0.5f, 0.0f, 1.0f,
                0.0f, 0.5f, 0.0f, 1.0f,

                0.5f, 0.0f, 0.0f, 1.0f,
                0.5f, 0.5f, 0.0f, 1.0f,
                0.0f, 0.5f, 0.0f, 1.0f*/
        };

        float textureCoords[] = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,

                ///////////

                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,

                1.0f, 0.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,

                ///////////

                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,

                1.0f, 0.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,

                ///////////

                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,

                1.0f, 0.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,
                /*,

                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f*/
        };

        vertexBuffer = ByteBuffer.allocateDirect(vertieces.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertieces);

        vertexBuffer.position(0);

        textureCoordsBuffer = ByteBuffer.allocateDirect(textureCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureCoords);

        textureCoordsBuffer.position(0);

        colorBuffer = ByteBuffer.allocateDirect(colors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(colors);

        colorBuffer.position(0);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        this.texture = TextureGenerator.generate(context, R.drawable.texture);
    }

    private int creteShader(String source, int type) {
        int shaderId = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shaderId, source);
        GLES20.glCompileShader(shaderId);

        Log.d("shader", GLES20.glGetShaderInfoLog(shaderId));

        return shaderId;
    }

    private int createShaderProgram() {
        int vs = this.creteShader(this.vertexShader, GLES20.GL_VERTEX_SHADER);
        int fs = this.creteShader(this.fragmentShader, GLES20.GL_FRAGMENT_SHADER);

        int shaderProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(shaderProgram, vs);
        GLES20.glAttachShader(shaderProgram, fs);
        GLES20.glLinkProgram(shaderProgram);

        return shaderProgram;
    }

    private float[] mulMatrix(float[] a, float[] b) {
        float[] result = new float[16];

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                float t = 0;

                for (int k = 0; k < 4; ++k) {
                    t += a[i * 4 + k] * b[k * 4 + j];
                }

                result[i * 4 + j] = t;
            }
        }

        return result;
    }

    private float[] mulMatrix(float a, float[] b) {
        float[] result = new float[16];

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                result[i * 4 + j] = a * b[i * 4 + j];
            }
        }

        result[15] = 1;

        return result;
    }

    private float[] translate(float x, float y, float z, float[] b) {
        float[] result = new float[16];

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                result[i * 4 + j] = b[i * 4 + j];
            }
        }

        result[12] = x;
        result[13] = y;
        result[14] = z;
        result[15] = 1;

        return result;
    }

    private void print(float[] b) {
        //float[] result = new float[16];

        for (int i = 0; i < 4; ++i) {
            String m = "";

            for (int j = 0; j < 4; ++j) {
                m += b[i * 4 + j] + ", ";
            }

            Log.d("matrix", m);
        }
/*
        result[3] = x;
        result[7] = y;
        result[11] = z;

        result[15] = 1;

        return result;*/
    }

    private float scalarDelta = 0;

    private void rotateX() {
        float angle = position[1] * 2 * (float)Math.PI;

        float[] rotateXMatrix = new float[]{
                1, 0, 0, 0,
                0, (float) Math.cos(angle), (float) -Math.sin(angle), 0,
                0, (float) Math.sin(angle), (float) Math.cos(angle), 0,
                0, 0, 0, 1,
        };

        viewMatrix = mulMatrix(rotateXMatrix, viewMatrix);
    }

    private void rotateY() {
        float angle = position[0] * 2 * (float)Math.PI;

        float[] rotateXMatrix = new float[]{
                (float) Math.cos(angle), 0, (float) Math.sin(angle), 0,
                0, 1, 0, 0,
                (float) -Math.sin(angle), 0, (float) Math.cos(angle), 0,
                0, 0, 0, 1,
        };

        viewMatrix = mulMatrix(rotateXMatrix, viewMatrix);
    }

    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(this.shader);

        //GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        //GLES20.glUniform1i(textureLocation, texture);

        viewMatrix = new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1,
        };

        viewMatrix = mulMatrix((float) /*Math.sin(scalarDelta) / 10 + */1.0f, viewMatrix);
        rotateX();
        rotateY();
        viewMatrix = translate(0.0f, 0.0f, -1.0f, viewMatrix);
        //Matrix.translateM(viewMatrix, 0, 0.1f, 0, 0);

        if(scalarDelta == 0)
            print(viewMatrix);

        scalarDelta += 0.1;

        GLES20.glUniformMatrix4fv(viewMatrixLocation, 1, false, viewMatrix, 0);

        GLES20.glEnableVertexAttribArray(vertexLocation);
        GLES20.glVertexAttribPointer(vertexLocation, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glEnableVertexAttribArray(textureCoordsLocation);
        GLES20.glVertexAttribPointer(textureCoordsLocation, 2, GLES20.GL_FLOAT, false, 0, textureCoordsBuffer);

        GLES20.glEnableVertexAttribArray(colorLocation);
        GLES20.glVertexAttribPointer(colorLocation, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexBuffer.capacity() / 3);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }
}

class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer mRenderer;
    private float[] position;

    private final MyGLSurfaceView view = this;

    public MyGLSurfaceView(final Context context) {
        super(context);

        setEGLContextClientVersion(2);

        position = new float[]{0, 0};
        mRenderer = new MyGLRenderer(position, context);
        setRenderer(mRenderer);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        position[0] = event.getX() / view.getWidth();
                        position[1] = event.getY() / view.getHeight();
                        return true;
                }

                return false;
            }
        });
    }
}

public class MainActivity extends AppCompatActivity {
    MyGLSurfaceView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MyGLSurfaceView(this);
        setContentView(view);
    }
}
