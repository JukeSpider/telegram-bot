package com.juke;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

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

        new Message(5L, 6L, 6L), //17
        new Message(1L, 7L, 8L), //16
        new Message(9L, 2L, 6L), //17
        new Message(10L, 11L, 2L), //23
        new Message(7L, 4L, 5L), //16

        new Message(9L, 8L, 4L), //21
        new Message(10L, 12L, 5L), //27
        new Message(0L, 6L, 3L), //9
        new Message(13L, 2L, 1L), //16
        new Message(12L, 0L, 4L) //16
    );

    List<Long> topTenMessage = list.stream().map(x -> x.getPoint1() + x.getPoint2() + x.getPoint3()).sorted(Comparator.reverseOrder()).limit(10).toList();

    for (Long message : topTenMessage) {
      System.out.println(message);
    }
  }
}

class Message {

  private Long point1;
  private Long point2;
  private Long point3;

  public Message(Long point1, Long point2, Long point3) {
    this.point1 = point1;
    this.point2 = point2;
    this.point3 = point3;
  }

  public Long getPoint1() {
    return point1;
  }

  public void setPoint1(Long point1) {
    this.point1 = point1;
  }

  public Long getPoint2() {
    return point2;
  }

  public void setPoint2(Long point2) {
    this.point2 = point2;
  }

  public Long getPoint3() {
    return point3;
  }

  public void setPoint3(Long point3) {
    this.point3 = point3;
  }
}
