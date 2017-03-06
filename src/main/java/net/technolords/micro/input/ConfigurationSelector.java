package net.technolords.micro.input;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.technolords.micro.model.jaxb.Configuration;

public class ConfigurationSelector {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String WILD_CARD = "\\*";

    /**
     * Auxiliary method to find the Configuration associated with a path and a map of configurations. Since wild cards
     * are supported, multiple matches can be found. If so, a decision is forced to return one match.
     *
     * @param path
     *  The path associated with the Configuration.
     * @param configurations
     *  A sub section of the entire map of Configurations. For example all GET related configurations. The smaller the
     *  map size, the quicker the matching is done. Note that no match means a null reference is returned (which will
     *  lead to a 404 not found).
     *
     * @return
     *  A matching configuration (or null when none was found).
     */
    public Configuration findMatchingConfiguration(String path, Map<String, Configuration> configurations) {
        Set<Configuration> matchingConfigurations = new HashSet<>();
        // Run through all configurations
        for (String key : configurations.keySet()) {
            Configuration currentConfiguration = configurations.get(key);
            if (key.contains("*")) {
                // Key contains one or more wild cards (means evaluation of regular expression)
                Pattern pattern = currentConfiguration.getPattern();
                if (pattern == null) {
                    pattern = this.createPattern(key);
                    currentConfiguration.setPattern(pattern);
                }
                Matcher matcher = pattern.matcher(path);
                if (matcher.matches()) {
                    LOGGER.debug("Got a match for regex -> possible config");
                    matchingConfigurations.add(configurations.get(key));
                    continue;
                }
            }
            if (key.equals(path)) {
                LOGGER.debug("Key matches path -> possible config");
                matchingConfigurations.add(configurations.get(key));
                continue;
            }
        }
        if (matchingConfigurations.size() > 1) {
            // Force selection
            LOGGER.debug("Problem, need to force selection (first match without wildcard)");
            return matchingConfigurations.stream().filter( (configuration -> !configuration.getUrl().contains("*")) ).findFirst().get();
        }
        if (matchingConfigurations.size() == 1) {
            LOGGER.debug("No problem, straightforward selection");
            return matchingConfigurations.stream().findFirst().get();
        }
        return null;
    }

    /**
     * Auxiliary method which basically turns a wild card into a regular expression.
     *
     * @param key
     *  The key associated with the expression (this represents the String to match).
     *
     * @return
     *  A regular expression, or rather, the Pattern thereof.
     */
    private Pattern createPattern(String key) {
        LOGGER.trace("Before replace: {}", key);
        String alteredKey = key.replaceAll(WILD_CARD, "(.+)");
        LOGGER.trace("After replace: {}", alteredKey);
        return Pattern.compile(alteredKey);
    }
}
