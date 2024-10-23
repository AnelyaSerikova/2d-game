package com.nellie.game.Sprites.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.nellie.game.Main;
import com.nellie.game.Screens.PlayScreen;
import com.nellie.game.Sprites.Mario;

public class Mushroom extends Item {
    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);  // Вызов конструктора родительского класса Item для инициализации экрана и координат
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);  // Устанавливаем изображение гриба
        velocity = new Vector2(0.7f, 0);  // Устанавливаем начальную скорость гриба по оси X (движется вправо)
    }

    // Метод для определения физического тела гриба в Box2D
    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();  // Создаем новый объект BodyDef для определения физического тела
        bdef.position.set(getX(), getY());  // Устанавливаем позицию гриба в игровом мире
        bdef.type = BodyDef.BodyType.DynamicBody;  // Определяем тип тела как динамическое, чтобы оно могло двигаться
        body = world.createBody(bdef);  // Создаем физическое тело в мире

        FixtureDef fdef = new FixtureDef();  // Создаем объект FixtureDef для определения формы и свойств физического объекта
        CircleShape shape = new CircleShape();  // Создаем круговую форму (подходит для гриба)
        shape.setRadius(6 / Main.PPM);  // Устанавливаем радиус круга в пикселях на метр (Main.PPM - пиксели на метр)

        // Устанавливаем маски фильтров, чтобы тело взаимодействовало только с определенными типами объектов
        fdef.filter.categoryBits = Main.ITEM_BIT;  // Устанавливаем категорию гриба как ITEM_BIT
        fdef.filter.maskBits = Main.MARIO_BIT |
            Main.OBJECT_BIT |
            Main.GROUND_BIT |
            Main.COIN_BIT |
            Main.BRICK_BIT;  // Гриб может столкнуться с персонажем Mario, объектами, землей, монетами и кирпичами

        fdef.shape = shape;  // Применяем форму к физическому объекту
        body.createFixture(fdef).setUserData(this);  // Создаем фикстуру для тела и связываем ее с данным объектом (grub)
    }

    // Метод для использования гриба персонажем Mario
    @Override
    public void use(Mario mario) {
        destroy();  // Удаляем гриб из мира
        mario.grow();  // Персонаж Mario становится больше (увеличивается в размере)
    }

    // Метод для обновления состояния гриба
    @Override
    public void update(float dt) {
        super.update(dt);  // Вызов метода обновления родительского класса (Item), чтобы управлять уничтожением
        // Устанавливаем позицию гриба в зависимости от позиции его физического тела
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        velocity.y = body.getLinearVelocity().y;  // Поддерживаем вертикальную скорость гриба как у его физического тела
        body.setLinearVelocity(velocity);  // Обновляем скорость гриба
    }
}
