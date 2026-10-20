package com.wac.autocore.data;

import com.wac.autocore.model.WorkOrder;
import com.wac.autocore.model.WorkOrderStatus;
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
            WorkOrder saved = s.merge(wo);
            transaction.commit();
            return saved;
        }
    }

    @Override
    public List<WorkOrder> getAll() {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("from WorkOrder", WorkOrder.class)
                    .list();
        }
    }

    @Override
    public List<WorkOrder> getAllWithItems() {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("select distinct wo from WorkOrder wo " +
                            "left join fetch wo.items ",
                            WorkOrder.class)
                    .list();
        }
    }

    @Override
    public Optional<WorkOrder> get(int id) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "from WorkOrder wo where wo.id = :id" ,WorkOrder.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<WorkOrder> getCompletedWorkOrdersWithoutInvoices() {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "from WorkOrder wo " +
                                    "where wo.status = :completed " +
                                    "and not exists (" +
                                    "from Invoice i where i.workOrder.id = wo.id)" ,
                            WorkOrder.class)
                    .setParameter("completed", WorkOrderStatus.COMPLETED)
                    .getResultList();

        }
    }

    @Override
    public Optional<WorkOrder> getWithItems(int id) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "select distinct wo from WorkOrder wo " +
                                    "left join fetch wo.items " +
                                    "where wo.id = :id" ,WorkOrder.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
        }
    }


    @Override
    public boolean mechanicHasUncompletedWorkOrders(int mechanicId) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            Long numOfUncompletedWorkOrders = s.createQuery(
                            "select count(wo) from WorkOrder wo " +
                                    "where wo.mechanic.id = :mechanicId " +
                                    "and (wo.status = :draft " +
                                        "or wo.status = :confirmed " +
                                        "or wo.status = :inProgress)" ,
                            Long.class)
                    .setParameter("mechanicId", mechanicId)
                    .setParameter("draft", WorkOrderStatus.DRAFT)
                    .setParameter("confirmed", WorkOrderStatus.CONFIRMED)
                    .setParameter("inProgress", WorkOrderStatus.IN_PROGRESS)
                    .uniqueResult();

            return numOfUncompletedWorkOrders > 0;
        }
    }

    @Override
    public List<WorkOrder> getWorkOrdersByMechanic(int mechanicId) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "from WorkOrder wo " +
                                    "where wo.mechanic.id = :mechanicId ",
                            WorkOrder.class)
                    .setParameter("mechanicId", mechanicId)
                    .getResultList();
        }
    }

    @Override
    public List<WorkOrder> getWithStatus(WorkOrderStatus status) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "select distinct wo from WorkOrder wo " +
                            "left join fetch wo.items " +
                                    "where wo.status = :status " +
                                    "order by wo.id desc",
                            WorkOrder.class)
                    .setParameter("status", status)
                    .getResultList();
        }
    }
}
