package com.lmonkiewicz.spring.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {


  private final AnalyzerService analyzerService;

  @Autowired
  public ApplicationStartup(AnalyzerService analyzerService) {
    this.analyzerService = analyzerService;
  }

  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {
    try {
      analyzerService.processData();
    } catch (IOException e) {
      log.error("Error while processing data", e);
    }

  }
 
}