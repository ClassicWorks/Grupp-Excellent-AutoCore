package com.wac.autocore.data;

import com.wac.autocore.model.User;
import com.wac.autocore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

public class UserRepoImpl implements UserRepo {

    @Override
    public User save(User user) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = s.beginTransaction();
            s.persist(user);
            transaction.commit();
            return user;
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from User u where u.username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        }
    }
}
