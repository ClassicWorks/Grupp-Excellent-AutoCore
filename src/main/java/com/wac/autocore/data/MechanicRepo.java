package com.wac.autocore.data;

import com.wac.autocore.model.Mechanic;

import java.util.List;
import java.util.Optional;

public interface MechanicRepo {
    Mechanic save(Mechanic m);
    List<Mechanic> getMechanics();
    List<Mechanic> getAvailableMechanics();
    Optional<Mechanic> getMechanic(int id);
}
