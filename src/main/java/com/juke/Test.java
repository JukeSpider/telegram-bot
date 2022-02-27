package com.juke;

import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class Test {

  public static void main(String[] args) {

    List<Message> list = List.of(
        new Message(3L, 5L, 1L), //9
        new Message(1L, 8L, 9L), //18
        new Message(4L, 6L, 2L), //12
        new Message(4L, 3L, 11L), //18
        new Message(0L, 7L, 2L), //17

        new Message(10L, 4L, 5L), //19
        new Message(3L, 7L, 9L), //19
        new Message(2L, 1L, 4L), //7
        new Message(9L, 12L, 0L), //21
        new Message(8L, 8L, 4L), //20

        new Message(0L, 0L, 6L), //17
        new Message(1L, 7L, 8L), //16
        new Message(9L, 2L, 6L), //17
        new Message(10L, 11L, 2L), //23
        new Message(7L, 4L, 5L), //16

        new Message(9L, 8L, 4L), //21
        new Message(10L, 12L, 5L), //27
        new Message(0L, 6L, 3L), //9
        new Message(13L, 2L, 1L), //16
        new Message(20L, 20L, 20L) //16
    );
    List<Message> top10Messages = list.stream()
        .sorted((o1, o2) -> {
          Long x = o1.getPoint1() + o1.getPoint2() + o1.getPoint3();
          Long y = o2.getPoint1() + o2.getPoint2() + o2.getPoint3();
          return y.compareTo(x);
        })
        .limit(5)
        .toList();
    for (Message message : top10Messages) {
      System.out.println(message);
    }
  }
}

@Setter
@Getter
@AllArgsConstructor
class Message {

  private Long point1;
  private Long point2;
  private Long point3;

  @Override
  public String toString() {
    return "Message {" + point1 + ", " + point2 + ", " + point3 + "} = " + (point1 + point2 + point3);
  }
}