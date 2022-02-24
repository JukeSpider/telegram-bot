package com.juke.telegram;

public enum Button {

  JAVA("✅ Java"),
  PYTHON("✅ Python"),
  DATA("✅ Data"),
  BACK("❌ Back"),
  REG("\uD83D\uDCDD Registration"),
  TASK("\uD83D\uDCCA Task"),
  SETTINGS("⚙ Settings"),
  ABOUT("❓ About"),
  LEADERBOARD("\uD83C\uDFC6 Leader");

  private final String name;

  Button(String name) {
    this.name = name;
  }

  public String getName(){
    return name;
  }
}
