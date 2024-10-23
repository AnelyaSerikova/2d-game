package com.nellie.game.Sprites.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.nellie.game.Main;
import com.nellie.game.Screens.PlayScreen;
import com.nellie.game.Sprites.Mario;

public abstract class InteractiveTileObject {
    protected World world;  // Мир Box2D, в котором происходит физическое взаимодействие
    protected TiledMap map;  // Карта уровня (TiledMap)
    protected Rectangle bounds;  // Границы объекта (прямоугольник)
    protected Body body;  // Физическое тело объекта
    protected PlayScreen screen;  // Экран, на котором находится объект
    protected MapObject object;  // Карта-объект для связывания с физическим телом

    protected Fixture fixture;  // Оболочка, определяющая физические взаимодействия объекта

    // Конструктор, который принимает экран и объект карты для создания физического тела
    public InteractiveTileObject(PlayScreen screen, MapObject object){
        this.object = object;
        this.screen = screen;
        this.world = screen.getWorld();  // Получаем доступ к миру физики
        this.map = screen.getMap();  // Получаем доступ к карте уровня
        this.bounds = ((RectangleMapObject) object).getRectangle();  // Получаем размеры объекта на карте

        // Создаем тело Box2D
        BodyDef bdef = new BodyDef();  // Определяем тело
        FixtureDef fdef = new FixtureDef();  // Определяем оболочку
        PolygonShape shape = new PolygonShape();  // Определяем форму

        bdef.type = BodyDef.BodyType.StaticBody;  // Устанавливаем тип тела как статичное (не двигается)
        bdef.position.set((bounds.getX() + bounds.getWidth() / 2) / Main.PPM, (bounds.getY() + bounds.getHeight() / 2) / Main.PPM);  // Устанавливаем позицию

        // Создаем физическое тело
        body = world.createBody(bdef);

        // Создаем форму (прямоугольник) для взаимодействия с телом
        shape.setAsBox(bounds.getWidth() / 2 / Main.PPM, bounds.getHeight() / 2 / Main.PPM);
        fdef.shape = shape;  // Устанавливаем форму для оболочки
        fixture = body.createFixture(fdef);  // Применяем оболочку к телу
    }

    // Получает ячейку из слоя карты, где находится физическое тело объекта
    public TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);  // Получаем второй слой карты (предполагается, что это слой с объектами)
        return layer.getCell((int)(body.getPosition().x * Main.PPM / 16),
            (int)(body.getPosition().y * Main.PPM / 16));  // Возвращаем ячейку на основе позиции тела
    }

    // Абстрактный метод для обработки ударов по объекту (реализуется в наследующих классах)
    public abstract void onHeadHit(Mario mario);
}
