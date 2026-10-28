package com.wac.autocore.controller;

import com.wac.autocore.model.ServiceItem;
import com.wac.autocore.model.ServicePack;
import com.wac.autocore.service.ServiceItemService;

import java.util.List;
import java.util.Optional;

public class CatalogController {
    private final ServiceItemService serviceItemService;
    private final ErrorHandler errorHandler;

    public CatalogController(
            ServiceItemService serviceItemService, ErrorHandler errorHandler
    ) {
        this.serviceItemService = serviceItemService;
        this.errorHandler = errorHandler;
    }

    public Result<List<ServiceItem>> getServiceItems() {
        return errorHandler.call(serviceItemService::getServiceItems);
    }

    public Result<List<ServiceItem>> getServiceItemsByIds(List<Integer> serviceIds) {
        return errorHandler.call(() -> serviceItemService.getServiceItemsByIds(serviceIds));
    }

    public Result<List<Integer>> getMissingIds(
            List<Integer> lookingForIds,
            List<ServiceItem> serviceList
    ) {
        return errorHandler.call(() -> serviceItemService.getMissingIds(lookingForIds, serviceList));
    }

    public Result<Optional<ServiceItem>> getServiceItem(int id) {
        return errorHandler.call(() ->serviceItemService.getServiceItem(id));
    }

    public Result<ServiceItem> updateServiceItemPrice(int serviceItemId, double newPrice) {
        return errorHandler.call(() -> serviceItemService.updateServiceItemPrice(serviceItemId, newPrice));
    }

    public Result<List<ServicePack>> getServicePacks() {
        return errorHandler.call(serviceItemService::getServicePacks);
    }

    public Result<Optional<ServicePack>> getServicePack(int id) {
        return errorHandler.call(() -> serviceItemService.getServicePack(id));
    }

    public Result<ServicePack> updateServicePack(
            int servicePackId,
            ServicePack updatedServicePack
    ) {
        return errorHandler.call(() -> serviceItemService.updateServicePack(
                servicePackId,
                updatedServicePack
        ));
    }

    public Result<ServicePack> createServicePack(String name, int... serviceItemIds) {
            return errorHandler.call(() -> serviceItemService.createServicePack(name, serviceItemIds));
    }

    public void deleteServicePack(int id) {
            errorHandler.run(() -> serviceItemService.deleteServicePack(id));
    }
}
