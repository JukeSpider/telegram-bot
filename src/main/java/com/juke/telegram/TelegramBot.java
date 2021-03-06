package com.juke.telegram;

import static com.juke.telegram.VarConstant.ACCEPT;
import static com.juke.telegram.VarConstant.BACK;
import static com.juke.telegram.VarConstant.BOT_COMMAND;
import static com.juke.telegram.VarConstant.CSV_NAME;
import static com.juke.telegram.VarConstant.DATA;
import static com.juke.telegram.VarConstant.DAY_NUMBER;
import static com.juke.telegram.VarConstant.DECLINE;
import static com.juke.telegram.VarConstant.GO;
import static com.juke.telegram.VarConstant.HEADERS;
import static com.juke.telegram.VarConstant.JAVA;
import static com.juke.telegram.VarConstant.LEADER;
import static com.juke.telegram.VarConstant.PLAYER_LIST;
import static com.juke.telegram.VarConstant.PROFILE;
import static com.juke.telegram.VarConstant.TASK;

import com.juke.dto.PlayerDto;
import com.juke.service.PlayerServiceImpl;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
  private static final String ADMIN = System.getenv("ADMIN");
  private static final LocalDate START_DATE = LocalDate.parse("2022-02-20");

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
          "?????? ???? ?????????????????? ???? ?????????????????? ???? ???????????? ??????????!");
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
          "?? ??????????????????, ?????? ???????????????? ???? ?????????????????? ???????????????????????? "
              + "????????????, ???? ???? ?????????????? ???????????????????????? ??????????.");
      return;
    }

    KeyboardRow firstRow = new KeyboardRow();
    firstRow.add(getButton(ACCEPT, true));
    firstRow.add(getButton(DECLINE, false));

    List<KeyboardRow> keyboard = List.of(firstRow);

    executeSendMessage(message.getChatId().toString(),
        "????????????????????, ???????????????????????? ???????????? ?? ???????????? ????????????????:",
        getReplyKeyboardMarkup(true, true, true, keyboard));
  }

  private void registration(Message message) {

    Contact contact = message.getContact();

    if (service.hasTelegramId(contact.getUserId())) {
      executeSendMessage(message.getChatId().toString(), "???? ?????? ????????????????????????????????!");
      return;
    }

    PlayerDto registrationDto = PlayerDto.builder()
        .telegramId(contact.getUserId())
        .userName(message.getChat().getUserName())
        .phone(phoneConverter(contact.getPhoneNumber()))
        .build();

    if (phoneConverter(contact.getPhoneNumber()).equals(ADMIN)) {
      service.save(registrationDto, true);
      executeSendMessage(message.getChatId().toString(),
          """
              ?????????????????????? ???????????? ??????????????!
              ???? ?????????????????? ??????????????????????????????.
              ?????? ???????????????? ???????????????????????????? ??????????????.
              """);
    } else {
      service.save(registrationDto, false);
      executeSendMessage(message.getChatId().toString(), "?????????????????????? ???????????? ??????????????!");
    }

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
            "?? ??????????????????, ?????? ???????????????? ???? ?????????????????? ???????????????????????? ????????????, "
                + "???? ???? ?????????????? ???????????????????????? ??????????.");
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
        "????????????????????, ???????????????? ???????? ???????? ????????????????????????????????:",
        InlineKeyboardMarkup.builder().keyboard(buttons).build());
  }

  private void profileMethod(Message message) {
    PlayerDto player = service.findByTelegramId(message.getChatId());

    String playerProfile = String.format("""
            ??? Username: %s
            ??? Phone: %s
            ??? Total points: %d
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

    //TODO: ???????????????????? ???????? ???????? ?????? ?????????????????????? ?????? ?????????????????????? ???????????? ???????????? ??????????????;
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

      //TODO: ???????? ???????????????? ???????? SWITCH, ???????????????? ???????????????? ?????????? ??????????????;
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

      case PLAYER_LIST:
        getPlayerList(message);
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

    executeSendMessage(message.getChatId().toString(), "???????????????? ????????:",
        InlineKeyboardMarkup.builder().keyboard(buttons).build());
  }

  private void startMethod(Message message) {

    KeyboardRow firstLine = new KeyboardRow();
    firstLine.add(TASK);
    firstLine.add(LEADER);
    firstLine.add(PROFILE);

    List<KeyboardRow> keyboard = new ArrayList<>();
    keyboard.add(firstLine);

    if (service.findByTelegramId(message.getChatId()).isAdmin()) {
      KeyboardRow secondLine = new KeyboardRow();
      secondLine.add(PLAYER_LIST);
      keyboard.add(secondLine);
    }

    executeSendMessage(message.getChatId().toString(),
        "\uD83D\uDCF1 ?????????? ???????????????????? ?? GiveMeTask ??????!",
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

  private String phoneConverter(String phoneNumber) {

    if (phoneNumber.toCharArray()[0] == '7') {
      phoneNumber = phoneNumber.replaceFirst("7", "8");
    }

    return phoneNumber.replaceAll(" ", "")
        .replaceAll("\\+7", "8")
        .replaceAll("\\(", "")
        .replaceAll("\\)", "")
        .replaceAll("-", "");
  }

  @SneakyThrows
  private void getPlayerList(Message message) {

    try (CSVPrinter printer = new CSVPrinter(new FileWriter(CSV_NAME), CSVFormat.EXCEL)) {
      printer.printRecord((Object) HEADERS);
      printer.printRecord("1", "juke", "88005553535", "0", "12", "22", "34");
      printer.printRecord("2", "pavuk", "88618552210", "15", "1", "1", "17");
      printer.printRecord("3", "pipka", "88005684133", "0", "0", "4", "4");
      printer.printRecord("4", "ghamin", "88003551125", "11", "22", "33", "66");
    }
  }
}
