package com.personal.kafka.config;

import com.personal.kafka.kafkaInterceptor.ProducerInterceptorWithLogging;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import java.util.*;

@Configuration
public class KafkaInterceptorConfig {

    @Bean
//    @ConditionalOnProperty(name = "spring.kafka.producer.interceptor.enabled", havingValue = "true")
    public BeanPostProcessor producerFactoryInterceptor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof DefaultKafkaProducerFactory<?, ?> producerFactory) {
                    Map<String, Object> configs = new HashMap<>(producerFactory.getConfigurationProperties());

                    Object existingInterceptor = configs.get(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG);
                    List<String> interceptors = new ArrayList<>();

                    // Multiple interceptors in a String separated by comma
                    if (existingInterceptor instanceof String s) {
                        for (String interceptorStr : s.split(",")) {
                            String item = interceptorStr.trim();
                            if (!item.isBlank()) {
                                interceptors.add(item);
                            }
                        }
                        // Multiple interceptors in a list
                    } else if (existingInterceptor instanceof Collection<?> interceptorList) {
                        interceptorList.forEach(item -> {
                            if (item != null) interceptors.add(item.toString());
                        });
                    }

                    String loggingInterceptor = ProducerInterceptorWithLogging.class.getName();
                    if (!interceptors.contains(loggingInterceptor)) {
                        interceptors.add(loggingInterceptor);
                    }

                    configs.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

                    // apply the new configs to the factory
                    producerFactory.updateConfigs(configs);
                }
                return bean;
            }
        };
    }
}
