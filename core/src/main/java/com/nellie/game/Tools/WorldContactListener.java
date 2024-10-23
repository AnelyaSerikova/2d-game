package com.nellie.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.nellie.game.Main;
import com.nellie.game.Sprites.Enemies.Enemy;
import com.nellie.game.Sprites.Items.Item;
import com.nellie.game.Sprites.Mario;
import com.nellie.game.Sprites.Other.FireBall;
import com.nellie.game.Sprites.TileObjects.InteractiveTileObject;

public class WorldContactListener implements ContactListener {
    // метод при столкновения между объектами
    @Override
    public void beginContact(Contact contact) {
        // физ объекты, которые столкнулись
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // категория столкновения
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // обработка разных типов категорий столкновений
        switch (cDef){
            // когда марио ударяет головой по кирпичу или монете
            case Main.MARIO_HEAD_BIT | Main.BRICK_BIT:
            case Main.MARIO_HEAD_BIT | Main.COIN_BIT:
                if(fixA.getFilterData().categoryBits == Main.MARIO_HEAD_BIT)
                    // если фиксА это марио, то взаимодействуем с фиксВ (кирпич или монета)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                else
                    // если фиксВ это марио, то взаимодействуем с фиксА (кирпич или монета)
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;

                // когда марио ударяет головой по врагу
            case Main.ENEMY_HEAD_BIT | Main.MARIO_BIT:
                // если фиксА это враг
                if(fixA.getFilterData().categoryBits == Main.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                else
                    // если фиксВ это враг
                    ((Enemy)fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                break;

                // когда враг сталиквается с объектом
            case Main.ENEMY_BIT | Main.OBJECT_BIT:
                // если фиксА это враг, то разворачиваем его движение
                if(fixA.getFilterData().categoryBits == Main.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                // если фиксВ это враг, то разворачиваем его движение
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;

                // когда марио сталкивается с врагом,
            case Main.MARIO_BIT | Main.ENEMY_BIT:
                // если фиксА это марио, то он сталкивается с врагом
                if(fixA.getFilterData().categoryBits == Main.MARIO_BIT)
                    ((Mario) fixA.getUserData()).hit((Enemy)fixB.getUserData());
                else
                    // если фиксВ это марио, то он сталиквается с врагом
                    ((Mario) fixB.getUserData()).hit((Enemy)fixA.getUserData());
                break;

                // когда два врага сталкиваются друг с другом
            case Main.ENEMY_BIT | Main.ENEMY_BIT:
                // если два врага сталкиваются, то вызывается метод хитБайЕнеми у каждого врага
                ((Enemy)fixA.getUserData()).hitByEnemy((Enemy)fixB.getUserData());
                ((Enemy)fixB.getUserData()).hitByEnemy((Enemy)fixA.getUserData());
                break;

                // если предмет сталкивается с объектом
            case Main.ITEM_BIT | Main.OBJECT_BIT:
                // если фиксА это предмет, то разворачиваем его движение
                if(fixA.getFilterData().categoryBits == Main.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                else
                    // если фиксВ это предмет, то разворачиваем его движение
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                break;

                // когда предмет сталкивается с марио
            case Main.ITEM_BIT | Main.MARIO_BIT:
                // если фиксА это предмет, то используем его
                if(fixA.getFilterData().categoryBits == Main.ITEM_BIT)
                    ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
                else
                    // если фиксВ это предмет, то используем его
                    ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
                break;

                // когда огненный шар сталкивается с объектом
            case Main.FIREBALL_BIT | Main.OBJECT_BIT:
                // если фиксА это шар, то помечаем его для уничтожения
                if(fixA.getFilterData().categoryBits == Main.FIREBALL_BIT)
                    ((FireBall)fixA.getUserData()).setToDestroy();
                else
                    // если фиксВ это шар, то помечаем его для уничтожения
                    ((FireBall)fixB.getUserData()).setToDestroy();
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
