package br.com.amilitao.springpubsubtest;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;
import com.google.cloud.spring.pubsub.support.converter.PubSubMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;


@SpringBootApplication
public class SpringPubsubTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringPubsubTestApplication.class, args);
	}

	@Bean
	@Primary
	public PubSubMessageConverter pubSubMessageConverter(ObjectMapper objectMapper) {
		return new JacksonPubSubMessageConverter(objectMapper);
	}


}

