package io.github.qe7.managers.api;

import lombok.Getter;

import java.util.HashMap;

@Getter
public abstract class Manager<T, V> {

    private final HashMap<T, V> registry = new HashMap<>();
}
