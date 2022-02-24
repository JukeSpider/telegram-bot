package com.juke.controller;

import com.juke.dto.PlayerDto;
import com.juke.service.PlayerServiceImpl;
import java.util.List;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Setter
@Getter
@RequiredArgsConstructor
@RequestMapping("/player")
@RestController
public class PlayerController {

  private final PlayerServiceImpl service;

  @GetMapping("/{username}")
  public ResponseEntity<PlayerDto> findByUsername(@PathVariable String username) {
    return ResponseEntity.status(HttpStatus.OK).body(service.findByUsername(username));
  }

  @GetMapping("/all")
  public ResponseEntity<List<PlayerDto>> findAll() {
    return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
  }

  @PostMapping
  public ResponseEntity<PlayerDto> save(@RequestBody PlayerDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
  }

  @PutMapping("/delete/{username}")
  public void delete(@PathVariable String username) {
    service.deleteByUsername(username);
  }
}