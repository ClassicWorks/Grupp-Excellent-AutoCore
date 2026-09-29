package com.wac.autocore.data;

import com.wac.autocore.model.Invoice;
import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class InvoiceRepoImpl implements InvoiceRepo{
    @Override
    public Invoice save(Invoice i) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            s.persist(i);
            transaction.commit();
            return i;
        }
    }

    @Override
    public Invoice update(Invoice i) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            Invoice saved = (Invoice) s.merge(i);
            transaction.commit();
            return saved;
        }
    }

    @Override
    public List<Invoice> getAll() {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("from Invoice", Invoice.class)
                    .list();
        }
    }

    @Override
    public Optional<Invoice> get(int id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "from Invoice i where i.id = :id", Invoice.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }
}
