package com.lmonkiewicz.spring.analyzer.app;

import com.lmonkiewicz.spring.analyzer.domain.AnalyzerAPI;
import com.lmonkiewicz.spring.analyzer.domain.exception.AnalyzerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {


  private final AnalyzerAPI analyzerAPI;

  @Autowired
  public ApplicationStartupListener(AnalyzerAPI analyzerAPI) {
    this.analyzerAPI = analyzerAPI;
  }

  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {
    try {
      analyzerAPI.processData();
    } catch (AnalyzerException e) {
      log.error("Error while processing data", e);
    }

  }
 
}