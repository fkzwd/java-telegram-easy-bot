# Java telegram easy bot
# 📚 tg-lib

## INFO
___
Библиотека для создания ботов в телеграм.

Позволяет быстро запустить бота с возможностью:
- обработки команд
- обработки текстовых сообщений
- создания форм

### Подключение библиотеки
[Здесь](#2-подключение-библиотеки)

### 📝 Пример формы
![e](./docs/img/example_bot.gif)
> Каждая форма привязана к своему сообщению  
> И хранит контекст

[Код формы](./example-echo-bot/src/main/java/com/vk/dwzkf/tglib/example/echobot/commands/SimpleFormHandler.java)

### 📝 Пример обработки текста
![](./docs/img/example_text.gif)

[Код обработки](./example-echo-bot/src/main/java/com/vk/dwzkf/tglib/example/echobot/text/EchoTextMessageHandler.java)


<br>

## ⚡️ Quick start
___
### Настройка
#### Настройка репозитория [GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages)
##### 1. Настройка **github**
> Создать AccessToken (если токен есть этот шаг можно пропустить)
1. Перейти в [настройку токенов](https://github.com/settings/tokens)
2. Создать токен с правами **read:packages**
![](./docs/img/0001_github_token_scopes.png)
3. Скопировать токен
4. Открыть файл `%userprofile%/.gradle/gradle.properties` (Windows)
5. Создать в нем следующие проперти:
```properties
gpr.user=<логин github>
gpr.key=<ключ из шага 3>
```

##### 2. Настройка репозиториев
-  в **build.gradle** добавить **GitHub Packages** репозиторий:
```groovy
ext {
    gitRepostioryName = 'java-telegram-easy-bot'
}

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/fkzwd/$gitRepostioryName")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
    }
} 
```
*Теперь gradle сможет подтянуть зависимость*
___
### Создание бота
#### 1. Создаем проект
1. Создать `springboot` проект
2. Подключаем зависимость
```groovy
depdendencies {
    implementation 'org.springframework.boot:spring-boot-starter'
}
```
#### 2. Подключение библиотеки
`build.gradle`:
```groovy
ext {
    tglibBotCoreVersion = '1.0.0'
    telegramBotsVersion = '6.9.7.1'
}
dependencies {
    implementation "com.vk.dwzkf.tglib:bot-core:$tglibBotCoreVersion"
    implementation "org.telegram:telegrambots:$telegramBotsVersion"
}
```
#### 3. Настройка проперти
```yaml
bot:
  bot-name: "my_custom_bot" # имя бота при создании
  token: "mybotapikey" # токен полученный от https://t.me/BotFather
  secure: false # бот будет отвечать всем
```
*По умолчанию `bot.secure=true`; бот игнорирует сообщения от всех*

#### 4. Включение бинов бота в контекст приложения
```java
import com.vk.dwzkf.tglib.botcore.BotCore;
//...
@SpringBootApplication(scanBasePackageClasses = {Main.class, BotCore.class})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```
#### 4. Фильтр авторизации
> TODO
#### 5. Создание простого обработчика команды
1. Создаем компонент спринга
2. Вешаем на него аннотацию `@RouteCommand(command = "your_command")`
3. Создаем метод который на вход принимает `MessageContext`
```java
@RouteCommand(command = "annotation")
@Component
public class AnnotationCommandHandler {
    @RouteCommandHandler
    public void handle(MessageContext ctx) throws BotCoreException {
        ctx.doAnswer("[ANNOTATED]: "+ctx.getRawData());
    }
}
```
![](./docs/img/example_annotation.gif)
___
#### 6. Создание обработчика текста
1. Создаем компонент имплементирующий интерфейс `TextMessageHandler<T extends MessageContext>`
> Здесь используется `extends DefaultTextMessageHandler` - он работает с `MessageContext`
```java
@Service
@TextHandler
@Order(1)
public class EchoTextMessageHandler extends DefaultTextMessageHandler  {
    @Override
    public boolean match(MessageContext messageContext) {
        return true;
    }
    @Override
    public void handle(MessageContext messageContext) throws BotCoreException {
        messageContext.doAnswer("[ECHO]: "+messageContext.getMessage());
    }
}
```
![](./docs/img/example_text.gif)
#### 7. Отправка формы с кнопками
- Создаем форму, которая выводит текущее время и может обновляться

`CurrentTimeForm.java`:
```java
public class CurrentTimeForm extends Form {
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public CurrentTimeForm() {
        setTitle("<b>Заголовок формы</b>");
        setTextProvider(() ->
                "<b>Текущее время:</b> <code>%s</code>"
                        .formatted(LocalDateTime.now().format(DATE_TIME_FORMAT))
        );
        addRow().addButton(new RefreshButton(this));
        createControls(false,true); //создаем кнопку закрытия формы
    }
}
```  

`OneButtonFormhandler.java` - обработчик команды `/time`:
```java
@Component
@RouteCommand(command = "time")
public class OneButtonFormHandler extends FormSender<MessageContext> implements CommandHandler<MessageContext> {

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void handle(MessageContext messageContext) throws BotCoreException {
        Form form = new CurrentTimeForm();
        sendForm(form, messageContext);
    }

    @Override
    public Class<MessageContext> supports() {
        return MessageContext.class;
    }
}
```
![](./docs/img/example_time.gif)
#### 8. Пользовательский контекст
*TODO: доделать*  
> По умолчанию все обработчики на вход принимают `MessageContext`  
> Это можно изменить создав свою реализацию `factory`
- Пользовательский контекст:
```java
@Getter
@Setter
public class CustomContext extends MessageContext {
    private String uuid = UUID.randomUUID().toString();
}
```
- Пользовательская `factory`:
```java
@Component
public class CustomContextFactory implements MessageContextFactory<CustomContext> {
    @Override
    public CustomContext create() {
        return new CustomContext();
    }
}
```
- Обработчик команд принимающий `CustomContext`
```java
@RouteCommand(command = "custom")
@Component
public class CustomContextHandler {
    @RouteCommandHandler
    public void handle(CustomContext context) throws BotCoreException {
        context.doAnswer(
                "[<code>%s</code>]\ncommand: <b>%s</b>\nargs: <b>%s</b>".formatted(
                        context.getUuid(),
                        context.getCommandContext().getCommand(),
                        context.getCommandContext().getRawArgs()
                ));
    }
}
```
*Результат:*  
![](./docs/img/example_custom_context.gif)
#### 9. Последовательный ввод данных
Для последовательного ввода данных используется класс `InputWaiterService`  
и его методы `await`, `thenAwait`  

По умолчанию `await` принимает текстовый инпут только в том чате,  
в которым был инициирован, и от того юзера, который инициировал.

> `await` - ждет текстового инпута от юзера-инициатора  

> `thenAwait` - позволяет зачейнить ожидание инпута для этого юзера.
> После того, как предыдущий инпут отработал, на его место встаёт тот,
> который передан в `thenAwait`

> В случае нескольких вызовов `await` подряд -  
> они отрабатывают по очереди

На примере заполнения следующего контекста:  
```java
private static class ChainedInputContext {
    private String firstName;
    private String lastName;
    private int age;
}
```

- Создаем обработчик для команды и внедряем в него `InputWaiterService`
```java
@RouteCommand(command = "chained")
@Service
@RequiredArgsConstructor
public class ChainedInputWait {
    private final InputWaiterService inputWaiterService;

    @RouteCommandHandler
    public void handle(CustomContext ctx) throws BotCoreException {

    }
}
```
- Создаем обработчик текстового инпута; Он будет ждать корректного ввода.  
При успехе будет обрабатывать данные и отвечать юзеру
```java
    private static InputAwait getSimpleString(
            Consumer<String> valueConsumer,
            String errorMessage,
            String replyMessage
    ) throws OnInputException {
        return inputContext -> {
            String message = inputContext.getMessage();
            if (!message.matches("[a-zA-Z]+")) {
                throw new OnInputException(
                        List.of(
                                ActionType.REPLY_TEXT.createAction(errorMessage)
                        )
                ).setInterrupt(false);
            }
            valueConsumer.accept(message);
            return List.of(
                    ActionType.REPLY_TEXT.createAction(replyMessage)
            );
        };
    }
```
- Создаем обработчик для ввода возраста  
Он будет ждать корректного возраста и при успехе возвращать форму  
с введенными данными
```java
    private InputAwait getAge(ChainedInputContext context) throws OnInputException {
        return inputContext -> {
            String message = inputContext.getMessage();
            if (!message.matches("\\d+")) {
                throw new OnInputException(
                        List.of(
                                ActionType.REPLY_TEXT.createAction("Некорректный ввод. Введите возраст")
                        )
                ).setInterrupt(false);
            }
            context.age = Integer.parseInt(message);
            return List.of(
                    ActionType.CREATE_FORM.createAction(createDataForm(context))
            );
        };
    }
```
- Метод для создания формы вывода данных
```java
    private Form createDataForm(ChainedInputContext ctx) {
        Form form = new Form();
        form.setTextProvider(() -> {
            return """
                    <b>Имя:</b> %s
                    <b>Фамилия:</b> %s
                    <b>Возраст:</b> %s
                    """.formatted(
                    ctx.firstName,
                    ctx.lastName,
                    ctx.age
            );
        });
        form.createControls(false, true);
        return form;
    }
```
- Собираем всё вместе
```java
    @RouteCommandHandler
    public void handle(CustomContext ctx) throws BotCoreException {
        ChainedInputContext context = new ChainedInputContext();
        ctx.doReply("Введите имя");
        inputWaiterService
                .await(
                        getSimpleString(
                                value -> context.firstName = value,
                                "Некорректный ввод. Введите имя",
                                "Введите фамилию"
                        ),
                        ctx
                )
                .thenAwait(getSimpleString(
                        value -> context.lastName = value,
                        "Некорректный ввод. Введите фамилию",
                        "Введите возраст"
                ))
                .thenAwait(getAge(context));
    }
```
_Результат:_  
![](./docs/img/example_chained_input.gif)
##### Отмена `await`-ов:
> Для отмены `await`-ов используется метод `cancelAll(MessageContext)`
> - Метод отменяет все `await`-ы созданные в **данном** чате **данным юзером**
> - И возвращает список всех сообщений которые запустили эти `await`-ы

**Создадим простой обработчик, который:**
- Будет отменять все `await`-ы в чате
- На каждое сообщение, которое запустило `await`, будет делать `reply` с сообщением _"Команда остановлена"_
```java
@Service
@RouteCommand(command = "cancelawait")
@RequiredArgsConstructor
public class CancelAwaitCommand {
    private final InputWaiterService inputWaiterService;

    @RouteCommandHandler
    public void handle(MessageContext messageContext) throws BotCoreException {
        List<MessageContext> messageContexts = inputWaiterService.cancelAll(messageContext);
        if (messageContexts.isEmpty()) {
            messageContext.doReply("Нет ожидающих команд");
        }
        for (MessageContext context : messageContexts) {
            context.doReply("Команда остановлена");
        }
    }
}
```
_Результат:_  
![](./docs/img/example_cancelawait.gif)
#### 10. Настройка частоты отправки ботом сообщений
По умолчанию бот отправляет не более **2 сообщений в секунду**  
Интервал между сообщениями **150 мс**
- Настройка интервала между сообщениями:  
_application.yml_
```yaml
bot:
  task-queue:
    smart-bot-task-queue:
      default-bot-task-queue-config:
        task-execution-rate: 50 
        task-execution-time-unit: MILLISECONDS
        # 50 миллисекунд между сообщениями
```

- Настройка лимитов  
_application.yml_
```yaml
bot:
  task-queue:
    smart-bot-task-queue:
      window: 5
      unit: SECONDS
      limit: 2
      # Не более двух сообщений в 5 секунд
```
##### Настройка лимитов для _приватного_ и _группового_ чатов:
> Приватный и групповой чат по `chatId`:  
> Отрицательный `chatId` = групповой чат  
> Положительный `chatId` = приватный чат
- Настройка лимитов для приватных чатов:
```yaml
bot:
  task-queue:
    smart-bot-task-queue:
      private-chat-config:
        window: 1
        unit: SECONDS
        limit: 3
        # Не больше трех сообщений в секунду
```
- Настройка лимитов для групповых чатов:
```yaml
bot:
  task-queue:
    smart-bot-task-queue:
      group-chat-config:
        window: 1
        unit: MINUTES
        limit: 20
        # Не больше 20 сообщений в минуту
```

#### 11.
-- todo
#### 42. Заполнение пользовательского контекста
> TODO: ...
___
## TODO
1. Поддержка различных `ActionType`
2. Добавить `UnauthorizedHandler`
3. Обработка когда форма обновляется точно такой же `[400] Bad Request: message is not modified`
4.  