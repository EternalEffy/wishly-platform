package com.wishly.common.generator;

public interface HashGenerator {
    /**
     * Сгенерировать уникальный хеш
     * @param length длина хеша
     * @return уникальный хеш
     */
    String generate(int length);
}
