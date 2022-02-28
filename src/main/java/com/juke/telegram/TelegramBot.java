package com.juke.telegram;

import static com.juke.telegram.VarConstant.ACCEPT;
import static com.juke.telegram.VarConstant.BACK;
import static com.juke.telegram.VarConstant.BOT_COMMAND;
import static com.juke.telegram.VarConstant.DATA;
import static com.juke.telegram.VarConstant.DAY_NUMBER;
import static com.juke.telegram.VarConstant.DECLINE;
import static com.juke.telegram.VarConstant.GO;
import static com.juke.telegram.VarConstant.JAVA;
import static com.juke.telegram.VarConstant.LEADER;
import static com.juke.telegram.VarConstant.PROFILE;
import static com.juke.telegram.VarConstant.TASK;

import com.juke.dto.PlayerDto;
import com.juke.service.PlayerServiceImpl;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramLongPollingBot {

  private static final String TOKEN = System.getenv("TOKEN");
  private static final String BOT_USERNAME = System.getenv("BOT_USERNAME");
  private static final LocalDate START_DATE = LocalDate.parse(System.getenv("START_DATE"));

  private final PlayerServiceImpl service;

  @Override
  public String getBotUsername() {
    return BOT_USERNAME;
  }

  @Override
  public String getBotToken() {
    return TOKEN;
  }

  @Override
  public void onUpdateReceived(Update update) {

    if (update.hasMessage()) {
      catchMessage(update.getMessage());
    }

    if (update.hasCallbackQuery()) {
      catchCallbackQuery(update.getCallbackQuery());
    }
  }

  private void catchMessage(Message message) {

    if (message.getFrom().getIsBot()) {
      executeSendMessage(message.getChatId().toString(),
          "Бот не реагирует на сообщения от других ботов!");
    }

    boolean isRegisteredPerson = service.hasTelegramId(message.getChatId());

    if (!isRegisteredPerson && !message.hasContact()) {
      dataCollectionAgreement(message);
      return;
    }

    if (!isRegisteredPerson && message.hasContact()) {
      registration(message);
      return;
    }

    if (message.hasText()) {
      catchTextMessage(message);
    }
  }

  private void dataCollectionAgreement(Message message) {

    if (!message.hasText()) {
      return;
    }

    if (message.getText().equals(DECLINE)) {
      executeSendMessage(message.getChatId().toString(),
          "К сожалению, без согласия на обработку персональных "
              + "данных, вы не сможете пользоваться ботом.");
      return;
    }

    KeyboardRow firstRow = new KeyboardRow();
    firstRow.add(getButton(ACCEPT, true));
    firstRow.add(getButton(DECLINE, false));

    List<KeyboardRow> keyboard = List.of(firstRow);

    executeSendMessage(message.getChatId().toString(),
        "Пожалуйста, предоставьте доступ к Вашему контакту:",
        getReplyKeyboardMarkup(true, true, true, keyboard));
  }

  private void registration(Message message) {

    Contact contact = message.getContact();

    if (service.hasTelegramId(contact.getUserId())) {
      executeSendMessage(message.getChatId().toString(), "Вы уже зарегистрированы!");
      return;
    }

    PlayerDto registrationDto = PlayerDto.builder().telegramId(contact.getUserId())
        .userName(message.getChat().getUserName()).phone(contact.getPhoneNumber()).build();

    service.save(registrationDto);
    executeSendMessage(message.getChatId().toString(), "Регистрация прошла успешно!");
    startMethod(message);
  }

  private void catchTextMessage(Message message) {

    if (message.hasEntities()) {
      commandMethod(message);
      return;
    }

    switch (message.getText()) {
      case DECLINE:
        executeSendMessage(message.getChatId().toString(),
            "К сожалению, без согласия на обработку персональных данных, "
                + "вы не сможете пользоваться ботом.");
        break;

      case TASK:
        taskMethod(message);
        break;

      case PROFILE:
        profileMethod(message);
        break;

      case LEADER:
        leaderMethod(message);
        break;
    }
  }

  private void taskMethod(Message message) {
    List<List<InlineKeyboardButton>> buttons = List.of(
        List.of(getInlineButton(JAVA), getInlineButton(GO), getInlineButton(DATA)),
        List.of(getInlineButton(BACK)));

    executeSendMessage(message.getChatId().toString(),
        "Пожалуйста, выберите свой язык программирования:",
        InlineKeyboardMarkup.builder().keyboard(buttons).build());
  }

  private void profileMethod(Message message) {
    PlayerDto player = service.findByTelegramId(message.getChatId());

    String playerProfile = String.format("""
            • Username: %s
            • Phone: %s
            • Total points: %d
              - Java: %d
              - Go: %d
              - Data: %d
            """, player.getUserName(), player.getPhone(), player.getTotalScore(), player.getJavaScore(),
        player.getGoScore(), player.getDataScore());

    executeSendMessage(message.getChatId().toString(), playerProfile);
  }

  private void leaderMethod(Message message) {

    List<PlayerDto> topPlayerList = service.findAll().stream().sorted((o1, o2) -> {
      Long x = o1.getJavaScore() + o1.getGoScore() + o1.getDataScore();
      Long y = o2.getJavaScore() + o1.getGoScore() + o1.getDataScore();
      return y.compareTo(x);
    }).limit(10).toList();

    //TODO: переделать блок кода для нормального для нормального вывода списка лидеров;
    for (PlayerDto player : topPlayerList) {
      executeSendMessage(message.getChatId().toString(),
          player.getUserName() + " " + (player.getJavaScore() + player.getGoScore()
              + player.getDataScore()));
    }
  }

  private void commandMethod(Message message) {
    Optional<MessageEntity> commandEntity = message.getEntities().stream()
        .filter(e -> e.getType().equals(BOT_COMMAND)).findFirst();

    if (commandEntity.isPresent()) {
      String command = message.getText()
          .substring(commandEntity.get().getOffset(), commandEntity.get().getLength());

      //TODO: пока оставить блок SWITCH, возможно появятся новые команды;
      switch (command) {
        case "/start":
          startMethod(message);
          break;
      }
    }
  }

  @SneakyThrows
  private void catchCallbackQuery(CallbackQuery callbackQuery) {
    Message message = callbackQuery.getMessage();

    if (!service.hasTelegramId(message.getChatId())) {
      dataCollectionAgreement(message);
      return;
    }

    switch (callbackQuery.getData()) {

      case JAVA:
        getTask(message, JAVA);
        break;

      case GO:
        getTask(message, GO);
        break;

      case DATA:
        getTask(message, DATA);
        break;

      case BACK:
        startMethod(message);
        break;
    }
  }

  private void getTask(Message message, String language) {
    int taskDays = getTaskDays();
    List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

    for (int i = 1; i < taskDays; i += 2) {
      buttons.add(
          List.of(
              getInlineButton(String.format("%s. %s%d", language, DAY_NUMBER, i)),
              getInlineButton(String.format("%s. %s%d", language, DAY_NUMBER, i + 1))
          )
      );
    }

    if (taskDays % 2 != 0) {
      buttons.add(
          List.of(
              getInlineButton(String.format("%s. %s%d", language, DAY_NUMBER, taskDays))
          )
      );
    }

    buttons.add(List.of(getInlineButton(BACK)));

    executeSendMessage(message.getChatId().toString(), "Выберите день:",
        InlineKeyboardMarkup.builder().keyboard(buttons).build());
  }

  private void startMethod(Message message) {

    KeyboardRow firstLine = new KeyboardRow();
    firstLine.add(TASK);
    firstLine.add(LEADER);
    firstLine.add(PROFILE);

    List<KeyboardRow> keyboard = List.of(firstLine);

    executeSendMessage(message.getChatId().toString(),
        "\uD83D\uDCF1 Добро пожаловать в GiveMeTask бот!",
        getReplyKeyboardMarkup(true, true, true, keyboard));
  }

  @SneakyThrows
  private void executeSendMessage(String chatId, String text) {
    execute(SendMessage.builder()
        .chatId(chatId)
        .text(text)
        .build());
  }

  @SneakyThrows
  private void executeSendMessage(String chatId, String text, InlineKeyboardMarkup buttons) {
    execute(SendMessage.builder()
        .chatId(chatId)
        .text(text)
        .replyMarkup(buttons)
        .build());
  }

  @SneakyThrows
  private void executeSendMessage(String chatId, String text, ReplyKeyboardMarkup buttons) {
    execute(SendMessage.builder()
        .chatId(chatId)
        .text(text)
        .replyMarkup(buttons)
        .build());
  }

  private InlineKeyboardButton getInlineButton(String text) {
    return InlineKeyboardButton.builder()
        .text(text)
        .callbackData(text)
        .build();
  }

  private ReplyKeyboardMarkup getReplyKeyboardMarkup(Boolean selective, Boolean resizeKeyboard,
      Boolean oneTimeKeyboard, List<KeyboardRow> keyboard) {
    return ReplyKeyboardMarkup.builder()
        .selective(selective)
        .resizeKeyboard(resizeKeyboard)
        .oneTimeKeyboard(oneTimeKeyboard)
        .keyboard(keyboard)
        .build();
  }

  private KeyboardButton getButton(String buttonName, Boolean requestContract) {
    return KeyboardButton.builder()
        .text(buttonName)
        .requestContact(requestContract)
        .build();
  }

  private int getTaskDays() {
    LocalDate localDate = LocalDate.now();

    if (localDate.getDayOfYear() - START_DATE.getDayOfYear() + 1 <= 0) {
      return localDate.getDayOfYear() + START_DATE.lengthOfYear() - START_DATE.getDayOfYear() + 1;
    }

    return localDate.getDayOfYear() - START_DATE.getDayOfYear() + 1;
  }
}