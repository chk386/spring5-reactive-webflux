
# Iterable, Observable, Reactive Streams, Reactor

## Iterable Pattern
```java
    private static void iterablePattern() {
        Iterator<Integer> iter = list.iterator();

        while (iter.hasNext()) {
            log.info("iter : {}", iter.next());
        }

        log.info("exit");
    }
```

## Observable Pattern
```java
    private static void observablePattern() {
        ExamObservable observable = new ExamObservable(list);
        observable.addObserver((o, arg) -> log.info("observable : {}", arg));

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(observable);

        log.info("exit");
        executorService.shutdown();
    }

    @SuppressWarnings("deprecation")
    static class ExamObservable extends Observable implements Runnable {

        List<Integer> list;

        private ExamObservable(List<Integer> list) {
            this.list = list;
        }

        @Override
        public void run() {
            list.forEach(i -> {
                setChanged();
                notifyObservers(i);
            });
        }
    }
```
android, swing, javascript등 대부분의 GUI, web(client)에서 많이 사용되는 패턴(비동기)


## iterable vs observable pattern
```
     Iterable Pattern         <-----> Observable Pattern
     pull                     <-----> push
     int i = iter.next()      <-----> notifyObservers(i);;
     DATA method(Void)        <-----> Void method(DATA);
     
     duality : 결과는 같다.
```     

## reactive programming

### observable 단점
1. onComplete : 완료시점을 알수 없다.
1. onError
- 비동기로 처리할때 아주(!) 까다롭다
- 예외전파가 굉장히(!) 어렵다.
3. backpressure 
- publisher와 subscriber와의 속도차이를 조절
- A서버와 B서버 통신
![Backpressure-Limiting-Valve.jpg](/files/2189942280857478825)

### reactive streams API

#### 예제 소스
```java
@Slf4j
public class ReactiveExam {
    private static List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

    public static void main(String[] args) {
        Publisher<Integer> publisher = s -> {
            ExecutorService es = Executors.newSingleThreadExecutor();

            s.onSubscribe(new Subscription() {

                @Override
                public void request(long n) {
                    es.execute(() -> list.forEach(i -> {
                        s.onNext(i);

                        if(i == 3) {
                            s.onError(new RuntimeException("3은 에러야!!"));
                        }
                    }));

                    s.onComplete();
                    es.shutdown();
                }

                @Override
                public void cancel() {

                }
            });
        };

        Subscriber<Integer> subscriber = new Subscriber<>() {

            @Override
            public void onSubscribe(Subscription s) {
                log.info("onSubscribe", s);
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.info("onNext : {}", integer);
            }

            @Override
            public void onError(Throwable t) {
                log.error("onError", t);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        };

        publisher.subscribe(subscriber);
    }
}
```

### Reactive Streams vs Observable
Publisher<T> == Observable    발행
**Pubscriber.subsribe() == Observable.addObserver()**
Subscriber<T> == Observer      구독
Subscription : backpressure
Processor<T, R> : publisher에서 subscriber로 데이터가 push할때 중간에서 데이터 처리가 필요할때 사용(filter, transform, map)

### iterable vs reactive
![스크린샷 2018-04-11 오후 4.49.03.png](/files/2190508789840572293)
[Reactive Streams API](http://www.reactive-streams.org/reactive-streams-1.0.2-javadoc/org/reactivestreams/package-summary.html)

### publisher, subscriber Flow
![image](http://wiki.jikexueyuan.com/project/reactor-2.0/images/3.png)
[reactivestreams interface](http://www.reactive-streams.org/reactive-streams-1.0.0-javadoc/org/reactivestreams/package-summary.html)

[JDK9-FLOW API](https://community.oracle.com/docs/DOC-1006738)

 
### reactive programming(async non-blocking)
- Reactive Extensions(ReactiveX) , 에릭마이어
MS -> Netflix
구현체 : RxJAVA
![스크린샷 2018-04-11 오후 4.35.23.png](/files/2190501884013089539)

- Reactive Streams (표준 인터페이스 정의)
구현체 : [project reactor](https://projectreactor.io/)
Kaazing, Lightbend, Netflix, Pivotal, Red Hat, Twitter and many others


### reactive
#### 설명1
>reactive는 원래 외부 자극에 수동적으로 반응한다는 의미라서 부정적인 뉘앙스가 있었는데 프로그래밍 업계에 수용되면서 현대적 소프트웨어가 갖추어야 하는 바람직한 속성으로 의미가 달라졌다.
>
>즉, 리액티브 시스템이란 외부에서 들어오는 요청에 계속해서 응답하는 시스템이다. 이 논문은 리액티브 시스템을 구현하는 데에 적합한 프로그래밍 방법론에 대한 이야기를 담고 있는데, 이를 리액티브 프로그래밍으로 이해할 수 있다.
>
>여기에서 힌트를 하나 얻었다. 계속해서 응답한다는 건 ‘반응’한다는 뜻이다. 그렇다면 리액티브 프로그래밍의 목적이 외부에서 들어온 자극에 반응하는 구조를 만드는 데 있다고 볼 수 있지 않을까? 여기에서 ‘반응’은 아래 두 가지 의미를 내포한다.

><span style="color:#e11d21">자극은 밖에서 안으로 흐른다.</span>
><span style="color:#e11d21">자극이 있어야만 반응하는 수동성을 갖는다.</span>


>정리하자면 프로그램이 외부와 상호 작용하는 방식을 거꾸로 뒤집어서 수동적 반응성을 획득하는 일, 이것이 리액티브 프로그래밍의 목적이다. 에릭 마이어가 리액티브 프레임워크를 소개하는 강연에서 보여주었던 아래 그림은 리액티브 프로그래밍의 핵심을 잘 보여준다.

#### 설명2
>리액티브라는 말의 정의는 사실 간단하다. 
>사용자가 해당 소프트웨어를 사용하기 위해서 어떤 입력을 발생 시켰을 때 꾸물거리지 않고 최대한 빠른 시간 내에 응답을 한다는 의미다. 
>너무나 상식적인 이야기라서 오히려 이해하기 어렵다. 
>리액티브라는 용어의 의미를 정의하려고 노력하는 리액티브 선언(http://www.reactivemanifesto.org/)에 따르면 리액티브는 4가지 속성으로 이루어진다.
>**응답성(responsive), 유연성(resilient), 신축성(elastic), 그리고 메시지 주도(message driven)가** 그들이다.
![image](https://www.reactivemanifesto.org/images/reactive-traits.svg)


### Reactor
- reactive streams interface의 대표적인 구현체(주로 server-side, 유사 jvm언어 지원)
- spring5 reactive program
[project reactor](https://projectreactor.io/)

#### Mono, Flux
```java
@Slf4j
public class ReactorExam {

    public static void main(String[] args) {
        Scheduler scheduler = Schedulers.newParallel("parallel");

        log.info("start");

        Flux.just(1,2,3,4,5)
            .log()
            .subscribeOn(scheduler)
            .doOnComplete(() -> {
                log.info("doOnComplete");
                scheduler.dispose();
            })
            .doOnError(t -> log.error("doOnError", t))
            .map(String::valueOf)
            .subscribe(v -> {
                log.info(v);
                if("3".equals(v)) {
                    throw new RuntimeException("");
                }
            });

        log.info("end");
    }
}
```
  mono, flux는 데이터가 bouned, unbound 스트림이며 비동기
- mono, flux는 Publisher의 구현체
- 다양한 operator를 지원
[reactor-core](https://projectreactor.io/docs/core/release/api/)


# sync - async, blocking - nonblocking
blocking : 실행이 끝나고 나서 리턴(대기)
nonblocking :  바로 리턴

sync : A를 호출시 실행 쓰레드에 콜백을 전달하지 않음(완료여부를 알수 없음)
async : A를 호출시 실행 쓰레드에 콜백을 넘겨주어서 완료시 메인쓰레드에서 콜백을 실행(callback hell?)

sync-blocking :  java web 개발
sync-nonblocking : while(!future.isDone()) 
async-blocking : 비동기 프로그램에서 jdbc 호출
async-nonblocking : node.js, netty, spring5 reactive
![논블럭.png](/files/2190419757666507755)


# async programming
## spring4 
- @Async
- ListenableFuture<T>
- AsyncRestTemplate
- deferredResult<T>
- WebAsyncTask<T>
- CompletionStage<T>
- ResponseBodyEmitter

## jdk7
- Future<T>
- FutureTask<T>
- Callable<T>

## jdk8
- CompletableFuture<T>

## spring5
- Mono, Flux

## jdk9 (reactive streams API)
- Publisher
- Subscriber
- Subscription
- Processor

# spring webflux
- Java 8 lambda style routing and handling
![image](https://docs.spring.io/spring/docs/current/spring-framework-reference/images/spring-mvc-and-webflux-venn.png)

![image](https://docs.spring.io/spring/docs/5.0.0.M4/spring-framework-reference/html/images/web-reactive-overview.png)

[WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-programming-models)


# Functional Endpoints
## servlet 3.1
```java
@WebServlet(name = "productServlet", urlPatterns = "/products/*")
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String productNo = req.getPathInfo().replace("/", "");
        String type = req.getParameter("type");
        String contentType = req.getHeader(HttpHeaders.CONTENT_TYPE);
        String clientId = req.getHeader("clientId");

        if (clientId == null || type == null || !contentType.equals(MediaType.APPLICATION_JSON_VALUE)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "clientId, type 필수");
            resp.flushBuffer();
        } else {
            resp.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = resp.getWriter();
            writer.println("{\"productNo\":\"" + productNo + "\"}");
            writer.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestBody = req.getReader().lines().reduce("", (a, ac) -> a + ac);
        System.out.println(requestBody);

        resp.setStatus(Response.SC_OK);

        PrintWriter writer = resp.getWriter();
        writer.println("success");
        writer.flush();
    }
}
```
## spring4 mvc
```java
@RestController
@RequestMapping("mvc/products")
public class ProductController {

    @GetMapping("{productNo}")
    public Product getProduct(@PathVariable("productNo") long productNo, @RequestParam("type") String type,
                              @RequestHeader("clientId") String clientId,
                              @RequestHeader(HttpHeaders.CONTENT_TYPE) MediaType mediaType) {

        return new Product(productNo, "나이키 신발", new String[]{"/a.jpg", "/b.jpg"});
    }

    @PostMapping
    public Long addProduct(@RequestBody Product product) {

        return product.getProductNo();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public class Product {
        long productNo;
        String productName;
        String[] imageUrls;
    }
}

```

## webflux
routerFunction, routerHandler, filter, nest....


```java
// 예제
public class ProductRouter {

    private final ProductHandler productHandler;

    @Bean
    //@formatter:off
    public RouterFunction<ServerResponse> productRoute() {
        return nest(path("/reactive/products"),
                    route(GET("/{productNo}"),productHandler::getProduct)
                    .andRoute(method(HttpMethod.POST), productHandler::addProduct)
                    .andRoute(method(HttpMethod.GET), productHandler::goProductDetail))
              .andNest(path("/cache/products"),
                    route(method(HttpMethod.POST), productHandler::addProductByRedis)
                    .andRoute(GET("/{productNo}"), productHandler::getProductByRedis))
              .andNest(path("/repository/products"),
                    route(method(HttpMethod.POST), productHandler::addProductByRedisRepository)
                    .andRoute(GET("/{productNo}"), productHandler::getProductByRedisRepository))
              .andRoute(GET("/send/{message}"), productHandler::sendMessage)
              .andRoute(GET("/categories"), productHandler::getCategoriesByWebClient)
              .andRoute(GET("/stream"), productHandler::getStream)
              .filter((request, next) -> {
                  long begin = System.currentTimeMillis();
                  log.info("before: {}", request.uri());
                  Mono<ServerResponse> response = next.handle(request);

                  log.info("after: {} ms", System.currentTimeMillis() - begin);

                  return response;
              });
    }
    //@formatter:on
}
```

## User(회원) CRUD API작성

### GET  - 내정보 가져오기
- request
```
GET /users/10?type=payco HTTP/1.1\r\n  
accept: application/json\r\n
clientId: ncp\r\n
```
- response
```
{
    "userNo": 10,
    "userName": "홍길동",
    "types": ["payco", "naver", "kakao"]
}
```

### POST - 회원 가입
- request
```
POST /users HTTP/1.1\r\n  
content-type:: application/json\r\n
clientId: ncp\r\n
\r\n
{
    "userNo": 10,
    "userName": "홍길동",
    "types": ["payco", "naver", "kakao"]
}
```
- response

### PUT - 내정보 수정
- request
```
PUT /users/10 HTTP/1.1\r\n  
content-type:: application/json\r\n
clientId: ncp\r\n
\r\n
{
    "userNo": 10,
    "userName": "고길동",
    "types": ["payco"]
}
```

- response

### DELETE - 탈퇴
- request
```
DELETE /users/10 HTTP/1.1\r\n  
accept:: application/json\r\n
clientId: ncp\r\n

```
- response

### file upload
```shell
 curl -F upload=@/Users/nhnent/a.txt  http://localhost:8080/users/upload
```

```java
Mono<MultiValueMap<String, Part>> mono  = request.body(BodyExtractors.toMultipartData());
```



## View Template
[webflux-view](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-view)
**spring은 jsp를 권장하지 않는다.**
- 기본 내장 서버가 tomcat이 아니라 reactor netty가 spring boot 2.0에서 기본 embed server
- servlet의 view template engine(jasper engine)은 blocking
- war를 권장하지 않고 jar형태로 deploy하여 embed server로 실행하는것을 권장함
- gradle에서 multi war project구조 일 경우 최악
```
common (jar)
web-common (war)
     web.admin.service (war)
     web.admin.partner (war)
     web.admin.platform (war)

web-common에 있는 jsp를 자식 모듈에서 사용 못함.. 
```
- JSP가 아닌 다른 view template 엔진을 사용하여 jar로 패키징 권장
- thymeleaf가 reactive를 지원함
[Thymeleaf Sandbox: BigList, Spring WebFlux](https://github.com/thymeleaf/thymeleafsandbox-biglist-reactive)

### 유저 목록 보기 View
http://localhost:8080/users

# http stream with flux
## Server-Sent-Event
- Flux.mergeWith
```java
    public Mono<ServerResponse> getStream(ServerRequest request) {
        return ServerResponse.ok()
                             .contentType(MediaType.TEXT_EVENT_STREAM)
                             .body(stream1().mergeWith(stream2()), String.class);
    }
```
- Flux.create -> sink
```java
        Flux<String> resultMessage = Flux.create(sink -> addProduct().subscribe(v -> {
            sink.next(v.toString() + "번호로 상품이 등록되었습니다.");
            addOption(v).subscribe(x -> sink.next(x.toString() + "번호로 옵션이 등록되었습니다."));
            addImage(v).subscribe(x -> sink.next(x.toString() + "번호 이미지가 등록되었습니다."));
        }), FluxSink.OverflowStrategy.LATEST);
```

- Mono.first
```java
Mono.first(addProduct(), addOption(10l), addImage(10l)).subscribe(v -> {
       //run
});
```

## EventSource
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>WebSocket Example</title>
</head>
<body>
<input type="text" id="input"/>
<button id="send">전송</button>
<textarea id="window" style="color: chocolate;width: 300px;height: 300px"></textarea>
</body>

<script type="text/javascript">
    (() => {
        let ws = new WebSocket(`ws://localhost:8080/websocket`);

        ws.onopen = (e) => {
            console.log(e);
        };

        ws.onmessage = (message) => {
            let window = document.getElementById('window');
            window.textContent = window.textContent + `\nServer Sent Message : ${message.data}`;
        };

        let btn = document.getElementById('send');
        btn.addEventListener('click', () => ws.send(document.getElementById('input').value));
    })();
</script>
</html>

```

# WebClient
- restTemplate 삭제
- apache HTTP Component(HttpClient) 의존성 제거
- pool 사용 안함
```java
Mono<String> result = WebClient.create("http://sandbox-api.e-ncp.com")
                                       .get()
                                       .uri("/addresses/search?keyword={keyword}", request.pathVariable("keyword"))
                                       .header("clientId", "f7IuuZPHwmdYXu+n2npI6w==")
                                       .retrieve()
                                       .bodyToMono(String.class);
```
[WebClient](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-client)


# TestCase
## WebTestClient
- bindToRouterFunction는 mockMVC와 유사
- bindToServer는 서버를 실행하여 routeHandler가 동작함 

```java
    @Test
    public void webTest() {
        WebTestClient.bindToRouterFunction(productRouter.router())
                     .build()
                     .get()
                     .uri("/products/cache")
                     .accept(MediaType.APPLICATION_JSON)
                     .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody(Product.class);
    }
```
 [WebTestClient](https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#webtestclient)

## StepVerifier (Test)
```
StepVerifier.create(Flux.just("one", "two")).expectNext("one").expectNext("two").expectComplete();
```

# Reactive Redis

## Lettuce
- spring boot 2.0.+ 기본 redis client
- 최근 redis-cluster를 지원하고 나서 기본 클라이언트로 선정됨
- 유일한 reactive redis client

```yml
# application.yml
spring:
  redis:
    host: localhost
    port: 6379
```

## 3가지 구현 방법
### 1. spring 추상화
@Cacheable, @CacheEvict
```java
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CACHE_NAMES, key = "'mallAppkeyService:' + #p0", condition = "#p0 != null")
    public Mall getMallByAppkey(@NonNull String appkey) {
        Optional<MallAppkey> op = mallAppkeyRepository.findByAppkeyTypeAndAppkey(MallAppkeyType.MALL_CLIENT_ID, appkey);

        return op.map(k -> mallService.getMall(k.getMall().getMallNo())).orElseThrow(() -> new NCPException(NCPMallErrorCode.CLIENT_ID_IS_NOT_VALID));
    }
```

### 2. reactiveRestTemplate
```java
    @Bean
    public ReactiveRedisTemplate reactiveRedisTemplate(final ReactiveRedisConnectionFactory factory) {
        RedisSerializer redisSerializer = new GenericJackson2JsonRedisSerializer(new ObjectMapper());
        RedisSerializationContext context = RedisSerializationContext.fromSerializer(redisSerializer);
        ReactiveRedisTemplate redisTemplate = new ReactiveRedisTemplate<>(factory, context);

        return redisTemplate;
    }
```

### 3. ReactiveRepository
- @EnableRedisRepository
- @RedisHash
- @Id

#### mongDB
- @EnableReactiveMongoRepositories
- @Document
- @Id

## redis pub/sub
redis-cli -h 10.161.240.158 -p 6379
시연

# Websocket
## SimpleUrlHandlerMapping
- setOrder(10) <-- routerFunction보다 우선순위를 높여야 동작함
```java
@Configuration
public class WebsocketConfiguration {

    private final String CHANNEL = "test";

    @Bean
    public HandlerMapping webSocketMapping() {
        RedisPubSubReactiveCommands<String, String> receiverCommands = commands();
        Map<String, WebSocketHandler> map = new HashMap<>();

        map.put("/websocket", session -> {
            // client -> server -> redis publish
            Flux<WebSocketMessage> receiver = session.receive();

            receiver.subscribe(v -> {
                String message = v.getPayloadAsText();
                receiverCommands.publish(CHANNEL, message).subscribe();
            });

            // redis subscriber -> server -> client
            RedisPubSubReactiveCommands<String, String> senderCommands = commands();
            senderCommands.subscribe(CHANNEL).subscribe();

            Flux<WebSocketMessage> sender = senderCommands.observeChannels()
                                                          .map((message) -> session.textMessage(message.getMessage()));

            return session.send(sender);
        });

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(1);

        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    private RedisPubSubReactiveCommands<String, String> commands() {
        return RedisClient.create("redis://localhost:6379").connectPubSub().reactive();
    }
}
```


## WebSocketHandlerAdapter
```java
    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
```

## javascript
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>WebSocket Example</title>
</head>
<body>
<input type="text" id="input" title="input"/>
<button id="send">전송</button>
<textarea id="window" style="color: chocolate;width: 300px;height: 300px" title="area"></textarea>
</body>

<script type="text/javascript">
    (() => {
        let ws = new WebSocket(`ws://localhost:8080/websocket`);

        ws.onopen = (e) => {
            console.log(e);
        };

        ws.onmessage = (message) => {
            let window = document.getElementById('window');
            window.textContent = window.textContent + `\nServer Sent Message : ${message.data}`;
        };

        let btn = document.getElementById('send');
        btn.addEventListener('click', () => ws.send(document.getElementById('input').value));
    })();
</script>
</html>

```

# 마무리
## 신경써야할 요소
- trancsaction
두레이 김병부 책임님 발표자료 참고
- logging
org.springframework.transaction.interceptor: TRACE
- debugging
- monitoring(특히 thread와 cpu사용률)
- functional endpoint : api 문서화 지원이 아직..
[Annotation-based Programming Model](https://docs.spring.io/spring/docs/5.0.0.M4/spring-framework-reference/html/web-reactive.html#web-reactive-server-annotation)
- tomcat server일 경우
```
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        Servlet servlet = new TomcatHttpHandlerAdapter(RouterFunctions.toHttpHandler(router));
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(servlet);

       return registrationBean;
    }
````

## reactive functional programming 장점
- 성능 향상(자원의 효율화)
- 함수형 스타일 코드 : 간결하고 읽기 좋고 조합하기 좋다.
- 데이터 흐름에서 다양한 오퍼레이터 적용 가능(Flux.map, Flux.flatMap, Mono.first, Flux.mergeWith 등등)
- reactor로 코드를 작성하면 추상화되어 비동기 처리 하는부분이 애플리케이션 핵심 코드에 영향도를 줄인다.
- backpressure
- server간 통신(rest, rpc), imdb(cache), nosql, mq등 다른 서버를 호출할때 개발 방식이 모두 같다!. (리턴타입이 Mono , Flux)
- [stackoverflow survey 2017](https://insights.stackoverflow.com/survey/2017)

## 결론
0. 패러다임이 바뀌었다!!! reactive functional programming!!!
1. reactive를 지원하는 라이브러리 이용하자.
2. blocking IO(jdbc...)를 사용할땐 쓰레드풀을 따로 만들어서 실행
3. 되도록 코드에서 블록킹 작업이 발생하지 않도록 Flux 스트림, Mono에 데이터를 넣어서 사용하자.
4. 바로 도입하자
3년전 jdk8 적용했더니 주니어들 코드 스타일이 먼저 바뀜.
지금바로 spring reactive 적용하면 쥬니어들이 아마 더 잘할꺼에요.. ㅜㅜ
5. 더 봐야할 것들
reactive streams의 API components에 대한 이해
https://github.com/reactive-streams/reactive-streams-jvm/blob/v1.0.2/README.md

>“지금까지 프로그래밍 언어는 “동기성(synchrony)”과 “블로킹(blocking)”을 기반으로 하는 방식에 익숙해왔다. 
> 하지만 이러한 방식은 한계에 도달했으며 프로그래밍 언어는 이제 C#의 async, await 키워드로 대표되는 비동기적 방식을 보편적으로 채택해야 한다고 말한다.
> 
>  대부분의 현대 언어는 이미 그러한 방법을 문법적으로 지원하고 있다. 
>  주류언어 중에서 그러한 기능을 지원하지 않는 언어는 자바 하나일 뿐이다.
>  
>이보게, 브라이언 괴츠, C#, 파이선, 자바스크립트는 물론, 심지어 PHP도 async, await를 지원하고 있다네. 그런 기능이 없는 언어는 자바일 뿐이야. 
>
>람다를 이용해서 콜백callback 함수를 사용하면 된다고? 천만에. 콜백은 최악이야. 
>도움이 안 된다고. 자바 9 버전에 담으려고 하는 걸 다 내려놓고 지금 당장 async, await부터 넣으라고. 그래야 모두가 행복해질 수 있어.”

http://m.zdnet.co.kr/column_view.asp?artice_id=20151214081719#imadnews

**callback hell -> promise, completableFuture(method chaining) -> await,async(ECMAScript 2017,  c#, c++ 17, kotlin) : Coroutines**

## 참고 자료 & 인용
http://huns.me/development/2051
https://spr.com/reactive-systems-reactor-spring/
https://tech.io/playgrounds/929/reactive-programming-with-reactor-3/Intro
https://ahea.wordpress.com/2017/02/13/reactive-streams/
https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-with-Java-9

**spring camp 2017** 2018 예정
https://www.youtube.com/channel/UCj5gqpKTDDxsXqceYwn1Feg
 
**토비님의 reactive programming** 
https://www.youtube.com/channel/UCcqH2RV1-9ebRBhmN_uaSNg


## github
reactiveMongoRepository를 예제를 추가했습니다.
https://github.com/chk386/spring5-reactive-webflux
