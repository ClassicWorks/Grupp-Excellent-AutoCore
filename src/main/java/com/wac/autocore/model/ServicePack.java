package com.wac.autocore.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_packs")
public class ServicePack {
    //*När en Service tas bort från databas ska varning komma upp för att godkänna att man
    // då tar bort den från kopplade Servicepaket, så man får en varning ifall man vill
    // redigera ServicePaketet innan. (Glöm inte implementera detta i
    // garageSystem.removeServiceItem())
    //*När man skapar och redigerar ServicePack sparas en temporär lista med ServiceItems,
    // när man sedan sparar .setServiceItems(list)
    //*Vid CreateWorkOrderForm kan man välja ett servicepaket, då hamnar alla serviceItems
    // som servicepaketet innehåller i den temporära listan ovanför (samma utseende som
    // EditWorkOrderForm) med alla valda ServiceItems, man kan fortsatt ta bort ServiceItems
    // från listan då detta är helt separerat från ServicePack. När WorkOrder sedan skapas
    // så hämtar man listan av ServiceItems från den temporära listan.
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

    public String getName() {
        return name;
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
