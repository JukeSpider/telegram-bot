package com.juke.telegram;

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
import org.telegram.telegrambots.meta.api.objects.Chat;
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
public class BotEngine extends TelegramLongPollingBot {

  private static final String TOKEN = "5183559762:AAHUdtOqdR_BQNfJynVaAjClGhbiDCL5HE4";
  private static final String BOT_USERNAME = "jukeBot";

  private final PlayerServiceImpl service;

  @Override
  public String getBotUsername() {
    return BOT_USERNAME;
  }

  @Override
  public String getBotToken() {
    return TOKEN;
  }


  @SneakyThrows
  @Override
  public void onUpdateReceived(Update update) {

    if (update.hasMessage()) {

      if (update.getMessage().getFrom().getIsBot()) {
        return;
      }

      Message message = update.getMessage();
      boolean isRegisteredPerson = service.hasTelegramId(message.getChatId());

      if (message.hasText()) {
        if(message.getText().equals("❌ Отклонить")) {
          executeSendMessage(
              message.getChatId().toString(),
              "К сожалению, без согласия на обработку персональных "
                  + "данных, вы не сможете пользоваться ботом."
          );
          return;
        }
      }

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

    if (update.hasCallbackQuery()) {
      handleCallbackQuery(update.getCallbackQuery());
    }
  }

  private void dataCollectionAgreement(Message message) {

    if (!message.hasText()) {
      return;
    }

    KeyboardRow firstRow = new KeyboardRow();
    firstRow.add(KeyboardButton.builder().text("✅ Предоставить").requestContact(true).build());
    firstRow.add(KeyboardButton.builder().text("❌ Отклонить").build());

    List<KeyboardRow> keyboard = List.of(firstRow);

    executeSendMessage(message.getChatId().toString(),
        "Пожалуйста, предоставьте доступ к Вашему контакту:",
        ReplyKeyboardMarkup.builder()
            .selective(true)
            .resizeKeyboard(true)
            .oneTimeKeyboard(true)
            .keyboard(keyboard)
            .build());
  }

  private void registration(Message message) {
    Contact contact = message.getContact();

    if (service.hasTelegramId(contact.getUserId())) {
      executeSendMessage(message.getChatId().toString(), "Вы уже зарегистрированы!");
      return;
    }

    PlayerDto registrationDto = PlayerDto.builder()
        .telegramId(contact.getUserId())
        .userName(message.getChat().getUserName())
        .phone(contact.getPhoneNumber())
        .build();

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
        case "❌ Отклонить":
          executeSendMessage(message.getChatId().toString(),
              "К сожалению, без согласия на обработку персональных данных, "
                  + "вы не сможете пользоваться ботом.");
          break;

        case "\uD83D\uDCCA Task":
          taskMethod(message);
          break;

        case "About ❓":
          //TODO: create aboutMethod();
          break;
      }
    }
  }

  private void commandMethod(Message message) {
    Optional<MessageEntity> commandEntity = message.getEntities().stream()
        .filter(e -> e.getType().equals("bot_command")).findFirst();

    if (commandEntity.isPresent()) {
      String command = message.getText()
          .substring(commandEntity.get().getOffset(), commandEntity.get().getLength());

      switch (command) {
        case "/start":
          startMethod(message);
          break;
      }
    }
  }

  @SneakyThrows
  private void handleCallbackQuery(CallbackQuery callbackQuery) {
    Message message = callbackQuery.getMessage();
    String callBack = callbackQuery.getData();

    switch (callBack) {

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

      case "PROFILE":
        profileMethod(message);
        break;
    }
  }

  private void menuMethod(Message message) {
    KeyboardRow firstLine = new KeyboardRow();
    firstLine.add(Button.TASK.getName());
    firstLine.add(Button.SETTINGS.getName());
    firstLine.add(Button.ABOUT.getName());

    List<KeyboardRow> keyboard = List.of(firstLine);

    executeSendMessage(message.getChatId().toString(),
        "\uD83D\uDCA0 Главное меню:",
        ReplyKeyboardMarkup.builder()
            .selective(true)
            .resizeKeyboard(true)
            .oneTimeKeyboard(true)
            .keyboard(keyboard)
            .build());
  }

  @SneakyThrows
  private void taskMethod(Message message) {
    List<List<InlineKeyboardButton>> buttons = List.of(
        List.of(
            getInlineButton(Button.JAVA.getName()),
            getInlineButton(Button.PYTHON.getName()),
            getInlineButton(Button.DATA.getName())
        ),
        List.of(
            getInlineButton(Button.BACK.getName())
        )
    );

    executeSendMessage(
        message.getChatId().toString(),
        "Please, choose your language:",
        InlineKeyboardMarkup.builder().keyboard(buttons).build()
    );
  }

  private void startMethod(Message message) {

    KeyboardRow firstLine = new KeyboardRow();

    firstLine.add(Button.TASK.getName());
    firstLine.add(Button.LEADERBOARD.getName());
    firstLine.add(Button.ABOUT.getName());

    List<KeyboardRow> keyboard = List.of(firstLine);

    executeSendMessage(message.getChatId().toString(),
        "\uD83D\uDCF1 Добро пожаловать в GiveMeTask бот!",
        ReplyKeyboardMarkup.builder()
            .selective(true)
            .resizeKeyboard(true)
            .oneTimeKeyboard(true)
            .keyboard(keyboard)
            .build());
  }

  private void profileMethod(Message message) {
    PlayerDto profileDto = service.findByTelegramId(message.getChat().getId());
    executeSendMessage(message.getChatId().toString(), profileDto.toString());
  }

  @SneakyThrows
  private void executeSendMessage(String chatId, String text) {
    execute(
        SendMessage.builder()
            .chatId(chatId)
            .text(text)
            .build()
    );
  }

  @SneakyThrows
  private void executeSendMessage(String chatId, String text, InlineKeyboardMarkup buttons) {
    execute(
        SendMessage.builder()
            .chatId(chatId)
            .text(text)
            .replyMarkup(buttons)
            .build()
    );
  }

  @SneakyThrows
  private void executeSendMessage(String chatId, String text, ReplyKeyboardMarkup buttons) {
    execute(
        SendMessage.builder()
            .chatId(chatId)
            .text(text)
            .replyMarkup(buttons)
            .build()
    );
  }


  private InlineKeyboardButton getInlineButton(String buttonName) {
    return InlineKeyboardButton.builder()
        .text(buttonName)
        .callbackData(buttonName.split(" ")[1].toUpperCase())
        .build();
  }

//  private KeyboardButton getButton(String buttonName) {
//
//  }
}