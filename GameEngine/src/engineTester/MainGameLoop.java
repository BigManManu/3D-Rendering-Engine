package engineTester;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TextureModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class MainGameLoop {

    public static void main(String[] args) throws LWJGLException {
        DisplayManager.createDisplay();
        Loader loader = new Loader();    
        
        
        RawModel model = OBJLoader.loadOBJModel("dirt", loader);
        TextureModel texturedModel = new TextureModel(model, new ModelTexture(loader.loadTexture("image")));
        ModelTexture texture = texturedModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        Entity entity = new Entity(texturedModel, new Vector3f(0,0,-25),0,160,0,1);
        Light light = new Light(new Vector3f(3000,2000,20),new Vector3f(1,1,1));
        Camera camera = new Camera();
        MasterRenderer renderer = new MasterRenderer();
        
        while(!Display.isCloseRequested()){
        	//entity.increasePosition(0, 0, -0.001f);
        	entity.increaseRotation(0, 1, 0);
        	camera.move();
        	renderer.processEntity(entity);
        	renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
