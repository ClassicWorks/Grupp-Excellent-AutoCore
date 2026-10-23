package com.wac.autocore.service;

import com.wac.autocore.data.ServiceItemRepo;
import com.wac.autocore.data.ServicePackRepo;
import com.wac.autocore.exceptions.InputNotValidException;
import com.wac.autocore.exceptions.NotUniqueException;
import com.wac.autocore.exceptions.ServiceItemNotFoundException;
import com.wac.autocore.exceptions.ServicePackNotFoundException;
import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePack;

import java.util.*;
import java.util.function.Function;
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


    /**
     * Identifies and returns the IDs from the provided list of lookingForIds
     * that are not present in the list of serviceList.
     *
     * @param lookingForIds a list of integers representing the IDs to look for
     * @param serviceList a list of ServiceItem objects from which the IDs are extracted
     * @return a list of integers representing the IDs that are present in lookingForIds
     *         but not found within the serviceList
     */
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

    /**
     * Updates the price of the specified service item.
     *
     * @param serviceItemId the ID of the service item whose price is to be updated
     * @param newPrice the new price to set for the service item; must be non-negative
     * @return the updated ServiceItem object with the new price
     * @throws InputNotValidException if the provided price is negative
     * @throws ServiceItemNotFoundException if no service item is found with the specified ID
     */
    public ServiceItem updateServiceItemPrice(int serviceItemId, double newPrice) {
        if (newPrice < 0) {
            throw new InputNotValidException("Price can not  be negative");
        }

        ServiceItem serviceItem = getServiceItem(serviceItemId)
                .orElseThrow(() -> new ServiceItemNotFoundException(serviceItemId));

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

    /**
     * Updates the details of an existing service pack identified by its ID.
     *
     * @param servicePackId the ID of the service pack to be updated
     * @param updatedServicePack the updated ServicePack object containing the new details;
     *                           must have a non-empty name, a unique name, and non-null service items
     * @return the updated ServicePack object after applying the changes
     * @throws InputNotValidException if the updated service pack has an empty name or service items are empty
     * @throws NotUniqueException if the updated service pack's name is different from before and not unique
     * @throws ServicePackNotFoundException if no service pack is found with the specified ID
     * @throws ServiceItemNotFoundException if any service item in the updated service pack does not exist
     */
    public ServicePack updateServicePack(int servicePackId, ServicePack updatedServicePack){
        if(updatedServicePack.getName() == null || updatedServicePack.getName().trim().isEmpty()){
            throw new InputNotValidException("Name cannot be empty");
        }
        if(updatedServicePack.getServiceItems() == null || updatedServicePack.getServiceItems().isEmpty()){
            throw new InputNotValidException("Service items cannot be empty");
        }

        ServicePack servicePackToUpdate = getServicePack(servicePackId)
                .orElseThrow(() -> new ServicePackNotFoundException(servicePackId));

        if(!servicePackToUpdate.getName().equals(updatedServicePack.getName()) && servicePackRepo.existsByName(updatedServicePack.getName())){
            throw new NotUniqueException("Service pack with name \"" + updatedServicePack.getName() + "\" already exists.");
        }

        List<ServiceItem> serviceItemsNoDuplicates = new ArrayList<>(
                updatedServicePack.getServiceItems().stream()
                        .collect(Collectors.toMap(
                                ServiceItem::getId,
                                Function.identity(),
                                (existing, duplicate) -> existing,
                                LinkedHashMap::new
                        ))
                        .values()
        );

        //Om någon ServiceItem inte finns kastas exception
        serviceItemsNoDuplicates.forEach(si -> {
            Optional<ServiceItem> optionalServiceItem = getServiceItem(si.getId());
            if(optionalServiceItem.isEmpty()) {
                throw new ServiceItemNotFoundException(si.getId());
            }
        });

        servicePackToUpdate.setName(updatedServicePack.getName());
        servicePackToUpdate.setServiceItems(serviceItemsNoDuplicates);

        return servicePackRepo.update(servicePackToUpdate);
    }

    /**
     * Creates and saves a new service pack with the specified name and service items.
     *
     * @param name the name of the service pack to be created; must not be null or empty
     * @param serviceItemIds the IDs of the service items to be included in the service pack
     * @return the created and saved ServicePack object containing the specified details
     * @throws InputNotValidException if the provided name is null or empty, or service items are null or empty
     * @throws NotUniqueException if a service pack with the given name already exists
     * @throws ServiceItemNotFoundException if any of the provided service item IDs does not exist
     */
    public ServicePack createServicePack(String name, int... serviceItemIds){
        if(name == null || name.trim().isEmpty()) {
            throw new InputNotValidException("Service pack name cannot be empty.");
        }
        if(serviceItemIds == null || serviceItemIds.length == 0){
            throw new InputNotValidException("Service items cannot be empty");
        }
        if(servicePackRepo.existsByName(name)){
            throw new NotUniqueException("Service pack with name \"" + name + "\" already exists.");
        }
        ServicePack servicePack = new ServicePack(name);

        //Ta bort dubletter
        serviceItemIds = Arrays.stream(serviceItemIds).distinct().toArray();

        for(int serviceItemId : serviceItemIds) {
            ServiceItem serviceItem = getServiceItem(serviceItemId)
                    .orElseThrow(() -> new ServiceItemNotFoundException(serviceItemId));

            servicePack.addServiceItem(serviceItem);
        }

        return servicePackRepo.save(servicePack);
    }

    public void deleteServicePack(int id){
        servicePackRepo.delete(id);
    }
}
