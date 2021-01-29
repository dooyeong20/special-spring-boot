# SSL (Secure Socket Layer)

- 포트 번호 : 443
- prefix : https://
- 공개키-개인키의 대칭키 기반
- 넷스케이프에서 공개 후 TLS로 이름을 바꿔 국제 표준으로 등록됨.
- OSI 7 Layer 중 전송계층 프로토콜에 해당 됨
- 대칭키/공개키 암호화. 일방향 해시함수. 메시지 인증코드, 의사 난수 생성, 전자 서명 등을 조합하여 안전한 통신을 수행



# 대칭키과 비대칭키

- 대칭키 : 암호화, 복호화에 사용되는 키가 서로 동일함
  - 따라서 절대 유출되면 안됨. = 비밀키
  - 대칭키 기반 알고리즘 : DES, 3DES, SEED, AES 등
  - 장점 : 비대칭키보다 빠른 연산 수행.
  - 단점 : 
    - 상대에게도 같은 키를 전송해야 함.
    - 인증되지 않은 상대..
    - 중간자에게 키를 탈취 당할 가능성..
    - 키교환 알고리즘이 요구됨.

- 비대칭키 : 암호화와 복호화에 사용되는 키가 서로 다름. 공개키/개인키라도고 함.
  - 비대칭키 기반 알고리즘 :  RSA, ECC
  - 순서
    1. A가 키 생성을 한다. 
       - 1쌍의 키가 생성된다. `개인키`와 `공개키`
       - `개인키`는 자신(암호화한 자)이 갖고 절대 유출하지 않는다.
       - `공개키`는 상대에게 제공해준다.
    2. B는 A가 보낸 `공개키`로 전송할 내용을 암호화한다.
    3. 내용을 받은 A는 자신만이 가진 `개인키`로 내용을 복호화한다.
    4. 반대로 B가 A에게 내용을 전송하고 싶으면 A가 했던 행위를 똑같이 하면 된다.
  - 단점 : 대칭키에 비해 느린 연산. 대용량 데이터 암호화에 적합하지 않음.
- 단점 보완 : 전송할 내용은 대칭키로 암복호화. 이때 사용되는 대칭키를 비대칭키로 암복호화.
- 그래도 여전히 존재하는 단점 : 사이트가 가짜 사이트인 경우



# 인증서

- 인증기관(CA; Certificate Authority)으로부터 인증서를 발급받아 인증된 사이트가 되어야 한다.



## 인증서와 handshaking

​	https://itsandtravels.blogspot.com/2019/07/ssl-ssl-handshake.html

​	https://m.blog.naver.com/alice_k106/221468341565		

​	https://opentutorials.org/course/228/4894



# 구현

## 1. 키스토어 만들기 (공개키, 개인키 생성)

### application.properties

```properties
server.ssl.key-store=keystore.p12
server.ssl.key-store-password=123456
server.ssl.keyStoreType=PKCS12
server.ssl.keyAlias=tomcat
```



### generate-keystore.sh

```sh
keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 4000
```



실행 - Bad Request

앞에 `https://`  붙여보기. <-- not private 



## 2. http 도 접속 가능하게 하기

### application.properties

```properties
server.ssl.key-store=keystore.p12
server.ssl.key-store-password=123456
server.ssl.keyStoreType=PKCS12
server.ssl.keyAlias=tomcat
server.port=8443
```



### Main 클래스

```java
package com.megait.https_http2;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HttpsHttp2Application {

	public static void main(String[] args) {
		SpringApplication.run(HttpsHttp2Application.class, args);
	}

	@Bean
	public ServletWebServerFactory serverFactory(){
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		tomcat.addAdditionalTomcatConnectors(createStandardConnector());
		return tomcat;
	}

	private Connector createStandardConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setPort(8080);
		return connector;
	}
}
```

이렇게 하면 우리 서버는 

`http://127.0.0.1:8080`

`https://127.0.0.1:8443` 

두 가지 방법으로 접속이 가능함.



****



# HTTP/2

기존의 http/1.1을 보완하여 등장한 새로운 버전의 http 프로토콜

https://developers.google.com/web/fundamentals/performance/http2?hl=ko







## application.properties

```properties
server.http2.enabled=true
```



주의 : 서블릿 컨테이너마다 설정 방법이 모두 다름.

tomcat 8.x 는 조금 복잡함. undertow, tomcat 9.0(JDK 9) 는 위 외부설정 1개면 됨.

