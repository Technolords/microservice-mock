package net.technolords.micro.input.json;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.technolords.micro.model.jaxb.Configuration;

public class JsonPathEvaluator {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonPathEvaluator.class);

    public boolean evaluateXpathExpression(String jsonpathAsString, String message, Configuration configuration) {
        DocumentContext jsonContext = JsonPath.parse(message);
        LOGGER.trace("Jsoncontext: {}", jsonContext);
        LOGGER.debug("About to compile the json expression for: '{}'", jsonpathAsString);
        JsonPath compiledJsonPath = JsonPath.compile(jsonpathAsString);
        LOGGER.debug("Compiled Jsonpath: {} -> {}", compiledJsonPath, compiledJsonPath.getPath());
        LOGGER.debug("Is definite: {}", compiledJsonPath.isDefinite());
        if (compiledJsonPath.isDefinite()) {
            String match = jsonContext.read(compiledJsonPath);
            boolean result = !Strings.isEmpty(match);
            LOGGER.debug("evaluated match: {} -> result: {}", match, result);
            return result;
        } else {
            List<String> matches = jsonContext.read(compiledJsonPath);
            boolean result = matches != null && matches.size() > 0;
            LOGGER.debug("matches: {} -> result: {}", matches, result);
            return result;
        }
    }
}
