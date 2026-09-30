package com.wac.autocore.data;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class ServiceItemRepoImpl implements ServiceItemRepo {
    @Override
    public ServiceItem save(ServiceItem si) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            s.persist(si);
            transaction.commit();
            return si;
        }
    }

    @Override
    public ServiceItem update(ServiceItem si) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            ServiceItem saved = (ServiceItem) s.merge(si);
            transaction.commit();
            return saved;
        }    }

    @Override
    public List<ServiceItem> getAll() {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("from ServiceItem", ServiceItem.class)
                    .list();
        }    }

    @Override
    public Optional<ServiceItem> get(int id) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "from ServiceItem si where si.id = :id" ,ServiceItem.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }
}
