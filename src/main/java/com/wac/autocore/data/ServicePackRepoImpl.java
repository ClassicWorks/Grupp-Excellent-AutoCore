package com.wac.autocore.data;

import com.wac.autocore.model.ServicePack;
import com.wac.autocore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class ServicePackRepoImpl implements ServicePackRepo {

    @Override
    public ServicePack save(ServicePack sp) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            s.persist(sp);
            transaction.commit();
            return sp;
        }
    }

    @Override
    public ServicePack update(ServicePack sp) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            ServicePack saved = (ServicePack) s.merge(sp);
            transaction.commit();
            return saved;
        }
    }

    @Override
    public List<ServicePack> getAll() {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("from ServicePack", ServicePack.class)
                    .list();
        }
    }

    @Override
    public Optional<ServicePack> getById(int id) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "from ServicePack sp where sp.id = :id" ,ServicePack.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }

    @Override
    public boolean nameAvailable(String name) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery(
                    "from ServicePack sp where sp.name = :name", ServicePack.class)
                    .setParameter("name", name)
                    .getResultList().isEmpty();
        }
    }

    @Override
    public void delete(int id) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            ServicePack sp = s.get(ServicePack.class, id);
            if(sp != null){
                s.delete(sp);
            }

            transaction.commit();
        }
    }
}
