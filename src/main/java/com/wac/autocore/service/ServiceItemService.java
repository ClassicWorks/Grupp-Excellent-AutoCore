package com.wac.autocore.service;

import com.wac.autocore.data.ServiceItemRepo;
import com.wac.autocore.data.ServicePackRepo;
import com.wac.autocore.exceptions.InputNotValidException;
import com.wac.autocore.exceptions.ServiceItemNotFoundException;
import com.wac.autocore.exceptions.ServicePackNotFoundException;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePack;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ServiceItemService {
    private final ServiceItemRepo serviceItemRepo;
    private final ServicePackRepo servicePackRepo;

    public ServiceItemService(ServiceItemRepo serviceItemRepo, ServicePackRepo servicePackRepo) {
        this.serviceItemRepo = serviceItemRepo;
        this.servicePackRepo = servicePackRepo;
    }

    public void showServiceItems() {
        System.out.println();
        System.out.println("=== SERVICES ===");

        if (serviceItemRepo.getAll().isEmpty()) {
            System.out.println("No services found.");
            return;
        }

        for (ServiceItem serviceItem : serviceItemRepo.getAll()) {
            System.out.println(serviceItem);
        }
    }

    public List<ServiceItem> getServiceItems() {
        List<ServiceItem> serviceItems = serviceItemRepo.getAll();
        if (serviceItems.isEmpty()) {
            System.out.println("No services found.");
        }

        return serviceItems;
    }

    public List<ServiceItem> getServiceItemsByIds(List<Integer> serviceIds) {
        List<ServiceItem> serviceItems = serviceItemRepo.getByIds(serviceIds);
        if (serviceItems.isEmpty()) {
            System.out.println("No services found.");
        }

        return serviceItems;
    }


    public List<Integer> getMissingIds(List<Integer> lookingForIds, List<ServiceItem> serviceList) {
        Set<Integer> foundIds = serviceList.stream()
                .map(ServiceItem::getId)
                .collect(Collectors.toSet());

        return lookingForIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());
    }

    public Optional<ServiceItem> getServiceItem(int id){
        return serviceItemRepo.get(id);
    }

    public ServiceItem updateServiceItemPrice(int serviceItemId, double newPrice) {
        if (newPrice < 0) {
            throw new InputNotValidException("Price can not  be negative");
        }

        Optional<ServiceItem> optionalServiceItem = getServiceItem(serviceItemId);

        if (!optionalServiceItem.isPresent()) {
            throw new ServiceItemNotFoundException(serviceItemId);
        }

        ServiceItem serviceItem = optionalServiceItem.get();

        serviceItem.setPrice(newPrice);
        ServiceItem savedServiceItem = serviceItemRepo.update(serviceItem);

        System.out.println("Price for " + savedServiceItem.getName() + " updated to " + newPrice + " SEK.");

        return savedServiceItem;
    }

    public void showServicePacks() {
        System.out.println();
        System.out.println("=== SERVICE PACKS ===");
        if (servicePackRepo.getAll().isEmpty()) {
            System.out.println("No services found.");
            return;
        }
        for (ServicePack servicePack : servicePackRepo.getAll()) {
            System.out.println(servicePack);
        }
    }

    public List<ServicePack> getServicePacks() {
        return servicePackRepo.getAll();
    }

    public Optional<ServicePack> getServicePack(int id){
        return servicePackRepo.getById(id);
    }

    //TODO get values and Garagesystem check
    public ServicePack updateServicePack(int servicePackId, ServicePack updatedServicePack){
        Optional<ServicePack> optionalServicePack = getServicePack(servicePackId);
        if(!optionalServicePack.isPresent()){
            throw new ServicePackNotFoundException(servicePackId);
        }

        //Om någon ServiceItem inte finns kasta exception
        updatedServicePack.getServiceItems().forEach(si -> {
            Optional<ServiceItem> optionalServiceItem = getServiceItem(si.getId());
            if(!optionalServiceItem.isPresent()) {
                throw new ServiceItemNotFoundException(si.getId());
            }
        });

        ServicePack servicePackToUpdate = optionalServicePack.get();
        servicePackToUpdate.setName(updatedServicePack.getName());
        servicePackToUpdate.setServiceItems(updatedServicePack.getServiceItems());

        return servicePackRepo.update(servicePackToUpdate);
    }

    public void deleteServicePack(int id){
        servicePackRepo.delete(id);
    }

    public ServicePack createServicePack(String name, int... serviceItemIds){
        if(!servicePackRepo.nameAvailable(name)){
            throw new InputNotValidException("Service pack with name \"" + name + "\" already exists.");
        }
        ServicePack servicePack = new ServicePack(name);

        for(int serviceItemId : serviceItemIds) {
            Optional<ServiceItem> optionalServiceItem = getServiceItem(serviceItemId);
            if(!optionalServiceItem.isPresent()) {
                throw new ServiceItemNotFoundException(serviceItemId);
            }
            ServiceItem serviceItem = optionalServiceItem.get();

            servicePack.addServiceItem(serviceItem);
        }

        return servicePackRepo.save(servicePack);
    }
}
