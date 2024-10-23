package com.nellie.game.Sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.nellie.game.Main;
import com.nellie.game.Scenes.Hud;
import com.nellie.game.Screens.PlayScreen;
import com.nellie.game.Sprites.Items.ItemDef;
import com.nellie.game.Sprites.Items.Mushroom;
import com.nellie.game.Sprites.Mario;

public class Coin extends InteractiveTileObject {
    // Набор плиток для карты
    private static TiledMapTileSet tileSet;
    // Идентификатор пустой монеты (монета, которая была собрана)
    private final int BLANK_COIN = 28;

    // Конструктор класса Coin
    public Coin(PlayScreen screen, MapObject object){
        super(screen, object);  // Вызываем конструктор родительского класса
        tileSet = map.getTileSets().getTileSet("tileset_gutter");  // Получаем набор плиток для карты
        fixture.setUserData(this);  // Устанавливаем данные для физического взаимодействия с объектом
        setCategoryFilter(Main.COIN_BIT);  // Устанавливаем фильтр для категорий объектов
    }

    // Метод для установки категории фильтра для физического взаимодействия (настраивает взаимодействие с другими объектами)
    private void setCategoryFilter(short coinBit) {
        // Этот метод можно дополнить, чтобы установить фильтр для объектов (например, монеты)
    }

    // Метод, вызываемый при ударе по монете (например, при прыжке персонажа)
    @Override
    public void onHeadHit(Mario mario) {
        // Проверяем, является ли плитка пустой монетой (BLANK_COIN)
        if(getCell().getTile().getId() == BLANK_COIN) {
            // Если да, проигрываем звук удара по блоку
            Main.manager.get("audio/sounds/bump.wav", Sound.class).play();
        } else {
            // Если это обычная монета, проверяем, есть ли свойство "mushroom" (например, если объект связан с грибом)
            if(object.getProperties().containsKey("mushroom")) {
                // Если есть, спауним гриб
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / Main.PPM),
                    Mushroom.class));
                // Проигрываем звук спауна предмета
                Main.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            } else {
                // В противном случае проигрываем звук обычной монеты
                Main.manager.get("audio/sounds/coin.wav", Sound.class).play();
            }
            // Заменяем плитку монеты на пустую плитку (BLANK_COIN)
            getCell().setTile(tileSet.getTile(BLANK_COIN));
            // Увеличиваем счет игрока на 100
            Hud.addScore(100);
        }
    }
}
