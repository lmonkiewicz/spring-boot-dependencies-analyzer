package com.lmonkiewicz.spring.analyzer;

import com.lmonkiewicz.spring.analyzer.adapter.metadata.MetadataProvider;
import com.lmonkiewicz.spring.analyzer.config.properties.AnalyzerProperties;
import com.lmonkiewicz.spring.analyzer.domain.AnalyzerAPI;
import com.lmonkiewicz.spring.analyzer.domain.dto.metadata.ApplicationMetadata;
import com.lmonkiewicz.spring.analyzer.domain.exception.AnalyzerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {


  private final AnalyzerAPI analyzerAPI;
  private final AnalyzerProperties analyzerProperties;
  private final MetadataProvider metadataProvider;

  @Autowired
  public ApplicationStartupListener(AnalyzerAPI analyzerAPI, AnalyzerProperties analyzerProperties, MetadataProvider metadataProvider) {
    this.analyzerAPI = analyzerAPI;
    this.analyzerProperties = analyzerProperties;
    this.metadataProvider = metadataProvider;
  }

  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {
    try {
      final ApplicationMetadata applicationInfo = metadataProvider.getApplicationInfo();

      analyzerAPI.loadIntoGraph(applicationInfo, analyzerProperties.isClearOnStart());

    } catch (AnalyzerException e) {
      log.error("Error while processing data", e);
    } catch (IOException e) {
      log.error("Error while reading input data", e);
    }

  }
 
}