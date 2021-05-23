package com.ticticboooom.mods.mm.client.jei.ingredients;

import com.ticticboooom.mods.mm.client.jei.ingredients.model.PressureStack;
import com.ticticboooom.mods.mm.inventory.botania.PortManaInventory;
import mezz.jei.api.ingredients.IIngredientHelper;

import javax.annotation.Nullable;

public class ManaIngredientHelper implements IIngredientHelper<PortManaInventory> {

    @Nullable
    @Override
    public PortManaInventory getMatch(Iterable<PortManaInventory> ingredients, PortManaInventory ingredientToMatch) {
        return ingredientToMatch;
    }

    @Override
    public String getDisplayName(PortManaInventory ingredient) {
        return "Pressure";
    }

    @Override
    public String getUniqueId(PortManaInventory ingredient) {
        return ingredient.getManaStored() + "";
    }

    @Override
    public String getModId(PortManaInventory ingredient) {
        return "botania";
    }

    @Override
    public String getResourceId(PortManaInventory ingredient) {
        return "mana";
    }

    @Override
    public PortManaInventory copyIngredient(PortManaInventory ingredient) {
        return new PortManaInventory(0, ingredient.getManaStored());
    }

    @Override
    public String getErrorInfo(@Nullable PortManaInventory ingredient) {
        return "Error";
    }
}