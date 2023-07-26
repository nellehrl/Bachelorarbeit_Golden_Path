package com.mygdx.dijkstra;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;


public class DijkstraAlgorithm extends Game {

	public SpriteBatch batch;
	public BitmapFont font;
	int offset = 25, vertices, space = 15, mangos = 30;
	public Skin mySkin;
	public Skin fontSkin;
	public ArrayList<City> allCities = new ArrayList<>();
	public ArrayList<City> northWest = new ArrayList<>();
	public ArrayList<City> southWest = new ArrayList<>();
	public ArrayList<City> northMid = new ArrayList<>();
	public ArrayList<City> southMid = new ArrayList<>();
	public ArrayList<City> northEast = new ArrayList<>();
	public ArrayList<City> southEast = new ArrayList<>();
	public ArrayList<City> cities = new ArrayList<>();
	public double currentLevel;
	int city;
	public Music backGroundMusic;
	AssetManager assetManager;

	boolean firstOpened = true;

	public void create() {

		assetManager = new AssetManager();
		assetManager.load("background.png", Texture.class);
		assetManager.load("bucket.png", Texture.class);
		assetManager.load("mainMenuScreen.png", Texture.class);
		assetManager.load("mango.png", Texture.class);
		assetManager.load("mangoCounter.png", Texture.class);
		assetManager.load("map.png", Texture.class);
		assetManager.load("parrott.png", Texture.class);
		assetManager.load("port.png", Texture.class);
		assetManager.load("shadow.png", Texture.class);
		assetManager.load("ship.png", Texture.class);
		assetManager.load("shipWreck.png", Texture.class);
		assetManager.load("transparent.png", Texture.class);
		assetManager.load("treasure.png", Texture.class);
		assetManager.load("triangle.png", Texture.class);
		assetManager.load("white 1.png", Texture.class);
		assetManager.load("levelWon.png", Texture.class);
		assetManager.load("gameWon.png", Texture.class);
		assetManager.load("worldMap 1.png", Texture.class);
		assetManager.load("box.png", Texture.class);
		assetManager.load("battle.wav", Sound.class);
		assetManager.load("yesss.wav", Sound.class);
		assetManager.load("ambiente.wav", Sound.class);
		assetManager.load("pirates.mp3", Music.class);
		assetManager.finishLoading();

		createCities(allCities);
		currentLevel = 1.1;

		batch = new SpriteBatch();
		font = new BitmapFont(); // use libGDX's default Arial font
		mySkin = new Skin(Gdx.files.internal("quantum-horizon/skin/quantum-horizon-ui.json"));
		fontSkin = new Skin(Gdx.files.internal("neon/skin/neon-ui.json"));

		backGroundMusic = assetManager.get("pirates.mp3", Music.class);
		backGroundMusic.setLooping(true);
		backGroundMusic.play();

		int widthWorld = (int) (Gdx.graphics.getWidth() * 0.725);
		int heightWorld = Gdx.graphics.getHeight() + Gdx.graphics.getHeight()/3;
		for(City city : allCities){
			if(city.x <= widthWorld/3&& city.y >= heightWorld/2){
			northWest.add(city);
			}
			else if(city.x <= widthWorld*0.66 && city.x>widthWorld/3 && city.y >= heightWorld/2){
				northMid.add(city);
			}
			else if(city.x >= widthWorld*0.66 && city.y >= heightWorld/2){
				northEast.add(city);
			}
			else if(city.x <= widthWorld/3 && city.y <= heightWorld/2){
				southWest.add(city);
			}
			else if(city.x <= widthWorld*0.66 && city.x>=widthWorld/3 && city.y <= heightWorld/2){
				southMid.add(city);
			}
			else if(city.x >= widthWorld*0.66 && city.y <= heightWorld/2){
				southEast.add(city);
			}
		}

		for (int i = 0; i < 6; i++) {
			if(i==0){
				city = (int) (Math.random() * northWest.size());
				cities.add(northWest.get(city));
			}
			else if(i==1){
				city = (int) (Math.random() * southWest.size());
				cities.add(southWest.get(city));
			}
			else if(i==2){
				city = (int) (Math.random() * southMid.size());
				cities.add(southMid.get(city));
			}
			else if(i==3){
				city = (int) (Math.random() * northMid.size());
				cities.add(northMid.get(city));
			}
			else if(i==4){
				city = (int) (Math.random() * northEast.size());
				cities.add(northEast.get(city));
			}
			else {
				city = (int) (Math.random() * southEast.size());
				cities.add(southEast.get(city));
			}
		}

		for(int i = 0; i < cities.size(); i++){
			City city = cities.get(i);
			city.setX((int) (city.x*1.3) + 50);
		}

		vertices = cities.size();

		this.setScreen(new MainMenuScreen(this, currentLevel));
	}

	public void render() {
		assetManager.update();
		super.render(); // important!
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
	}

	static void createCities(ArrayList<City> cities) {
		cities.add(new City("Shanghai", (int) (467*1.5), (int) (330*1.5),"SH"));
		cities.add(new City("Singapore", (int) (448*1.5), (int) (290*1.5), "SP"));
		cities.add(new City("Rotterdam", (int) (280*1.5), (int) (360*1.5), "RD"));
		cities.add(new City("Bergen", (int) (280*1.5), (int) (390*1.5), "BG"));
		cities.add(new City("Jebel Ali", (int) (352*1.5), (int) (325*1.5), "JA"));
		cities.add(new City("Los Angeles", (int) (110*1.5), (int) (350*1.5), "LA"));
		cities.add(new City("New York", (int) (184*1.5), (int) (360*1.5),"NY"));
		cities.add(new City("Colombo", (int) (395*1.5), (int) (290*1.5),"CB"));
		cities.add(new City("Colon", (int) (160*1.5), (int) (295*1.5),"CL"));
		cities.add(new City("Santos", (int) (225*1.5), (int) (250*1.5),"ST"));
		cities.add(new City("Buenos Aires", (int) (200*1.5), (int) (210*1.5),"BA"));
		cities.add(new City("Antisarana", (int) (351*1.5), (int) (255*1.5),"AS"));
		cities.add(new City("Banjul", (int) (261*1.5), (int) (305*1.5),"BJ"));
		cities.add(new City("Portland", (int) (482*1.5), (int) (210*1.5),"PL"));
		cities.add(new City("Wyndham", (int) (453*1.5), (int) (250*1.5),"WH"));
		cities.add(new City("Lima", (int) (167*1.5), (int) (250*1.5),"LM"));
	}

}
