package co.uk.stirling_index.inventory.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerController {


    public Logger logger;

    public LoggerController(Class<?> callee) {
        this.logger = LoggerFactory.getLogger(callee);
    }
}
