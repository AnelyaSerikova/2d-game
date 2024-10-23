package com.nellie.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nellie.game.Main;
import com.nellie.game.Scenes.Hud;
import com.nellie.game.Sprites.Enemies.Enemy;
import com.nellie.game.Sprites.Items.Item;
import com.nellie.game.Sprites.Items.ItemDef;
import com.nellie.game.Sprites.Items.Mushroom;
import com.nellie.game.Sprites.Mario;
import com.nellie.game.Tools.B2WorldCreator;
import com.nellie.game.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
    private Main game;
    private TextureAtlas atlas;
    public static boolean alreadyDestroyed = false;

    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    private Mario player;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;


    public PlayScreen(Main game){
        atlas = new TextureAtlas("Mario_and_Enemies.pack");  // Загружаем текстуры для спрайтов.

        this.game = game;  // Инициализируем ссылку на основной объект игры.
        gamecam = new OrthographicCamera();  // Создаем камеру.

        gamePort = new FitViewport(Main.V_WIDTH / Main.PPM, Main.V_HEIGHT / Main.PPM, gamecam);  // Подстраиваем вид экрана под разные разрешения.

        hud = new Hud(game.batch);  // Инициализируем HUD.

        maploader = new TmxMapLoader();  // Загружаем карту.
        map = maploader.load("level1.tmx");  // Загружаем конкретную карту.
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Main.PPM);  // Инициализируем рендерер для отрисовки карты.

        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);  // Камера следит за центром экрана.

        world = new World(new Vector2(0, -10), true);  // Создаем мир с гравитацией (направление вниз).
        b2dr = new Box2DDebugRenderer();  // Инициализируем отладочный рендерер для Box2D.

        creator = new B2WorldCreator(this);  // Создаем мир с физическими объектами (например, стены, враги).

        player = new Mario(this);  // Инициализируем игрока.

        world.setContactListener(new WorldContactListener());  // Устанавливаем слушателя для обработки столкновений.

        music = Main.manager.get("audio/music/mario_music.ogg", Music.class);  // Загружаем фоновую музыку.
        music.setLooping(true);  // Музыка будет воспроизводиться в цикле.
        music.setVolume(0.3f);  // Устанавливаем громкость музыки.

        items = new Array<Item>();  // Инициализируем массив предметов.
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();  // Очередь для предметов, которые нужно создать.
    }

    // Метод для добавления предметов для спауна.
    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);  // Добавляем предмет в очередь на спаун.
    }


    // Метод для обработки спауна предметов.
    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {  // Если очередь не пуста, создаем предметы.
            ItemDef idef = itemsToSpawn.poll();  // Извлекаем предмет из очереди.
            if (idef.type == Mushroom.class) {  // Если это гриб, создаем объект гриба.
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }


    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {


    }

    // Метод для обработки ввода игрока.
    public void handleInput(float dt) {
        if (player.currentState != Mario.State.DEAD) {  // Если Марио не мертв:
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))  // Прыжок.
                player.jump();
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)  // Движение вправо.
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)  // Движение влево.
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))  // Стрельба.
                player.fire();
        }
    }

    // Метод для обновления логики игры.
    public void update(float dt) {
        handleInput(dt);  // Обработка ввода.
        handleSpawningItems();  // Обработка спауна предметов.

        world.step(1 / 60f, 6, 2);  // Шаг физики (60 кадров в секунду).

        player.update(dt);  // Обновляем игрока.
        for (Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);  // Обновляем всех врагов.
            if (enemy.getX() < player.getX() + 224 / Main.PPM) {  // Активируем врагов, когда они приближаются к игроку.
                enemy.b2body.setActive(true);
            }
        }

        for (Item item : items)
            item.update(dt);  // Обновляем все предметы.

        hud.update(dt);  // Обновляем HUD.

        // Камера следует за игроком.
        if (player.currentState != Mario.State.DEAD) {
            gamecam.position.x = player.b2body.getPosition().x;
        }

        gamecam.update();  // Обновляем камеру.
        renderer.setView(gamecam);  // Обновляем рендерер с учетом новой позиции камеры.
    }


    // Метод для отрисовки экрана.
    @Override
    public void render(float delta) {
        update(delta);  // Обновляем игру.

        // Проверка условия конца игры
        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
            return;  // Остановим дальнейшую отрисовку текущего экрана.
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);  // Очищаем экран.
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();  // Рендерим карту.
        b2dr.render(world, gamecam.combined);  // Рендерим отладку Box2D.

        game.batch.setProjectionMatrix(gamecam.combined);  // Настроим спрайт-батч на отрисовку в мировых координатах.
        game.batch.begin();  // Начинаем отрисовку спрайтов.
        player.draw(game.batch);  // Отображаем игрока.

        for (Item item : items)
            item.draw(game.batch);  // Отображаем все предметы.

        for (Enemy enemy : creator.getEnemies())
            enemy.draw(game.batch);  // Отображаем всех врагов.

        game.batch.end();  // Завершаем отрисовку.

        hud.stage.draw();  // Отображаем HUD.
    }


    // Метод для проверки условий окончания игры (Game Over)
    public boolean gameOver(){
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);

    }

    public TiledMap getMap(){
        return map;
    }
    public World getWorld(){
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public Hud getHud(){ return hud; }
}
