package com.kiseru.asteroids.server.handler

import com.kiseru.asteroids.server.model.Direction
import com.kiseru.asteroids.server.model.Game
import com.kiseru.asteroids.server.model.GameObject
import com.kiseru.asteroids.server.model.GameObject.Asteroid
import com.kiseru.asteroids.server.model.GameObject.Garbage
import com.kiseru.asteroids.server.model.GameObject.Spaceship
import com.kiseru.asteroids.server.model.Player

/**
 * Обработчик команд игрового сервера.
 *
 * Преобразует входящие команды в соответствующие игровые действия и формирует ответные сообщения
 * в заданном текстовом формате.
 *
 * @param game Текущая игровая сессия, содержащая состояние поля и объектов.
 * @param player Активный игрок, чьи действия обрабатываются.
 * @param spaceship Космический корабль, управляемый игроком.
 */
class CommandHandler(
    private val game: Game,
    private val player: Player,
    private val spaceship: Spaceship,
) {

    private companion object {
        // Константы символов для унификации отображения
        private const val SYMBOL_ASTEROID = "A"
        private const val SYMBOL_GARBAGE = "G"

        // Максимальное количество шагов, которое игрок может совершить до автоматической гибели
        private const val MAX_PLAYER_STEPS = 1500
    }

    /**
     * Обрабатывает входящую команду и возвращает ответ.
     *
     * @param command Команда для обработки (может быть null).
     * @return Строковый ответ, зависящий от типа команды.
     */
    fun handleCommand(command: Command?): String =
        when (command) {
            Command.Go -> handleGo()
            Command.Left -> handleLeft()
            Command.Right -> handleRight()
            Command.Up -> handleUp()
            Command.Down -> handleDown()
            Command.IsAsteroid -> handleIsAsteroid()
            Command.IsGarbage -> handleIsGarbage()
            Command.IsWall -> handleIsWall()
            Command.GameField -> handleGameField()
            else -> handleUnknownCommand()
        }

    /**
     * Обрабатывает команду движения корабля вперёд.
     *
     * Выполняет следующие действия:
     * 1. Проверяет, не исчерпан ли лимит шагов игрока (1500 шагов).
     * 2. Если лимит исчерпан — инициирует гибель игрока.
     * 3. Если лимит не исчерпан — пытается выполнить перемещение корабля.
     * 4. После перемещения проверяет статус игрока:
     *    - если игрок погиб (например, из‑за столкновения) — обрабатывает гибель;
     *    - если игрок жив — увеличивает счётчик шагов и возвращает количество очков.
     *
     * @return Строковое представление результата:
     *   - числовое значение очков игрока (`player.score.toString()`) при успешном перемещении;
     *   - текстовое сообщение о гибели игрока в формате:
     *     `"died\nYou've collected ${player.score} score"`
     *     если превышен лимит шагов или произошло столкновение.
     *
     * @note
     *   - Лимит шагов жёстко задан как 1500.
     *   - Гибель игрока сопровождается удалением корабля с игрового поля.
     *   - Счётчик шагов (`player.steps`) увеличивается только при успешном перемещении.
     */
    private fun handleGo(): String =
        if (player.steps >= MAX_PLAYER_STEPS) {
            handleDeath(player)
        } else {
            game.onSpaceshipMove(player, spaceship)
            if (player.status == Player.Status.Dead) {
                handleDeath(player)
            } else {
                player.steps += 1
                player.score.toString()
            }
        }

    /**
     * Обрабатывает гибель игрока.
     *
     * Устанавливает статус игрока как «мёртв» и удаляет корабль с игрового поля.
     *
     * @param player Игрок, который погибает.
     * @return Сообщение о гибели и количестве набранных очков.
     */
    private fun handleDeath(player: Player): String {
        player.status = Player.Status.Dead
        game.removeGameObject(spaceship)
        return "died\nYou've collected ${player.score} score"
    }

    /**
     * Обрабатывает команду поворота корабля влево.
     *
     * @return Строка `"success"` при успешном выполнении.
     */
    private fun handleLeft(): String =
        onChangeDirection(Direction.LEFT)

    /**
     * Обрабатывает команду поворота корабля вправо.
     *
     * @return Строка `"success"` при успешном выполнении.
     */
    private fun handleRight(): String =
        onChangeDirection(Direction.RIGHT)

    /**
     * Обрабатывает команду поворота корабля вверх.
     *
     * @return Строка `"success"` при успешном выполнении.
     */
    private fun handleUp(): String =
        onChangeDirection(Direction.UP)

    /**
     * Обрабатывает команду поворота корабля вниз.
     *
     * @return Строка `"success"` при успешном выполнении.
     */
    private fun handleDown(): String =
        onChangeDirection(Direction.DOWN)

    /**
     * Изменяет направление движения корабля.
     *
     * @param direction Новое направление движения.
     * @return Строка `"success"` после установки направления.
     */
    private fun onChangeDirection(direction: Direction): String {
        player.direction = direction
        return "success"
    }

    /**
     * Проверяет наличие астероида по направлению движения корабля.
     *
     * @return `"t"` если астероид обнаружен, `"f"` если нет.
     */
    private fun handleIsAsteroid(): String =
        onBooleanSend(game.isAsteroidAhead(player.direction, spaceship))

    /**
     * Проверяет наличие мусора по направлению движения корабля.
     *
     * @return `"t"` если мусор обнаружен, `"f"` если нет.
     */
    private fun handleIsGarbage(): String =
        onBooleanSend(game.isGarbageAhead(player.direction, spaceship))

    /**
     * Проверяет наличие стены по направлению движения корабля.
     *
     * @return `"t"` если стена обнаружена, `"f"` если нет.
     */
    private fun handleIsWall(): String =
        onBooleanSend(game.isWallAhead(player.direction, spaceship))

    /**
     * Преобразует булево значение в краткую строковую репрезентацию для передачи по протоколу.
     *
     * @param value Булево значение, которое нужно сериализовать.
     * @return Строка `"t"` если [value] истинно, `"f"` если ложно.
     *
     * @note Используется компактный формат (`"t"`/`"f"`) для экономии трафика в протоколах передачи.
     *       Для человеко‑читаемого вывода рассмотрите альтернативные форматы.
     */
    private fun onBooleanSend(value: Boolean): String =
        if (value) "t" else "f"

    /**
     * Формирует строковое представление игрового поля.
     *
     * Метод проходит по всем клеткам игрового поля, определяет наличие игровых объектов на каждой клетке и формирует
     * отформатированную строку с переносами строк.
     *
     * @return Строковое представление игрового поля, где:
     *  - каждая строка завершается символом перевода строки ('\n');
     *  - в конце добавляется дополнительный перевод строки;
     *  - пустые клетки обозначаются точкой ('.');
     *  - символы объектов выравниваются по 3 позиции (кроме первого столбца).
     *
     * @throws IllegalArgumentException если:
     *  - высота игрового поля не положительная;
     *  - ширина игрового поля не положительная.
     *
     * @see com.kiseru.asteroids.server.model.GameField — класс, описывающий игровое поле.
     * @see GameObject — базовый класс игровых объектов.
     * @see view — функция отображения объекта в символьный вид.
     */
    private fun handleGameField(): String {
        require(game.gameField.height > 0) {
            "Высота игрового поля должна быть положительной, получено: ${game.gameField.height}"
        }

        require(game.gameField.width > 0) {
            "Ширина игрового поля должна быть положительной, получено: ${game.gameField.width}"
        }

        return buildString {
            for (i in 1..game.gameField.height) {
                for (j in 1..game.gameField.width) {
                    val gameObject = game.gameField.objects.firstOrNull { it.x == j && it.y == i }
                    val symbol = if (gameObject != null) view(gameObject) else "."
                    val paddedSymbol = if (j == 1) symbol else symbol.padStart(3)
                    append(paddedSymbol)
                }
                append("\n")
            }
            append("\n")
        }
    }

    /**
     * Преобразует игровой объект в символьное представление для отображения на игровом поле.
     *
     * Использует унифицированные символьные константы для обеспечения согласованного визуального формата.
     *
     * @param gameObject Объект, который нужно отобразить.
     * @return Строковый символ или идентификатор, соответствующий типу объекта:
     *   1. [Asteroid] → [SYMBOL_ASTEROID] (`"A"`) — единый символ для всех астероидов;
     *   2. [Garbage] → [SYMBOL_GARBAGE] (`"G"`) — единый символ для всех объектов мусора;
     *   3. [Spaceship] → его уникальный идентификатор ([Spaceship.id]), обрезанный до [MAX_ID_LENGTH] символов.
     *
     * @note Поскольку [GameObject] предполагается sealed‑типом, все возможные подклассы
     *      должны быть явно перечислены в `when`. Добавление нового подкласса требует:
     *      - определения константы-символа (при необходимости);
     *      - добавления ветки в `when`-выражение;
     *      - обновления документации.
     *
     * @see GameObject — базовый класс всех игровых объектов (sealed).
     * @see Asteroid — класс астероидов.
     * @see Garbage — класс мусора.
     * @see Spaceship — класс космических кораблей.
     */
    private fun view(gameObject: GameObject): String =
        when (gameObject) {
            is Asteroid -> SYMBOL_ASTEROID
            is Garbage -> SYMBOL_GARBAGE
            is Spaceship -> gameObject.id
        }

    /**
     * Формирует ответное сообщение при получении неизвестной команды.
     *
     * @return Строка с уведомлением об ошибке и перечнем всех доступных команд,
     *         в формате: `"Unknown command. Available: Go, Up, Right, ..."`
     *
     * @note Использует перечисление [Command] для динамического получения списка команд.
     *       Это гарантирует, что справка всегда соответствует актуальному набору команд.
     *
     * @see Command — перечисление всех доступных команд игрового интерфейса.
     * @see handleCommand — функция обработки команд, где может использоваться данный метод.
     */
    private fun handleUnknownCommand(): String =
        "Unknown command. Available: ${Command.entries.joinToString()}"
}
