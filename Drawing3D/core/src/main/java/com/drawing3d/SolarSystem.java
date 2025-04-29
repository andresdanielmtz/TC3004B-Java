package com.drawing3d;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class SolarSystem extends ApplicationAdapter {
    public PerspectiveCamera camera;
    public CameraInputController camController;
    public ModelBatch modelBatch;
    public Environment environment;

    public Model sunModel, earthModel, moonModel;
    public ModelInstance sunInstance, earthInstance, moonInstance;
    public Texture sunTexture, earthTexture, moonTexture;

    private float earthRotation = 0;
    private float earthOrbit = 0;
    private float moonRotation = 0;
    private float moonOrbit = 0;

    private final float SUN_RADIUS = 8f;
    private final float EARTH_RADIUS = 2f;
    private final float MOON_RADIUS = 0.5f;
    // Distance ratios (not to scale)
    private final float EARTH_ORBIT_RADIUS = 20f;
    private final float MOON_ORBIT_RADIUS = 4f;
    // Time scales (speed up for demonstration)
    private final float EARTH_ROTATION_SPEED = 50f; // degrees/sec
    private final float EARTH_ORBIT_SPEED = 10f; // degrees/sec
    private final float MOON_ROTATION_SPEED = 5f; // degrees/sec
    private final float MOON_ORBIT_SPEED = 40f; // degrees/sec

    private void loadTextures() {
        sunTexture = new Texture(Gdx.files.internal("sun.png"));
        earthTexture = new Texture(Gdx.files.internal("earth.jpg"));
        moonTexture = new Texture(Gdx.files.internal("moon.jpg"));
    }

    private void createModels() {
        ModelBuilder modelBuilder = new ModelBuilder();
        sunModel = modelBuilder.createSphere(
            SUN_RADIUS, SUN_RADIUS, SUN_RADIUS,
            64, 64,
            new Material(
                TextureAttribute.createDiffuse(sunTexture),
                new ColorAttribute(ColorAttribute.Emissive, 1f, 0.9f, 0.9f, 1f),
                new FloatAttribute(FloatAttribute.Shininess, 0f)
            ),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates
        );

        earthModel = modelBuilder.createSphere(
            EARTH_RADIUS, EARTH_RADIUS, EARTH_RADIUS,
            64, 64,
            new Material(TextureAttribute.createDiffuse(earthTexture)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates
        );

        moonModel = modelBuilder.createSphere(
            MOON_RADIUS, MOON_RADIUS, MOON_RADIUS,
            32, 32,
            new Material(TextureAttribute.createDiffuse(moonTexture)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates
        );

        sunInstance = new ModelInstance(sunModel);
        earthInstance = new ModelInstance(earthModel);
        moonInstance = new ModelInstance(moonModel);
        modelBatch = new ModelBatch();
    }

    private void setupCamera() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 30, 50);
        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.far = 500f;
        camera.update();
        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);
    }

    private void setupLighting() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.1f, 0.1f, 0.1f, 1f));
// Stronger, more orange sunlight
        Color sunLightColor = new Color(1f, 0.9f, 0.9f, 1f); // Orange light
        Vector3 lightDirection = new Vector3(-1f, -0.8f, -0.2f).nor();
// Sun will be our light source
        environment.add(new DirectionalLight().set(
            sunLightColor, // Sunlight color
            lightDirection // Light direction
        ));
        environment.add(new PointLight()
            .set(sunLightColor,
                sunInstance.transform.getTranslation(new Vector3()),
                SUN_RADIUS * 200f)); // Glow around sun

    }

    @Override
    public void create() {
        loadTextures();
        createModels();
        setupCamera();
        setupLighting();
    }

    private void updateCelestialBodies() {
        float delta = Gdx.graphics.getDeltaTime();
// Update rotations and orbits
        earthRotation += EARTH_ROTATION_SPEED * delta;
        earthOrbit += EARTH_ORBIT_SPEED * delta;
        moonRotation += MOON_ROTATION_SPEED * delta;
        moonOrbit += MOON_ORBIT_SPEED * delta;
// Sun stays at center (just rotates slowly)
        sunInstance.transform.setToRotation(Vector3.Y, earthOrbit * 0.1f);
// Earth orbits around Sun
        earthInstance.transform.idt()
            .translate(
                (float)Math.cos(Math.toRadians(earthOrbit)) * EARTH_ORBIT_RADIUS,
                0,
                (float)Math.sin(Math.toRadians(earthOrbit)) * EARTH_ORBIT_RADIUS
            )
            .rotate(Vector3.Y, earthRotation)
            .rotate(Vector3.Z, 23.5f); // Earth's axial tilt
// Moon orbits around Earth
        Vector3 earthPosition = earthInstance.transform.getTranslation(new Vector3());
        moonInstance.transform.idt()
            .translate(earthPosition)
            .translate(
                (float)Math.cos(Math.toRadians(moonOrbit)) * MOON_ORBIT_RADIUS,
                0,
                (float)Math.sin(Math.toRadians(moonOrbit)) * MOON_ORBIT_RADIUS
            )
            .rotate(Vector3.Y, moonRotation);
    }
    @Override
    public void render() {
        updateCelestialBodies();
// Clear screen with space background
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
// Render all bodies
        modelBatch.begin(camera);
        modelBatch.render(sunInstance, environment);
        modelBatch.render(earthInstance, environment);
        modelBatch.render(moonInstance, environment);
        if (modelBatch instanceof ModelBatch) {
            sunInstance.materials.get(0).set(ColorAttribute.createEmissive(1f, 0.7f, 0.4f, 1f));
            modelBatch.render(sunInstance);
            sunInstance.materials.get(0).set(ColorAttribute.createEmissive(0.8f, 0.5f, 0.3f, 1f));
        }
        modelBatch.end();
        camController.update();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        sunModel.dispose();
        earthModel.dispose();
        moonModel.dispose();
        sunTexture.dispose();
        earthTexture.dispose();
        moonTexture.dispose();
    }
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
    }


