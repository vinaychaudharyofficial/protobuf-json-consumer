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
}
