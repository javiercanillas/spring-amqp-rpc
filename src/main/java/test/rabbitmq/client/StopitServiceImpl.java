package test.rabbitmq.client;

import org.springframework.stereotype.Service;
import test.rabbitmq.model.StopitService;

@Service
public class StopitServiceImpl implements StopitService {

    boolean askedForStop = false;

    @Override
    public void stopit() {
        this.askedForStop = true;
    }

    public boolean isAskedForStop() {
        return askedForStop;
    }
}
