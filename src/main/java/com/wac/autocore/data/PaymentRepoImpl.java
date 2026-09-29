package com.wac.autocore.data;

import com.wac.autocore.model.Payment;
import com.wac.autocore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class PaymentRepoImpl implements PaymentRepo{
    @Override
    public Payment save(Payment p) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            s.persist(p);
            transaction.commit();
            return p;
        }
    }

    @Override
    public List<Payment> getAll() {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("from Payment",Payment.class)
                    .list();
        }    }

    @Override
    public Optional<Payment> get(int id) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "from Payment p where p.id = :id", Payment.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }
}
