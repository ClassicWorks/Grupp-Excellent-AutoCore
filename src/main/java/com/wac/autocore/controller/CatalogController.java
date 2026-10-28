package com.wac.autocore.controller;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePack;
import com.wac.autocore.service.ServiceItemService;

import java.util.List;
import java.util.Optional;

public class CatalogController {
    private final ServiceItemService serviceItemService;
    //private final GlobalExceptionHandler exceptionHandler;

    public CatalogController(
            ServiceItemService serviceItemService
            //GlobalExceptionHandler exceptionHandler
    ) {
        this.serviceItemService = serviceItemService;
        //this.exceptionHandler = exceptionHandler;
    }

    public List<ServiceItem> getServiceItems() {
        try {
            return serviceItemService.getServiceItems();
        } catch (Exception e) {
            //exceptionHandler.handle(e);
            return List.of();
        }
    }

    public List<ServiceItem> getServiceItemsByIds(List<Integer> serviceIds) {
        try {
            return serviceItemService.getServiceItemsByIds(serviceIds);
        } catch (Exception e) {
            //exceptionHandler.handle(e);
            return List.of();
        }
    }

    public List<Integer> getMissingIds(
            List<Integer> lookingForIds,
            List<ServiceItem> serviceList
    ) {
        try {
            return serviceItemService.getMissingIds(lookingForIds, serviceList);
        } catch (Exception e) {
            //exceptionHandler.handle(e);
            return List.of();
        }
    }

    public Optional<ServiceItem> getServiceItem(int id) {
        try {
            return serviceItemService.getServiceItem(id);
        } catch (Exception e) {
            //exceptionHandler.handle(e);
            return Optional.empty();
        }
    }

    public ServiceItem updateServiceItemPrice(int serviceItemId, double newPrice) {
        try {
            return serviceItemService.updateServiceItemPrice(serviceItemId, newPrice);
        } catch (Exception e) {
            //exceptionHandler.handle(e);
            return null;
        }
    }

    public List<ServicePack> getServicePacks() {
        try {
            return serviceItemService.getServicePacks();
        } catch (Exception e) {
            //exceptionHandler.handle(e);
            return List.of();
        }
    }

    public Optional<ServicePack> getServicePack(int id) {
        try {
            return serviceItemService.getServicePack(id);
        } catch (Exception e) {
            //exceptionHandler.handle(e);
            return Optional.empty();
        }
    }

    public ServicePack updateServicePack(
            int servicePackId,
            ServicePack updatedServicePack
    ) {
        try {
            return serviceItemService.updateServicePack(
                    servicePackId,
                    updatedServicePack
            );
        } catch (Exception e) {
            //exceptionHandler.handle(e);
            return null;
        }
    }

    public ServicePack createServicePack(String name, int... serviceItemIds) {
        try {
            return serviceItemService.createServicePack(name, serviceItemIds);
        } catch (Exception e) {
            //exceptionHandler.handle(e);
            return null;
        }
    }

    public void deleteServicePack(int id) {
        try {
            serviceItemService.deleteServicePack(id);
        } catch (Exception e) {
            //exceptionHandler.handle(e);
        }
    }
}
