package com.protobuf.consumer.protobufconsumer.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.protobuf.producer.protobufproducer.emp.Employee.Emp;
import com.protobuf.producer.protobufproducer.emp.empDTO.EmpDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
public class CommandLinerSonarTest {

	private static final String EMP_URL = "http://localhost:8087/emp/1";
	private static final String EMP_URL1 = "http://localhost:8087/empdto/1";

	@GetMapping(value = "/hit/consumer")
	public void run() throws Exception {

		for(int i=1;i<=20;i++) {
			callByUnirest(i);
			//callByRestTemplate(i);
			//callByWebClient(i);
		}		
	}

	private void callByUnirest(int number) throws Exception{
		long start = (new Date()).getTime();
		Emp empResponse = Emp.parseFrom(Unirest.get(EMP_URL)
				.asBinary().getRawBody());
		long time = (new Date()).getTime() - start;
		System.out.println("\n"+number+" :: Consumer Protobuf for :: "+empResponse.getEmpName()+" :: Response Time Taken is :: " + time + "ms");

		long start2 = (new Date()).getTime();
		JsonNode jsonNode = Unirest.get(EMP_URL1)
				.asJson().getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		EmpDto empDto = objectMapper.readValue(jsonNode.toString(), EmpDto.class);

		long time2 = (new Date()).getTime() - start2;
		System.out.println(number+" :: Consumer JSON for :: "+empDto.getEmp_name()+" :: Response Time Taken is :: " + time2 + "ms");
	}
	
	private void callByWebClient(int number){
		long start = (new Date()).getTime();
		ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(64 * 1024 * 1024)).build();
		WebClient webClient1 = WebClient.builder()
				.baseUrl(EMP_URL)
				.defaultCookie("cookieKey", "cookieValue")
				.defaultUriVariables(Collections.singletonMap("url", EMP_URL))
				.exchangeStrategies(exchangeStrategies)
				.build();
		Mono<Emp> response1 = webClient1.get()
				//.accept(MediaType.parse("application/x-protobuf"))
				.retrieve()
				.bodyToMono(Emp.class);

		Emp empResponse = response1.block();
		long time = (new Date()).getTime() - start;
		System.out.println("\n"+number+" :: Consumer Protobuf for :: "+empResponse.getEmpName()+" :: Response Time Taken is :: " + time + "ms");

		long start2 = (new Date()).getTime();
		WebClient webClient = WebClient.builder()
				.baseUrl(EMP_URL1)
				.defaultCookie("cookieKey", "cookieValue")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", EMP_URL1))
				.exchangeStrategies(exchangeStrategies)
				.build();
		Mono<EmpDto> response = webClient.get()
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(EmpDto.class);

		EmpDto empDto = response.block();
		long time2 = (new Date()).getTime() - start2;
		System.out.println(number+" :: Consumer JSON for :: "+empDto.getEmp_name()+" :: Response Time Taken is :: " + time2 + "ms");
	}
	
	
	private void callByRestTemplate(int number){
	
		RestTemplate restTemplate=new RestTemplate(Arrays.asList(new ProtobufHttpMessageConverter()));

		long start = (new Date()).getTime();
		ResponseEntity<Emp> empResponse = restTemplate.getForEntity(EMP_URL, Emp.class);
		long time = (new Date()).getTime() - start;
		System.out.println("\n"+number+" :: Consumer Protobuf  for :: "+empResponse.getBody().getEmpName()+" :: Response Time Taken is :: " + time + "ms");
		
		RestTemplate restTemplateJson=new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
		messageConverters.add(converter);
		restTemplateJson.setMessageConverters(messageConverters);
		
		long start2 = (new Date()).getTime();
		ResponseEntity<EmpDto> empResponseJson = restTemplateJson.getForEntity(EMP_URL1, EmpDto.class);
		long time2 = (new Date()).getTime() - start2;
		System.out.println(number+" :: Consumer JSON for :: "+empResponseJson.getBody().getEmp_name()+" :: Response Time Taken is :: " + time2 + "ms");
	}
}
