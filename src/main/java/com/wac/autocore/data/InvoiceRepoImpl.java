package com.wac.autocore.data;

import com.wac.autocore.model.Invoice;
import com.wac.autocore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
            Invoice saved = s.merge(i);
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
    public List<Invoice> getWherePayedIs(boolean isPayed) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("from Invoice i where i.paid = :isPayed", Invoice.class)
                    .setParameter("isPayed", isPayed)
                    .list();
        }
    }

    @Override
    public boolean existsByWorkOrderId(int workOrderId) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            Long numOfInvoices = s.createQuery(
                            "select count(i) from Invoice i " +
                                    "where i.workOrder.id = :workOrderId",
                            Long.class)
                    .setParameter("workOrderId", workOrderId)
                    .uniqueResult();

            return numOfInvoices > 0;
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
