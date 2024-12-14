package com.desafio.literatura.service;

public interface IDataConversor {
    <T> T convertData(String json, Class<T> clase);
}