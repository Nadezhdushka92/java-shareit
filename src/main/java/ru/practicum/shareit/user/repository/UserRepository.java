package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();
    User save(User user);
    User getById(long id);
    User update(User user);
    void delete(long id);
}