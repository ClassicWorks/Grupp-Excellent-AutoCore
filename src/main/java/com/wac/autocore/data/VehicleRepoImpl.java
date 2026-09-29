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
}
