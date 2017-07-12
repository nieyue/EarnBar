package com;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.nieyue.interceptor.SessionControllerInterceptor;
@SpringBootApplication
@Configuration
//@EnableRedisHttpSession
//@Import({DynamicDataSourceRegister.class})
@ServletComponentScan
public class MyApplication extends WebMvcConfigurerAdapter implements ApplicationListener<ApplicationReadyEvent> {
	@Resource
	StringRedisTemplate stringRedisTemplate;
	@Resource
	SessionControllerInterceptor sessionControllerInterceptor;
	
	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class,args);
		
	}
	 @Override
	  public void addCorsMappings(CorsRegistry registry) {
	    registry.addMapping("/**").allowedOrigins("*")
	   .allowCredentials(true)
	    .allowedMethods("POST","GET", "OPTIONS", "DELETE").allowedHeaders("x-requested-with");
	  }
	  /** 
	     * 配置拦截器 
	     * @author lance 
	     * @param registry 
	     */  
	    public void addInterceptors(InterceptorRegistry registry) {  
	        registry.addInterceptor(sessionControllerInterceptor).addPathPatterns("/**");  
	    } 
	@Bean
	public EmbeddedServletContainerCustomizer  containerCustomizer() {

		return new EmbeddedServletContainerCustomizer() {
	        @Override
	        public void customize(ConfigurableEmbeddedServletContainer container) {

	            //ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.html");
	            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
	           // ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");

	            container.addErrorPages( error404Page);
	        }
	    };
	}
	
	@Value("${myPugin.projectName}")
	String projectName;
	@Value("${myPugin.shareDomain}")
	String shareDomain;
	@Value("${myPugin.gmwDomain}")
	String gmwDomain;
	@Value("${myPugin.ssDomain}")
	String ssDomain;
	@Value("${myPugin.adDomain}")
	String adDomain;
	/**
	 * 容器初始化
	 * @param event
	 */
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		//初始化合伙人收益增量
		BoundValueOperations<String, String> bvo=stringRedisTemplate.boundValueOps(projectName+"ScaleIncrement");
		if(bvo.size()<=0){
			bvo.set("0");
		}
		//初始化基准收益单价
		BoundValueOperations<String, String> etfubp=stringRedisTemplate.boundValueOps(projectName+"FinanceUnitBaseProfit");
		if(etfubp.size()<=0){
			etfubp.set("1");
		}
		//初始化分享页面域名
		BoundValueOperations<String, String> etsd=stringRedisTemplate.boundValueOps(projectName+"ShareDomain");
		if(etsd.size()<=0){
			etsd.set(shareDomain);
		}
		//初始化光明网页面域名
		BoundZSetOperations<String, String> gmwbzso=stringRedisTemplate.boundZSetOps(projectName+"GmwDomain");
		if(gmwbzso.size()<=0){
			gmwbzso.add(gmwDomain, 1);
		}
		//初始化三俗页面域名
		BoundValueOperations<String, String> sszso=stringRedisTemplate.boundValueOps(projectName+"SsDomain");
		if(sszso.size()<=0){
			sszso.set(ssDomain);
		}
		//初始化跨域广告页面域名
		BoundValueOperations<String, String> etad=stringRedisTemplate.boundValueOps(projectName+"AdDomain");
		if(etad.size()<=0){
			etad.set(adDomain);
		}
		
		
	}
	}
/*public class MyApplication extends SpringBootServletInitializer {
	@Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder application) {
        return application.sources(MyApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}*/