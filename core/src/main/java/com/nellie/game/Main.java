package com.nellie.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nellie.game.Screens.PlayScreen;

// Основной класс игры, который наследует Game и управляет экранами.
public class Main extends Game {
    // Ширина и высота виртуального экрана в пикселях.
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;

    // Пикселей на метр для физики.
    public static final float PPM = 100;

    // Объект для рендеринга спрайтов.
    public SpriteBatch batch;

    // Флаги для определения столкновений объектов.
    public static final short NOTHING_BIT = 0;
    public static final short GROUND_BIT = 1;
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short OBJECT_BIT = 32;
    public static final short ENEMY_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;
    public static final short ITEM_BIT = 256;
    public static final short MARIO_HEAD_BIT = 512;
    public static final short FIREBALL_BIT = 1024;

    // Менеджер для загрузки и управления ресурсами (текстурами, звуками).
    public static AssetManager manager;

    @Override
    public void create () {
        // Создаем объект для рендеринга спрайтов.
        batch = new SpriteBatch();

        // Инициализируем менеджер активов.
        manager = new AssetManager();

        // Загружаем музыку и звуки.
        manager.load("audio/music/mario_music.ogg", Music.class);
        manager.load("audio/sounds/coin.wav", Sound.class);
        manager.load("audio/sounds/bump.wav", Sound.class);
        manager.load("audio/sounds/breakblock.wav", Sound.class);
        manager.load("audio/sounds/powerup_spawn.wav", Sound.class);
        manager.load("audio/sounds/powerup.wav", Sound.class);
        manager.load("audio/sounds/powerdown.wav", Sound.class);
        manager.load("audio/sounds/stomp.wav", Sound.class);
        manager.load("audio/sounds/mariodie.wav", Sound.class);

        // Ждем, пока все ресурсы загрузятся.
        manager.finishLoading();

        // Устанавливаем начальный экран игры.
        setScreen(new PlayScreen(this));
    }

    @Override
    public void dispose() {
        // Освобождаем ресурсы при завершении игры.
        super.dispose();
        manager.dispose();
        batch.dispose();
    }

    @Override
    public void render () {
        // Обновляем и рисуем текущий экран.
        super.render();
    }
}

