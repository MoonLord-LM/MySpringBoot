package cn.moonlord.springboot;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class CustomEventNotifier extends AbstractEventNotifier {

    private static final Logger logger = LoggerFactory.getLogger(CustomEventNotifier.class);

    protected CustomEventNotifier(InstanceRepository repository) {
        super(repository);
    }

    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        return Mono.fromRunnable(() -> {
            if (event instanceof InstanceStatusChangedEvent) {
                logger.info("doNotify: {} ({}) {} to {}", instance.getRegistration().getName(), event.getInstance(), event.getType(), ((InstanceStatusChangedEvent) event).getStatusInfo().getStatus());
            } else {
                logger.info("doNotify: {} ({}) {}", instance.getRegistration().getName(), event.getInstance(), event.getType());
            }
        });
    }

}
