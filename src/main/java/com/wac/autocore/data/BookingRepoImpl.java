package com.wac.autocore.data;

import com.wac.autocore.model.Booking;
import com.wac.autocore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class BookingRepoImpl implements BookingRepo{
    @Override
    public Booking save(Booking b) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            s.persist(b);
            transaction.commit();
            return b;
        }
    }

    @Override
    public Booking update(Booking b) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            Booking savedBooking = (Booking)s.merge(b);
            transaction.commit();
            return savedBooking;
        }
    }

    @Override
    public List<Booking> getAll() {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("from Booking ", Booking.class)
                    .list();
        }    }

    @Override
    public Optional<Booking> get(int id) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "from Booking b where b.id = :id" , Booking.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }    }
}
