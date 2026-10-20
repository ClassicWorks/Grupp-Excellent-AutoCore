package com.wac.autocore.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_packs")
public class ServicePack implements ServiceComponent{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "join_service_pack_id_service_item_id",
    joinColumns = {@JoinColumn(name = "service_pack_id")},
    inverseJoinColumns = {@JoinColumn(name = "service_item_id")})
    private List<ServiceItem> serviceItems;

    protected ServicePack() {}

    public ServicePack(String name) {
        this.name = name;
        serviceItems = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getEstimatedMinutes() {
        return this.serviceItems.stream()
                .mapToInt(ServiceItem::getEstimatedMinutes)
                .sum();
    }

    @Override
    public double getPrice() {
        return serviceItems.stream()
                .mapToDouble(ServiceItem::getPrice)
                .sum();
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ServiceItem> getServiceItems() {
        return serviceItems;
    }

    public boolean addServiceItem(ServiceItem serviceItem) {
        return serviceItems.add(serviceItem);
    }

    public boolean removeServiceItem(ServiceItem serviceItem) {
        return serviceItems.remove(serviceItem);
    }

    public void setServiceItems (List<ServiceItem> serviceItems){
        this.serviceItems = serviceItems;
    }

    @Override
    public String toString() {
        return "ServicePack{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", serviceItems=" + serviceItems +
                '}';
    }

    public boolean hasService(int serviceItemId) {
        return serviceItems.stream()
                .anyMatch(item -> item.getId() == serviceItemId);
    }
}
