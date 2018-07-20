package io.github.carlosthe19916.repeid.instantiators;

import java.util.Arrays;
import java.util.function.Function;

public class BeanInstantiatorFactory {

    private BeanInstantiatorFactory() {
        // Factory Clas
    }

    public static <T> BeanInstantiator<T> txtInstantiator(Class<T> zClass, String headerText, String regex) {
        String[] headers = headerText.split(regex);
        headers = Arrays.stream(headers).map(String::trim).toArray(String[]::new);
        return new BeanInstantiator<T>(zClass, headers);
    }

    public static <T> BeanInstantiator<T> txtInstantiator(Class<T> zClass, String headerText, String regex, Function<String, String> headerMapper) {
        String[] headers = headerText.split(regex);
        headers = Arrays.stream(headers).map(String::trim).map(headerMapper).toArray(String[]::new);
        return new BeanInstantiator<T>(zClass, headers);
    }

}
