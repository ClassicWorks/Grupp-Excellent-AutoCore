package com.wac.autocore.data;

import com.wac.autocore.model.Customer;
import com.wac.autocore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class CustomerRepoImpl implements CustomerRepo{

    @Override
    public Customer save(Customer c) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            s.persist(c);
            transaction.commit();
            return c;
        }
    }

    @Override
    public List<Customer> getCustomers() {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("from Customer", Customer.class)
                    .list();
        }
    }

    @Override
    public Optional<Customer> getCustomer(int id) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "from Customer c where c.id = :id" ,Customer.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }
}
