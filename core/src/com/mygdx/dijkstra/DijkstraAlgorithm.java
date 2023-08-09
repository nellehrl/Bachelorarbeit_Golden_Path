package com.mygdx.dijkstra;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.dijkstra.models.City;
import com.mygdx.dijkstra.screens.MainMenuScreen;

import java.util.ArrayList;

public class DijkstraAlgorithm extends Game {

    private SpriteBatch batch;
    private BitmapFont font;
    private int offset, vertices, space, mangos = 30;
    private ArrayList<City> northWest = new ArrayList<>(), northMid = new ArrayList<>(), northEast = new ArrayList<>(),
            southWest = new ArrayList<>(), southMid = new ArrayList<>(), southEast = new ArrayList<>();
    private Skin mySkin;
    private Skin fontSkin;
    private ArrayList<City> allCities;
    private ArrayList<City> cities;
    private int currentLevel;
    private Image blood, parrotImage, infoImage;
    private Music backGroundMusic;
    private AssetManager assetManager;
    private Sound dropSound, battle;
    private OrthographicCamera camera;
    private boolean firstOpened = true;
    private FitViewport fitViewport;

    public void create() {
        initCamera();
        initAssets();
        initUIElements();
        initCities();
        configureCities();
        this.setScreen(new MainMenuScreen(this, currentLevel));
    }

    public void render() {
        assetManager.update();
        super.render(); // important!
    }

    private void initCamera() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, camera.viewportWidth, camera.viewportHeight);
        fitViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
    }

    private void initUIElements() {
        batch = new SpriteBatch();

        font = new BitmapFont(); // use libGDX's default Arial font
        mySkin = new Skin(Gdx.files.internal("quantum-horizon/skin/quantum-horizon-ui.json"));
        fontSkin = new Skin(Gdx.files.internal("neon/skin/neon-ui.json"));

        backGroundMusic = assetManager.get("pirates.mp3", Music.class);
        backGroundMusic.setLooping(true);
        backGroundMusic.play();

        dropSound = assetManager.get("drop.wav", Sound.class);
        battle = assetManager.get("battle.wav", Sound.class);

        blood = new Image(assetManager.get("blood.png", Texture.class));
        blood.setSize(camera.viewportWidth, camera.viewportHeight);
        blood.setPosition(0, 0);

        parrotImage = new Image(assetManager.get("parrott.png", Texture.class));
        infoImage = new Image(assetManager.get("info.png", Texture.class));

        currentLevel = 1;
        space = (int) (camera.viewportWidth / 100);
        offset = (int) (camera.viewportWidth / 50);
    }

    public void configureCities() {
        int widthWorld = (int) (camera.viewportWidth * 0.725);
        int heightWorld = (int) (camera.viewportHeight * 0.66);
        int midHeightWorld = (int) (heightWorld / 2 + camera.viewportHeight / 3);

        for (City city : allCities) {
            if (city.getX() <= widthWorld / 3 && city.getY() >= midHeightWorld) {
                northWest.add(city);
            } else if (city.getX() <= widthWorld * 0.66 && city.getX() > widthWorld / 3 && city.getY() >= midHeightWorld) {
                northMid.add(city);
            } else if (city.getX() >= widthWorld * 0.66 && city.getY() >= midHeightWorld) {
                northEast.add(city);
            } else if (city.getX() <= widthWorld / 3 && city.getY() <= midHeightWorld) {
                southWest.add(city);
            } else if (city.getX() <= widthWorld * 0.66 && city.getX() >= widthWorld / 3 && city.getY() <= midHeightWorld) {
                southMid.add(city);
            } else if (city.getX() >= widthWorld * 0.66 && city.getY() <= midHeightWorld) {
                southEast.add(city);
            }
        }

        generateRandomCities();
        for (City city : cities) city.setX((int) (city.getX() * 1.3 + camera.viewportWidth * 0.0416));
        vertices = cities.size();
    }

    private void generateRandomCities() {
        int cityIndex = 0;
        cities = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                cityIndex = (int) (Math.random() * northWest.size());
                cities.add(northWest.get(cityIndex));
            } else if (i == 1) {
                cityIndex = (int) (Math.random() * southWest.size());
                cities.add(southWest.get(cityIndex));
            } else if (i == 2) {
                cityIndex = (int) (Math.random() * southMid.size());
                cities.add(southMid.get(cityIndex));
            } else if (i == 3) {
                cityIndex = (int) (Math.random() * northMid.size());
                cities.add(northMid.get(cityIndex));
            } else if (i == 4) {
                cityIndex = (int) (Math.random() * northEast.size());
                cities.add(northEast.get(cityIndex));
            } else {
                cityIndex = (int) (Math.random() * southEast.size());
                cities.add(southEast.get(cityIndex));
            }
        }
    }

    private void initCities() {
        allCities = new ArrayList<>();
        allCities.add(new City("Shanghai", calcX(0.58), calcY(525), "SH"));
        allCities.add(new City("Singapore", calcX(0.56), calcY(435), "SP"));
        allCities.add(new City("Rotterdam", calcX(0.35), calcY(555), "RD"));
        allCities.add(new City("Bergen", calcX(0.37), calcY(600), "BG"));
        allCities.add(new City("Jebel Ali", calcX(0.44), calcY(487), "JA"));
        allCities.add(new City("Los Angeles", calcX(0.1375), calcY(525), "LA"));
        allCities.add(new City("New York", calcX(0.23), calcY(540), "NY"));
        allCities.add(new City("Colombo", calcX(0.493), calcY(435), "CB"));
        allCities.add(new City("Colon", calcX(0.2), calcY(412), "CL"));
        allCities.add(new City("Santos", calcX(0.268), calcY(360), "ST"));
        allCities.add(new City("Buenos Aires", calcX(0.225), calcY(300), "BA"));
        allCities.add(new City("Antisarana", calcX(0.4375), calcY(383), "AS"));
        allCities.add(new City("Banjul", calcX(0.3258), calcY(459), "BJ"));
        allCities.add(new City("Portland", calcX(0.6025), calcY(315), "PL"));
        allCities.add(new City("Wyndham", calcX(0.565), calcY(375), "WH"));
        allCities.add(new City("Lima", calcX(0.208), calcY(405), "LM"));
    }

    private void initAssets() {
        assetManager = new AssetManager();
        assetManager.load("background.png", Texture.class);
        assetManager.load("bucket.png", Texture.class);
        assetManager.load("mainMenuScreen.png", Texture.class);
        assetManager.load("mango.png", Texture.class);
        assetManager.load("mangoCounter.png", Texture.class);
        assetManager.load("map.png", Texture.class);
        assetManager.load("parrott.png", Texture.class);
        assetManager.load("port.png", Texture.class);
        assetManager.load("info.png", Texture.class);
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
        assetManager.load("blood.png", Texture.class);
        assetManager.load("box.png", Texture.class);
        assetManager.load("xClose.png", Texture.class);
        assetManager.load("battle.wav", Sound.class);
        assetManager.load("drop.wav", Sound.class);
        assetManager.load("ambiente.wav", Sound.class);
        assetManager.load("pirates.mp3", Music.class);
        assetManager.finishLoading();
    }

    private int calcY(int y) {
        return (int) (camera.viewportHeight * y / 720);
    }

    private int calcX(double x) {
        return (int) (camera.viewportWidth * x);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Skin getMySkin() {
        return mySkin;
    }

    public Skin getFontSkin() {
        return fontSkin;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public Image getBlood() {
        return blood;
    }

    public Image getParrotImage() {
        return parrotImage;
    }

    public Image getInfoImage() {
        return infoImage;
    }

    public Music getBackGroundMusic() {
        return backGroundMusic;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public Sound getDropSound() {
        return dropSound;
    }

    public Sound getBattle() {
        return battle;
    }

    public boolean isFirstOpened() {
        return firstOpened;
    }

    public void setFirstOpened(boolean firstOpened) {
        this.firstOpened = firstOpened;
    }

    public int getOffset() {
        return offset;
    }

    public int getVertices() {
        return vertices;
    }

    public int getSpace() {
        return space;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public BitmapFont getFont() {
        return font;
    }

    public FitViewport getFitViewport() {
        return fitViewport;
    }

    public int getMangos() {
        return mangos;
    }

    public void setMangos(int mangos) {
        this.mangos = mangos;
    }

    public Image createActor(int width, float height, float x, float y, Texture texture) {
        Image image = new Image(texture);
        image.setSize(width, height);
        image.setPosition(x, y);
        return image;
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
