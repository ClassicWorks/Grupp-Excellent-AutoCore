package com.wac.autocore.data;

import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class WorkOrderRepoImpl implements WorkOrderRepo {
    @Override
    public WorkOrder save(WorkOrder wo) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            s.persist(wo);
            transaction.commit();
            return wo;
        }
    }

    @Override
    public WorkOrder update(WorkOrder wo) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = s.beginTransaction();
            WorkOrder saved = (WorkOrder)s.merge(wo);
            transaction.commit();
            return saved;
        }
    }

    @Override
    public List<WorkOrder> getAll() {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("from WorkOrder", WorkOrder.class)
                    .list();
        }    }

    @Override
    public Optional<WorkOrder> get(int id) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "from WorkOrder wo where wo.id = :id" ,WorkOrder.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }    }
}
