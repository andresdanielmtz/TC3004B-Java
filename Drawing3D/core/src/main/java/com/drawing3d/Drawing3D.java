package com.drawing3d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Drawing3D extends ApplicationAdapter {
    public PerspectiveCamera camera;
    public ModelBatch modelBatch;
    public Environment environment;
    public Model boxModel, sphereModel, cylinderModel, coneModel;
    public ModelInstance boxInstance, sphereInstance, cylinderInstance,
        coneInstance;

    // Rotation angles

    private float rotationAngle = 0;

    @Override
    public void create() {
        camera = new PerspectiveCamera(
            67,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // Create materials with different colors
        Material boxMaterial = new Material(ColorAttribute.createDiffuse(Color.RED));
        Material sphereMaterial = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        Material cylinderMaterial = new Material(ColorAttribute.createDiffuse(Color.BLUE));
        Material coneMaterial = new Material(ColorAttribute.createDiffuse(Color.YELLOW));
// Create models using ModelBuilder
        ModelBuilder modelBuilder = new ModelBuilder();

        // Box (2x2x2 units)
        boxModel = modelBuilder.createBox(2f, 2f, 2f, boxMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
// Sphere (radius 1.5 units)
        sphereModel = modelBuilder.createSphere(3f, 3f, 3f, 20, 20, sphereMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
// Cylinder (radius 1, height 3 units)
        cylinderModel = modelBuilder.createCylinder(2f, 3f, 2f, 20, cylinderMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
// Cone (radius 1.5, height 3 units)
        coneModel = modelBuilder.createCone(3f, 3f, 3f, 20, coneMaterial,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        boxInstance = new ModelInstance(boxModel, -5f, 0f, 0f);
        sphereInstance = new ModelInstance(sphereModel, 0f, 0f, 5f);
        cylinderInstance = new ModelInstance(cylinderModel, 5f, 0f, 0f);
        coneInstance = new ModelInstance(coneModel, 0f, 0f, -5f);
    }

    private void handleInput() {
        float delta = Gdx.graphics.getDeltaTime() * 5f;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) camera.position.add(camera.direction.cpy().scl(delta));
        if (Gdx.input.isKeyPressed(Input.Keys.S)) camera.position.add(camera.direction.cpy().scl(-delta));
        if (Gdx.input.isKeyPressed(Input.Keys.A)) camera.position.add(camera.direction.cpy().crs(camera.up).nor().scl(-delta));
        if (Gdx.input.isKeyPressed(Input.Keys.D)) camera.position.add(camera.direction.cpy().crs(camera.up).nor().scl(delta));
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) camera.rotate(Vector3.Y, 1f);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) camera.rotate(Vector3.Y, -1f);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) camera.rotate(camera.direction.cpy().crs(camera.up).nor(), 1f);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.rotate(camera.direction.cpy().crs(camera.up).nor(), -1f);
        camera.update();
    }
    @Override
    public void render() {
        handleInput();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        rotationAngle += Gdx.graphics.getDeltaTime() * 20f;
        boxInstance.transform.setToRotation(0, 1, 0, rotationAngle).trn(-5f, 0f, 0f);
        sphereInstance.transform.setToRotation(1, 0, 0, rotationAngle).trn(0f, 0f, 5f);
        cylinderInstance.transform.setToRotation(0, 0, 1, rotationAngle).trn(5f, 0f, 0f);
        coneInstance.transform.setToRotation(1, 1, 0, rotationAngle).trn(0f, 0f, -5f);
        modelBatch.begin(camera);
        modelBatch.render(boxInstance, environment);
        modelBatch.render(sphereInstance, environment);
        modelBatch.render(cylinderInstance, environment);
        modelBatch.render(coneInstance, environment);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        boxModel.dispose();
        sphereModel.dispose();
        cylinderModel.dispose();
        coneModel.dispose();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
}
