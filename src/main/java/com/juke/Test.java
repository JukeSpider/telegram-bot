package com.juke;

import java.time.LocalDate;
import java.time.Month;

public class Test {

  public static void main(String[] args) {
    LocalDate end = LocalDate.of(2021, Month.JANUARY, 3);
    LocalDate start = LocalDate.of(2020, Month.DECEMBER, 31);
    int length;

    if (end.getDayOfYear() - start.getDayOfYear() + 1 <= 0) {
      length = end.getDayOfYear() + start.lengthOfYear() - start.getDayOfYear() + 1;
    } else {
      length = end.getDayOfYear() - start.getDayOfYear() + 1;
    }

    System.out.println(length);
  }
}