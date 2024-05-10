package com.thoxia.limbosecure.bot.backend.http;

import lombok.SneakyThrows;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {

    public final static String METHOD_NAME = "GET";

    @SneakyThrows
    public HttpGetWithEntity(String uri) {
        this.setURI(new URI(uri));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

}