package com.nellie.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nellie.game.Main;

public class Hud implements Disposable {
    public Stage stage;  // Сцена для отображения всех UI-элементов.
    private Viewport viewport;  // Виджет, управляющий масштабированием экрана.

    private Integer worldTimer;  // Таймер для отсчета времени в игре.
    private boolean timeUp;  // Флаг, указывающий, истекло ли время.
    private float timeCount;  // Счетчик времени для обновления таймера каждую секунду.
    private static Integer score;  // Очки игрока.

    private Label countdownLabel;  // Метка для отображения времени.
    private static Label scoreLabel;  // Метка для отображения очков.
    private Label timeLabel;  // Метка для надписи "TIME".
    private Label levelLabel;  // Метка для уровня (например, 1-1).
    private Label worldLabel;  // Метка для надписи "WORLD".
    private Label marioLabel;  // Метка для имени игрока "MARIO".

    // Конструктор HUD, инициализирующий все метки и добавляющий их в сцену.
    public Hud(SpriteBatch sb){
        worldTimer = 300;  // Инициализируем таймер на 300 секунд.
        timeCount = 0;  // Инициализируем счетчик времени.
        score = 0;  // Инициализируем очки на 0.

        // Устанавливаем масштабируемый viewport для UI.
        viewport = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);  // Создаем сцену с использованием viewport.

        // Создаем таблицу для размещения меток.
        Table table = new Table();
        table.top();  // Размещаем таблицу в верхней части экрана.
        table.setFillParent(true);  // Таблица занимает всю доступную площадь сцены.

        // Инициализируем все метки.
        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));  // Таймер.
        scoreLabel =new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));  // Очки.
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));  // Надпись "TIME".
        levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));  // Уровень.
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));  // Надпись "WORLD".
        marioLabel = new Label("MARIO", new Label.LabelStyle(new BitmapFont(), Color.WHITE));  // Имя игрока.

        // Добавляем метки в таблицу.
        table.add(marioLabel).expandX().padTop(10);  // Размещаем "MARIO".
        table.add(worldLabel).expandX().padTop(10);  // Размещаем "WORLD".
        table.add(timeLabel).expandX().padTop(10);  // Размещаем "TIME".
        table.row();  // Создаем новую строку.
        table.add(scoreLabel).expandX();  // Размещаем очки.
        table.add(levelLabel).expandX();  // Размещаем уровень.
        table.add(countdownLabel).expandX();  // Размещаем таймер.

        // Добавляем таблицу в сцену.
        stage.addActor(table);
    }

    // Метод обновления HUD, который обновляет таймер каждую секунду.
    public void update(float dt){
        timeCount += dt;  // Увеличиваем счетчик времени.
        if(timeCount >= 1){  // Если прошло 1 секунда:
            if (worldTimer > 0) {  // Если таймер не закончился:
                worldTimer--;  // Уменьшаем таймер на 1.
            } else {
                timeUp = true;  // Если таймер достиг 0, устанавливаем флаг timeUp в true.
            }
            countdownLabel.setText(String.format("%03d", worldTimer));  // Обновляем отображаемое значение таймера.
            timeCount = 0;  // Сбрасываем счетчик времени.
        }
    }

    // Метод для добавления очков.
    public static void addScore(int value){
        score += value;  // Увеличиваем очки на указанное значение.
        scoreLabel.setText(String.format("%06d", score));  // Обновляем отображаемое значение очков.
    }

    @Override
    public void dispose() { stage.dispose(); }  // Освобождаем ресурсы сцены.

    // Метод для проверки, истекло ли время.
    public boolean isTimeUp() { return timeUp; }
}
