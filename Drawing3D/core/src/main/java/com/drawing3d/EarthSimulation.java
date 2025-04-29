package com.drawing3d;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class EarthSimulation extends ApplicationAdapter {
    public PerspectiveCamera camera;
    public CameraInputController camController;
    public ModelBatch modelBatch;
    public Environment environment;

    public Model earthModel;
    public ModelInstance earthInstance;
    public Texture earthTexture;
    private float rotationAngle = 0;
    private final float EARTH_RADIUS = 5f;
    private final float ROTATION_SPEED = 10f; // Degrees per second

    @Override
    public void create() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 15);
        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.far = 300f;
        camera.update();
        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // Load Earth texture
        earthTexture = new Texture(Gdx.files.internal("earth.jpg"));

        // Create Earth model
        ModelBuilder modelBuilder = new ModelBuilder();
        earthModel = modelBuilder.createSphere(
            EARTH_RADIUS, EARTH_RADIUS, EARTH_RADIUS,
            64, 64, // divisions (higher = smoother)
            new Material(TextureAttribute.createDiffuse(earthTexture)),
            VertexAttributes.Usage.Position |
                VertexAttributes.Usage.Normal |
                VertexAttributes.Usage.TextureCoordinates
        );
        earthInstance = new ModelInstance(earthModel);

    }

    @Override
    public void render() {
        rotationAngle += ROTATION_SPEED * Gdx.graphics.getDeltaTime();
        earthInstance.transform.setToRotation(Vector3.Y, rotationAngle);
// Clear screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
// Render Earth
        modelBatch.begin(camera);
        modelBatch.render(earthInstance, environment);
        modelBatch.end();
// Update camera controller
        camController.update();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        earthModel.dispose();
        earthTexture.dispose();
    }
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

}
