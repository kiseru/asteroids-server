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
    private Status status = Status.ALIVE;
    private Direction direction;
    private CourseChecker courseChecker;

    public Spaceship(Position position, UserHandler owner) {
        super(position);
        this.owner = owner;
    }

    @Override
    public void render(Screen screen) {
        if (isVisible) {
            screen.draw(position, String.valueOf(owner.getUser().id().id()));
        }
    }

    /**
     * Делает шаг в текущем направлении
     */
    public void go() {
        position = position.aheadOf(direction);
    }

    /**
     * Увеличивает очки корабля за собранный мусор
     *
     * @throws IllegalStateException если корабль разрушен
     */
    public void addScore() {
        ensureAlive();
        score = score.increaseBy(10);
    }

    /**
     * Уменьшает очки корабля при столкновении
     *
     * @throws IllegalStateException если корабль разрушен
     */
    public void subtractScore() {
        ensureAlive();
        if (score.value() < 0) {
            return;
        }
        score = score.decreaseBy(50);
        if (score.value() < 0 && status == Status.ALIVE) {
            destroy();
        }
    }

    /**
     * Разрушает корабль, делая его неживым и невидимым
     *
     * @throws IllegalStateException если корабль не жив
     */
    public void destroy() {
        ensureAlive();
        status = Status.DESTROYED;
        isVisible = false;
    }

    /**
     * Проверяет, что корабль жив
     *
     * @throws IllegalStateException если корабль не жив
     */
    private void ensureAlive() {
        if (status != Status.ALIVE) {
            throw new IllegalStateException("spaceship is not alive");
        }
    }

    public Status getStatus() {
        return status;
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

    public void setPosition(Position position) {
        this.position = position;
    }

    public enum Status {
        ALIVE,
        DESTROYED
    }
}
