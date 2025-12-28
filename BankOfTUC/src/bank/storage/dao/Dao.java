package bank.storage.dao;

import java.io.IOException;

public interface Dao<T> {
    T load() throws IOException;
    void save(T data) throws IOException;
}