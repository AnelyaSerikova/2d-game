package com.nellie.game.Sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.nellie.game.Main;
import com.nellie.game.Scenes.Hud;
import com.nellie.game.Screens.PlayScreen;
import com.nellie.game.Sprites.Mario;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(Main.BRICK_BIT);
    }

    private void setCategoryFilter(short brickBit) {
    }

    // метод, вызываемый при ударе Марио по блоку сверху
    @Override
    public void onHeadHit(Mario mario) {
        // проверяем, является ли марио большим
        if(mario.isBig()) {
            // если да, меняем категорию фильтра на уничтоженный
            setCategoryFilter(Main.DESTROYED_BIT);
            // убираем плитку блока (разрушаем его)
            getCell().setTile(null);
            // увеличиваем счет на 200 очков
            Hud.addScore(200);
            // проигрываем звук разрушения блока
            Main.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        // если Марио не большой, проигрываем звук удара по блоку
        Main.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }
}
