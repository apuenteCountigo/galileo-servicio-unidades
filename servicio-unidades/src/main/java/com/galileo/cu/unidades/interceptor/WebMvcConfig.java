package com.galileo.cu.unidades.interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.MappedInterceptor;

@Component
public class WebMvcConfig {
	@Autowired
	UnidadesInterceptor unidadesInterceptor;

	@Bean
    public MappedInterceptor uniIntercept() {
        return new MappedInterceptor(new String[]{"/**"}, unidadesInterceptor);
    }
}
