package com.ep.sysinfo.MafisSyStemInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
public class MafisSyStemInfoApplication {

	private static final Logger logger = LoggerFactory.getLogger(MafisSyStemInfoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MafisSyStemInfoApplication.class, args);
        logger.info("SystemInfoApplication - Application Started at {}", new Date());
	}

}
