package com.vk.dwzkf.tglib.botcore.forms;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.forms.actions.AfterClickAction;
import com.vk.dwzkf.tglib.botcore.forms.buttons.FormButton;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Представляет собой форму с кнопками в сообщении
 * @author Roman Shageev
 * @since 11.11.2023
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Form {
    public static final String CONTROLS_GROUP = "controls";
    public static final String DEFAULT_GROUP = "default";
    private static final AtomicLong idx = new AtomicLong();


    @EqualsAndHashCode.Include
    private String name;
    /**
     * <pre>
     * Заголовок формы, опциональный
     * </pre>
     */
    @Setter
    private String title;
    /**
     * <pre>
     *     Текст формы, опциональный, используется если {@link #setTextProvider(Supplier)} не задан
     * </pre>
     */
    @Setter
    private String text;
    /**
     * <pre>
     *     Провайдер текста формы, вызывается каждый раз перед отправкой/обновлением формы
     * </pre>
     */
    private Supplier<String> textProvider;
    /**
     * <pre>
     *     Вызывается каждый раз перед рендером формы в сообщение телеграм
     * </pre>
     */
    private Runnable preRender;
    private Map<String, List<List<FormButton>>> gridGroups = new LinkedHashMap<>();
    private LinkedList<String> groupOrder = new LinkedList<>();
    protected Map<Object, Object> attributes = new ConcurrentHashMap<>();
    private MessageContext currentContext;

    /**
     * Позволяет задавать пользовательские атрибуты формы
     * @param key ключ
     * @param value значение
     * @param <T> кладем любой объект
     */
    public <T> void putAttribute(Object key, T value) {
        attributes.put(key, value);
    }

    /**
     * Достает ранее заданный атрибут {@link #putAttribute(Object, Object)}
     * @param key ключ
     * @return <code>nullable</code> значение
     * @param <T> автоматически приводит атрибут к нужному типу
     * @throws ClassCastException если атрибут по заданному <code>key</code> не соответствующего типа
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(Object key) {
        return (T) attributes.get(key);
    }

    {
        createGroup(DEFAULT_GROUP);
        setName("form-"+idx.incrementAndGet());
    }

    private Form parent;


    /**
     * Возвращает текст формы
     * @return текст формы
     */
    public String renderText() {
        return textProvider == null ? text : textProvider.get();
    }

    /**
     * Создает группу кнопок с заданным именем
     * @param group имя группы
     * @return таблица кнопок
     */
    public List<List<FormButton>> createGroup(@NotNull String group) {
        Objects.requireNonNull(group);
        if (!groupOrder.contains(group)) groupOrder.add(group);
        return getGroup(group);
    }

    /**
     * Получить таблицу кнопок по имени группы
     * Если группа не создана то она создается
     * @param group имя группы
     * @return таблица кнопок
     */
    private List<List<FormButton>> getGroup(@NotNull String group) {
        Objects.requireNonNull(group);
        return gridGroups.computeIfAbsent(group, g -> new ArrayList<>());
    }

    /**
     * <pre>
     * Создает группу кнопок после заданной
     * </pre>
     * Если {@code before} == {@code null}, то группа {@code group} не изменит своего положения
     * @param group имя новой группы
     * @param before имя существующей
     * @return таблица кнопок
     */
    public List<List<FormButton>> createGroupBefore(@NotNull String group, String before) {
        Objects.requireNonNull(group);
        if (positionUnchanged(group, before)) return getGroup(group);
        groupOrder.remove(group);
        ListIterator<String> iterator = groupOrder.listIterator(groupOrder.indexOf(before));
        while (iterator.hasNext()) {
            String currentGroup = iterator.next();
            if (currentGroup.equals(before)) {
                iterator.set(group);
                iterator.add(currentGroup);
                break;
            }
        }
        return getGroup(group);
    }

    /**
     * <pre>
     * Создает группу кнопок после заданной
     * </pre>
     * Если {@code after} == {@code null}, то группа {@code group} не изменит своего положения
     * @param group имя новой группы
     * @param after имя существующей
     * @return таблица кнопок
     */
    public List<List<FormButton>> createGroupAfter(@NotNull String group, String after) {
        Objects.requireNonNull(group);
        if (positionUnchanged(group, after)) return getGroup(group);
        groupOrder.remove(group);
        ListIterator<String> iterator = groupOrder.listIterator(groupOrder.indexOf(after));
        while (iterator.hasNext()) {
            String currentGroup = iterator.next();
            if (currentGroup.equals(after)) {
                iterator.add(group);
                break;
            }
        }
        return getGroup(group);
    }

    private boolean positionUnchanged(String group, String another) {
        if (another == null || !groupOrder.contains(another)) {
            if (groupOrder.contains(group)) {
                return true;
            }
            groupOrder.add(group);
            return true;
        }
        return false;
    }

    /**
     * Очищает группу кнопок
     * @param group имя группы
     * @return пустая таблица кнопок
     */
    public List<List<FormButton>> clearGroup(@NotNull String group) {
        Objects.requireNonNull(group);
        List<List<FormButton>> prev = createGroup(group);
        gridGroups.put(group, new ArrayList<>());
        return prev;
    }

    /**
     * do not use for adding buttons
     * @return
     */
    List<List<FormButton>> getButtonGrid() {
        List<List<FormButton>> buttonGrid = new ArrayList<>();
        groupOrder.forEach(g -> {
            buttonGrid.addAll(gridGroups.get(g));
        });
        return buttonGrid;
    }

    public boolean hasButtons() {
        List<List<FormButton>> grid = getButtonGrid();
        int rows = grid.size();
        if (rows == 0) return false;
        for (List<FormButton> formButtons : grid) {
            if (!formButtons.isEmpty()) return true;
        }
        return false;
    }

    public void preRender() {
        if (preRender != null) {
            preRender.run();
        }
    }

    /**
     * Очистка всех кнопок
     */
    public void clear() {
        gridGroups.keySet().forEach(group -> {
            gridGroups.get(group).clear();
        });
    }

    /**
     * Билдер кнопок для одной строки с кнопками
     */
    //TODO: duplicate javadoc with Form.class
    @AllArgsConstructor
    public class RowCreator {
        private List<FormButton> buttons;

        /**
         * Добавить кнопку в данную строку с кнопками
         * @param button кнопка
         * @return {@link RowCreator}
         */
        public RowCreator addButton(FormButton button) {
            buttons.add(button);
            return this;
        }

        /**
         * Возвращает форму, которую настраивает
         * @return {@link Form}
         */
        public Form and() {
            return Form.this;
        }

        /**
         * Добавляет еще одну строку с кнопками в {@link #DEFAULT_GROUP} группу кнопок
         * @return
         */
        public RowCreator addRow() {
            return Form.this.addRow();
        }

        /**
         * Добавляет еще одну строку с кнопками в заданной группе кнопок
         * @param group группа кнопок
         * @return
         */
        public RowCreator addRow(String group) {
            return Form.this.addRow(group);
        }

        /**
         * Добавляет строку с кнопками в заданную группу и применяет {@code consumer} для настройки этой строки
         * @param group группа кнопок
         * @param consumer настройка строки
         * @return
         */
        public Form addRow(String group, Consumer<RowCreator> consumer) {
            return Form.this.addRow(group, consumer);
        }

        /**
         * Добавляет строку с кнопками в {@link #DEFAULT_GROUP} группу кнопок и применяет {@code consumer}
         * для настройки этой строки
         * @param consumer
         * @return
         */
        public Form addRow(Consumer<RowCreator> consumer) {
            return Form.this.addRow(consumer);
        }
    }

    /**
     * Добавляет строку с кнопками в {@link #DEFAULT_GROUP} группу кнопок
     * @param consumer
     * @return
     */
    public Form addRow(Consumer<RowCreator> consumer) {
        return addRow(DEFAULT_GROUP, consumer);
    }

    /**
     * Добавляет еще одну строку с кнопками в заданной группе кнопок
     * @param group группа кнопок
     * @return
     */
    public RowCreator addRow(String group) {
        ArrayList<FormButton> row = new ArrayList<>();
        gridGroups.get(group).add(row);
        return new RowCreator(row);
    }

    /**
     * Добавляет еще одну строку с кнопками в {@link #DEFAULT_GROUP} группу кнопок
     * @return
     */
    public RowCreator addRow() {
        return addRow(DEFAULT_GROUP);
    }

    /**
     * Добавляет строку с кнопками в заданную группу и применяет {@code consumer} для настройки этой строки
     * @param group группа кнопок
     * @param consumer настройка строки
     * @return
     */
    public Form addRow(String group, Consumer<RowCreator> consumer) {
        ArrayList<FormButton> row = new ArrayList<>();
        gridGroups.get(group).add(row);
        RowCreator rowCreator = new RowCreator(row);
        consumer.accept(rowCreator);
        return this;
    }

    public List<AfterClickAction<?>> onButtonClick(int row, int col, MessageContext ctx) {
        setCurrentContext(ctx);
        if (getButtonGrid().size() <= row) {
            throw new IllegalArgumentException("Not found row with index = "+row);
        }
        List<FormButton> buttons = getButtonGrid().get(row);
        if (buttons.size() <= col) {
            throw new IllegalArgumentException("Not found col with index = "+row);
        }
        FormButton button = buttons.get(col);
        return button.onClick(this, ctx);
    }

    /**
     * Создает кнопки <b>Назад</b> и <b>Закрыть</b> у данной формы <br/>
     * <i>Если у данной формы отсутствует {@link #getParent()} то кнопка <b>Назад</b> создана не будет</i>
     * @param back создавать ли кнопку назад
     * @param exit создавать ли кнопку закрыть
     */
    public void createControls(boolean back, boolean exit) {
        List<FormButton> controls = new ArrayList<>();
        if (back && parent != null) {
            controls.add(FormButton.createBackButton(this));
        }
        if (exit) {
            controls.add(FormButton.createCloseButton(this));
        }
        createGroup(CONTROLS_GROUP);
        clearGroup(CONTROLS_GROUP);
        addRow(CONTROLS_GROUP, r -> {
            controls.forEach(r::addButton);
        });
    }
}
