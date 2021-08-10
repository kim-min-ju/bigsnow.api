package kr.co.bigsnow.config;
//
//import kr.co.ezpmp.core.filter.OscClientHttpRequestInterceptor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.http.client.BufferingClientHttpRequestFactory;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.web.client.RestTemplate;
//
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//import java.security.SecureRandom;
//import java.security.cert.X509Certificate;
//
//@Slf4j
//@Configuration
//public class RestTemplateConfig {
//
//    @Autowired
//    private OscClientHttpRequestInterceptor oscClientHttpRequestInterceptor;
//
//    @Bean
//    @Primary
//    public RestTemplate restTemplate() {
//        log.info(">>> EZPMP restTemplate init");
//        RestTemplate restTemplate = new RestTemplate(bufferingClientHttpRequestFactory());
//        restTemplate.getInterceptors().add(oscClientHttpRequestInterceptor);
//        return restTemplate;
//    }
//
//    @Bean
//    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
//        log.info(">>> EZPMP clientHttpRequestFactory init");
//
//        TrustManager[ ] certs = new TrustManager[ ] {
//            new X509TrustManager() {
//
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//                public void checkServerTrusted(X509Certificate[] chain, String authType) {}
//                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
//            }
//        };
//
//        SSLContext ctx = null;
//        try {
//            ctx = SSLContext.getInstance("SSL");
//            ctx.init(null, certs, new SecureRandom());
//        } catch (java.security.GeneralSecurityException ignored) {
//        }
//
//        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
//        CloseableHttpClient httpClient = HttpClientBuilder.create()
//                .setMaxConnTotal(600) // connection pool 적용
//                .setMaxConnPerRoute(120) // connection pool 적용
//                .setSSLHostnameVerifier((hostname, session) -> true)
//                .setSSLContext(ctx)
//                .build();
//
//        clientHttpRequestFactory.setHttpClient(httpClient);
//        //clientHttpRequestFactory.setConnectionRequestTimeout(4000);
//        //clientHttpRequestFactory.setReadTimeout(4000);
//        return clientHttpRequestFactory;
//    }
//
//    @Bean
//    public BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory() {
//        log.info(">>> EZPMP bufferingClientHttpRequestFactory init");
//
//        BufferingClientHttpRequestFactory clientHttpRequestFactory = new BufferingClientHttpRequestFactory(clientHttpRequestFactory());
//
//        return clientHttpRequestFactory;
//    }
//
//}
