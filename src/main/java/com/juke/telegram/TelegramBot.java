package com.juke.telegram;

import static com.juke.telegram.VarConstant.ABOUT;
import static com.juke.telegram.VarConstant.ACCEPT;
import static com.juke.telegram.VarConstant.BACK;
import static com.juke.telegram.VarConstant.BOT_COMMAND;
import static com.juke.telegram.VarConstant.DATA;
import static com.juke.telegram.VarConstant.DENIED;
import static com.juke.telegram.VarConstant.GO;
import static com.juke.telegram.VarConstant.JAVA;
import static com.juke.telegram.VarConstant.LEADER;
import static com.juke.telegram.VarConstant.TASK;

import com.juke.dto.PlayerDto;
import com.juke.service.PlayerServiceImpl;
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

    handleTextMessage(message);
  }

  private void dataCollectionAgreement(Message message) {

    if (!message.hasText()) {
      return;
    }

    if (message.getText().equals(DENIED)) {
      executeSendMessage(message.getChatId().toString(),
          "К сожалению, без согласия на обработку персональных "
              + "данных, вы не сможете пользоваться ботом.");
      return;
    }

    KeyboardRow firstRow = new KeyboardRow();
    firstRow.add(getButton(ACCEPT, true));
    firstRow.add(getButton(DENIED, false));

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

  @SneakyThrows
  private void handleTextMessage(Message message) {
    if (message.hasText() && message.hasEntities()) {
      commandMethod(message);
    } else {
      switch (message.getText()) {
        case DENIED:
          executeSendMessage(message.getChatId().toString(),
              "К сожалению, без согласия на обработку персональных данных, "
                  + "вы не сможете пользоваться ботом.");
          break;

        case TASK:
          taskMethod(message);
          break;

        case LEADER:
          leaderMethod(message);
          break;

        case ABOUT:
//          aboutMethod(message);
          break;
      }
    }
  }

  private void leaderMethod(Message message) {

    List<PlayerDto> topPlayerList = service.findAll().stream().sorted((o1, o2) -> {
      Long x = o1.getJavaScore() + o1.getPythonScore() + o1.getDataScore();
      Long y = o2.getJavaScore() + o1.getPythonScore() + o1.getDataScore();
      return y.compareTo(x);
    }).limit(10).toList();

    for (PlayerDto player : topPlayerList) {
      executeSendMessage(message.getChatId().toString(),
          player.getUserName() + " " + (player.getJavaScore() + player.getPythonScore()
              + player.getDataScore()));
    }
  }

  private void commandMethod(Message message) {
    Optional<MessageEntity> commandEntity = message.getEntities().stream()
        .filter(e -> e.getType().equals(BOT_COMMAND)).findFirst();

    if (commandEntity.isPresent()) {
      String command = message.getText()
          .substring(commandEntity.get().getOffset(), commandEntity.get().getLength());

      //leave switch construction for possible additional commands
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

    switch (callbackQuery.getData()) {

      case "JAVA":
        //TODO: create javaMethod();
        executeSendMessage(message.getChatId().toString(), "You have chosen Java!");
        break;

      case "PYTHON":
        //TODO: create pythonMethod();
        executeSendMessage(message.getChatId().toString(), "You have chosen Python!");
        break;

      case "DATA":
        //TODO: create dataMethod();
        executeSendMessage(message.getChatId().toString(), "You have chosen Data!");
        break;

      case "BACK":
        startMethod(message);
        break;
    }
  }

  @SneakyThrows
  private void taskMethod(Message message) {
    List<List<InlineKeyboardButton>> buttons = List.of(
        List.of(getInlineButton(JAVA), getInlineButton(GO), getInlineButton(DATA)),
        List.of(getInlineButton(BACK))
    );

    executeSendMessage(message.getChatId().toString(),
        "Пожалуйста, выберите свой язык программирования:",
        InlineKeyboardMarkup.builder().keyboard(buttons).build());
  }

  private void startMethod(Message message) {

    KeyboardRow firstLine = new KeyboardRow();

    firstLine.add(TASK);
    firstLine.add(LEADER);
    firstLine.add(ABOUT);

    List<KeyboardRow> keyboard = List.of(firstLine);

    executeSendMessage(message.getChatId().toString(),
        "\uD83D\uDCF1 Добро пожаловать в GiveMeTask бот!",
        ReplyKeyboardMarkup.builder().selective(true).resizeKeyboard(true).oneTimeKeyboard(true)
            .keyboard(keyboard).build());
  }

  @SneakyThrows
  private void executeSendMessage(String chatId, String text) {
    execute(SendMessage.builder().chatId(chatId).text(text).build());
  }

  @SneakyThrows
  private void executeSendMessage(String chatId, String text, InlineKeyboardMarkup buttons) {
    execute(SendMessage.builder().chatId(chatId).text(text).replyMarkup(buttons).build());
  }

  @SneakyThrows
  private void executeSendMessage(String chatId, String text, ReplyKeyboardMarkup buttons) {
    execute(SendMessage.builder().chatId(chatId).text(text).replyMarkup(buttons).build());
  }


  private InlineKeyboardButton getInlineButton(String buttonName) {
    return InlineKeyboardButton.builder().text(buttonName)
        .callbackData(buttonName.split(" ")[1].toUpperCase()).build();
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
    return KeyboardButton.builder().text(buttonName).requestContact(requestContract).build();
  }
}