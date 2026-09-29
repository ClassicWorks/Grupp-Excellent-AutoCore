package com.wac.autocore.data;

import com.wac.autocore.model.Mechanic;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MechanicRepoImpl implements MechanicRepo {
    @Override
    public Mechanic save(Mechanic m) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            s.persist(m);
            transaction.commit();
            return m;
        }    }

    @Override
    public List<Mechanic> getAll() {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("from Mechanic", Mechanic.class)
                    .list();
        }
    }

    @Override
    public List<Mechanic> getAllAvailable() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from Mechanic m where m.available = true", Mechanic.class)
                    .list();
        }
    }

    @Override
    public Optional<Mechanic> get(int id) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "from Mechanic m where m.id = :id", Mechanic.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }
}
