package com.wac.autocore.model;

import java.util.List;

public class ServicePack {
    private String name;
    private List<ServiceItem> serviceItems;

    //*Lägger in flera existerande service i lista
    //*När en Service tas bort från databas ska varning komma upp för att godkänna att man
    // då tar bort den från kopplade Servicepaket, så man får en varning ifall man vill
    // redigera ServicePaketet innan. (Glöm inte implementera detta i
    // garageSystem.removeServiceItem())
    //*Vid CreateWorkOrderForm kan man välja ett servicepaket, då hamnar alla serviceItems
    // som servicepaketet innehåller i den temporära listan ovanför (samma utseende som
    // EditWorkOrderForm) med alla valda ServiceItems, man kan fortsatt ta bort ServiceItems
    // från listan då detta är helt separerat från ServicePack. När WorkOrder sedan skapas
    // så hämtar man listan av ServiceItems från den temporära listan.
}
