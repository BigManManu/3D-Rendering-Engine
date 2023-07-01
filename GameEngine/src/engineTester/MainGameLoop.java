package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TextureModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.EntityRenderer;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

    public static void main(String[] args) throws LWJGLException {
        DisplayManager.createDisplay();
        Loader loader = new Loader();  
        
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
        		gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        
        RawModel model = OBJLoader.loadOBJModel("tree", loader);
        RawModel tree = OBJLoader.loadOBJModel("lowPolyTree", loader);
        RawModel grass = OBJLoader.loadOBJModel("grassModel", loader);
        RawModel fern = OBJLoader.loadOBJModel("fern", loader);
        
        TextureModel texturedModel = new TextureModel(model, new ModelTexture(loader.loadTexture("tree")));
        TextureModel texturedTree = new TextureModel(tree, new ModelTexture(loader.loadTexture("lowPolyTree")));
        TextureModel texturedGrass = new TextureModel(grass, new ModelTexture(loader.loadTexture("grassTexture")));
        
        texturedGrass.getTexture().setHasTransparency(true);
        texturedGrass.getTexture().setUseFakeLighting(true);
        ModelTexture fernTexAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTexAtlas.setNumberOfRows(2);
        TextureModel texturedFern = new TextureModel(fern, fernTexAtlas);
        texturedFern.getTexture().setHasTransparency(true);
        
        List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		Light light = new Light(new Vector3f(3000,2000,2000),new Vector3f(1,1,1));
        Terrain terrain = new Terrain(0,-1,loader,texturePack,blendMap, "heightmap");
        
		for(int i=0;i<500;i++){
			if(i % 20 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * - 600;
				float y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(texturedFern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360,
						0, 0.9f));
			}
			if(i % 5 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * - 600;
				float y = terrain.getHeightOfTerrain(x, z) - 1;
				entities.add(new Entity(texturedModel, new Vector3f(x, y, z), 0, 0, 0, random
						.nextFloat()* 1 + 4));
				x = random.nextFloat() * 800;
				z = random.nextFloat() * - 600;
				y = terrain.getHeightOfTerrain(x, z) - 1;
				entities.add(new Entity(texturedTree, new Vector3f(x, y, z), 0, random.nextFloat() * 360,
						0, random.nextFloat() * 0.1f + 0.6f));
			}
		}
        
        
        
        RawModel playerModel = OBJLoader.loadOBJModel("person", loader);
        TextureModel texturedPlayer = new TextureModel(playerModel, new ModelTexture(loader.loadTexture("playerTexture")));
        Player player = new Player(texturedPlayer, new Vector3f(100, 0, -50), 0, 180, 0, 0.6f);
        Camera camera = new Camera(player);
        
        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("health"), new Vector2f(-0.65f, -0.80f), new Vector2f(0.25f, 0.25f));
        guis.add(gui);
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        
        MasterRenderer renderer = new MasterRenderer();
        while(!Display.isCloseRequested()){
        	//entity.increasePosition(0, 0, -0.001f);
        	//entity.increaseRotation(0, 0, 0);
        	camera.move();
        	player.move(terrain);
        	renderer.processEntity(player);
        	renderer.processTerrain(terrain);
        	for(Entity entity:entities){
				renderer.processEntity(entity);
			}
        	
        	renderer.render(light, camera);
        	//guiRenderer.render(guis);
            DisplayManager.updateDisplay();
        }
        //guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
