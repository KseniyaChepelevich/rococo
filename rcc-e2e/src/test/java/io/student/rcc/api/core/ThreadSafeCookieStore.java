package io.student.rcc.api.core;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jspecify.annotations.NonNull;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public enum ThreadSafeCookieStore implements CookieStore, CookieJar {
    INSTANCE;

    private final ThreadLocal<CookieStore> threadLocalCookiesStore = ThreadLocal.withInitial(
            this::inMemoryCookieStore
    );

    private CookieStore inMemoryCookieStore() {
        return new CookieManager().getCookieStore();
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        getStore().add(uri, cookie);

    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return getStore().get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return getStore().getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return getStore().getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return getStore().remove(uri, cookie);
    }

    @Override
    public boolean removeAll() {
        return getStore().removeAll();
    }

    public String cookieValue(String name) {
        return getCookies().stream()
                .filter(c -> c.getName().equals(name))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElseThrow();
    }


    private CookieStore getStore() {
        return threadLocalCookiesStore.get();
    }

    @Override
    public @NonNull List<Cookie> loadForRequest(@NonNull HttpUrl httpUrl) {
        return List.of();
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl httpUrl, @NonNull List<Cookie> list) {

    }
}
