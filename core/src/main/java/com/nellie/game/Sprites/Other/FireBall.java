package com.nellie.game.Sprites.Other;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.nellie.game.Main;
import com.nellie.game.Screens.PlayScreen;

public class FireBall extends Sprite {
    // поля для хранения ссылок на экран игры, мир и состояние огненного шара
    PlayScreen screen;
    World world;
    Array<TextureRegion> frames; // для хранения кадров анимации огненного шара
    Animation fireAnimation;     // анимация огненного шара
    float stateTime;             // время состояния (для анимации)
    boolean destroyed;           // отслеживает, был ли уничтожен огненный шар
    boolean setToDestroy;        // указывает, что огненный шар помечен для уничтожения
    boolean fireRight;           // определяет направление движения огненного шара

    // физическое тело огненного шара в Box2D
    Body b2body;

    public FireBall(PlayScreen screen, float x, float y, boolean fireRight){
        this.fireRight = fireRight;
        this.screen = screen;
        this.world = screen.getWorld();

        // создание массива для кадров анимации огненного шара
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 4; i++){
            // извлечение 4 кадров из спрайт-листа (каждый кадр 8x8 пикселей)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("fireball"), i * 8, 0, 8, 8));
        }

        // создание анимации с длительностью кадра 0.2 секунды
        fireAnimation = new Animation(0.2f, frames);
        // установка начального региона (первый кадр анимации)
        setRegion((Texture) fireAnimation.getKeyFrame(0));
        // установка позиции и размера огненного шара (масштабированного под физическую систему игры)
        setBounds(x, y, 6 / Main.PPM, 6 / Main.PPM);

        // определение физических свойств огненного шара
        defineFireBall();
    }

    // определение физических свойств шара
    public void defineFireBall(){
        // определение тела шара
        BodyDef bdef = new BodyDef();
        // установка позиции движется ли огненный шар вправо или влево
        bdef.position.set(fireRight ? getX() + 12 /Main.PPM : getX() - 12 /Main.PPM, getY());
        bdef.type = BodyDef.BodyType.DynamicBody; // делаем огненный шар динамическим телом (может двигаться и взаимодействовать с физикой)

        // создание тела в мире, если мир не заблокирован (нет одновременной симуляции физики)
        if(!world.isLocked())
            b2body = world.createBody(bdef);

        // определение физических свойств огненного шара (форма, правила столкновений и т.д.)
        FixtureDef fdef = new FixtureDef();
        // создание круглой формы для огненного шара (радиус 3 пикселя)
        CircleShape shape = new CircleShape();
        shape.setRadius(3 / Main.PPM);

        // установка категории столкновений и маски для определения с чем может столкнуться шар
        fdef.filter.categoryBits = Main.FIREBALL_BIT;
        fdef.filter.maskBits = Main.GROUND_BIT |
            Main.COIN_BIT |
            Main.BRICK_BIT |
            Main.ENEMY_BIT |
            Main.OBJECT_BIT;

        // применение формы к Fixture
        fdef.shape = shape;
        fdef.restitution = 1; // огненный шар будет отскакивать от поверхностей
        fdef.friction = 0;    // нет трения (огненный шар не замедляется при столкновении)

        // применяем Fixture к телу огненного шара и устанавливаем пользовательские данные (сам огненный шар)
        b2body.createFixture(fdef).setUserData(this);

        // устанавливаем начальную скорость огненного шара (движение по x и y)
        b2body.setLinearVelocity(new Vector2(fireRight ? 2 : -2, 2.5f));
    }

    // анимация и движение огненного шара
    public void update(float dt){
        // увеличиваем время состояния (для анимации)
        stateTime += dt;
        // устанавливаем текущий регион огненного шара для анимации
        setRegion((Texture) fireAnimation.getKeyFrame(stateTime, true));

        // обновляем позицию огненного шара на основе его физического тела
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

        // если огненный шар находится в игре более 3 секунд или помечен для уничтожения,
        // уничтожаем его физическое тело и помечаем как уничтоженный.
        if((stateTime > 3 || setToDestroy) && !destroyed) {
            world.destroyBody(b2body); // Удаляем тело огненного шара из мира
            destroyed = true;          // Помечаем огненный шар как уничтоженный
        }

        // если огненный шар двигается вверх слишком быстро, ограничиваем его вертикальную скорость,
        // чтобы не улететь слишком высоко
        if(b2body.getLinearVelocity().y > 2f)
            b2body.setLinearVelocity(b2body.getLinearVelocity().x, 2f);

        // если скорость огненного шара по оси x противоположна его предполагаемому движению,
        // помечаем его для уничтожения.
        if((fireRight && b2body.getLinearVelocity().x < 0) || (!fireRight && b2body.getLinearVelocity().x > 0))
            setToDestroy();  // помечаем огненный шар для уничтожения, если он движется в неправильном направлении
    }

    // помечает огненный шар для уничтожения
    public void setToDestroy(){
        setToDestroy = true;
    }

    // возвращает, был ли огненный шар уничтожен
    public boolean isDestroyed(){
        return destroyed;
    }
}
