package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompletableRecipeRegistryTest {
    private CompletableRecipeRegistry registry;
    private TestRecipe testRecipe;
    private TestAltar testAltar;

    @BeforeEach
    void setUp() {
        this.registry = new CompletableRecipeRegistry();
        this.testRecipe = new TestRecipe(true);
        this.testAltar = new TestAltar();
    }

    @Test
    void registerRecipe_withNewRecipe_returnsTrue() {
        boolean result = this.registry.registerRecipe(this.testRecipe);

        assertTrue(result);
    }

    @Test
    void registerRecipe_withDuplicateRecipe_returnsFalse() {
        this.registry.registerRecipe(this.testRecipe);
        boolean result = this.registry.registerRecipe(this.testRecipe);

        assertFalse(result);
    }

    @Test
    void registerRecipe_withDifferentRecipesSameType_returnsTrue() {
        TestRecipe secondRecipe = new TestRecipe(true);

        this.registry.registerRecipe(this.testRecipe);
        boolean result = this.registry.registerRecipe(secondRecipe);

        assertTrue(result);
    }

    @Test
    void getRecipesByAltarType_withNoRecipes_returnsEmptyList() {
        List<CompletableRecipeInterface> recipes = this.registry.getRecipesByAltarType(TestAltar.class);

        assertNotNull(recipes);
        assertTrue(recipes.isEmpty());
    }

    @Test
    void getRecipesByAltarType_withRegisteredRecipes_returnsRecipes() {
        this.registry.registerRecipe(this.testRecipe);

        List<CompletableRecipeInterface> recipes = this.registry.getRecipesByAltarType(TestAltar.class);

        assertEquals(1, recipes.size());
        assertTrue(recipes.contains(this.testRecipe));
    }

    @Test
    void findMatchingRecipe_withNoRecipes_returnsNull() {
        CompletableRecipeInterface result = this.registry.findMatchingRecipe(this.testAltar);

        assertNull(result);
    }

    @Test
    void findMatchingRecipe_withMatchingRecipe_returnsRecipe() {
        this.registry.registerRecipe(this.testRecipe);

        CompletableRecipeInterface result = this.registry.findMatchingRecipe(this.testAltar);

        assertEquals(this.testRecipe, result);
    }

    @Test
    void findMatchingRecipe_withNonMatchingRecipe_returnsNull() {
        TestRecipe nonMatchingRecipe = new TestRecipe(false);
        this.registry.registerRecipe(nonMatchingRecipe);

        CompletableRecipeInterface result = this.registry.findMatchingRecipe(this.testAltar);

        assertNull(result);
    }

    @Test
    void unregisterRecipe_withExistingRecipe_returnsTrue() {
        this.registry.registerRecipe(this.testRecipe);

        boolean result = this.registry.unregisterRecipe(this.testRecipe);

        assertTrue(result);
    }

    @Test
    void unregisterRecipe_withNonExistingRecipe_returnsFalse() {
        boolean result = this.registry.unregisterRecipe(this.testRecipe);

        assertFalse(result);
    }

    @Test
    void registerRecipes_withMultipleRecipes_registersAll() {
        TestRecipe secondRecipe = new TestRecipe(true);
        TestRecipe thirdRecipe = new TestRecipe(true);

        List<CompletableRecipeInterface> recipes = new ArrayList<>();
        recipes.add(this.testRecipe);
        recipes.add(secondRecipe);
        recipes.add(thirdRecipe);

        boolean result = this.registry.registerRecipes(recipes);

        assertTrue(result);
        assertEquals(3, this.registry.getRecipesByAltarType(TestAltar.class).size());
    }

    static class TestAltar implements AltarInterface {
        @Override
        public org.bukkit.Location getLocation() {
            return null;
        }

        @Override
        public org.bukkit.entity.ItemFrame getPedestal(de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation location) {
            return null;
        }

        @Override
        public java.util.Map<de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation, org.bukkit.entity.ItemFrame> getPedestals() {
            return null;
        }
    }

    static class TestRecipe implements CompletableRecipeInterface {
        private final boolean shouldValidate;

        TestRecipe(boolean shouldValidate) {
            this.shouldValidate = shouldValidate;
        }

        @Override
        public Class<? extends AltarInterface> getAltarType() {
            return TestAltar.class;
        }

        @Override
        public boolean validateAltarState(AltarInterface altar) {
            return this.shouldValidate;
        }

        @Override
        public boolean autoCleanupAltar() {
            return false;
        }

        @Override
        public java.util.Map<de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation, org.bukkit.inventory.ItemStack> getRecipe() {
            return null;
        }

        @Override
        public void onRecipeComplete(org.bukkit.plugin.Plugin plugin, AltarInterface altar, io.papermc.paper.event.player.PlayerItemFrameChangeEvent event, Runnable removeLock) {
        }
    }
}
