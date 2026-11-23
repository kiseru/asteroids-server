package ru.kiseru.logics.models;

import ru.kiseru.logics.CourseChecker;
import ru.kiseru.logics.Score;
import ru.kiseru.logics.Screen;
import ru.kiseru.logics.Steps;
import ru.kiseru.logics.auxiliary.Position;
import ru.kiseru.logics.auxiliary.Direction;
import ru.kiseru.logics.auxiliary.Type;
import ru.kiseru.server.UserHandler;

public class Spaceship extends Point implements Model{

    private final UserHandler owner;

    private Steps steps = new Steps();
    private Score score = new Score();
    private Direction direction;
    private CourseChecker courseChecker;

    public Spaceship(Position position, UserHandler owner) {
        super(position);
        this.owner = owner;
    }

    @Override
    public void render(Screen screen) {
        if (isVisible) {
            screen.draw(position, String.valueOf(owner.getUser().id()));
        }
    }

    /**
     * Делает шаг в текущем направлении
     */
    public void go() {
        position = position.aheadOf(direction);
    }

    /**
     * Вызывается при выявлении столкновения корабля с чем-либо
     * @param type: тип объекта, с которым произошло столкновение
     */
    public void crash(Type type) {
        synchronized (owner.getRoom().getGame()) {
            if (type == Type.ASTEROID) {
                subtractScore();
            } else if (type == Type.GARBAGE) {
                addScore();
                int collected = owner.getRoom().getGame().incrementCollectedGarbageCount();
                if (collected >= owner.getRoom().getGame().getGarbageNumber()) {
                    synchronized (owner.getRoom()) {
                        owner.getRoom().notify();
                    }
                }
            } else if (type == Type.WALL) {
                // возвращаемся назад, чтобы не находится на стене
                position = position.aheadOf(direction.opposite());
                subtractScore();
            }
            if (!owner.getIsAlive()) {
                this.destroy();
            }
        }
    }

    /**
     * Увеличивает очки корабля за собранный мусор
     */
    public void addScore() {
        score = score.increaseBy(10);
    }

    /**
     * Уменьшает очки корабля при столкновении
     */
    public void subtractScore() {
        if (score.value() < 0) {
            return;
        }
        score = score.decreaseBy(50);
        if (score.value() < 0) {
            owner.kill();
        }
    }

    /**
     * Разрушение корабля - прекращение его отображения
     */
    private void destroy() {
        this.isVisible = false;
    }

    public Steps getSteps() {
        return steps;
    }

    public void setSteps(Steps steps) {
        this.steps = steps;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Type getType() {
        return Type.SPACESHIP;
    }

    public CourseChecker getCourseChecker() {
        return courseChecker;
    }

    public void setCourseChecker(CourseChecker courseChecker) {
        this.courseChecker = courseChecker;
    }

    public boolean isOwnerAlive() {
        return owner.getIsAlive();
    }
}
