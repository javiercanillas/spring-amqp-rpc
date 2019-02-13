package test.rabbitmq.model;

import java.util.List;

public interface FibonacciService {
    List<Long> calculateFirst(final int terms);
}
