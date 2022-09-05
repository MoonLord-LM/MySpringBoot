package cn.moonlord.springboot;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.web.client.InstanceExchangeFilterFunction;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Configuration
public class CustomInstanceExchangeFilterFunction implements InstanceExchangeFilterFunction {

    private static final Logger logger = LoggerFactory.getLogger(CustomInstanceExchangeFilterFunction.class);

    @Override
    public Mono<ClientResponse> filter(Instance instance, ClientRequest request, ExchangeFunction next) {
        return next.exchange(request).doOnSubscribe(new Consumer<Subscription>() {
            @Override
            public void accept(Subscription subscription) {
                logger.info("filter instance: {} request: {} next: {} subscription: {}", instance, request, next, subscription);
                logger.info("filter method: {} instance: {} url: {}", request.method(), instance.getId(), request.url());
            }
        });
    }

}
