package com.juke.controller;

import com.juke.dto.TaskDto;
import com.juke.service.TaskServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/task")
@RestController
public class TaskController {

  private final TaskServiceImpl service;

  @GetMapping("/{taskNumber}")
  public ResponseEntity<TaskDto> findByTaskNumber(@PathVariable String taskNumber) {
    return ResponseEntity.status(HttpStatus.OK).body(service.findByTaskNumber(taskNumber));
  }

  @GetMapping("/all")
  public ResponseEntity<List<TaskDto>> findALl() {
    return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
  }

  @PostMapping
  public ResponseEntity<TaskDto> save(@RequestBody TaskDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
  }

  @PutMapping("/delete/{taskNumber}")
  public void deleteByTaskNumber(@PathVariable String taskNumber) {
    service.deleteByTaskNumber(taskNumber);
  }
}