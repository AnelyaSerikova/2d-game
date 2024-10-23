package com.nellie.game.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.nellie.game.Main;
import com.nellie.game.Screens.PlayScreen;
import com.nellie.game.Sprites.Mario;

public class Turtle extends Enemy {
    // Константы для направления пинка
    public static final int KICK_LEFT = -2;
    public static final int KICK_RIGHT = 2;

    // Перечисление для состояний черепахи
    public enum State {WALKING, MOVING_SHELL, STANDING_SHELL}
    public State currentState;   // Текущее состояние черепахи
    public State previousState;  // Предыдущее состояние черепахи

    private float stateTime;  // Время для анимации
    private Animation walkAnimation;  // Анимация для движения
    private Array<TextureRegion> frames;  // Массив фреймов для анимации
    private TextureRegion shell;  // Текстура для раковины черепахи
    private boolean setToDestroy;  // Флаг для уничтожения (не используется)
    private boolean destroyed;  // Флаг для проверки, уничтожена ли черепаха

    // Конструктор для черепахи
    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);  // Вызов конструктора родительского класса

        // Создаем массив фреймов для анимации движения
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16, 24));

        // Текстура для раковины черепахи
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24);

        // Создаем анимацию для движения
        walkAnimation = new Animation(0.2f, frames);
        currentState = previousState = State.WALKING;  // Устанавливаем начальное состояние

        setBounds(getX(), getY(), 16 / Main.PPM, 24 / Main.PPM);  // Устанавливаем размеры спрайта
    }

    // Метод для определения физического тела черепахи
    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());  // Устанавливаем позицию
        bdef.type = BodyDef.BodyType.DynamicBody;  // Устанавливаем тип тела как динамическое
        b2body = world.createBody(bdef);  // Создаем физическое тело

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();  // Устанавливаем форму коллизии как круг
        shape.setRadius(6 / Main.PPM);  // Радиус формы
        fdef.filter.categoryBits = Main.ENEMY_BIT;  // Устанавливаем категорию
        fdef.filter.maskBits = Main.GROUND_BIT |
            Main.COIN_BIT |
            Main.BRICK_BIT |
            Main.ENEMY_BIT |
            Main.OBJECT_BIT |
            Main.MARIO_BIT;  // Маска для взаимодействий

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);  // Создаем фикстуру для тела

        // Создаем голову черепахи для обработки столкновений
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1 / Main.PPM);
        vertice[1] = new Vector2(5, 8).scl(1 / Main.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / Main.PPM);
        vertice[3] = new Vector2(3, 3).scl(1 / Main.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 1.8f;  // Коэффициент упругости
        fdef.filter.categoryBits = Main.ENEMY_HEAD_BIT;  // Устанавливаем категорию для столкновений с Марио сверху
        b2body.createFixture(fdef).setUserData(this);  // Создаем фикстуру для головы
    }

    // Метод для получения текущего фрейма анимации
    public TextureRegion getFrame(float dt) {
        TextureRegion region;

        switch (currentState) {
            case MOVING_SHELL:
            case STANDING_SHELL:
                region = shell;  // Если в раковине, используем текстуру раковины
                break;
            case WALKING:
            default:
                region = (TextureRegion) walkAnimation.getKeyFrame(stateTime, true);  // Анимация движения
                break;
        }

        // Флипаем изображение в зависимости от направления движения
        if (velocity.x > 0 && region.isFlipX() == false) {
            region.flip(true, false);
        }
        if (velocity.x < 0 && region.isFlipX() == true) {
            region.flip(true, false);
        }

        // Обновляем время для анимации
        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;  // Обновляем предыдущее состояние
        return region;  // Возвращаем текущий кадр анимации
    }

    // Метод для обновления черепахи (движение и анимация)
    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));  // Устанавливаем текущий кадр анимации

        // Если черепаха стоит в раковине больше 5 секунд, она "просыпается"
        if (currentState == State.STANDING_SHELL && stateTime > 5) {
            currentState = State.WALKING;  // Возвращаемся к состоянию WALKING
            velocity.x = 1;  // Устанавливаем скорость
            System.out.println("WAKE UP SHELL");
        }

        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - 8 / Main.PPM);  // Обновляем позицию
        b2body.setLinearVelocity(velocity);  // Устанавливаем скорость
    }

    // Обработка удара по черепахе сверху
    @Override
    public void hitOnHead(Mario mario) {
        if (currentState == State.STANDING_SHELL) {
            // Если Марио пинает черепаху из правой стороны, черепаха идет влево, и наоборот
            if (mario.b2body.getPosition().x > b2body.getPosition().x) {
                velocity.x = -2;  // Кидаем влево
            } else {
                velocity.x = 2;  // Кидаем вправо
            }
            currentState = State.MOVING_SHELL;  // Черепаха начинает двигаться как раковина
            System.out.println("Set to moving shell");
        } else {
            currentState = State.STANDING_SHELL;  // Переворачиваем черепаху в раковину
            velocity.x = 0;  // Останавливаем движение
        }
    }

    // Обработка удара черепахой по другому врагу
    @Override
    public void hitByEnemy(Enemy enemy) {
        reverseVelocity(true, false);  // Меняем направление движения
    }

    // Метод для пинка черепахи в определенную сторону
    public void kick(int direction) {
        velocity.x = direction;  // Устанавливаем скорость в заданном направлении
        currentState = State.MOVING_SHELL;  // Переводим черепаху в состояние раковины
    }
}
