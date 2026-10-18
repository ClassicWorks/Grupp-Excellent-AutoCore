package com.wac.autocore.data;

import com.wac.autocore.model.Vehicle;
import com.wac.autocore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class VehicleRepoImpl implements VehicleRepo{
    public Vehicle save(Vehicle v){
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            s.persist(v);
            transaction.commit();
            return v;
        }
    }

    @Override
    public Vehicle update(Vehicle v) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            Vehicle saved = (Vehicle) s.merge(v);
            transaction.commit();
            return saved;
        }
    }

    public List<Vehicle> getAll(){
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("from Vehicle", Vehicle.class)
                    .list();
        }
    }

    public Optional<Vehicle> get(int id){
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "from Vehicle v where v.id = :id" ,Vehicle.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Vehicle> getVehicleByCustomerId(int customerId) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "from Vehicle v where v.customer.id = :customerId" ,Vehicle.class)
                    .setParameter("customerId", customerId)
                    .list();
        }
    }

    @Override
    public boolean customerHasVehicle(int customerId) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            Long numOfVehicles = s.createQuery(
                            "select count(v) from Vehicle v " +
                                    "where v.customer.id = :customerId",
                            Long.class)
                    .setParameter("customerId", customerId)
                    .uniqueResult();

            return numOfVehicles > 0;
        }
    }

    @Override
    public void delete(int id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = s.beginTransaction();
            Vehicle v = s.get(Vehicle.class, id);
            if (v != null) {
                s.delete(v);
            }
            transaction.commit();
        }
    }
}
